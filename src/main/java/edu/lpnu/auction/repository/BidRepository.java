package edu.lpnu.auction.repository;

import edu.lpnu.auction.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {
}