package edu.lpnu.auction.integration;

import edu.lpnu.auction.dto.request.*;
import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.dto.response.TransactionResponse;
import edu.lpnu.auction.factory.CarFactory;
import edu.lpnu.auction.factory.UserFactory;
import edu.lpnu.auction.model.Lot;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.LotStatus;
import edu.lpnu.auction.model.enums.TransactionType;
import edu.lpnu.auction.repository.LotRepository;
import edu.lpnu.auction.repository.TransactionRepository;
import edu.lpnu.auction.repository.UserRepository;
import edu.lpnu.auction.service.BidService;
import edu.lpnu.auction.service.ImageService;
import edu.lpnu.auction.service.LotService;
import edu.lpnu.auction.service.WalletService;
import edu.lpnu.auction.utils.AuctionScheduler;
import edu.lpnu.auction.utils.exception.types.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuctionIntegrationTest extends AbstractIntegrationTest {

    @Autowired private LotService lotService;
    @Autowired private BidService bidService;
    @Autowired private WalletService walletService;
    @Autowired private AuctionScheduler auctionScheduler;
    @Autowired private LotRepository lotRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TransactionRepository transactionRepository;

    @MockitoBean
    private ImageService imageService;

    private User seller;
    private User richBidder;
    private User secondBidder;

    private final MockMultipartFile dummyImage = new MockMultipartFile(
            "images", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        lotRepository.deleteAll();
        userRepository.deleteAll();

        when(imageService.saveImages(any())).thenReturn(List.of("https://fake-url.com/image.jpg"));

        seller = UserFactory.createPersistedUser(userRepository, "seller", BigDecimal.ZERO);

        richBidder = UserFactory.createPersistedUser(userRepository, "rich", BigDecimal.ZERO);
        topUpUser(richBidder, new BigDecimal("50000"));

        secondBidder = UserFactory.createPersistedUser(userRepository, "competitor", BigDecimal.ZERO);
        topUpUser(secondBidder, new BigDecimal("20000"));
    }

    @Test
    void fullAuctionLifecycleTest() {
        CreateLotRequest createRequest = new CreateLotRequest();
        createRequest.setCarRequest(CarFactory.getFullGasolineRequest());
        createRequest.setReservePrice(new BigDecimal("15000"));

        LotResponse createdLot = lotService.createLot(createRequest, List.of(dummyImage), seller);
        UUID lotId = createdLot.getId();

        assertThat(createdLot.getStatus()).isEqualTo(LotStatus.PENDING_REVIEW);

        LotApproveRequest approveRequest = new LotApproveRequest();
        approveRequest.setStartPrice(new BigDecimal("1000"));
        approveRequest.setMinBidIncrement(new BigDecimal("100"));
        approveRequest.setStartTime(LocalDateTime.now().minusMinutes(1));
        approveRequest.setEndTime(LocalDateTime.now().plusMinutes(10));

        lotService.approveLot(lotId, approveRequest);

        auctionScheduler.checkAuctionStatus();
        Lot lot = lotRepository.findById(lotId).orElseThrow();
        assertThat(lot.getStatus()).isEqualTo(LotStatus.ACTIVE);

        placeBid(lotId, new BigDecimal("10000"), richBidder);

        User richState = userRepository.findById(richBidder.getId()).orElseThrow();
        assertThat(richState.getFrozenBalance()).isEqualByComparingTo("1000");

        placeBid(lotId, new BigDecimal("16000"), secondBidder);

        User richStateAfterLoss = userRepository.findById(richBidder.getId()).orElseThrow();
        assertThat(richStateAfterLoss.getFrozenBalance()).isEqualByComparingTo("0.00");

        User winnerState = userRepository.findById(secondBidder.getId()).orElseThrow();
        assertThat(winnerState.getFrozenBalance()).isEqualByComparingTo("1600.00");

        lot = lotRepository.findById(lotId).orElseThrow();
        lot.setEndTime(LocalDateTime.now().minusSeconds(1));
        lotRepository.save(lot);

        auctionScheduler.checkAuctionStatus();

        Lot soldLot = lotRepository.findById(lotId).orElseThrow();
        assertThat(soldLot.getStatus()).isEqualTo(LotStatus.SOLD);
        assertThat(soldLot.getCurrentHighBidder().getId()).isEqualTo(secondBidder.getId());

        User freshWinner = userRepository.findById(secondBidder.getId()).orElseThrow();

        lotService.payForLot(lotId, freshWinner);

        Lot paidLot = lotRepository.findById(lotId).orElseThrow();
        assertThat(paidLot.getStatus()).isEqualTo(LotStatus.PAID);

        User sellerFinal = userRepository.findById(seller.getId()).orElseThrow();
        assertThat(sellerFinal.getBalance()).isEqualByComparingTo("16000.00");

        User winnerFinal = userRepository.findById(secondBidder.getId()).orElseThrow();
        assertThat(winnerFinal.getFrozenBalance()).isEqualByComparingTo("0.00");
        assertThat(winnerFinal.getBalance()).isEqualByComparingTo("4000.00");

        Page<TransactionResponse> history = walletService.getUserTransactions(
                winnerFinal,
                PageRequest.of(0, 10, Sort.by("createdAt").descending())
        );
        assertThat(history.getContent()).hasSize(2);
        assertThat(history.getContent().get(0).getType()).isEqualTo(TransactionType.PAYMENT);
    }

    @Test
    void validationAndUnsoldTest() {
        UUID lotId = createAndActivateLot(seller, new BigDecimal("20000"));

        assertThrows(BadRequestException.class, () ->
                placeBid(lotId, new BigDecimal("1000"), seller));

        User brokeUser = UserFactory.createPersistedUser(userRepository, "broke", new BigDecimal("500"));
        assertThrows(BadRequestException.class, () ->
                placeBid(lotId, new BigDecimal("6000"), brokeUser));

        placeBid(lotId, new BigDecimal("200"), richBidder);
        assertThrows(BadRequestException.class, () ->
                placeBid(lotId, new BigDecimal("250"), richBidder));

        Lot lot = lotRepository.findById(lotId).orElseThrow();
        lot.setEndTime(LocalDateTime.now().minusSeconds(1));
        lotRepository.save(lot);

        auctionScheduler.checkAuctionStatus();

        Lot finishedLot = lotRepository.findById(lotId).orElseThrow();
        assertThat(finishedLot.getStatus()).isEqualTo(LotStatus.UNSOLD);

        User richState = userRepository.findById(richBidder.getId()).orElseThrow();
        assertThat(richState.getFrozenBalance()).isEqualByComparingTo("0.00");
    }


    private void placeBid(UUID lotId, BigDecimal amount, User user) {
        User freshUser = userRepository.findById(user.getId()).orElseThrow();

        CashRequest req = new CashRequest();
        req.setAmount(amount);
        bidService.placeBid(lotId, freshUser, req);
    }

    private void topUpUser(User user, BigDecimal amount) {
        TransactionRequest req = new TransactionRequest();
        req.setAmount(amount);

        CardData card = new CardData();
        card.setCardNumber("1234567812345678");
        card.setExpiryDate("12/30");
        card.setCvv("123");
        card.setHolderName("TEST USER");
        req.setCard(card);

        walletService.topUpBalance(user, req);
    }

    private UUID createAndActivateLot(User seller, BigDecimal reserve) {
        CreateLotRequest cr = new CreateLotRequest();
        cr.setCarRequest(CarFactory.getFullGasolineRequest());
        cr.setReservePrice(reserve);

        LotResponse lr = lotService.createLot(cr, List.of(dummyImage), seller);

        LotApproveRequest approve = new LotApproveRequest();
        approve.setStartPrice(new BigDecimal("100"));
        approve.setMinBidIncrement(new BigDecimal("100"));
        approve.setStartTime(LocalDateTime.now().minusMinutes(1));
        approve.setEndTime(LocalDateTime.now().plusHours(1));

        lotService.approveLot(lr.getId(), approve);
        auctionScheduler.checkAuctionStatus();
        return lr.getId();
    }
}