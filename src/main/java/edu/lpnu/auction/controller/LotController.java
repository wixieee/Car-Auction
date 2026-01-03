package edu.lpnu.auction.controller;

import edu.lpnu.auction.dto.request.CarRequest;
import edu.lpnu.auction.dto.request.CreateLotRequest;
import edu.lpnu.auction.dto.request.LotFilterRequest;
import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.service.CarService;
import edu.lpnu.auction.service.LotService;
import edu.lpnu.auction.utils.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lot")
public class LotController {
    private final LotService lotService;
    private final CarService carService;

    @GetMapping("/prefill")
    public ResponseEntity<CarRequest> prefill(@RequestParam String vin) {
        CarRequest carRequest = carService.prefill(vin);
        return ResponseEntity.ok(carRequest);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LotResponse> createLot(
            @RequestPart("lot") @Valid CreateLotRequest lotRequest,
            @RequestPart(value = "images") List<MultipartFile> images,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return new ResponseEntity<>(
                lotService.createLot(lotRequest, images, currentUser.getUser()),
                HttpStatus.CREATED);
    }

    @PostMapping("/pay/{lotId}")
    public ResponseEntity<LotResponse> payForLot(
            @PathVariable UUID lotId,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return ResponseEntity.ok(
                lotService.payForLot(lotId, currentUser.getUser())
        );
    }

    @GetMapping
    public ResponseEntity<Page<LotResponse>> getAllLots(
            @ModelAttribute LotFilterRequest filterRequest,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(lotService.getAllActiveLots(pageable, filterRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LotResponse> getLotById(@PathVariable UUID id) {
        return ResponseEntity.ok(lotService.getLotById(id));
    }

    @PostMapping("/{lotId}/cancel")
    public ResponseEntity<String> cancelLot(
            @PathVariable UUID lotId,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        lotService.cancelLot(lotId, currentUser.getUser());
        return ResponseEntity.ok("Лот успішно скасовано");
    }
}
