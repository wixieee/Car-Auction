package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.request.CashRequest;
import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.dto.websocket.AuctionEventDto;
import edu.lpnu.auction.model.Bid;
import edu.lpnu.auction.model.Lot;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.LotStatus;
import edu.lpnu.auction.repository.LotRepository;
import edu.lpnu.auction.utils.exception.types.BadRequestException;
import edu.lpnu.auction.utils.exception.types.NotFoundException;
import edu.lpnu.auction.utils.mapper.LotMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {

    private final LotRepository lotRepository;
    private final WalletService walletService;
    private final LotMapper lotMapper;
    private final SimpMessagingTemplate messagingTemplate;

    private static final long ANTI_SNIPING_MINUTES = 5;

    @Transactional
    public LotResponse placeBid(UUID lotId, User user, CashRequest bid){
        Lot lot = lotRepository.findById(lotId).orElseThrow(
                () -> new NotFoundException("Лот не знайдено")
        );

        validateBid(lot, bid.getAmount(), user);

        walletService.freezeDeposit(user, bid.getAmount());

        if (lot.getCurrentHighBidder() != null) {
            walletService.unfreezeDeposit(
                    lot.getCurrentHighBidder(),
                    lot.getCurrentPrice()
            );
        }

        Bid newBid = new Bid();
        newBid.setLot(lot);
        newBid.setBidder(user);
        newBid.setAmount(bid.getAmount());
        newBid.setBidTime(LocalDateTime.now());

        lot.getBids().add(newBid);
        lot.setCurrentPrice(bid.getAmount());
        lot.setCurrentHighBidder(user);
        lot.setBidCount(lot.getBidCount() + 1);

        handleAntiSniping(lot);

        Lot savedLot = lotRepository.save(lot);

        sendAuctionUpdate(savedLot);

        return lotMapper.toDto(savedLot);
    }

    private void sendAuctionUpdate(Lot lot) {
        try {
            List<AuctionEventDto.BidderDto> last10Bids = lot.getBids().stream()
                    .sorted((b1, b2) -> b2.getBidTime().compareTo(b1.getBidTime()))
                    .limit(10)
                    .map(b -> AuctionEventDto.BidderDto.builder()
                            .name(maskName(b.getBidder()))
                            .amount(b.getAmount())
                            .time(b.getBidTime())
                            .build())
                    .collect(Collectors.toList());

            AuctionEventDto update = AuctionEventDto.builder()
                    .lotId(lot.getId().toString())
                    .currentPrice(lot.getCurrentPrice())
                    .bidCount(lot.getBidCount())
                    .endTime(lot.getEndTime())
                    .lastBids(last10Bids)
                    .build();

            messagingTemplate.convertAndSend("/topic/lot/" + lot.getId(), update);

        } catch (Exception e) {
            log.error("Помилка відправлення оновлень {}", lot.getId(), e);
        }
    }

    private String maskName(User user) {
        if (user.getFirstName() == null) return "User";
        String lastInitial = (user.getLastName() != null && !user.getLastName().isEmpty())
                ? user.getLastName().charAt(0) + "."
                : "";
        return user.getFirstName() + " " + lastInitial;
    }

    private void validateBid(Lot lot, BigDecimal amount, User bidder) {
        if (lot.getStatus() != LotStatus.ACTIVE) {
            throw new BadRequestException("Аукціон не активний");
        }
        if (LocalDateTime.now().isAfter(lot.getEndTime())) {
            throw new BadRequestException("Час аукціону вийшов");
        }
        if (lot.getSeller().getId().equals(bidder.getId())) {
            throw new BadRequestException("Ви не можете ставити на власний лот");
        }
        if (lot.getCurrentHighBidder() != null &&
                lot.getCurrentHighBidder().getId().equals(bidder.getId())) {
            throw new BadRequestException("Ви вже є лідером торгів");
        }
        BigDecimal minNextBid = lot.getCurrentPrice().add(lot.getMinBidIncrement());
        if (amount.compareTo(minNextBid) < 0) {
            throw new BadRequestException("Мінімальна ставка: " + minNextBid);
        }
    }

    private void handleAntiSniping(Lot lot) {
        LocalDateTime now = LocalDateTime.now();
        if (lot.getEndTime().minusMinutes(ANTI_SNIPING_MINUTES).isBefore(now)) {
            lot.setEndTime(lot.getEndTime().plusMinutes(ANTI_SNIPING_MINUTES));
        }
    }
}