package edu.lpnu.auction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnumResponse {
    private String key;
    private String label;
}