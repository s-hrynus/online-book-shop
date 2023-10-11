package mate.academy.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.cartitem.UpdateRequestDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.service.cartitem.CartItemService;
import mate.academy.service.shoppingcart.ShoppingCartService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
public class ShoppingCartController {
    private final CartItemService cartItemService;
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ShoppingCartDto getAll() {
        return shoppingCartService.getAll();
    }

    @PostMapping
    public CartItemResponseDto addCartItem(@RequestBody @Valid CartItemRequestDto requestDto) {
        return shoppingCartService.addCartItem(requestDto);
    }

    @PutMapping("/cart-items/{cartItemId}")
    public CartItemResponseDto update(@PathVariable Long cartItemId,
                                      @RequestBody @Valid UpdateRequestDto updateRequestDto) {
        return cartItemService.updateByCartItemId(cartItemId, updateRequestDto);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    public void delete(@PathVariable Long cartItemId) {
        cartItemService.deleteById(cartItemId);
    }
}
