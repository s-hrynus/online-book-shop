package mate.academy.util;

import java.math.BigDecimal;
import java.util.HashSet;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.cartitem.UpdateRequestDto;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CategoryRequestDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.Category;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class TestDataUtil {
    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = -1L;
    private static final int DEFAULT_QUANTITY = 100;
    private static User user;
    private static Book book;
    private static CartItem cartItem;
    private static ShoppingCart shoppingCart;
    private static Category category;
    private static CategoryRequestDto categoryRequestDto;
    private static CategoryRequestDto invalidCategoryRequestDto;

    public static User getDefaultUser() {
        user = new User();
        user.setId(VALID_ID);
        user.setEmail("bob@in.ua");
        user.setPassword("qwerty");
        user.setFirstName("Bob");
        user.setLastName("Alison");
        user.setShippingAddress("USA");
        user.setRoles(new HashSet<>());
        return user;
    }

    public static Book getDefaultBook() {
        book = new Book();
        book.setId(VALID_ID);
        book.setTitle("Kobzar");
        book.setAuthor("Taras Shevchenko");
        book.setIsbn("978-966-10-0135-9");
        book.setPrice(new BigDecimal(299));
        book.setDescription("This book include all best works wrote by T.Shevchenko");
        book.setCoverImage("image_1");
        book.setCategories(new HashSet<>());
        return book;
    }

    public static ShoppingCart getDefaultShoppingCart() {
        shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setId(VALID_ID);
        shoppingCart.setCartItems(new HashSet<>());
        return shoppingCart;
    }

    public static ShoppingCartDto getDefaultShoppingCartDto() {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(shoppingCart.getId());
        shoppingCartDto.setUserId(shoppingCart.getUser().getId());
        shoppingCartDto.setCartItems(new HashSet<>());
        return shoppingCartDto;
    }

    public static CartItem getDefaultCartItem() {
        cartItem = new CartItem();
        cartItem.setId(VALID_ID);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(DEFAULT_QUANTITY);
        return cartItem;
    }

    public static UpdateRequestDto getDefaultUpdateRequestDto() {
        UpdateRequestDto updateRequestDto = new UpdateRequestDto();
        updateRequestDto.setQuantity(DEFAULT_QUANTITY);
        return updateRequestDto;
    }

    public static CartItemRequestDto getBookInvalidRequestDto() {
        return new CartItemRequestDto(INVALID_ID, DEFAULT_QUANTITY);
    }

    public static CartItemRequestDto getDefaultCartItemRequestDto() {
        return new CartItemRequestDto(book.getId(), DEFAULT_QUANTITY);
    }

    public static CartItemResponseDto getDefaultCartItemResponseDto() {
        CartItemResponseDto cartItemResponseDto = new CartItemResponseDto();
        cartItemResponseDto.setId(cartItem.getId());
        cartItemResponseDto.setBookId(cartItem.getBook().getId());
        cartItemResponseDto.setQuantity(cartItem.getQuantity());
        cartItemResponseDto.setBookTitle(book.getTitle());
        return cartItemResponseDto;
    }

    public static Authentication getAuthentication() {
        return new UsernamePasswordAuthenticationToken(user, null);
    }

    public static CreateBookRequestDto getDefaultBookRequestDto() {
        return new CreateBookRequestDto()
                .setTitle("Kobzar")
                .setAuthor("Taras Shevchenko")
                .setIsbn("978-966-10-0135-9")
                .setPrice(new BigDecimal(299))
                .setDescription("This book include all best works wrote by T.Shevchenko")
                .setCoverImage("image_1")
                .setCategoriesIds(new HashSet<>());
    }

    public static CreateBookRequestDto getDefaultBookInvalidRequestDto() {
        return new CreateBookRequestDto()
                .setTitle("")
                .setAuthor("")
                .setIsbn("")
                .setPrice(new BigDecimal(299))
                .setDescription("")
                .setCoverImage("")
                .setCategoriesIds(new HashSet<>());
    }

    public static BookDto getDefaultBookDto() {
        return new BookDto()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage());
    }

    public static BookDtoWithoutCategoryIds getDefaultBookDtoWithoutCategoryIds() {
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds.setId(VALID_ID);
        bookDtoWithoutCategoryIds.setAuthor("Taras Shevchenko");
        bookDtoWithoutCategoryIds.setTitle("Kobzar");
        bookDtoWithoutCategoryIds.setIsbn("978-966-10-0135-9");
        bookDtoWithoutCategoryIds.setPrice(new BigDecimal(299));
        bookDtoWithoutCategoryIds.setDescription("This book include all "
                + "best works wrote by T.Shevchenko");
        bookDtoWithoutCategoryIds.setCoverImage("image_1");
        return bookDtoWithoutCategoryIds;
    }

    public static CategoryRequestDto getDefaultCategoryRequestDto() {
        categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Poem");
        categoryRequestDto.setDescription("smth");
        return categoryRequestDto;
    }

    public static Category getDefaultCategory() {
        category = new Category();
        category.setId(VALID_ID);
        category.setDescription(categoryRequestDto.getDescription());
        category.setName(categoryRequestDto.getName());
        return category;
    }

    public static CategoryDto getDefaultCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setDescription(category.getDescription());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    public static CategoryRequestDto getDefaultInvalidCategoryRequestDto() {
        invalidCategoryRequestDto = new CategoryRequestDto();
        invalidCategoryRequestDto.setName("");
        invalidCategoryRequestDto.setDescription("");
        return invalidCategoryRequestDto;
    }
}
