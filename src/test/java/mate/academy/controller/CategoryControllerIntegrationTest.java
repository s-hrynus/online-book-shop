package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CategoryRequestDto;
import mate.academy.model.Category;
import mate.academy.util.TestDataUtil;
import org.junit.jupiter.api.BeforeAll;
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
class CategoryControllerIntegrationTest {
    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = -1L;
    private static Category category;
    private static CategoryRequestDto requestDto;
    private static CategoryRequestDto invalidCategoryRequestDto;
    private static CategoryDto categoryDto;
    private static BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds;
    
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        requestDto = TestDataUtil.getDefaultCategoryRequestDto();
        invalidCategoryRequestDto = TestDataUtil.getDefaultInvalidCategoryRequestDto();
        category = TestDataUtil.getDefaultCategory();
        categoryDto = TestDataUtil.getDefaultCategoryDto();
        bookDtoWithoutCategoryIds = TestDataUtil.getDefaultBookDtoWithoutCategoryIds();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Create a new category")
    @Sql(scripts = "classpath:database/categories/remove-poem-category-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequestDto_SuccessCreatingANewCategory() throws Exception {
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(categoryDto, actual, "id"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Throw exception in create() when wasn't valid requestDto")
    @Sql(scripts = "classpath:database/categories/remove-poem-category-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_NotValidRequestDto_ShouldThrowException() throws Exception {
        mockMvc.perform(post("/categories")
                .content(objectMapper.writeValueAsString(invalidCategoryRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Get all categories")
    @Sql(scripts = "classpath:database/categories/add-poem-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/remove-poem-category-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAll_ValidPageable_SuccessReturnListOfCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories")).andReturn();

        List<CategoryDto> expected = List.of(categoryDto);
        List<CategoryDto> actual = List.of(objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto[].class));

        assertTrue(EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0), "id"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Get category by id")
    @Sql(scripts = "classpath:database/categories/add-poem-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/remove-poem-category-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getById_ValidCategoryId_SuccessReturningCategoryById() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/" + VALID_ID)).andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertEquals(categoryDto, actual);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Throw exception in getById() when wasn't valid id")
    @Sql(scripts = "classpath:database/categories/add-poem-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/remove-poem-category-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getById_NotValidCategoryId_ShouldThrowException() throws Exception {
        mockMvc.perform(get("/categories/" + INVALID_ID)).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Update category by id")
    @Sql(scripts = "classpath:database/categories/add-poem-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/remove-poem-category-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_ValidRequestDto_SuccessUpdateCategory() throws Exception {
        MvcResult result = mockMvc.perform(put("/categories/" + VALID_ID)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertEquals(categoryDto, actual);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Throw exception in update() when wasn't valid id")
    @Sql(scripts = "classpath:database/categories/add-poem-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/remove-poem-category-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_NotValidRequestDto_ShouldThrowException() throws Exception {
        mockMvc.perform(put("/categories/" + INVALID_ID)
                .content(objectMapper.writeValueAsString(invalidCategoryRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Delete category by id")
    @Sql(scripts = "classpath:database/categories/add-poem-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/remove-poem-category-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_ValidCategoryId_SuccessSoftDeleteById() throws Exception {
        mockMvc.perform(delete("/categories/" + VALID_ID)).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Get all book by category id without category id")
    @Sql(scripts = {
            "classpath:database/books/add-kobzar-book-to-books-table.sql",
            "classpath:database/categories/add-poem-category-to-categories-table.sql",
            "classpath:database/books-categories/"
                    + "add-kobzar-poem-category-to-books-categories-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books-categories/"
                    + "remove-kobzar-poem-category-from-books-categories-table.sql",
            "classpath:database/books/remove-books-from-books-table.sql",
            "classpath:database/categories/remove-poem-category-from-categories-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBooksByCategoryId_ValidCategoryId_SuccessReturnListOfBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/" + VALID_ID + "/books")).andReturn();

        List<BookDtoWithoutCategoryIds> expected = List.of(bookDtoWithoutCategoryIds);
        List<BookDtoWithoutCategoryIds> actual = List.of(objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDtoWithoutCategoryIds[].class));

        assertTrue(EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0),
                "id", "categoriesIds"));
    }
}
