package mate.academy.repository.cartitem;

import java.util.Optional;
import java.util.Set;
import mate.academy.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CartItemRepository extends JpaRepository<CartItem, Long>,
        JpaSpecificationExecutor<CartItem> {
    Optional<CartItem> findCartItemById(Long cartItemId);

    Set<CartItem> getCartItemsByShoppingCartId(Long id);
}
