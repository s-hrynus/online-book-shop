package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    CartItemResponseDto toDto(CartItem cartItem);

    @AfterMapping
    default void initialize(@MappingTarget CartItemResponseDto responseDto, CartItem cartItem) {
        responseDto.setId(cartItem.getId());
        responseDto.setBookId(cartItem.getBook().getId());
        responseDto.setBookTitle(cartItem.getBook().getTitle());
    }
}
