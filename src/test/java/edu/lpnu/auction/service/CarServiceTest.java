package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.CarRequest;
import edu.lpnu.auction.dto.NhtsaResponse;
import edu.lpnu.auction.factory.CarFactory;
import edu.lpnu.auction.factory.TestConstants;
import edu.lpnu.auction.model.Car;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.repository.CarRepository;
import edu.lpnu.auction.utils.exception.types.AlreadyExistsException;
import edu.lpnu.auction.utils.exception.types.InternalServerError;
import edu.lpnu.auction.utils.exception.types.NotFoundException;
import edu.lpnu.auction.utils.mapper.CarMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock private CarRepository carRepository;
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
    void create_WhenVINExists_ShouldThrowException() {
        CarRequest expectedRequest = CarFactory.getFullCarRequest();
        when((carRepository.existsByVin(expectedRequest.getVin()))).thenReturn(true);

        assertThrows(AlreadyExistsException.class,
                () -> carService.create(expectedRequest, new ArrayList<>(), new User()));

        verifyNoInteractions(carMapper, imageService);
        verify(carRepository, never()).save(any());
    }

    @Test
    void create_WhenMissingImages_ShouldThrowException() {
        CarRequest expectedRequest = CarFactory.getFullCarRequest();
        when((carRepository.existsByVin(expectedRequest.getVin()))).thenReturn(false);

        assertThrows(NotFoundException.class,
                () ->  carService.create(expectedRequest, null, new User()));

        assertThrows(NotFoundException.class,
                () ->  carService.create(expectedRequest, Collections.emptyList(), new User()));

        verifyNoInteractions(carMapper, imageService);
    }

    @Test
    void create_WhenValidArguments_ShouldReturnCreatedCar() {
        CarRequest request = CarFactory.getFullCarRequest();
        User seller = new User();
        Car mappedCar = new Car();
        List<MultipartFile> files = List.of(new MockMultipartFile("img", new byte[1]));
        List<String> uploadedUrls = List.of("url1", "url2");

        when(carRepository.existsByVin(any())).thenReturn(false);
        when(carMapper.toEntity(request)).thenReturn(mappedCar);
        when(imageService.saveImages(files)).thenReturn(uploadedUrls);
        when(carRepository.save(mappedCar)).thenReturn(mappedCar);

        Car result = carService.create(request, files, seller);

        assertThat(result).isNotNull();
        assertThat(result.getSeller()).isEqualTo(seller);
        assertThat(result.getImageUrls()).isEqualTo(uploadedUrls);

        verify(imageService).saveImages(files);
        verify(carRepository).save(mappedCar);
        verify(imageService, never()).deleteImages(any());
    }

    @Test
    void create_WhenDbFails_ShouldRollbackAndWrapException() {
        CarRequest request = CarFactory.getFullCarRequest();
        List<MultipartFile> files = List.of(new MockMultipartFile("img", new byte[1]));
        List<String> uploadedUrls = List.of("url_to_delete_1", "url_to_delete_2");

        Car mappedCar = new Car();
        RuntimeException dbException = new RuntimeException("Database constraint violation");

        when(carRepository.existsByVin(any())).thenReturn(false);
        when(carMapper.toEntity(any())).thenReturn(mappedCar);
        when(imageService.saveImages(files)).thenReturn(uploadedUrls);

        when(carRepository.save(any())).thenThrow(dbException);

        InternalServerError exception = assertThrows(InternalServerError.class,
                () -> carService.create(request, files, new User()));

        assertThat(exception.getMessage()).isEqualTo("Помилка збереження авто");
        assertThat(exception.getCause()).isEqualTo(dbException);

        verify(imageService).deleteImages(uploadedUrls);
    }
}
