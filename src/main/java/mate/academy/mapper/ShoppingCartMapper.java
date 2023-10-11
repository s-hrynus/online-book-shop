package mate.academy.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.config.MapperConfig;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @AfterMapping
    default void setUserId(@MappingTarget ShoppingCartDto cartDto, ShoppingCart shoppingCart) {
        cartDto.setUserId(shoppingCart.getUser().getId());
    }

    @AfterMapping
    default void setCartItemDto(@MappingTarget ShoppingCartDto cartDto,
                                ShoppingCart shoppingCart) {
        Set<CartItemResponseDto> itemsDto = shoppingCart.getCartItems().stream()
                .map(this::mapToDto)
                .collect(Collectors.toSet());
        cartDto.setCartItems(itemsDto);
    }

    default CartItemResponseDto mapToDto(CartItem cartItem) {
        CartItemResponseDto responseDto = new CartItemResponseDto();
        responseDto.setId(cartItem.getId());
        responseDto.setBookId(cartItem.getBook().getId());
        responseDto.setBookTitle(cartItem.getBook().getTitle());
        responseDto.setQuantity(cartItem.getQuantity());
        return responseDto;
    }
}
