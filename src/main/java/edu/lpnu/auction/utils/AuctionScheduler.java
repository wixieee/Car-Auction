package edu.lpnu.auction.utils;

import edu.lpnu.auction.model.Lot;
import edu.lpnu.auction.model.enums.LotStatus;
import edu.lpnu.auction.repository.LotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionScheduler {
    private final LotRepository lotRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void checkAuctionStatus() {
        LocalDateTime now = LocalDateTime.now();

        startAuctions(now);
        endAuctions(now);
    }

    private void startAuctions(LocalDateTime now) {
        List<Lot> toStart = lotRepository.findAllByStatusAndStartTimeBefore(LotStatus.APPROVED, now);

        if(!toStart.isEmpty()){
            log.info("Початок {} аукціонів...", toStart.size());
            for (Lot lot : toStart) {
                lot.setStatus(LotStatus.ACTIVE);
                log.info("Лот ID: {} тепер ACTIVE", lot.getId());
            }

            lotRepository.saveAll(toStart);
        }
    }

    private void endAuctions(LocalDateTime now) {
        List<Lot> lotsToEnd = lotRepository.findAllByStatusAndEndTimeBefore(LotStatus.ACTIVE, now);

        if (!lotsToEnd.isEmpty()) {
            log.info("Завершення {} аукціонів...", lotsToEnd.size());
            for (Lot lot : lotsToEnd) {
                processFinishedLot(lot);
            }
            lotRepository.saveAll(lotsToEnd);
        }
    }

    private void processFinishedLot(Lot lot) {
        if (lot.getBidCount() == 0) {
            lot.setStatus(LotStatus.UNSOLD);
            log.info("Лот ID: {} завершено як UNSOLD (Немає ставок)", lot.getId());
            return;
        }

        if (lot.getReservePrice() != null) {
            if (lot.getCurrentPrice().compareTo(lot.getReservePrice()) >= 0) {
                lot.setStatus(LotStatus.SOLD);
                log.info("Лот ID: {} SOLD! Вартість: {}", lot.getId(), lot.getCurrentPrice());
            } else {
                lot.setStatus(LotStatus.UNSOLD);
                log.info("Лот ID: {} завершено як UNSOLD (Недосягнуто резерв: {} < {})",
                        lot.getId(), lot.getCurrentPrice(), lot.getReservePrice());
            }
        }
    }

}
