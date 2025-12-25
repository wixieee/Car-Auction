package edu.lpnu.auction.model.enums.car;

public enum CarCondition {
    NEW("Новий"),

    USED("Вживаний"),

    DAMAGED("Пошкоджений"),

    RUN_AND_DRIVE("Заводиться та їде"),

    SALVAGE("Утиль / Під відновлення (Salvage)"),

    FOR_PARTS("На запчастини"),

    REBUILT("Відновлений");

    CarCondition(String ukrainianLabel) {
    }
}
