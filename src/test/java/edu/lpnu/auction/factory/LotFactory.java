package edu.lpnu.auction.factory;

import edu.lpnu.auction.dto.request.CreateLotRequest;
import edu.lpnu.auction.dto.request.LotApproveRequest;
import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.model.Car;
import edu.lpnu.auction.model.Lot;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.LotStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class LotFactory {

    public static Lot getLot(User seller, LotStatus status) {
        return Lot.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .car(new Car()) // Car can be empty for LotService logic
                .reservePrice(new BigDecimal("10000"))
                .status(status)
                .currentPrice(BigDecimal.ZERO)
                .bidCount(0)
                .bids(new ArrayList<>())
                .build();
    }

    public static Lot getPendingLot(User seller) {
        return getLot(seller, LotStatus.PENDING_REVIEW);
    }

    public static Lot getActiveLot(User seller) {
        Lot lot = getLot(seller, LotStatus.ACTIVE);
        lot.setStartTime(LocalDateTime.now().minusHours(1));
        lot.setEndTime(LocalDateTime.now().plusHours(1));
        lot.setCurrentPrice(new BigDecimal("100"));
        return lot;
    }

    public static Lot getSoldLot(User seller, User winner, BigDecimal price) {
        Lot lot = getLot(seller, LotStatus.SOLD);
        lot.setCurrentHighBidder(winner);
        lot.setCurrentPrice(price);
        return lot;
    }

    public static CreateLotRequest getCreateLotRequest() {
        CreateLotRequest request = new CreateLotRequest();
        request.setCarRequest(CarFactory.getFullGasolineRequest());
        request.setReservePrice(new BigDecimal("10000"));
        return request;
    }

    public static LotApproveRequest getValidApproveRequest() {
        LotApproveRequest request = new LotApproveRequest();
        request.setStartPrice(new BigDecimal("100"));
        request.setMinBidIncrement(new BigDecimal("50"));
        request.setStartTime(LocalDateTime.now().plusMinutes(1));
        request.setEndTime(LocalDateTime.now().plusHours(2));
        return request;
    }

    public static LotApproveRequest getInvalidTimeApproveRequest() {
        LotApproveRequest request = getValidApproveRequest();
        request.setStartTime(LocalDateTime.now().plusHours(2));
        request.setEndTime(LocalDateTime.now().plusHours(1)); // End before Start
        return request;
    }

    public static LotResponse getLotResponse(Lot lot) {
        LotResponse response = new LotResponse();
        response.setId(lot.getId());
        response.setStatus(lot.getStatus());
        response.setCurrentPrice(lot.getCurrentPrice());
        return response;
    }
}