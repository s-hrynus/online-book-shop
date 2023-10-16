package mate.academy.repository.order;

import java.util.List;
import java.util.Optional;
import mate.academy.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {
    Optional<Order> findOrderById(Long orderId);

    List<Order> getOrderByUserId(Long userId);
}
