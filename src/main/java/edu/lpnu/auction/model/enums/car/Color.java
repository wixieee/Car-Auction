package edu.lpnu.auction.model.enums.car;

import lombok.Getter;

@Getter
public enum Color {
    WHITE("Білий"),

    BLACK("Чорний"),

    GRAY("Сірий"),

    SILVER("Сріблястий"),

    BLUE("Синій / Блакитний"),

    RED("Червоний"),

    GREEN("Зелений"),

    BROWN("Коричневий"),

    BEIGE("Бежевий"),

    YELLOW("Жовтий"),

    ORANGE("Помаранчевий"),

    PURPLE("Фіолетовий"),

    OTHER("Інше");

    private final String ukrainianLabel;

    Color(String ukrainianLabel) {
        this.ukrainianLabel = ukrainianLabel;
    }
}
