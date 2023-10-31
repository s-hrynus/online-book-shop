package mate.academy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.BookSearchParameters;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.book.BookSpecificationBuilder;
import mate.academy.service.book.impl.BookServiceImpl;
import mate.academy.util.TestDataUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceImplUnitTest {
    private static final long INVALID_ID = -1L;
    private static CreateBookRequestDto requestDto;
    private static Book book;
    private static BookDto bookDto;
    private static BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeAll
    public static void setUp() {
        requestDto = TestDataUtil.getDefaultBookRequestDto();
        book = TestDataUtil.getDefaultBook();
        bookDto = TestDataUtil.getDefaultBookDto();
        bookDtoWithoutCategoryIds = TestDataUtil.getDefaultBookDtoWithoutCategoryIds();
    }
    
    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidCreateBookRequestDto_ShouldReturnValidBookDto() {
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        book.setCategories(bookMapper.mapToCategorySet(requestDto.getCategoriesIds()));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto savedBookDto = bookService.save(requestDto);
        assertThat(savedBookDto).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("Verify findById() method works")
    void findById_ValidBookId_ShouldReturnValidBookDto() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.findById(book.getId());
        assertEquals(bookDto, actual);
    }

    @Test
    @DisplayName("Verify findById() method doesn't work with invalid id")
    void findById_NegativeBookId_ShouldThrowException() {
        when(bookRepository.findById(INVALID_ID))
                .thenThrow(new EntityNotFoundException("Invalid id: " + INVALID_ID));
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(INVALID_ID));

        assertEquals("Invalid id: " + INVALID_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Verify findAll() method works")
    void findAll_ValidPageable_ShouldReturnListOfBookDto() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> bookDtoList = bookService.findAll(pageable);

        assertThat(bookDtoList).hasSize(1);
        assertThat(bookDtoList.get(0)).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("Verify delete() method works")
    void delete_ValidBookId_ShouldSoftDeleteBookById() {
        assertDoesNotThrow(() -> bookRepository.deleteById(book.getId()));
    }

    @Test
    @DisplayName("Verify delete() method doesn't work with invalid id")
    void delete_NotValidBookId_ShouldThrowException() {
        doThrow(new NoSuchElementException("Book with id " + INVALID_ID + " is absent in DB"))
                .when(bookRepository).deleteById(INVALID_ID);
        assertThrows(NoSuchElementException.class, () -> bookService.delete(INVALID_ID));
    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidBookId_ShouldReturnUpdatedBookDto() {
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setTitle("Two Friends");
        bookRequestDto.setAuthor("Ivan Franko");
        Book frankoBook = new Book();
        frankoBook.setId(book.getId());
        frankoBook.setTitle(bookRequestDto.getTitle());
        frankoBook.setAuthor(bookRequestDto.getAuthor());
        BookDto frankoBookDto = new BookDto();
        frankoBookDto.setId(frankoBook.getId());
        frankoBookDto.setTitle(frankoBook.getTitle());
        frankoBookDto.setAuthor(frankoBook.getAuthor());

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(frankoBook));
        when(bookMapper.toModel(bookRequestDto)).thenReturn(frankoBook);
        when(bookRepository.save(frankoBook)).thenReturn(frankoBook);
        when(bookMapper.toDto(frankoBook)).thenReturn(frankoBookDto);

        BookDto actual = bookService.update(book.getId(), bookRequestDto);
        assertEquals(frankoBookDto, actual);
    }

    @Test
    @DisplayName("Verify search() method works")
    void search_ValidBookSearchParameters_ShouldReturnListOfBookDto() {
        BookSearchParameters parameters = new BookSearchParameters(
                new String[]{"Kobzar"}, new String[0], new String[0]);
        Specification<Book> specification = mock(Specification.class);
        List<Book> bookList = List.of(book);

        when(bookSpecificationBuilder.build(parameters)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(bookList);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> expected = List.of(bookDto);
        List<BookDto> actual = bookService.search(parameters);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify findAllByCategoryId() method works")
    void findAllByCategoryId_ValidCategory_ShouldReturnListOfBookDtoByCategory() {
        List<Book> bookList = List.of(book);

        when(bookRepository.findAllByCategoriesId(anyLong())).thenReturn(bookList);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryIds);

        List<BookDtoWithoutCategoryIds> expected = List.of(bookDtoWithoutCategoryIds);
        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(book.getId());

        assertEquals(expected, actual);
    }
}
