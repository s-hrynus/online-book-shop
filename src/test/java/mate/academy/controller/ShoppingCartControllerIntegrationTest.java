package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashSet;
import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.CartItemResponseDto;
import mate.academy.dto.cartitem.UpdateRequestDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerIntegrationTest {
    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = -1L;
    private static final int DEFAULT_QUANTITY = 100;
    private static final String USER = "bob@in.ua";
    private static User user;
    private static Book book;
    private static CartItem cartItem;
    private static CartItemResponseDto cartItemResponseDto;
    private static CartItemRequestDto cartItemRequestDto;
    private static CartItemRequestDto invalidRequestDto;
    private static UpdateRequestDto updateRequestDto;
    private static ShoppingCart shoppingCart;
    private static ShoppingCartDto shoppingCartDto;
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        user = new User();
        user.setId(VALID_ID);
        user.setEmail(USER);
        user.setPassword("qwerty");
        user.setFirstName("Bob");
        user.setLastName("Alison");
        user.setShippingAddress("USA");
        user.setRoles(new HashSet<>());

        book = new Book();
        book.setId(VALID_ID);
        book.setTitle("Kobzar");
        book.setAuthor("Taras Shevchenko");
        book.setIsbn("978-966-10-0135-9");
        book.setPrice(new BigDecimal(299));
        book.setDescription("This book include all best works wrote by T.Shevchenko");
        book.setCoverImage("image_1");
        book.setCategories(new HashSet<>());

        shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setId(VALID_ID);
        shoppingCart.setCartItems(new HashSet<>());

        shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(VALID_ID);
        shoppingCartDto.setUserId(VALID_ID);
        shoppingCartDto.setCartItems(new HashSet<>());

        cartItem = new CartItem();
        cartItem.setId(VALID_ID);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(DEFAULT_QUANTITY);

        updateRequestDto = new UpdateRequestDto();
        updateRequestDto.setQuantity(DEFAULT_QUANTITY);

        cartItemRequestDto = new CartItemRequestDto(book.getId(),DEFAULT_QUANTITY);

        invalidRequestDto = new CartItemRequestDto(INVALID_ID, DEFAULT_QUANTITY);

        cartItemResponseDto = new CartItemResponseDto();
        cartItemResponseDto.setId(cartItem.getId());
        cartItemResponseDto.setBookId(cartItem.getBook().getId());
        cartItemResponseDto.setQuantity(cartItem.getQuantity());
        cartItemResponseDto.setBookTitle(book.getTitle());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"USER"})
    @DisplayName("Get shopping cart with all cart items")
    @Sql(scripts = {
            "classpath:database/users/add-bob-to-users-table.sql",
            "classpath:database/shoppingCarts/add-shoppingCart-into-shopping_carts-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/remove-bob-from-users-table.sql",
            "classpath:database/shoppingCarts/remove-shoppingCart-from-shopping_carts-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAll_ValidPageable_SuccessReturningShoppingCart() throws Exception {
        MvcResult result = mockMvc.perform(get("/cart")).andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(shoppingCartDto, actual, "id"));
    }

    @Test
    @WithMockUser(username = USER, authorities = {"USER"})
    @DisplayName("Add cart item to shopping cart")
    @Sql(scripts = {
            "classpath:database/users/add-bob-to-users-table.sql",
            "classpath:database/shoppingCarts/add-shoppingCart-into-shopping_carts-table.sql",
            "classpath:database/books/add-kobzar-book-to-books-table.sql",
            "classpath:database/cartItems/add-items-to-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/remove-bob-from-users-table.sql",
            "classpath:database/shoppingCarts/remove-shoppingCart-from-shopping_carts-table.sql",
            "classpath:database/books/remove-books-from-books-table.sql",
            "classpath:database/cartItems/remove-items-from-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addCartItem_ValidCartItemRequestDto_SuccessAddCartItemToShoppingCart() throws Exception {
        MvcResult result = mockMvc.perform(post("/cart")
                        .content(objectMapper.writeValueAsString(cartItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CartItemResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CartItemResponseDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(cartItemResponseDto, actual, "id"));
    }

    @Test
    @WithMockUser(username = USER, authorities = {"USER"})
    @DisplayName("Throw exception in addCartItem() when wasn't valid requestDto")
    @Sql(scripts = {
            "classpath:database/users/add-bob-to-users-table.sql",
            "classpath:database/shoppingCarts/add-shoppingCart-into-shopping_carts-table.sql",
            "classpath:database/books/add-kobzar-book-to-books-table.sql",
            "classpath:database/cartItems/add-items-to-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/remove-bob-from-users-table.sql",
            "classpath:database/shoppingCarts/remove-shoppingCart-from-shopping_carts-table.sql",
            "classpath:database/books/remove-books-from-books-table.sql",
            "classpath:database/cartItems/remove-items-from-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addCartItem_NotValidCartItemRequestDto_ShouldThrowException() throws Exception {
        mockMvc.perform(post("/cart")
                        .content(objectMapper.writeValueAsString(invalidRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"USER"})
    @DisplayName("Delete cart item by id")
    @Sql(scripts = {
            "classpath:database/books/add-kobzar-book-to-books-table.sql",
            "classpath:database/cartItems/add-items-to-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-books-from-books-table.sql",
            "classpath:database/cartItems/remove-items-from-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_ValidCartItemId_SuccessSoftDeleteCartItemById() throws Exception {
        mockMvc.perform(delete("/cart/cart-items/" + VALID_ID)).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"USER"})
    @DisplayName("Update cart item by id")
    @Sql(scripts = {
            "classpath:database/users/add-bob-to-users-table.sql",
            "classpath:database/shoppingCarts/add-shoppingCart-into-shopping_carts-table.sql",
            "classpath:database/books/add-kobzar-book-to-books-table.sql",
            "classpath:database/cartItems/add-items-to-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/remove-bob-from-users-table.sql",
            "classpath:database/shoppingCarts/remove-shoppingCart-from-shopping_carts-table.sql",
            "classpath:database/books/remove-books-from-books-table.sql",
            "classpath:database/cartItems/remove-items-from-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_ValidCartItemRequestDto_SuccessUpdateCartItemById() throws Exception {
        MvcResult result = mockMvc.perform(put("/cart/cart-items/" + VALID_ID)
                        .content(objectMapper.writeValueAsString(updateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CartItemResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CartItemResponseDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(cartItemResponseDto, actual, "id"));
    }

    @Test
    @WithMockUser(username = USER, authorities = {"USER"})
    @DisplayName("Throw exception in update() when wasn't valid cart item id")
    @Sql(scripts = {
            "classpath:database/users/add-bob-to-users-table.sql",
            "classpath:database/shoppingCarts/add-shoppingCart-into-shopping_carts-table.sql",
            "classpath:database/books/add-kobzar-book-to-books-table.sql",
            "classpath:database/cartItems/add-items-to-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/remove-bob-from-users-table.sql",
            "classpath:database/shoppingCarts/remove-shoppingCart-from-shopping_carts-table.sql",
            "classpath:database/books/remove-books-from-books-table.sql",
            "classpath:database/cartItems/remove-items-from-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_NotValidCartItemRequest_ShouldThrowException() throws Exception {
        mockMvc.perform(put("/cart/cart-items/" + INVALID_ID)
                        .content(objectMapper.writeValueAsString(updateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
