package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.OrderAddressDto;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderStatusDto;
import mate.academy.dto.orderitem.OrderItemDto;
import mate.academy.service.order.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(summary = "Get list of orders")
    public List<OrderDto> getAllOrders(Pageable pageable) {
        return orderService.findAllOrders(pageable);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @Operation(summary = "Add shipping address to order")
    public OrderDto addShippingAddress(@RequestBody @Valid OrderAddressDto request,
                                       Authentication authentication) {
        return orderService.addAddress(request, authentication);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get all order items by order id")
    public Set<OrderItemDto> getAllOrderItemsByOrderId(@PathVariable Long orderId) {
        return orderService.findAllOrderItems(orderId);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get order item by order id")
    public OrderItemDto getOrderItemByIds(@PathVariable Long orderId,
                                          @PathVariable Long itemId) {
        return orderService.findByIdOrderItem(orderId, itemId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Status manager of order")
    public OrderDto changeStatus(@PathVariable Long id,
                                         @RequestBody @Valid OrderStatusDto request) {
        return orderService.updateStatus(id, request);
    }
}
