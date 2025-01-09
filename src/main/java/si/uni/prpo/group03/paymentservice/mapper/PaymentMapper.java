package si.uni.prpo.group03.paymentservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import si.uni.prpo.group03.paymentservice.dto.PayResponseDTO;
import si.uni.prpo.group03.paymentservice.dto.PaymentRequestDTO;
import si.uni.prpo.group03.paymentservice.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(target = "paypalOrderId", ignore = true) // Ignore fields not present in DTO
    @Mapping(target = "reservationId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "id", ignore = true)
    Payment toPayment(PaymentRequestDTO paymentRequestDTO);

    @Mapping(target = "paypalOrderId", source = "paypalOrderId") // Map PayPal Order ID
    @Mapping(target = "amount", source = "amount") // Map amount
    @Mapping(target = "currency", source = "currency") // Map currency
    @Mapping(target = "description", source = "description") // Map description
    @Mapping(target = "status", source = "status") // Map status
    @Mapping(target = "userId", source = "userId") // Map userId
    @Mapping(target = "reservationId", source = "reservationId") // Map reservationId
    @Mapping(target = "createdAt", source = "createdAt") // Map createdAt timestamp
    PayResponseDTO toPayResponseDTO(Payment payment);
}
