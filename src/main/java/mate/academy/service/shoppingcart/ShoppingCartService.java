package mate.academy.service.shoppingcart;

import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getAll();

    CartItemResponseDto addCartItem(CartItemRequestDto cartItemRequestDto);
}
