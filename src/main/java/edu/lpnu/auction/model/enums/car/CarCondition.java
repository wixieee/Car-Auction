package edu.lpnu.auction.model.enums.car;

import lombok.Getter;

@Getter
public enum CarCondition {
    NEW("Новий"),

    USED("Вживаний"),

    DAMAGED("Пошкоджений"),

    RUN_AND_DRIVE("Заводиться та їде"),

    SALVAGE("Утиль / Під відновлення (Salvage)"),

    FOR_PARTS("На запчастини"),

    REBUILT("Відновлений");

    private final String ukrainianLabel;

    CarCondition(String ukrainianLabel) {
        this.ukrainianLabel = ukrainianLabel;
    }
}
