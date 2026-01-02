package edu.lpnu.auction.dto.websocket;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AuctionEventDto {
    private String lotId;
    private BigDecimal currentPrice;
    private Integer bidCount;
    private LocalDateTime endTime;
    private List<BidderDto> lastBids;

    @Data
    @Builder
    public static class BidderDto {
        private String name;
        private BigDecimal amount;
        private LocalDateTime time;
    }
}