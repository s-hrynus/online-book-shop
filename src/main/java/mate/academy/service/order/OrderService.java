package mate.academy.service.order;

import java.util.List;
import java.util.Set;
import mate.academy.dto.order.OrderAddressDto;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderStatusDto;
import mate.academy.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {

    List<OrderDto> findAllOrders(Pageable pageable);

    OrderDto addAddress(OrderAddressDto orderAddressDto, Authentication authentication);

    OrderDto updateStatus(Long id, OrderStatusDto orderStatusDto);

    Set<OrderItemDto> findAllOrderItems(Long orderId);

    OrderItemDto findByIdOrderItem(Long orderId, Long itemId);
}
