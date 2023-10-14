package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.orderitem.OrderItemDto;
import mate.academy.model.CartItem;
import mate.academy.model.Order;
import mate.academy.model.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    OrderItem cartItemToOrderItem(CartItem cartItem, Order order);

    @AfterMapping
    default void setBookId(@MappingTarget OrderItemDto orderItemDto, OrderItem orderItem) {
        orderItemDto.setBookId(orderItem.getBook().getId());
    }
}
