package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.CarRequest;
import edu.lpnu.auction.dto.NhtsaResponse;
import edu.lpnu.auction.model.Car;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.repository.CarRepository;
import edu.lpnu.auction.utils.exception.types.AlreadyExistsException;
import edu.lpnu.auction.utils.exception.types.InternalServerError;
import edu.lpnu.auction.utils.exception.types.NotFoundException;
import edu.lpnu.auction.utils.mapper.CarMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final NhtsaService nhtsaService;
    private final ImageService imageService;

    public CarRequest prefill(String vin){
        Optional<NhtsaResponse.NhtsaVinResult> nhtsaResponse = nhtsaService.decodeVin(vin);

        return carMapper.mapNhtsaToRequest(nhtsaResponse.orElse(null), vin);
    }

    @Transactional
    public Car create(CarRequest carRequest, List<MultipartFile> images, User seller) {
        if (carRepository.existsByVin(carRequest.getVin())) {
            throw new AlreadyExistsException("Авто з таким VIN вже існує");
        }
        boolean hasValidImages = images != null && images.stream().anyMatch(f -> !f.isEmpty());

        if (!hasValidImages) {
            throw new NotFoundException("Додайте хоча б одне фото");
        }

        Car car = carMapper.toEntity(carRequest);
        car.setSeller(seller);

        List<String> uploadedUrls = imageService.saveImages(images);
        car.setImageUrls(uploadedUrls);

        try {
            return carRepository.save(car);
        } catch (Exception e) {
            imageService.deleteImages(uploadedUrls);
            throw new InternalServerError("Помилка збереження авто", e);
        }
    }
}
