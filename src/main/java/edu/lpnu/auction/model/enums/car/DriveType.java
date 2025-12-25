package edu.lpnu.auction.model.enums.car;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum DriveType {
    FWD("Передній",
            "FWD/Front-Wheel Drive"),

    RWD("Задній",
            "RWD/Rear-Wheel Drive",
            "4x2",
            "6x2", "6x4",
            "8x2", "8x4", "8x6",
            "10x4", "10x6", "10x8",
            "12x4", "12x6",
            "14x4", "14x6",
            "2WD"),

    AWD("Повний (AWD)",
            "AWD/All-Wheel Drive"),

    FOUR_WD("Повний (4x4 / Off-Road)",
            "4WD/4-Wheel Drive/4x4",
            "2WD/4WD",
            "6x6", "8x8", "10x10"),

    OTHER("Інше / Спецтехніка",
            "Glider"),

    UNKNOWN("Невідомо", "Unknown");

    private final String ukrainianLabel;
    private final List<String> nhtsaValues;

    DriveType(String ukrainianLabel, String... nhtsaValues) {
        this.ukrainianLabel = ukrainianLabel;
        this.nhtsaValues = Arrays.asList(nhtsaValues);
    }

    public static DriveType fromString(String text) {
        if (text == null || text.isBlank()) return UNKNOWN;
        return Arrays.stream(DriveType.values())
                .filter(t -> t.nhtsaValues.stream().anyMatch(text::equalsIgnoreCase))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
