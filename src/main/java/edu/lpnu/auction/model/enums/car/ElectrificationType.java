package edu.lpnu.auction.model.enums.car;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ElectrificationType {
    BEV(FuelType.ELECTRIC, "BEV", "FCEV"),
    PHEV_HEV(FuelType.HYBRID, "PHEV", "HEV", "HYBRID");

    private final FuelType mappedFuelType;
    private final String[] keywords;

    ElectrificationType(FuelType mappedFuelType, String... keywords) {
        this.mappedFuelType = mappedFuelType;
        this.keywords = keywords;
    }

    public static FuelType mapToFuelType(String nhtsaString) {
        if (nhtsaString == null || nhtsaString.isBlank()) {
            return null;
        }

        String normalized = nhtsaString.toUpperCase();

        return Arrays.stream(values())
                .filter(type -> Arrays.stream(type.keywords).anyMatch(normalized::contains))
                .findFirst()
                .map(ElectrificationType::getMappedFuelType)
                .orElse(null);
    }
}