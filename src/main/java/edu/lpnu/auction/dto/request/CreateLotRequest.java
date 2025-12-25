package edu.lpnu.auction.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateLotRequest {
    @Valid
    @NotNull(message = "Дані автомобіля обов'язкові")
    private CarRequest carRequest;

    @NotNull(message = "Резервна ціна обов'язкова")
    @PositiveOrZero(message = "Резервна ціна не може бути від'ємною")
    private BigDecimal reservePrice;
}
