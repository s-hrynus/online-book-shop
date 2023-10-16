package mate.academy.repository.orderitem;

import java.util.Set;
import mate.academy.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>,
        JpaSpecificationExecutor<OrderItem> {
    Set<OrderItem> getAllByOrderId(Long id);
}
