package mate.academy.repository.cartitem;

import java.util.Optional;
import java.util.Set;
import mate.academy.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long>,
        JpaSpecificationExecutor<CartItem> {
    Optional<CartItem> findCartItemByIdAndShoppingCartId(Long cartItemId, Long shoppingCartId);

    @Query(value = "SELECT * FROM cart_item c WHERE c.shopping_cart_id = :id", nativeQuery = true)
    Set<CartItem> getCartItemsByShoppingCartId(Long id);
}
