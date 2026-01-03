package edu.lpnu.auction.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

@Data
public class CardData {
    @Pattern(regexp = "^\\d{16}$", message = "Номер карти має містити 16 цифр")
    @ToString.Exclude
    private String cardNumber;

    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Термін дії у форматі MM/YY")
    @ToString.Exclude
    private String expiryDate;

    @Pattern(regexp = "^\\d{3}$", message = "CVV має містити 3 цифри")
    @ToString.Exclude
    private String cvv;

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Ім'я власника лише латиницею")
    @ToString.Exclude
    private String holderName;
}