package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.request.CreateLotRequest;
import edu.lpnu.auction.dto.request.LotApproveRequest;
import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.model.Car;
import edu.lpnu.auction.model.Lot;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.LotStatus;
import edu.lpnu.auction.repository.LotRepository;
import edu.lpnu.auction.utils.exception.types.InternalServerError;
import edu.lpnu.auction.utils.exception.types.NotFoundException;
import edu.lpnu.auction.utils.mapper.LotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LotService {
    private final LotRepository lotRepository;
    private final ImageService imageService;
    private final CarService carService;
    private final LotMapper lotMapper;

    @Transactional
    public LotResponse createLot(CreateLotRequest createLotRequest, List<MultipartFile> images, User seller) {
        Car car = carService.prepareCarEntity(createLotRequest.getCarRequest(), images);

        Lot lot = Lot.builder()
                .seller(seller)
                .car(car)
                .reservePrice(createLotRequest.getReservePrice())
                .status(LotStatus.PENDING_REVIEW)
                .currentPrice(BigDecimal.ZERO)
                .bidCount(0)
                .build();

        try {
            return lotMapper.toDto(lotRepository.save(lot));
        } catch (Exception e) {
            imageService.deleteImages(car.getImageUrls());
            throw new InternalServerError("Не вдалося створити лот", e);
        }
    }

    public Page<LotResponse> getLotsByStatus(LotStatus lotStatus, Pageable pageable) {
        return lotRepository.findByStatus(lotStatus, pageable)
                .map(lotMapper::toDto);
    }

    @Transactional
    public LotResponse approveLot(UUID id, LotApproveRequest approveRequest) {
        Lot lot = findById(id);

        if (lot.getStatus() != LotStatus.PENDING_REVIEW) {
            throw new IllegalArgumentException("Можна затверджувати тільки лоти на перевірці");
        }

        if (approveRequest.getEndTime().isBefore(approveRequest.getStartTime())) {
            throw new IllegalArgumentException("Час завершення не може бути раніше часу початку");
        }

        lot.setStartPrice(approveRequest.getStartPrice());
        lot.setStartTime(approveRequest.getStartTime());
        lot.setEndTime(approveRequest.getEndTime());
        lot.setMinBidIncrement(approveRequest.getMinBidIncrement());
        lot.setCurrentPrice(approveRequest.getStartPrice());
        lot.setStatus(LotStatus.APPROVED);

        return lotMapper.toDto(lotRepository.save(lot));
    }

    @Transactional
    public LotResponse rejectLot(UUID id) {
        Lot lot = findById(id);

        lot.setStatus(LotStatus.REJECTED);
        return lotMapper.toDto(lotRepository.save(lot));
    }

    private Lot findById(UUID id) {
        return lotRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Лот не знайдено"));
    }
}
