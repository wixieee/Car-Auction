package edu.lpnu.auction.utils.mapper;

import edu.lpnu.auction.dto.RegisterRequest;
import edu.lpnu.auction.model.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterRequest registerRequest);

    RegisterRequest toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(RegisterRequest registerRequest, @MappingTarget User user);
}