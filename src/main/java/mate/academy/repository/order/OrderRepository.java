package mate.academy.repository.order;

import java.util.List;
import mate.academy.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {
    Order getOrderByIdAndUserId(Long orderId, Long userId);

    List<Order> getOrderByUserId(Long userId);
}
