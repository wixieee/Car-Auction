package edu.lpnu.auction.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceUpdateDto {
    private BigDecimal availableBalance;
    private BigDecimal frozenBalance;
}