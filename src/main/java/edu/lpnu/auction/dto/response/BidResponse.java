package edu.lpnu.auction.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BidResponse {
    private UUID id;
    private BigDecimal amount;
    private LocalDateTime bidTime;
    private UserResponse bidder;
}