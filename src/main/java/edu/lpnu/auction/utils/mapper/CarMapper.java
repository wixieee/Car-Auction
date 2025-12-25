package edu.lpnu.auction.utils.mapper;

import edu.lpnu.auction.dto.CarRequest;
import edu.lpnu.auction.dto.NhtsaResponse;
import edu.lpnu.auction.model.Car;
import edu.lpnu.auction.model.enums.car.*;
import edu.lpnu.auction.model.enums.car.ElectrificationType;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

    public CarRequest mapNhtsaToRequest(NhtsaResponse.NhtsaVinResult nhtsaResponse, String vin) {
        CarRequest carRequest = new CarRequest();
        carRequest.setVin(vin);

        if (nhtsaResponse == null) return carRequest;

        carRequest.setMake(nhtsaResponse.getMake());
        carRequest.setModel(nhtsaResponse.getModel());
        carRequest.setYear(parseIntSafe(nhtsaResponse.getYear()));
        carRequest.setTrim(nhtsaResponse.getTrim());
        carRequest.setBodyType(BodyType.fromString(nhtsaResponse.getBodyType()));

        carRequest.setFuelType(resolveFuelType(nhtsaResponse));

        carRequest.setTransmission(TransmissionType.fromString(nhtsaResponse.getTransmission()));
        carRequest.setDriveType(DriveType.fromString(nhtsaResponse.getDriveType()));
        carRequest.setHorsePower(parseIntSafe(nhtsaResponse.getHorsePower()));

        carRequest.setEngineVolume(parseDoubleSafe(nhtsaResponse.getDisplacementL()));
        carRequest.setCylinderCount(parseIntSafe(nhtsaResponse.getCylinders()));

        carRequest.setBatteryCapacity(parseDoubleSafe(nhtsaResponse.getBatteryKWh()));

        return carRequest;
    }

    public Car toEntity(CarRequest request) {
        Car car = new Car();
        car.setVin(request.getVin());
        car.setMake(request.getMake());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setTrim(request.getTrim());
        car.setBodyType(request.getBodyType());
        car.setFuelType(request.getFuelType());
        car.setTransmission(request.getTransmission());
        car.setDriveType(request.getDriveType());
        car.setColor(request.getColor());
        car.setCondition(request.getCondition());
        car.setHorsePower(request.getHorsePower());
        car.setMileage(request.getMileage());
        car.setDescription(request.getDescription());

        if (isIce(request.getFuelType())) {
            car.setEngineVolume(request.getEngineVolume());
            car.setCylinderCount(request.getCylinderCount());
        } else {
            car.setEngineVolume(null);
            car.setCylinderCount(null);
        }

        if (hasBattery(request.getFuelType())) {
            car.setBatteryCapacity(request.getBatteryCapacity());
            car.setElectricRange(request.getElectricRange());
            car.setChargingPort(request.getChargingPort());
            car.setBatteryHealth(request.getBatteryHealth());
        } else {
            car.setBatteryCapacity(null);
            car.setElectricRange(null);
            car.setChargingPort(null);
            car.setBatteryHealth(null);
        }

        return car;
    }

    private FuelType resolveFuelType(NhtsaResponse.NhtsaVinResult result) {
        FuelType fromLevel = ElectrificationType.mapToFuelType(result.getElectrificationLevel());
        if (fromLevel != null) {
            return fromLevel;
        }

        String primaryStr = result.getFuelType();
        String secondaryStr = result.getFuelTypeSecondary();
        FuelType primaryType = FuelType.fromString(primaryStr);
        FuelType secondaryType = FuelType.fromString(secondaryStr);

        if (primaryType == FuelType.ELECTRIC && isIce(secondaryType)) {
            return FuelType.HYBRID;
        }

        return primaryType;
    }

    private boolean isIce(FuelType type) {
        return type == FuelType.GASOLINE ||
                type == FuelType.DIESEL ||
                type == FuelType.HYBRID ||
                type == FuelType.GAS ||
                type == FuelType.FLEX_FUEL;
    }

    private boolean hasBattery(FuelType type) {
        return type == FuelType.ELECTRIC || type == FuelType.HYBRID;
    }

    private Integer parseIntSafe(String val) {
        try { return (val != null && !val.isBlank()) ? (int) Double.parseDouble(val) : null; }
        catch (NumberFormatException e) { return null; }
    }

    private Double parseDoubleSafe(String val) {
        try { return (val != null && !val.isBlank()) ? Double.parseDouble(val) : null; }
        catch (NumberFormatException e) { return null; }
    }
}