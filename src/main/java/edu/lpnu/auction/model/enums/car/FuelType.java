package edu.lpnu.auction.model.enums.car;

import lombok.Getter;
import java.util.Arrays;
import java.util.List;

@Getter
public enum FuelType {
    GASOLINE("Бензин", "Gasoline"),

    DIESEL("Дизель", "Diesel"),

    ELECTRIC("Електро", "Electric"),

    GAS("Газ (ГБО / Метан / Пропан)",
            "Liquefied Natural Gas (LNG)",
            "Compressed Natural Gas (CNG)",
            "Natural Gas",
            "Liquefied Petroleum Gas (propane or LPG)"),

    FLEX_FUEL("Flex Fuel (Спирт/Бензин)",
            "Ethanol (E85)",
            "Neat Ethanol (E100)",
            "Flexible Fuel Vehicle (FFV)",
            "Neat Methanol (M100)",
            "Methanol (M85)"),

    HYDROGEN("Водень", "Fuel Cell", "Compressed Hydrogen/Hydrogen"),

    HYBRID("Гібрид", "Hybrid"),

    OTHER("Інше"),

    UNKNOWN("Невідомо", "Unknown");


    private final String ukrainianLabel;
    private final List<String> nhtsaValues;

    FuelType(String ukrainianLabel, String... nhtsaValues) {
        this.ukrainianLabel = ukrainianLabel;
        this.nhtsaValues = Arrays.asList(nhtsaValues);
    }

    public static FuelType fromString(String text) {
        if (text == null || text.isBlank()) return UNKNOWN;
        return Arrays.stream(FuelType.values())
                .filter(t -> t.nhtsaValues.stream().anyMatch(text::equalsIgnoreCase))
                .findFirst()
                .orElse(UNKNOWN);
    }
}