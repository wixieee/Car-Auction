package edu.lpnu.auction.controller.admin;

import edu.lpnu.auction.dto.request.LotApproveRequest;
import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.model.enums.LotStatus;
import edu.lpnu.auction.service.LotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/lot")
//@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminLotController {
    private final LotService lotService;

    @GetMapping("/pending")
    public ResponseEntity<Page<LotResponse>> getPendingLots(Pageable pageable) {
        return ResponseEntity.ok(lotService.getLotsByStatus(LotStatus.PENDING_REVIEW, pageable));
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<LotResponse> approveLot(@PathVariable UUID id,
                                                  @RequestBody @Valid LotApproveRequest approveRequest) {
        return ResponseEntity.ok(lotService.approveLot(id, approveRequest));
    }

    @PatchMapping("/reject/{id}")
    public ResponseEntity<LotResponse> rejectLot(@PathVariable UUID id){
        return ResponseEntity.ok(lotService.rejectLot(id));
    }
}
