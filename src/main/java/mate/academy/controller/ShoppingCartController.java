package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.cartitem.UpdateRequestDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.service.shoppingcart.ShoppingCartService;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Get shopping cart with items by user")
    public ShoppingCartDto getAll() {
        return shoppingCartService.getShoppingCart();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @Operation(summary = "Add cart item to the shopping cart")
    public CartItemResponseDto addCartItem(@RequestBody @Valid CartItemRequestDto requestDto) {
        return shoppingCartService.addCartItem(requestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Update quantity of cart item by cart item id")
    public CartItemResponseDto update(@PathVariable Long cartItemId,
                                      @RequestBody @Valid UpdateRequestDto updateRequestDto) {
        return shoppingCartService.update(cartItemId, updateRequestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Delete cart items by cart item id")
    public void delete(@PathVariable Long cartItemId) {
        shoppingCartService.delete(cartItemId);
    }
}
