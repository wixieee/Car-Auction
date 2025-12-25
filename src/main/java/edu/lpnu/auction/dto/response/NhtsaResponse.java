package edu.lpnu.auction.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NhtsaResponse {
    @JsonProperty("Results")
    private List<NhtsaVinResult> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    public static class NhtsaVinResult {
        @JsonProperty("Make") private String make;
        @JsonProperty("Model") private String model;
        @JsonProperty("ModelYear") private String year;
        @JsonProperty("Trim") private String trim;
        @JsonProperty("BodyClass") private String bodyType;
        @JsonProperty("DisplacementL") private String displacementL;
        @JsonProperty("EngineCylinders") private String cylinders;
        @JsonProperty("EngineHP") private String horsePower;
        @JsonProperty("FuelTypePrimary") private String fuelType;
        @JsonProperty("TransmissionStyle") private String transmission;
        @JsonProperty("DriveType") private String driveType;
        @JsonProperty("BatteryKWh") private String batteryKWh;
        @JsonProperty("FuelTypeSecondary") private String fuelTypeSecondary;
        @JsonProperty("ElectrificationLevel") private String electrificationLevel;
    }
}
