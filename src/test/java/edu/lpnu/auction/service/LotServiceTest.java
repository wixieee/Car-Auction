package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.request.CreateLotRequest;
import edu.lpnu.auction.dto.request.LotApproveRequest;
import edu.lpnu.auction.dto.request.LotFilterRequest;
import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.factory.LotFactory;
import edu.lpnu.auction.factory.UserFactory;
import edu.lpnu.auction.model.Car;
import edu.lpnu.auction.model.Lot;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.LotStatus;
import edu.lpnu.auction.repository.LotRepository;
import edu.lpnu.auction.utils.exception.types.BadRequestException;
import edu.lpnu.auction.utils.exception.types.InternalServerError;
import edu.lpnu.auction.utils.exception.types.NotFoundException;
import edu.lpnu.auction.utils.mapper.LotMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LotServiceTest {

    @Mock private LotRepository lotRepository;
    @Mock private WalletService walletService;
    @Mock private ImageService imageService;
    @Mock private CarService carService;
    @Mock private LotMapper lotMapper;

    @InjectMocks
    private LotService lotService;


    @Test
    void createLot_WhenValidData_ShouldReturnResponse() {
        User seller = UserFactory.getLocalUser();
        CreateLotRequest request = LotFactory.getCreateLotRequest();
        List<MultipartFile> images = Collections.emptyList();
        Car preparedCar = new Car();
        Lot savedLot = LotFactory.getPendingLot(seller);
        LotResponse expectedResponse = LotFactory.getLotResponse(savedLot);

        when(carService.prepareCarEntity(request.getCarRequest(), images)).thenReturn(preparedCar);
        when(lotRepository.save(any(Lot.class))).thenReturn(savedLot);
        when(lotMapper.toDto(savedLot)).thenReturn(expectedResponse);

        LotResponse result = lotService.createLot(request, images, seller);

        assertThat(result).isEqualTo(expectedResponse);
        verify(lotRepository).save(any(Lot.class));
    }

    @Test
    void createLot_WhenSaveFails_ShouldDeleteImagesAndThrowException() {
        User seller = UserFactory.getLocalUser();
        CreateLotRequest request = LotFactory.getCreateLotRequest();
        List<MultipartFile> images = Collections.emptyList();
        Car preparedCar = new Car();
        preparedCar.setImageUrls(List.of("url1", "url2"));

        when(carService.prepareCarEntity(request.getCarRequest(), images)).thenReturn(preparedCar);
        when(lotRepository.save(any(Lot.class))).thenThrow(new RuntimeException("DB Error"));

        assertThrows(InternalServerError.class, () -> lotService.createLot(request, images, seller));

        verify(imageService).deleteImages(preparedCar.getImageUrls());
    }

    @Test
    void approveLot_WhenValidPendingLot_ShouldApprove() {
        User seller = UserFactory.getLocalUser();
        Lot lot = LotFactory.getPendingLot(seller);
        LotApproveRequest request = LotFactory.getValidApproveRequest();
        LotResponse expectedResponse = LotFactory.getLotResponse(lot);
        expectedResponse.setStatus(LotStatus.APPROVED);

        when(lotRepository.findById(lot.getId())).thenReturn(Optional.of(lot));
        when(lotRepository.save(lot)).thenReturn(lot);
        when(lotMapper.toDto(lot)).thenReturn(expectedResponse);

        LotResponse result = lotService.approveLot(lot.getId(), request);

        assertThat(result.getStatus()).isEqualTo(LotStatus.APPROVED);
        assertThat(lot.getStatus()).isEqualTo(LotStatus.APPROVED);
        assertThat(lot.getStartPrice()).isEqualTo(request.getStartPrice());
    }

    @Test
    void approveLot_WhenLotNotPending_ShouldThrowException() {
        User seller = UserFactory.getLocalUser();
        Lot lot = LotFactory.getActiveLot(seller);
        LotApproveRequest request = LotFactory.getValidApproveRequest();

        when(lotRepository.findById(lot.getId())).thenReturn(Optional.of(lot));

        assertThrows(IllegalArgumentException.class, () -> lotService.approveLot(lot.getId(), request));
    }

    @Test
    void approveLot_WhenEndTimeBeforeStartTime_ShouldThrowException() {
        User seller = UserFactory.getLocalUser();
        Lot lot = LotFactory.getPendingLot(seller);
        LotApproveRequest request = LotFactory.getInvalidTimeApproveRequest();

        when(lotRepository.findById(lot.getId())).thenReturn(Optional.of(lot));

        assertThrows(IllegalArgumentException.class, () -> lotService.approveLot(lot.getId(), request));
    }

    @Test
    void rejectLot_ShouldSetStatusRejected() {
        User seller = UserFactory.getLocalUser();
        Lot lot = LotFactory.getPendingLot(seller);
        LotResponse expectedResponse = LotFactory.getLotResponse(lot);

        when(lotRepository.findById(lot.getId())).thenReturn(Optional.of(lot));
        when(lotRepository.save(lot)).thenReturn(lot);
        when(lotMapper.toDto(lot)).thenReturn(expectedResponse);

        lotService.rejectLot(lot.getId());

        assertThat(lot.getStatus()).isEqualTo(LotStatus.REJECTED);
        verify(lotRepository).save(lot);
    }

    @Test
    void payForLot_WhenValid_ShouldTransferFundsAndSetPaid() {
        User seller = UserFactory.getLocalUser();
        User winner = UserFactory.getGoogleUser();
        Lot lot = LotFactory.getSoldLot(seller, winner, new BigDecimal("15000"));
        LotResponse expectedResponse = LotFactory.getLotResponse(lot);

        when(lotRepository.findById(lot.getId())).thenReturn(Optional.of(lot));
        when(lotRepository.save(lot)).thenReturn(lot);
        when(lotMapper.toDto(lot)).thenReturn(expectedResponse);

        lotService.payForLot(lot.getId(), winner);

        assertThat(lot.getStatus()).isEqualTo(LotStatus.PAID);
        verify(walletService).transferFunds(winner, seller.getId(), lot.getCurrentPrice());
    }

    @Test
    void payForLot_WhenStatusNotSold_ShouldThrowException() {
        User seller = UserFactory.getLocalUser();
        User winner = UserFactory.getGoogleUser();
        Lot lot = LotFactory.getActiveLot(seller);

        when(lotRepository.findById(lot.getId())).thenReturn(Optional.of(lot));

        assertThrows(BadRequestException.class, () -> lotService.payForLot(lot.getId(), winner));
    }

    @Test
    void payForLot_WhenPayerNotWinner_ShouldThrowException() {
        User seller = UserFactory.getLocalUser();
        User winner = UserFactory.getGoogleUser();
        User randomUser = UserFactory.getLocalUser();
        randomUser.setId(UUID.randomUUID());
        Lot lot = LotFactory.getSoldLot(seller, winner, new BigDecimal("15000"));

        when(lotRepository.findById(lot.getId())).thenReturn(Optional.of(lot));

        assertThrows(BadRequestException.class, () -> lotService.payForLot(lot.getId(), randomUser));
    }

    @Test
    void cancelLot_WhenPendingAndOwner_ShouldCancel() {
        User seller = UserFactory.getLocalUser();
        Lot lot = LotFactory.getPendingLot(seller);

        when(lotRepository.findById(lot.getId())).thenReturn(Optional.of(lot));

        lotService.cancelLot(lot.getId(), seller);

        assertThat(lot.getStatus()).isEqualTo(LotStatus.CANCELED);
        verify(lotRepository).save(lot);
    }

    @Test
    void cancelLot_WhenNotOwner_ShouldThrowException() {
        User seller = UserFactory.getLocalUser();
        User otherUser = UserFactory.getGoogleUser();
        otherUser.setId(UUID.randomUUID());
        Lot lot = LotFactory.getPendingLot(seller);

        when(lotRepository.findById(lot.getId())).thenReturn(Optional.of(lot));

        assertThrows(BadRequestException.class, () -> lotService.cancelLot(lot.getId(), otherUser));
    }

    @Test
    void cancelLot_WhenAlreadyActive_ShouldThrowException() {
        User seller = UserFactory.getLocalUser();
        Lot lot = LotFactory.getActiveLot(seller);

        when(lotRepository.findById(lot.getId())).thenReturn(Optional.of(lot));

        assertThrows(BadRequestException.class, () -> lotService.cancelLot(lot.getId(), seller));
    }

    @Test
    void getLotById_WhenExists_ShouldReturnLot() {
        Lot lot = LotFactory.getActiveLot(UserFactory.getLocalUser());
        LotResponse response = LotFactory.getLotResponse(lot);

        when(lotRepository.findById(lot.getId())).thenReturn(Optional.of(lot));
        when(lotMapper.toDto(lot)).thenReturn(response);

        assertThat(lotService.getLotById(lot.getId())).isEqualTo(response);
    }

    @Test
    void getLotById_WhenNotFound_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(lotRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> lotService.getLotById(id));
    }

    @Test
    void getAllActiveLots_ShouldCallRepoWithSpec() {
        Pageable pageable = Pageable.unpaged();
        LotFilterRequest filter = new LotFilterRequest();
        Page<Lot> page = new PageImpl<>(List.of(LotFactory.getActiveLot(UserFactory.getLocalUser())));

        when(lotRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        lotService.getAllActiveLots(pageable, filter);

        verify(lotRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getLotsByStatus_ShouldCallRepo() {
        Pageable pageable = Pageable.unpaged();
        Page<Lot> page = Page.empty();
        when(lotRepository.findByStatus(LotStatus.PENDING_REVIEW, pageable)).thenReturn(page);

        lotService.getLotsByStatus(LotStatus.PENDING_REVIEW, pageable);

        verify(lotRepository).findByStatus(LotStatus.PENDING_REVIEW, pageable);
    }

    @Test
    void getUserLots_ShouldCallRepo() {
        User user = UserFactory.getLocalUser();
        Pageable pageable = Pageable.unpaged();
        Page<Lot> page = Page.empty();
        when(lotRepository.findAllBySellerId(user.getId(), pageable)).thenReturn(page);

        lotService.getUserLots(user, pageable);

        verify(lotRepository).findAllBySellerId(user.getId(), pageable);
    }

    @Test
    void getUserBiddedLots_ShouldCallRepo() {
        User user = UserFactory.getLocalUser();
        Pageable pageable = Pageable.unpaged();
        Page<Lot> page = Page.empty();
        when(lotRepository.findLotsByBidder(user.getId(), pageable)).thenReturn(page);

        lotService.getUserBiddedLots(user, pageable);

        verify(lotRepository).findLotsByBidder(user.getId(), pageable);
    }
}