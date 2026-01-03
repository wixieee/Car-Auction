package edu.lpnu.auction.model.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    DEPOSIT("Поповнення"),
    WITHDRAWAL("Вивід"),
    PAYMENT("Оплата лоту");

    private final String ukrainianLabel;

    TransactionType(String ukrainianLabel) {
        this.ukrainianLabel = ukrainianLabel;
    }
}