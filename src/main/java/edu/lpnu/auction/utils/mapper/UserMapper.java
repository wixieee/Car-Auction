package edu.lpnu.auction.utils.mapper;

import edu.lpnu.auction.dto.request.RegisterRequest;
import edu.lpnu.auction.dto.response.UserResponse;
import edu.lpnu.auction.model.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterRequest registerRequest);

    UserResponse toDto(User user);
}