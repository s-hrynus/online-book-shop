package mate.academy.service.cartitem.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.cartitem.UpdateRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CartItemMapper;
import mate.academy.model.CartItem;
import mate.academy.repository.cartitem.CartItemRepository;
import mate.academy.service.cartitem.CartItemService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartItemResponseDto updateByCartItemId(Long cartItemId,
                                                  UpdateRequestDto updateRequestDto) {
        CartItem cartItem = cartItemRepository.findCartItemById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find cart item by id " + cartItemId));
        cartItem.setQuantity(updateRequestDto.getQuantity());
        cartItem.setId(cartItemId);
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }
}
