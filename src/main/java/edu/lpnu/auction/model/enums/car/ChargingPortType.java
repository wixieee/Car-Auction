package edu.lpnu.auction.model.enums.car;

import lombok.Getter;

@Getter
public enum ChargingPortType {
    TYPE_1("Type 1 (J1772) - USA/Japan"),
    TYPE_2("Type 2 (Mennekes) - EU"),
    CCS_1("CCS Type 1 - USA Fast Charge"),
    CCS_2("CCS Type 2 - EU Fast Charge"),
    CHADEMO("CHAdeMO - Nissan/Kia"),
    TESLA_NACS("Tesla (NACS) / Supercharger"),
    GBT_AC("GB/T (AC) - China"),
    GBT_DC("GB/T (DC) - China"),
    UNKNOWN("Невідомо / Інше");

    private final String label;

    ChargingPortType(String label) {
        this.label = label;
    }
}