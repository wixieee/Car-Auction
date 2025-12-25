package edu.lpnu.auction.factory;

import edu.lpnu.auction.dto.request.CarRequest;
import edu.lpnu.auction.dto.response.NhtsaResponse;
import edu.lpnu.auction.model.enums.car.ChargingPortType;
import edu.lpnu.auction.model.enums.car.FuelType;

public class CarFactory {

    private static NhtsaResponse.NhtsaVinResult getNhtsaVinResult(
            String year, String horsePower, String displacementL,
            String cylinderCount, String bodyType, String fuelType,
            String fuelTypeSecondary, String electrificationLevel,
            String batteryKWh
    ) {
        return NhtsaResponse.NhtsaVinResult.builder()
                .make(TestConstants.DEFAULT_MAKE)
                .model(TestConstants.DEFAULT_MODEL)
                .year(year != null ? year : TestConstants.NHTSA_YEAR_STR)
                .trim(TestConstants.DEFAULT_TRIM)
                .bodyType(bodyType != null ? bodyType : TestConstants.NHTSA_BODY_STR)
                .displacementL(displacementL)
                .cylinders(cylinderCount)
                .horsePower(horsePower != null ? horsePower : TestConstants.NHTSA_HP_STR)
                .fuelType(fuelType != null ? fuelType : TestConstants.NHTSA_FUEL_STR)
                .fuelTypeSecondary(fuelTypeSecondary)
                .electrificationLevel(electrificationLevel)
                .transmission(TestConstants.NHTSA_TRANSMISSION_STR)
                .driveType(TestConstants.NHTSA_DRIVE_STR)
                .batteryKWh(batteryKWh)
                .build();
    }

    public static NhtsaResponse.NhtsaVinResult getValidNthsaVinResult() {
        return getNhtsaVinResult(null, null, TestConstants.NHTSA_DISPLACEMENT_STR, TestConstants.NHTSA_CYLINDERS_STR, null, "Gasoline", null, null, null);
    }

    public static NhtsaResponse.NhtsaVinResult getValidGasolineNhtsaResult() {
        return getNhtsaVinResult(null, null, "3.0", "6", "SUV", "Gasoline", null, null, null);
    }

    public static NhtsaResponse.NhtsaVinResult getValidElectricNhtsaResult() {
        return getNhtsaVinResult(null, "283", "", "", "Sedan", "Electric", null, "BEV (Battery Electric Vehicle)", "75.0");
    }

    public static NhtsaResponse.NhtsaVinResult getPhevNhtsaResult() {
        return getNhtsaVinResult(null, "313", "2.0", "4", "SUV", "Electric", "Gasoline", "PHEV (Plug-in Hybrid Electric Vehicle)", null);
    }

    public static NhtsaResponse.NhtsaVinResult getRexNhtsaResult() {
        return getNhtsaVinResult(null, "170", "0.6", "2", "Hatchback", "Electric", "Gasoline", null, null);
    }

    public static NhtsaResponse.NhtsaVinResult getBrokenNthsaVinResult() {
        return getNhtsaVinResult("Two Thousand", "N/A", "", " ", "ERROR", "Unknown", null, null, null);
    }

    private static CarRequest getBaseCarRequest(boolean full) {
        CarRequest request = new CarRequest();
        request.setVin(TestConstants.DEFAULT_VIN);
        request.setMake(TestConstants.DEFAULT_MAKE);
        request.setModel(TestConstants.DEFAULT_MODEL);
        request.setYear(TestConstants.DEFAULT_YEAR);
        request.setTrim(TestConstants.DEFAULT_TRIM);
        request.setBodyType(TestConstants.DEFAULT_BODY_TYPE);
        request.setTransmission(TestConstants.DEFAULT_TRANSMISSION);
        request.setDriveType(TestConstants.DEFAULT_DRIVE_TYPE);

        if (full) {
            request.setMileage(TestConstants.DEFAULT_MILEAGE);
            request.setDescription(TestConstants.DEFAULT_DESCRIPTION);
            request.setColor(TestConstants.DEFAULT_COLOR);
            request.setCondition(TestConstants.DEFAULT_CONDITION);
            request.setHorsePower(TestConstants.DEFAULT_HORSE_POWER);
        }
        return request;
    }

    public static CarRequest getPrefiledCarRequest() {
        CarRequest req = getBaseCarRequest(false);
        req.setFuelType(TestConstants.DEFAULT_FUEL_TYPE);
        req.setEngineVolume(TestConstants.DEFAULT_ENGINE_VOLUME);
        req.setCylinderCount(TestConstants.DEFAULT_CYLINDERS);
        req.setHorsePower(TestConstants.DEFAULT_HORSE_POWER);
        return req;
    }

    public static CarRequest getFullCarRequest() {
        return getFullGasolineRequest();
    }

    public static CarRequest getFullGasolineRequest() {
        CarRequest request = getBaseCarRequest(true);
        request.setFuelType(FuelType.GASOLINE);
        request.setEngineVolume(TestConstants.DEFAULT_ENGINE_VOLUME);
        request.setCylinderCount(TestConstants.DEFAULT_CYLINDERS);
        return request;
    }

    public static CarRequest getFullElectricRequest() {
        CarRequest request = getBaseCarRequest(true);
        request.setFuelType(FuelType.ELECTRIC);
        request.setBatteryCapacity(75.0);
        request.setElectricRange(450);
        request.setChargingPort(ChargingPortType.TESLA_NACS);
        return request;
    }

    public static CarRequest getFullHybridRequest() {
        CarRequest request = getBaseCarRequest(true);
        request.setFuelType(FuelType.HYBRID);
        request.setEngineVolume(2.5);
        request.setCylinderCount(4);
        request.setBatteryCapacity(14.5);
        request.setElectricRange(60);
        request.setChargingPort(ChargingPortType.TYPE_2);
        return request;
    }

    public static CarRequest getCarRequestWithVin() {
        CarRequest carRequest = new CarRequest();
        carRequest.setVin(TestConstants.DEFAULT_VIN);
        return carRequest;
    }
}