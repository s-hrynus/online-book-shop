package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import java.util.List;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.model.Book;
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
class BookControllerIntegrationTest {
    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = -1L;
    private static Book book;
    private static BookDto bookDto;
    private static CreateBookRequestDto requestDto;
    private static CreateBookRequestDto invalidRequestDto;

    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        requestDto = new CreateBookRequestDto()
                .setTitle("Kobzar")
                .setAuthor("Taras Shevchenko")
                .setIsbn("978-966-10-0135-9")
                .setPrice(new BigDecimal(299))
                .setDescription("This book include all best works wrote by T.Shevchenko")
                .setCoverImage("image_1")
                .setCategoriesIds(new HashSet<>());

        invalidRequestDto = new CreateBookRequestDto()
                .setTitle("")
                .setAuthor("")
                .setIsbn("")
                .setPrice(new BigDecimal(299))
                .setDescription("")
                .setCoverImage("")
                .setCategoriesIds(new HashSet<>());

        book = new Book();
        book.setId(VALID_ID);
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());

        bookDto = new BookDto()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Create a new book")
    @Sql(scripts = "classpath:database/books/remove-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_SuccessCreatingANewBook() throws Exception {

        MvcResult result = mockMvc.perform(post("/books")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(bookDto, actual, "id", "categoriesIds"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Throw exception in createBook() when wasn't valid requestDto")
    @Sql(scripts = "classpath:database/books/remove-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_NotValidBookRequestDto_ShouldThrowException() throws Exception {
        mockMvc.perform(post("/books")
                .content(objectMapper.writeValueAsString(invalidRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Get all books")
    @Sql(scripts = "classpath:database/books/add-kobzar-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAll_ValidPageable_SuccessReturningOfListOfBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")).andReturn();

        List<BookDto> expected = List.of(bookDto);
        List<BookDto> actual = List.of(objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto[].class));

        assertTrue(EqualsBuilder.reflectionEquals(
                expected.get(0), actual.get(0), "id", "categoriesIds"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Get book by id")
    @Sql(scripts = "classpath:database/books/add-kobzar-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            "classpath:database/books/remove-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getById_ValidBookId_SuccessReturningBookById() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/" + VALID_ID)).andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(bookDto, actual, "id", "categoriesIds"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Throw exception in getById() when wasn't valid id")
    @Sql(scripts = "classpath:database/books/add-kobzar-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            "classpath:database/books/remove-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getById_NotValidBookId_ShouldThrowException() throws Exception {
        mockMvc.perform(get("/books/" + INVALID_ID)).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Delete book by id")
    @Sql(scripts = "classpath:database/books/add-kobzar-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            "classpath:database/books/remove-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_ValidBookId_SuccessSoftDeleteById() throws Exception {
        mockMvc.perform(delete("/books/" + VALID_ID)).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Update book by id")
    @Sql(scripts = "classpath:database/books/add-kobzar-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            "classpath:database/books/remove-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_ValidRequestDto_SuccessUpdateBookById() throws Exception {
        MvcResult result = mockMvc.perform(put("/books/" + VALID_ID)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(bookDto, actual, "id", "categoriesIds"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DisplayName("Throw exception in update() when wasn't valid requestDto")
    @Sql(scripts = "classpath:database/books/add-kobzar-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            "classpath:database/books/remove-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_NotValidRequestDto_ShouldThrowException() throws Exception {
        mockMvc.perform(put("/books/" + VALID_ID)
                .content(objectMapper.writeValueAsString(invalidRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Search books by criteria")
    @Sql(scripts = "classpath:database/books/add-kobzar-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            "classpath:database/books/remove-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void search_ValidParameters_SuccessReturnListOfBooks() throws Exception {
        List<BookDto> bookDtoList = List.of(bookDto);
        MvcResult result = mockMvc.perform(get("/books/search?author="
                + book.getAuthor())).andReturn();
        List<BookDto> actual = List.of(objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto[].class));

        assertTrue(EqualsBuilder.reflectionEquals(bookDtoList.get(0), actual.get(0),
                "id", "categoriesIds"));
    }
}
