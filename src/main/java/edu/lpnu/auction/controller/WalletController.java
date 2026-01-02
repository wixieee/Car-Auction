package edu.lpnu.auction.controller;

import edu.lpnu.auction.dto.request.CashRequest;
import edu.lpnu.auction.service.WalletService;
import edu.lpnu.auction.utils.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/top-up")
    public ResponseEntity<String> topUp(@RequestBody @Valid CashRequest cashRequest,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        walletService.topUpBalance(userDetails.getUser(), cashRequest);
        return ResponseEntity.ok("Баланс успішно поповнено");
    }
}
