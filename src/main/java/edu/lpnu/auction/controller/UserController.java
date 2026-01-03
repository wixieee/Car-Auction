package edu.lpnu.auction.controller;

import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.dto.response.UserResponse;
import edu.lpnu.auction.service.LotService;
import edu.lpnu.auction.utils.mapper.UserMapper;
import edu.lpnu.auction.utils.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final LotService lotService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(
                userMapper.toDto(user.getUser())
        );
    }

    @GetMapping("/me/lots")
    public ResponseEntity<Page<LotResponse>> getMyLots(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                lotService.getUserLots(userDetails.getUser(), pageable)
        );
    }

    @GetMapping("/me/bids")
    public ResponseEntity<Page<LotResponse>> getMyBiddedLots(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "endTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                lotService.getUserBiddedLots(userDetails.getUser(), pageable)
        );
    }
}
