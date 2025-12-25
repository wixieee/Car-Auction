package edu.lpnu.auction.utils.mapper;

import edu.lpnu.auction.dto.response.BidResponse;
import edu.lpnu.auction.model.Bid;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
public interface BidMapper {
    BidResponse toDto(Bid bid);
}