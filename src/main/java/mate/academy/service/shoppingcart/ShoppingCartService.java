package mate.academy.service.shoppingcart;

import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.cartitem.UpdateRequestDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(Authentication authentication);

    CartItemResponseDto addCartItem(CartItemRequestDto cartItemRequestDto,
                                    Authentication authentication);

    CartItemResponseDto update(Long id, UpdateRequestDto updateRequestDto);

    void delete(Long id);

    void clear(ShoppingCart shoppingCart, Authentication authentication);

    ShoppingCart getCurrentUserCart(User user);

    void createShoppingCart(User user);
}
