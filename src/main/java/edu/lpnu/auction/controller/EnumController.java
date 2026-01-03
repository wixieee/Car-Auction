package edu.lpnu.auction.controller;

import edu.lpnu.auction.dto.response.EnumResponse;
import edu.lpnu.auction.model.enums.LotStatus;
import edu.lpnu.auction.model.enums.car.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enums")
public class EnumController {

    @GetMapping("/body-types")
    public List<EnumResponse> getBodyTypes() {
        return Arrays.stream(BodyType.values())
                .map(e -> new EnumResponse(e.name(), e.getUkrainianLabel()))
                .collect(Collectors.toList());
    }

    @GetMapping("/fuel-types")
    public List<EnumResponse> getFuelTypes() {
        return Arrays.stream(FuelType.values())
                .map(e -> new EnumResponse(e.name(), e.getUkrainianLabel()))
                .collect(Collectors.toList());
    }

    @GetMapping("/transmissions")
    public List<EnumResponse> getTransmissions() {
        return Arrays.stream(TransmissionType.values())
                .map(e -> new EnumResponse(e.name(), e.getUkrainianLabel()))
                .collect(Collectors.toList());
    }

    @GetMapping("/drive-types")
    public List<EnumResponse> getDriveTypes() {
        return Arrays.stream(DriveType.values())
                .map(e -> new EnumResponse(e.name(), e.getUkrainianLabel()))
                .collect(Collectors.toList());
    }

    @GetMapping("/colors")
    public List<EnumResponse> getColors() {
        return Arrays.stream(Color.values())
                .map(e -> new EnumResponse(e.name(), e.getUkrainianLabel()))
                .collect(Collectors.toList());
    }

    @GetMapping("/conditions")
    public List<EnumResponse> getConditions() {
        return Arrays.stream(CarCondition.values())
                .map(e -> new EnumResponse(e.name(), e.getUkrainianLabel()))
                .collect(Collectors.toList());
    }

    @GetMapping("/lot-statuses")
    public List<EnumResponse> getLotStatuses() {
        return Arrays.stream(LotStatus.values())
                .map(e -> new EnumResponse(e.name(), e.getUkrainianLabel()))
                .collect(Collectors.toList());
    }
}