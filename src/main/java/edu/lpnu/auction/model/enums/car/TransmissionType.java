package edu.lpnu.auction.model.enums.car;

import lombok.Getter;
import java.util.Arrays;
import java.util.List;

@Getter
public enum TransmissionType {
    AUTOMATIC("Автомат",
            "Automatic",
            "Automated Manual Transmission (AMT)",
            "Direct Drive"),

    MANUAL("Механіка", "Manual/Standard"),

    CVT("Варіатор (CVT)",
            "Continuously Variable Transmission (CVT)",
            "Electronic Continuously Variable (e-CVT)",
            "Motorcycle - CVT Belt Drive"),

    DCT("Робот (DCT)", "Dual-Clutch Transmission (DCT)"),

    OTHER("Інше / Мото",
            "Motorcycle - Shaft Drive Off-Road",
            "Motorcycle - Chain Drive Off-Road",
            "Motorcycle - Shaft Drive",
            "Motorcycle - Chain Drive"),

    UNKNOWN("Невідомо", "Unknown");

    private final String ukrainianLabel;
    private final List<String> nhtsaValues;

    TransmissionType(String ukrainianLabel, String... nhtsaValues) {
        this.ukrainianLabel = ukrainianLabel;
        this.nhtsaValues = Arrays.asList(nhtsaValues);
    }

    public static TransmissionType fromString(String text) {
        if (text == null || text.isBlank()) return UNKNOWN;
        return Arrays.stream(TransmissionType.values())
                .filter(t -> t.nhtsaValues.stream().anyMatch(text::equalsIgnoreCase))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
