package edu.lpnu.auction.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LotApproveRequest {
    @NotNull(message = "Стартова ціна обов'язкова")
    @Positive(message = "Стартова ціна має бути додатною")
    private BigDecimal startPrice;

    @NotNull(message = "Мінімальний крок ставки обов'язковий")
    @Positive(message = "Крок ставки має бути додатним")
    private BigDecimal minBidIncrement;

    @NotNull(message = "Час початку обов'язковий")
    @Future(message = "Час початку має бути в майбутньому")
    private LocalDateTime startTime;

    @NotNull(message = "Час завершення обов'язковий")
    @Future(message = "Час завершення має бути в майбутньому")
    private LocalDateTime endTime;
}
