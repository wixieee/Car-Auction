package edu.lpnu.auction.dto.response;

import edu.lpnu.auction.model.enums.car.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CarResponse {
    private UUID id;
    private String vin;
    private String make;
    private String model;
    private Integer year;
    private String trim;
    private BodyType bodyType;
    private FuelType fuelType;
    private TransmissionType transmission;
    private DriveType driveType;
    private Color color;
    private CarCondition condition;
    private Double engineVolume;
    private Integer cylinderCount;
    private Integer horsePower;
    private Double batteryCapacity;
    private Integer electricRange;
    private ChargingPortType chargingPort;
    private Integer batteryHealth;
    private Integer mileage;
    private String description;
    private List<String> imageUrls;
}