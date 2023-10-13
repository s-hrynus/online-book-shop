package mate.academy.service.order.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.OrderAddressDto;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderStatusDto;
import mate.academy.dto.orderitem.OrderItemDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.OrderItemMapper;
import mate.academy.mapper.OrderMapper;
import mate.academy.model.Order;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.order.OrderRepository;
import mate.academy.repository.orderitem.OrderItemRepository;
import mate.academy.service.order.OrderService;
import mate.academy.service.shoppingcart.ShoppingCartService;
import mate.academy.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final ShoppingCartService shoppingCartService;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderDto> getAllOrder() {
        User user = userService.getAuthenticatedUser();
        return orderRepository.getOrderByUserId(user.getId()).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public OrderDto addAddress(OrderAddressDto orderAddressDto) {
        Order order = createOrder(orderAddressDto);
        order.setShippingAddress(orderAddressDto.getShippingAddress());
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto updateStatus(Long id, OrderStatusDto orderStatusDto) {
        Order order = orderRepository.getOrderByIdAndUserId(id,
                userService.getAuthenticatedUser().getId());
        order.setStatus(orderStatusDto.status());
        return orderMapper.toDto(order);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<OrderItemDto> getAllOrderItems(Long orderId) {
        return getAllOrderItemByOrderId(orderId)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    @Override
    public OrderItemDto findByIdOrderItem(Long orderId, Long itemId) {
        return getAllOrderItemByOrderId(orderId)
                .filter(i -> i.getId().equals(itemId))
                .findFirst().orElseThrow(
                        () -> new EntityNotFoundException("Can't find order item by id "
                                + itemId));
    }

    private Order createOrder(OrderAddressDto requestAddressDto) {
        Order order = new Order();
        ShoppingCart shoppingCart = shoppingCartService.getCurrentUserCart();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setShippingAddress(requestAddressDto.getShippingAddress());
        order.setUser(userService.getAuthenticatedUser());
        order.setTotal(shoppingCart.getCartItems().stream()
                .map(i -> i.getBook().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal::add)
                .orElseThrow());
        order.setOrderItems(shoppingCart.getCartItems().stream()
                .map(orderItemMapper::cartItemToOrderItem)
                .collect(Collectors.toSet()));
        return orderRepository.save(order);
    }

    private Stream<OrderItemDto> getAllOrderItemByOrderId(Long orderId) {
        return orderRepository.getOrderByIdAndUserId(orderId, userService.getAuthenticatedUser()
                .getId()).getOrderItems().stream()
                .map(orderItemMapper::toDto);
    }
}
