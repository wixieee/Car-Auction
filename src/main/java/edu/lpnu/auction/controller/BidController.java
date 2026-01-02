package edu.lpnu.auction.controller;

import edu.lpnu.auction.dto.request.CashRequest;
import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.service.BidService;
import edu.lpnu.auction.utils.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;

    @PostMapping("/{lotId}")
    public ResponseEntity<LotResponse> placeBid(@PathVariable UUID lotId,
                                   @RequestBody @Valid CashRequest bid,
                                   @AuthenticationPrincipal UserDetailsImpl user) {
        LotResponse lotResponse = bidService.placeBid(lotId, user.getUser(), bid);
        return ResponseEntity.ok(lotResponse);
    }
}
