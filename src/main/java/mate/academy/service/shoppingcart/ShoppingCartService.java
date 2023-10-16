package mate.academy.service.shoppingcart;

import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.cartitem.UpdateRequestDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart();

    CartItemResponseDto addCartItem(CartItemRequestDto cartItemRequestDto);

    CartItemResponseDto update(Long id, UpdateRequestDto updateRequestDto);

    void delete(Long id);

    void clear(ShoppingCart shoppingCart);

    ShoppingCart getCurrentUserCart();

    void createShoppingCart(User user);
}
