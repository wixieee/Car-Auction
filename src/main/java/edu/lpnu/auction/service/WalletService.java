package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.request.TransactionRequest;
import edu.lpnu.auction.dto.response.TransactionResponse;
import edu.lpnu.auction.dto.websocket.BalanceUpdateDto;
import edu.lpnu.auction.model.Transaction;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.TransactionType;
import edu.lpnu.auction.repository.TransactionRepository;
import edu.lpnu.auction.repository.UserRepository;
import edu.lpnu.auction.utils.exception.types.BadRequestException;
import edu.lpnu.auction.utils.exception.types.NotFoundException;
import edu.lpnu.auction.utils.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final BigDecimal DEPOSIT_PERCENTAGE = new BigDecimal("0.10");

    @Transactional
    public void freezeDeposit(User user, BigDecimal amount){
        BigDecimal requiredBalance = amount.multiply(DEPOSIT_PERCENTAGE);
        haveEnoughMoney(user, requiredBalance);

        user.setFrozenBalance(user.getFrozenBalance().add(requiredBalance));
        User savedUser = userRepository.save(user);

        notifyBalanceUpdate(savedUser);
    }

    @Transactional
    public void unfreezeDeposit(User user, BigDecimal bidAmount) {
        BigDecimal depositToReturn = bidAmount.multiply(DEPOSIT_PERCENTAGE);

        BigDecimal newFrozen = user.getFrozenBalance().subtract(depositToReturn);
        if (newFrozen.compareTo(BigDecimal.ZERO) < 0) {
            newFrozen = BigDecimal.ZERO;
        }

        user.setFrozenBalance(newFrozen);
        User savedUser = userRepository.save(user);

        notifyBalanceUpdate(savedUser);
    }

    @Transactional
    public void topUpBalance(User user, TransactionRequest request) {
        user.setBalance(user.getBalance().add(request.getAmount()));
        userRepository.save(user);

        saveTransaction(user, request.getAmount(), TransactionType.DEPOSIT, maskCardNumber(request.getCard().getCardNumber()));

        notifyBalanceUpdate(user);
    }

    @Transactional
    public void withdrawBalance(User user, TransactionRequest request) {
        if (user.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Недостатньо доступних коштів для виводу");
        }

        user.setBalance(user.getBalance().subtract(request.getAmount()));
        userRepository.save(user);

        saveTransaction(user, request.getAmount(), TransactionType.WITHDRAWAL, maskCardNumber(request.getCard().getCardNumber()));

        notifyBalanceUpdate(user);
    }

    @Transactional
    public void transferFunds(User buyer, UUID sellerId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сума переказу має бути додатною");
        }

        User seller = userRepository.findById(sellerId).orElseThrow(
                () -> new NotFoundException("Продавця не знайдено")
        );

        BigDecimal depositAmount = amount.multiply(DEPOSIT_PERCENTAGE);
        BigDecimal remainingAmount = amount.subtract(depositAmount);

        haveEnoughMoney(buyer, remainingAmount);

        buyer.setFrozenBalance(buyer.getFrozenBalance().subtract(depositAmount));
        buyer.setBalance(buyer.getBalance().subtract(amount));

        seller.setBalance(seller.getBalance().add(amount));

        User savedBuyer = userRepository.save(buyer);
        User savedSeller = userRepository.save(seller);

        saveTransaction(buyer, amount, TransactionType.PAYMENT, "Internal Wallet");
        saveTransaction(seller, amount, TransactionType.DEPOSIT, "Internal Wallet");

        notifyBalanceUpdate(savedBuyer);
        notifyBalanceUpdate(savedSeller);
    }

    public Page<TransactionResponse> getUserTransactions(User user, Pageable pageable) {
        return transactionRepository.findAllByUserId(user.getId(), pageable)
                .map(transactionMapper::toDto);
    }

    private void haveEnoughMoney(User user, BigDecimal amount) {
        if (user.getAvailableBalance().compareTo(amount) < 0) {
            throw new BadRequestException(
                    String.format("Недостатньо коштів. Необхідно: %s, Доступно: %s",
                            amount, user.getAvailableBalance())
            );
        }
    }

    private void notifyBalanceUpdate(User user) {
        try {
            BalanceUpdateDto dto = new BalanceUpdateDto(
                    user.getAvailableBalance(),
                    user.getFrozenBalance()
            );
            messagingTemplate.convertAndSendToUser(
                    user.getEmail(),
                    "/queue/balance",
                    dto
            );
        } catch (Exception e) {
            log.error("Помилка відправлення оновлень користувачу {}", user.getEmail(), e);
        }
    }

    private void saveTransaction(User user, BigDecimal amount, TransactionType type, String paymentMethod) {
        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(amount)
                .type(type)
                .paymentMethod(paymentMethod)
                .build();

        transactionRepository.save(transaction);
    }

    private String maskCardNumber(String cardNum) {
        if (cardNum == null || cardNum.length() <= 4) {
            return cardNum;
        }
        return "**** " + cardNum.substring(cardNum.length() - 4);
    }
}