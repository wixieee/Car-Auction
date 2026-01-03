package edu.lpnu.auction.model.enums;

import lombok.Getter;

@Getter
public enum LotStatus {
    PENDING_REVIEW("Очікує підтвердження"),
    APPROVED("Підтверджено"),
    ACTIVE("Активний"),
    SOLD("Продано"),
    PAID("Оплачено"),
    UNSOLD("Не продано"),
    REJECTED("Відмовлено"),
    CANCELED("Скасовано");

    private final String ukrainianLabel;

    LotStatus(String ukrainianLabel) {
        this.ukrainianLabel = ukrainianLabel;
    }
}
