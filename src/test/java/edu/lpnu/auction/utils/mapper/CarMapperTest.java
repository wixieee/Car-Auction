package edu.lpnu.auction.utils.mapper;

import edu.lpnu.auction.dto.request.CarRequest;
import edu.lpnu.auction.dto.response.NhtsaResponse;
import edu.lpnu.auction.factory.CarFactory;
import edu.lpnu.auction.factory.TestConstants;
import edu.lpnu.auction.model.Car;
import edu.lpnu.auction.model.enums.car.BodyType;
import edu.lpnu.auction.model.enums.car.FuelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CarMapperTest {
    private CarMapper carMapper;

    @BeforeEach
    void setUp() {
        carMapper = new CarMapper();
    }

    @Test
    void mapNhtsaToRequest_WhenBrokenNhtsaResponse_ShouldHandleWithNoErrors(){
        NhtsaResponse.NhtsaVinResult response = CarFactory.getBrokenNthsaVinResult();

        CarRequest result = carMapper.mapNhtsaToRequest(response, TestConstants.DEFAULT_VIN);

        assertNull(result.getYear());
        assertNull(result.getHorsePower());
        assertNull(result.getCylinderCount());
        assertNull(result.getEngineVolume());
        assertNull(result.getBatteryCapacity());

        assertEquals(BodyType.UNKNOWN, result.getBodyType());
    }

    @Test
    void mapNhtsaToRequest_WhenNullNhtsaResponse_ShouldReturnOnlyVIN(){
        CarRequest result = carMapper.mapNhtsaToRequest(null, TestConstants.DEFAULT_VIN);

        assertEquals(CarFactory.getCarRequestWithVin(), result);
    }

    @Test
    void mapNhtsaToRequest_WhenValidGasolineResponse_ShouldMapIceFields(){
        NhtsaResponse.NhtsaVinResult response = CarFactory.getValidGasolineNhtsaResult();

        CarRequest result = carMapper.mapNhtsaToRequest(response, TestConstants.DEFAULT_VIN);

        assertEquals(TestConstants.DEFAULT_VIN, result.getVin());
        assertEquals(FuelType.GASOLINE, result.getFuelType());
        assertThat(result.getEngineVolume()).isNotNull();
        assertThat(result.getCylinderCount()).isNotNull();
        assertNull(result.getBatteryCapacity());
    }

    @Test
    void mapNhtsaToRequest_WhenValidElectricResponse_ShouldMapBattery(){
        NhtsaResponse.NhtsaVinResult response = CarFactory.getValidElectricNhtsaResult();

        CarRequest result = carMapper.mapNhtsaToRequest(response, TestConstants.DEFAULT_VIN);

        assertEquals(FuelType.ELECTRIC, result.getFuelType());
        assertThat(result.getBatteryCapacity()).isEqualTo(75.0);

        assertNull(result.getEngineVolume());
    }

    @Test
    void mapNhtsaToRequest_WhenPhevLevel_ShouldMapToHybrid() {
        NhtsaResponse.NhtsaVinResult response = CarFactory.getPhevNhtsaResult();

        CarRequest result = carMapper.mapNhtsaToRequest(response, TestConstants.DEFAULT_VIN);

        assertEquals(FuelType.HYBRID, result.getFuelType());

        assertThat(result.getEngineVolume()).isEqualTo(2.0);
        assertThat(result.getCylinderCount()).isEqualTo(4);
    }

    @Test
    void mapNhtsaToRequest_WhenElectricWithGasSecondary_ShouldMapToHybrid() {
        NhtsaResponse.NhtsaVinResult response = CarFactory.getRexNhtsaResult();

        CarRequest result = carMapper.mapNhtsaToRequest(response, TestConstants.DEFAULT_VIN);

        assertEquals(FuelType.HYBRID, result.getFuelType());

        assertThat(result.getEngineVolume()).isEqualTo(0.6);
    }

    @Test
    void toEntity_WhenGasolineRequest_ShouldMapIceFields(){
        CarRequest carRequest = CarFactory.getFullGasolineRequest();

        Car actualCar = carMapper.toEntity(carRequest);

        assertThat(actualCar.getFuelType()).isEqualTo(FuelType.GASOLINE);
        assertThat(actualCar.getEngineVolume()).isEqualTo(carRequest.getEngineVolume());
        assertThat(actualCar.getCylinderCount()).isEqualTo(carRequest.getCylinderCount());

        assertNull(actualCar.getBatteryCapacity());
    }

    @Test
    void toEntity_WhenElectricRequest_ShouldSanitizeIceFields(){
        CarRequest carRequest = CarFactory.getFullElectricRequest();
        carRequest.setEngineVolume(2.0);
        carRequest.setCylinderCount(4);

        Car actualCar = carMapper.toEntity(carRequest);

        assertThat(actualCar.getFuelType()).isEqualTo(FuelType.ELECTRIC);

        assertNull(actualCar.getEngineVolume());
        assertNull(actualCar.getCylinderCount());

        assertThat(actualCar.getBatteryCapacity()).isEqualTo(carRequest.getBatteryCapacity());
        assertThat(actualCar.getChargingPort()).isEqualTo(carRequest.getChargingPort());
    }

    @Test
    void toEntity_WhenHybridRequest_ShouldMapBothIceAndBatteryFields() {
        CarRequest carRequest = CarFactory.getFullHybridRequest();

        Car actualCar = carMapper.toEntity(carRequest);

        assertThat(actualCar.getFuelType()).isEqualTo(FuelType.HYBRID);

        assertThat(actualCar.getEngineVolume()).isEqualTo(carRequest.getEngineVolume());
        assertThat(actualCar.getCylinderCount()).isEqualTo(carRequest.getCylinderCount());

        assertThat(actualCar.getBatteryCapacity()).isEqualTo(carRequest.getBatteryCapacity());
        assertThat(actualCar.getElectricRange()).isEqualTo(carRequest.getElectricRange());
        assertThat(actualCar.getChargingPort()).isEqualTo(carRequest.getChargingPort());
    }
}