package mate.academy.service.shoppingcart;

import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.model.ShoppingCart;

public interface ShoppingCartService {
    ShoppingCartDto getAll();

    CartItemResponseDto addCartItem(CartItemRequestDto cartItemRequestDto);

    ShoppingCart getCurrentUserCart();
    
}
