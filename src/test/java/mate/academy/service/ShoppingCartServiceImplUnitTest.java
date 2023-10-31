package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
import mate.academy.repository.user.UserRepository;
import mate.academy.service.shoppingcart.impl.ShoppingCartServiceImpl;
import mate.academy.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplUnitTest {
    private static final long INVALID_ID = -1L;
    private static User user;
    private static Book book;
    private static CartItem cartItem;
    private static CartItemResponseDto cartItemResponseDto;
    private static CartItemRequestDto cartItemRequestDto;
    private static UpdateRequestDto updateRequestDto;
    private static ShoppingCart shoppingCart;
    private static ShoppingCartDto shoppingCartDto;
    private static Authentication authentication;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @BeforeEach
    void setUp() {
        user = TestDataUtil.getDefaultUser();
        book = TestDataUtil.getDefaultBook();
        cartItem = TestDataUtil.getDefaultCartItem();
        cartItemRequestDto = TestDataUtil.getDefaultCartItemRequestDto();
        cartItemResponseDto = TestDataUtil.getDefaultCartItemResponseDto();
        shoppingCart = TestDataUtil.getDefaultShoppingCart();
        shoppingCartDto = TestDataUtil.getDefaultShoppingCartDto();
        authentication = TestDataUtil.getAuthentication();
    }

    @Test
    @DisplayName("Verify addCartItem() method works")
    void addCartItem_ValidCartItem_ShouldReturnCartItemResponseDto() {
        when(userRepository.getUserByUsername(any())).thenReturn(user);
        when(shoppingCartRepository.getShoppingCartByUserId(anyLong())).thenReturn(shoppingCart);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(cartItemRepository.save(any())).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(cartItemResponseDto);

        CartItemResponseDto actual = shoppingCartService
                .addCartItem(cartItemRequestDto, authentication);
        assertEquals(cartItemResponseDto, actual);
    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidUpdateRequestDto_ShouldUpdateCartItemById() {
        updateRequestDto = new UpdateRequestDto();
        updateRequestDto.setQuantity(10);
        cartItem.setQuantity(updateRequestDto.getQuantity());
        cartItemResponseDto.setQuantity(cartItem.getQuantity());

        when(cartItemRepository.findCartItemById(anyLong())).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(cartItemResponseDto);

        CartItemResponseDto actual = shoppingCartService.update(anyLong(), updateRequestDto);
        assertEquals(cartItemResponseDto, actual);
    }

    @Test
    @DisplayName("Throw exception in update() when wasn't valid cartItem id")
    void update_NotValidCartItemId_ShouldThrowException() {
        doThrow(new EntityNotFoundException("Can't find item with id= " + INVALID_ID))
                .when(cartItemRepository).findCartItemById(INVALID_ID);
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.update(INVALID_ID, updateRequestDto));
    }

    @Test
    @DisplayName("Verify getShoppingCart() method works")
    void getShoppingCart_ValidUser_ShouldReturnShoppingCartByCurrentUser() {
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(user);
        when(shoppingCartRepository.getShoppingCartByUserId(anyLong())).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto actual = shoppingCartService.getShoppingCart(authentication);
        assertEquals(shoppingCartDto, actual);
    }

    @Test
    @DisplayName("Verify delete() method works")
    void delete_ValidCartItemId_ShouldSoftDeleteCartItemById() {
        assertDoesNotThrow(() -> shoppingCartService.delete(cartItem.getId()));
    }

    @Test
    @DisplayName("Throw exception in delete() when wasn't valid cartItem id")
    void delete_NotValidCartItemId_ShouldThrowException() {
        doThrow(new EntityNotFoundException("Can't find item with id= " + INVALID_ID))
                .when(cartItemRepository).deleteById(INVALID_ID);

        assertThrows(EntityNotFoundException.class, () -> shoppingCartService.delete(INVALID_ID));
    }

    @Test
    @DisplayName("Verify getCurrentUserCart() method works")
    void getCurrentUserCart_ValidUser_ShouldReturnShoppingCartByUser() {
        when(shoppingCartRepository.getShoppingCartByUserId(anyLong())).thenReturn(shoppingCart);

        ShoppingCart actual = shoppingCartService.getCurrentUserCart(user);
        assertEquals(shoppingCart, actual);
    }

    @Test
    @DisplayName("Throw exception in getCurrentUserCart() when wasn't valid user")
    void getCurrentUserCart_NotValidUser_ShouldThrowException() {
        User invalidUser = new User();
        invalidUser.setId(INVALID_ID);
        doThrow(new EntityNotFoundException("User isn't present"))
                .when(shoppingCartRepository).getShoppingCartByUserId(INVALID_ID);

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getCurrentUserCart(invalidUser));
    }

    @Test
    @DisplayName("Verify clear() method works")
    void clear_ValidShoppingCart_ShouldDeleteAllCartItems() {
        when(shoppingCartRepository.getShoppingCartByUserId(anyLong())).thenReturn(shoppingCart);

        assertDoesNotThrow(() -> shoppingCartService.clear(shoppingCart, authentication));
    }

    @Test
    @DisplayName("Verify createShoppingCart() method works")
    void createShoppingCart_ValidUser_ShouldCreateShoppingCartForUser() {
        assertDoesNotThrow(() -> shoppingCartService.createShoppingCart(user));
    }
}
