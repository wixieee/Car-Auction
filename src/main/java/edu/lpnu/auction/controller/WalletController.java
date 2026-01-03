package edu.lpnu.auction.controller;

import edu.lpnu.auction.dto.request.TransactionRequest;
import edu.lpnu.auction.dto.response.TransactionResponse;
import edu.lpnu.auction.service.WalletService;
import edu.lpnu.auction.utils.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/top-up")
    public ResponseEntity<String> topUp(
            @RequestBody @Valid TransactionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        walletService.topUpBalance(userDetails.getUser(), request);
        return ResponseEntity.ok("Баланс успішно поповнено");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @RequestBody @Valid TransactionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        walletService.withdrawBalance(userDetails.getUser(), request);
        return ResponseEntity.ok("Запит на вивід успішно оброблено");
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionResponse>> getHistory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                walletService.getUserTransactions(userDetails.getUser(), pageable)
        );
    }
}