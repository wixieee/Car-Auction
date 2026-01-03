package edu.lpnu.auction.dto.request;

import edu.lpnu.auction.model.enums.LotStatus;
import edu.lpnu.auction.model.enums.car.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LotFilterRequest {
    private String make;
    private String model;

    private Integer yearFrom;
    private Integer yearTo;

    private BigDecimal priceMin;
    private BigDecimal priceMax;

    private Integer mileageMin;
    private Integer mileageMax;

    private Double engineVolumeMin;
    private Double engineVolumeMax;

    private Integer horsePowerMin;
    private Integer horsePowerMax;

    private BodyType bodyType;
    private FuelType fuelType;
    private TransmissionType transmission;
    private DriveType driveType;
    private Color color;
    private CarCondition condition;
    private LotStatus status;
}