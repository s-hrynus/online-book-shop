package mate.academy.controller;

import static mate.academy.util.TestDataUtil.getBookInvalidRequestDto;
import static mate.academy.util.TestDataUtil.getDefaultBook;
import static mate.academy.util.TestDataUtil.getDefaultCartItem;
import static mate.academy.util.TestDataUtil.getDefaultCartItemRequestDto;
import static mate.academy.util.TestDataUtil.getDefaultCartItemResponseDto;
import static mate.academy.util.TestDataUtil.getDefaultShoppingCart;
import static mate.academy.util.TestDataUtil.getDefaultShoppingCartDto;
import static mate.academy.util.TestDataUtil.getDefaultUpdateRequestDto;
import static mate.academy.util.TestDataUtil.getDefaultUser;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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

        user = getDefaultUser();
        book = getDefaultBook();
        cartItem = getDefaultCartItem();
        cartItemRequestDto = getDefaultCartItemRequestDto();
        cartItemResponseDto = getDefaultCartItemResponseDto();
        invalidRequestDto = getBookInvalidRequestDto();
        updateRequestDto = getDefaultUpdateRequestDto();
        shoppingCart = getDefaultShoppingCart();
        shoppingCartDto = getDefaultShoppingCartDto();
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
