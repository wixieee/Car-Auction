package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.request.CarRequest;
import edu.lpnu.auction.dto.response.NhtsaResponse;
import edu.lpnu.auction.factory.CarFactory;
import edu.lpnu.auction.factory.TestConstants;
import edu.lpnu.auction.model.Car;
import edu.lpnu.auction.utils.exception.types.NotFoundException;
import edu.lpnu.auction.utils.mapper.CarMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock private CarMapper carMapper;
    @Mock private NhtsaService nhtsaService;
    @Mock private ImageService imageService;

    @InjectMocks
    private CarService carService;

    @Test
    void prefill_WhenValidVIN_ShouldReturnPrefilledCar() {
        String vin = TestConstants.DEFAULT_VIN;
        NhtsaResponse.NhtsaVinResult apiResult = CarFactory.getValidNthsaVinResult();
        CarRequest expectedRequest = CarFactory.getPrefiledCarRequest();

        when(nhtsaService.decodeVin(vin)).thenReturn(Optional.of(apiResult));
        when(carMapper.mapNhtsaToRequest(apiResult, vin)).thenReturn(expectedRequest);

        CarRequest result = carService.prefill(vin);

        assertThat(result).isEqualTo(expectedRequest);
        verify(nhtsaService).decodeVin(vin);
        verify(carMapper).mapNhtsaToRequest(apiResult, vin);
    }

    @Test
    void prefill_WhenInValidVIN_ShouldReturnOnlyVIN() {
        String vin = TestConstants.DEFAULT_VIN;
        CarRequest expectedRequest = CarFactory.getCarRequestWithVin();

        when(nhtsaService.decodeVin(vin)).thenReturn(Optional.empty());
        when(carMapper.mapNhtsaToRequest(null, vin)).thenReturn(expectedRequest);

        CarRequest result = carService.prefill(vin);

        assertThat(result).isEqualTo(expectedRequest);
        verify(nhtsaService).decodeVin(vin);
        verify(carMapper).mapNhtsaToRequest(null, vin);
    }

    @Test
    void prepareCarEntity_WhenMissingImages_ShouldThrowException() {
        CarRequest expectedRequest = CarFactory.getFullCarRequest();

        assertThrows(NotFoundException.class,
                () ->  carService.prepareCarEntity(expectedRequest, null));

        assertThrows(NotFoundException.class,
                () ->  carService.prepareCarEntity(expectedRequest, Collections.emptyList()));

        verifyNoInteractions(carMapper, imageService);
    }

    @Test
    void prepareCarEntity_WhenValidArguments_ShouldReturnPreparedCar() {
        CarRequest request = CarFactory.getFullCarRequest();
        Car mappedCar = new Car();
        List<MultipartFile> files = List.of(new MockMultipartFile("img", new byte[1]));
        List<String> uploadedUrls = List.of("url1", "url2");

        when(carMapper.toEntity(request)).thenReturn(mappedCar);
        when(imageService.saveImages(files)).thenReturn(uploadedUrls);

        Car result = carService.prepareCarEntity(request, files);

        assertThat(result).isNotNull();
        assertThat(result.getImageUrls()).isEqualTo(uploadedUrls);

        verify(imageService).saveImages(files);
        verify(carMapper).toEntity(request);
    }
}