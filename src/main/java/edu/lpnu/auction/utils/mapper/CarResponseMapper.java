package edu.lpnu.auction.utils.mapper;

import edu.lpnu.auction.dto.response.CarResponse;
import edu.lpnu.auction.model.Car;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CarResponseMapper {
    CarResponse toDto(Car car);
}