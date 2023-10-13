package mate.academy.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import mate.academy.model.Order;
import mate.academy.model.OrderItem;

public class OrderDto {
    private Long id;
    private Long userId;
    private Set<OrderItem> orderItemSet;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Order.Status status;
}
