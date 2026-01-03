package edu.lpnu.auction.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal balance;
    private BigDecimal frozenBalance;

    public BigDecimal getAvailableBalance() {
        if (balance == null) return BigDecimal.ZERO;
        return balance.subtract(frozenBalance != null ? frozenBalance : BigDecimal.ZERO);
    }
}