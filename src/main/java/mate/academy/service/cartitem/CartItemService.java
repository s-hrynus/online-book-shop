package mate.academy.service.cartitem;

import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.cartitem.UpdateRequestDto;

public interface CartItemService {
    CartItemResponseDto updateByCartItemId(Long cartItemId, UpdateRequestDto updateRequestDto);

    void deleteById(Long id);
}
