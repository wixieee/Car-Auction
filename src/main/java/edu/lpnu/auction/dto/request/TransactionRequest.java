package edu.lpnu.auction.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    @NotNull
    @Positive
    private BigDecimal amount;

    @Valid
    @NotNull(message = "Дані карти обов'язкові")
    private CardData card;
}