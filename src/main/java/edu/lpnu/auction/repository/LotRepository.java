package edu.lpnu.auction.repository;

import edu.lpnu.auction.model.Lot;
import edu.lpnu.auction.model.enums.LotStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LotRepository extends JpaRepository<Lot, UUID> {
    Page<Lot> findByStatus(LotStatus status, Pageable pageable);

    List<Lot> findAllByStatusAndStartTimeBefore(LotStatus status, LocalDateTime startTimeBefore);

    List<Lot> findAllByStatusAndEndTimeBefore(LotStatus lotStatus, LocalDateTime endTimeBefore);
}