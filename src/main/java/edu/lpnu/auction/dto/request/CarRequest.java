package edu.lpnu.auction.dto.request;

import edu.lpnu.auction.model.enums.car.*;
import edu.lpnu.auction.utils.validation.ValidCarConfiguration;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@ValidCarConfiguration
public class CarRequest {
    @NotBlank(message = "VIN не може бути порожнім")
    @Size(min = 17, max = 17, message = "VIN має складатись рівно з 17 символів")
    private String vin;

    @NotBlank(message = "Марка обов'язкова")
    private String make;

    @NotBlank(message = "Модель обов'язкова")
    private String model;

    @NotNull(message = "Рік випуску обов'язковий")
    @Min(value = 1900, message = "Рік випуску має бути більшим за 1900") @Max(value = 2026, message = "Рік випуску має бути меншим за 2026")
    private Integer year;

    private String trim;

    @NotNull(message = "Тип кузова обов'язковий")
    private BodyType bodyType;

    @NotNull(message = "Тип палива обов'язковий")
    private FuelType fuelType;

    @NotNull(message = "Коробка передач обов'язкова")
    private TransmissionType transmission;

    @NotNull(message = "Привід обов'язковий")
    private DriveType driveType;

    @Positive(message = "Об'єм двигуна має бути більше нуля")
    private Double engineVolume;

    @Positive(message = "Кількість циліндрів має бути більше нуля")
    private Integer cylinderCount;

    @Positive(message = "Ємність батареї має бути більше нуля")
    private Double batteryCapacity;

    @Positive(message = "Запас ходу має бути більше нуля")
    private Integer electricRange;

    private ChargingPortType chargingPort;

    @Min(value = 0, message = "Мінімум 0%") @Max(value = 100, message = "Максимум 100%")
    private Integer batteryHealth;

    @NotNull(message = "Потужність обов'язкова")
    @Positive(message = "Кількість кс має бути більше нуля")
    private Integer horsePower;

    @NotNull(message = "Пробіг обов'язковий")
    @PositiveOrZero(message = "Пробіг має бути більший або рівний нулю")
    private Integer mileage;

    @NotNull(message = "Колір обов'язковий")
    private Color color;

    @Size(max = 2000, message = "Максимальний розмір опису 2000 символів")
    private String description;

    @NotNull(message = "Стан авто обов'язковий")
    private CarCondition condition;
}