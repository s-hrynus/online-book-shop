package mate.academy.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mate.academy.dto.orderitem.OrderItemDto;
import mate.academy.model.Order;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<OrderItemDto> orderItems;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Order.Status status;
}
