package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.request.CashRequest;
import edu.lpnu.auction.dto.websocket.BalanceUpdateDto;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.repository.UserRepository;
import edu.lpnu.auction.utils.exception.types.BadRequestException;
import edu.lpnu.auction.utils.exception.types.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    private final UserRepository userRepository;
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
    public void topUpBalance(User user, CashRequest amount) {
        user.setBalance(user.getBalance().add(amount.getAmount()));
        User savedUser = userRepository.save(user);
        notifyBalanceUpdate(savedUser);
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

        notifyBalanceUpdate(savedBuyer);
        notifyBalanceUpdate(savedSeller);
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
}