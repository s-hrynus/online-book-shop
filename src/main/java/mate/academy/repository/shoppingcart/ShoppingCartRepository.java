package mate.academy.repository.shoppingcart;

import java.util.Optional;
import mate.academy.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,
        Long>, JpaSpecificationExecutor<ShoppingCart> {
    Optional<ShoppingCart> findShoppingCartByUserEmail(String email);
}
