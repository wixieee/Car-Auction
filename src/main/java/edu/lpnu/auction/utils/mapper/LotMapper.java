package edu.lpnu.auction.utils.mapper;

import edu.lpnu.auction.dto.response.LotResponse;
import edu.lpnu.auction.model.Lot;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                CarResponseMapper.class,
                UserMapper.class,
                BidMapper.class
        })
public interface LotMapper {
    @Mapping(target = "reserveMet", ignore = true)
    LotResponse toDto(Lot lot);

    @AfterMapping
    default void calculateReserveMet(Lot source, @MappingTarget LotResponse target) {
        if (source.getReservePrice() != null && source.getCurrentPrice() != null) {
            boolean isMet = source.getCurrentPrice().compareTo(source.getReservePrice()) >= 0;
            target.setReserveMet(isMet);
        } else {
            target.setReserveMet(false);
        }
    }

}