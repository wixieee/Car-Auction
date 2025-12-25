package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.request.CarRequest;
import edu.lpnu.auction.dto.response.NhtsaResponse;
import edu.lpnu.auction.model.Car;
import edu.lpnu.auction.utils.exception.types.NotFoundException;
import edu.lpnu.auction.utils.mapper.CarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarMapper carMapper;
    private final NhtsaService nhtsaService;
    private final ImageService imageService;

    public CarRequest prefill(String vin){
        Optional<NhtsaResponse.NhtsaVinResult> nhtsaResponse = nhtsaService.decodeVin(vin);

        return carMapper.mapNhtsaToRequest(nhtsaResponse.orElse(null), vin);
    }

    public Car prepareCarEntity(CarRequest carRequest, List<MultipartFile> images) {
        boolean hasValidImages = images != null && images.stream().anyMatch(f -> !f.isEmpty());

        if (!hasValidImages) {
            throw new NotFoundException("Додайте хоча б одне фото");
        }

        Car car = carMapper.toEntity(carRequest);

        List<String> uploadedUrls = imageService.saveImages(images);
        car.setImageUrls(uploadedUrls);

        return car;
    }
}
