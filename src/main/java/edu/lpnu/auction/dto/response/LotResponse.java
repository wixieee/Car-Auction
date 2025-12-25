package edu.lpnu.auction.dto.response;

import edu.lpnu.auction.model.enums.LotStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class LotResponse {
    private UUID id;
    private UserResponse seller;
    private CarResponse car;
    private BigDecimal currentPrice;
    private BigDecimal startPrice;
    private BigDecimal minBidIncrement;
    private boolean reserveMet;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LotStatus status;
    private Integer bidCount;
    private UserResponse currentHighBidder;
    private List<BidResponse> bids;
}