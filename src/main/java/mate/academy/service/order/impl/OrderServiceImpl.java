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
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.CartItem;
import mate.academy.model.Order;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.order.OrderRepository;
import mate.academy.repository.orderitem.OrderItemRepository;
import mate.academy.service.order.OrderService;
import mate.academy.service.shoppingcart.ShoppingCartService;
import mate.academy.service.user.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartMapper shoppingCartMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Transactional
    @Override
    public List<OrderDto> findAllOrders(Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        List<Order> orders = orderRepository.getOrderByUserId(user.getId());
        for (Order order : orders) {
            order.setOrderItems(orderItemRepository.getAllByOrderId(order.getId()));
        }
        return orders.stream()
                .map(this::initializeOrderItems)
                .toList();
    }

    @Transactional
    @Override
    public OrderDto addAddress(OrderAddressDto orderAddressDto, Authentication authentication) {
        Order order = createOrder(orderAddressDto, authentication);
        order.setShippingAddress(orderAddressDto.getShippingAddress());
        shoppingCartService.clear(
                shoppingCartMapper.toEntity(
                        shoppingCartService.getShoppingCart(authentication)), authentication);
        return initializeOrderItems(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderDto updateStatus(Long id, OrderStatusDto orderStatusDto) {
        Order order = orderRepository.findOrderById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id " + id));
        order.setStatus(orderStatusDto.status());
        return initializeOrderItems(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    @Override
    public Set<OrderItemDto> findAllOrderItems(Long orderId) {
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

    private Order createOrder(OrderAddressDto requestAddressDto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Order order = new Order();
        ShoppingCart shoppingCart = shoppingCartService.getCurrentUserCart(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setShippingAddress(requestAddressDto.getShippingAddress());
        order.setUser(userService.getAuthenticatedUser());
        order.setTotal(shoppingCart.getCartItems().stream()
                .map(this::calculateItemPrice)
                .reduce(BigDecimal::add)
                .orElseThrow());
        Order savedOrder = orderRepository.save(order);
        order.setOrderItems(shoppingCart.getCartItems().stream()
                .map(item -> orderItemMapper.cartItemToOrderItem(item, savedOrder))
                .map(orderItemRepository::save)
                .collect(Collectors.toSet()));
        return savedOrder;
    }

    private Stream<OrderItemDto> getAllOrderItemByOrderId(Long orderId) {
        return orderRepository.getOrderByUserId(userService.getAuthenticatedUser().getId())
                .stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst().orElseThrow(
                        () -> new EntityNotFoundException("Can't find order with id=" + orderId))
                .getOrderItems().stream()
                .map(orderItemMapper::toDto);
    }

    private OrderDto initializeOrderItems(Order order) {
        OrderDto responseDto = orderMapper.toDto(order);
        responseDto.setOrderItems(orderItemRepository.getAllByOrderId(order.getId()).stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toSet()));
        return responseDto;
    }

    private BigDecimal calculateItemPrice(CartItem cartItem) {
        return cartItem.getBook().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }
}
