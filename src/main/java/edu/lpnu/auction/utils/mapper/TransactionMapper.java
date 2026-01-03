package edu.lpnu.auction.utils.mapper;

import edu.lpnu.auction.dto.response.TransactionResponse;
import edu.lpnu.auction.model.Transaction;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    TransactionResponse toDto(Transaction transaction);
}