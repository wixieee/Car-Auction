package edu.lpnu.auction.dto.response;

import edu.lpnu.auction.model.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private String id;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime createdAt;
    private String paymentMethod;
}