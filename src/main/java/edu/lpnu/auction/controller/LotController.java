package edu.lpnu.auction.controller;

import edu.lpnu.auction.dto.request.CreateLotRequest;
import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.service.LotService;
import edu.lpnu.auction.utils.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lot")
public class LotController {
    private final LotService lotService;

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
}
