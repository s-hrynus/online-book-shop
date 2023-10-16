package mate.academy.service.order;

import java.util.List;
import java.util.Set;
import mate.academy.dto.order.OrderAddressDto;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderStatusDto;
import mate.academy.dto.orderitem.OrderItemDto;

public interface OrderService {

    List<OrderDto> getAllOrder();

    OrderDto addAddress(OrderAddressDto orderAddressDto);

    OrderDto updateStatus(Long id, OrderStatusDto orderStatusDto);

    Set<OrderItemDto> getAllOrderItems(Long orderId);

    OrderItemDto findByIdOrderItem(Long orderId, Long itemId);
}
