package edu.lpnu.auction.repository;

import edu.lpnu.auction.model.Lot;
import edu.lpnu.auction.model.enums.LotStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LotRepository extends JpaRepository<Lot, UUID>, JpaSpecificationExecutor<Lot> {
    Page<Lot> findByStatus(LotStatus status, Pageable pageable);

    List<Lot> findAllByStatusAndStartTimeBefore(LotStatus status, LocalDateTime startTimeBefore);

    List<Lot> findAllByStatusAndEndTimeBefore(LotStatus lotStatus, LocalDateTime endTimeBefore);

    Page<Lot> findAllBySellerId(UUID sellerId, Pageable pageable);

    @Query("SELECT DISTINCT l FROM Lot l JOIN l.bids b WHERE b.bidder.id = :userId")
    Page<Lot> findLotsByBidder(@Param("userId") UUID userId, Pageable pageable);
}