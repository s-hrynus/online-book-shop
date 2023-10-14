package mate.academy.service.shoppingcart.impl;

import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.cartitem.UpdateRequestDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CartItemMapper;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.cartitem.CartItemRepository;
import mate.academy.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.service.shoppingcart.ShoppingCartService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getShoppingCart() {
        ShoppingCart shoppingCart = getCurrentUserCart();
        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toDto(shoppingCart);
        shoppingCartDto.setCartItems(shoppingCart.getCartItems().stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toSet()));
        return shoppingCartDto;
    }

    @Transactional
    @Override
    public CartItemResponseDto addCartItem(CartItemRequestDto cartItemRequestDto) {
        ShoppingCart shoppingCart = getCurrentUserCart();
        Book book = bookRepository.findById(cartItemRequestDto.bookId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find book with id=" + cartItemRequestDto.bookId()));
        CartItem cartItem = getCartItem(shoppingCart, book);
        cartItem.setQuantity(cartItemRequestDto.quantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemResponseDto update(Long id, UpdateRequestDto updateRequestDto) {
        CartItem cartItem = cartItemRepository
                .findCartItemByIdAndShoppingCartId(id, getCurrentUserCart().getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find item with id= " + id));
        cartItem.setQuantity(updateRequestDto.getQuantity());
        cartItem.setId(id);
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void delete(Long id) {
        cartItemRepository.deleteById(id);
    }

    @Override
    public void clear(ShoppingCart shoppingCart) {
        cartItemRepository.deleteAll(shoppingCart.getCartItems());
        getCurrentUserCart().getCartItems().clear();
    }

    @Override
    public ShoppingCart getCurrentUserCart() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return shoppingCartRepository.findShoppingCartByUserId(user.getId());
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    private CartItem getCartItem(ShoppingCart shoppingCart, Book book) {
        Optional<CartItem> cartItemOptional = shoppingCart.getCartItems().stream()
                .filter(i -> i.getBook().equals(book))
                .findFirst();
        CartItem cartItem;
        if (cartItemOptional.isPresent()) {
            cartItem = cartItemOptional.get();
        } else {
            cartItem = new CartItem();
            cartItem.setShoppingCart(shoppingCart);
            cartItem.setBook(book);
        }
        return cartItem;
    }
}
