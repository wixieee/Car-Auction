package edu.lpnu.auction.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashRequest {
    @NotNull(message = "Введіть суму")
    @Positive(message = "Сума має бути додатньою")
    private BigDecimal amount;
}
