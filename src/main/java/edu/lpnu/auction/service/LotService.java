package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.request.CreateLotRequest;
import edu.lpnu.auction.dto.request.LotApproveRequest;
import edu.lpnu.auction.dto.request.LotFilterRequest;
import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.model.Car;
import edu.lpnu.auction.model.Lot;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.LotStatus;
import edu.lpnu.auction.repository.LotRepository;
import edu.lpnu.auction.utils.LotSpecification;
import edu.lpnu.auction.utils.exception.types.BadRequestException;
import edu.lpnu.auction.utils.exception.types.InternalServerError;
import edu.lpnu.auction.utils.exception.types.NotFoundException;
import edu.lpnu.auction.utils.mapper.LotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LotService {
    private final LotRepository lotRepository;
    private final WalletService walletService;
    private final ImageService imageService;
    private final CarService carService;
    private final LotMapper lotMapper;

    @Transactional
    public LotResponse createLot(CreateLotRequest createLotRequest, List<MultipartFile> images, User seller) {
        Car car = carService.prepareCarEntity(createLotRequest.getCarRequest(), images);

        Lot lot = Lot.builder()
                .seller(seller)
                .car(car)
                .reservePrice(createLotRequest.getReservePrice())
                .status(LotStatus.PENDING_REVIEW)
                .currentPrice(BigDecimal.ZERO)
                .bidCount(0)
                .build();

        try {
            return lotMapper.toDto(lotRepository.save(lot));
        } catch (Exception e) {
            imageService.deleteImages(car.getImageUrls());
            throw new InternalServerError("Не вдалося створити лот", e);
        }
    }

    public Page<LotResponse> getLotsByStatus(LotStatus lotStatus, Pageable pageable) {
        return lotRepository.findByStatus(lotStatus, pageable)
                .map(lotMapper::toDto);
    }

    @Transactional
    public LotResponse approveLot(UUID id, LotApproveRequest approveRequest) {
        Lot lot = findById(id);

        if (lot.getStatus() != LotStatus.PENDING_REVIEW) {
            throw new IllegalArgumentException("Можна затверджувати тільки лоти на перевірці");
        }

        if (approveRequest.getEndTime().isBefore(approveRequest.getStartTime())) {
            throw new IllegalArgumentException("Час завершення не може бути раніше часу початку");
        }

        lot.setStartPrice(approveRequest.getStartPrice());
        lot.setStartTime(approveRequest.getStartTime());
        lot.setEndTime(approveRequest.getEndTime());
        lot.setMinBidIncrement(approveRequest.getMinBidIncrement());
        lot.setCurrentPrice(approveRequest.getStartPrice());
        lot.setStatus(LotStatus.APPROVED);

        return lotMapper.toDto(lotRepository.save(lot));
    }

    @Transactional
    public LotResponse rejectLot(UUID id) {
        Lot lot = findById(id);

        lot.setStatus(LotStatus.REJECTED);
        return lotMapper.toDto(lotRepository.save(lot));
    }

    @Transactional
    public LotResponse payForLot(UUID lotId, User payer) {
        Lot lot = findById(lotId);

        if (lot.getStatus() != LotStatus.SOLD) {
            throw new BadRequestException("Цей лот не готовий до оплати");
        }

        if (!lot.getCurrentHighBidder().getId().equals(payer.getId())) {
            throw new BadRequestException("Тільки переможець аукціону може оплатити лот");
        }

        walletService.transferFunds(
                payer,
                lot.getSeller().getId(),
                lot.getCurrentPrice()
        );

        lot.setStatus(LotStatus.PAID);
        Lot savedLot = lotRepository.save(lot);
        return lotMapper.toDto(savedLot);
    }

    public Page<LotResponse> getAllActiveLots(Pageable pageable, LotFilterRequest lotFilterRequest) {
        Specification<Lot> specification = LotSpecification.getSpec(lotFilterRequest);
        Page<Lot> page = lotRepository.findAll(specification, pageable);
        return page.map(lotMapper::toDto);
    }

    public LotResponse getLotById(UUID id) {
        return lotMapper.toDto(findById(id));
    }

    public Page<LotResponse> getUserLots(User user, Pageable pageable) {
        return lotRepository.findAllBySellerId(user.getId(), pageable)
                .map(lotMapper::toDto);
    }

    public Page<LotResponse> getUserBiddedLots(User user, Pageable pageable) {
        return lotRepository.findLotsByBidder(user.getId(), pageable)
                .map(lotMapper::toDto);
    }

    @Transactional
    public void cancelLot(UUID lotId, User user) {
        Lot lot = findById(lotId);

        if (!lot.getSeller().getId().equals(user.getId())) {
            throw new BadRequestException("Ви не можете керувати чужим лотом");
        }

        if (lot.getStatus() != LotStatus.PENDING_REVIEW && lot.getStatus() != LotStatus.APPROVED) {
            throw new BadRequestException("Лот не можна скасувати на цьому етапі (він вже активний або завершений)");
        }

        lot.setStatus(LotStatus.CANCELED);
        lotRepository.save(lot);
    }

    private Lot findById(UUID id) {
        return lotRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Лот не знайдено"));
    }
}
