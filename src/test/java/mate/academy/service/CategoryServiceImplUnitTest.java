package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CategoryRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Category;
import mate.academy.repository.category.CategoryRepository;
import mate.academy.service.category.impl.CategoryServiceImpl;
import mate.academy.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplUnitTest {
    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = -1L;
    private static Category category;
    private static CategoryDto categoryDto;
    private static CategoryRequestDto categoryRequestDto;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryRequestDto = TestDataUtil.getDefaultCategoryRequestDto();
        category = TestDataUtil.getDefaultCategory();
        categoryDto = TestDataUtil.getDefaultCategoryDto();
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidCategoryRequestDto_ShouldReturnCategoryDto() {
        when(categoryMapper.requestToEntity(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.save(categoryRequestDto);
        assertNotNull(actual);
        assertEquals(categoryDto, actual);
    }

    @Test
    @DisplayName("Verify findById() method works")
    void findById_ValidCategoryId_ShouldReturnCategoryDtoById() {
        when(categoryRepository.findById(VALID_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.findById(VALID_ID);
        assertNotNull(actual);
        assertEquals(categoryDto, actual);
    }

    @Test
    @DisplayName("Verify findById() method doesn't work with invalid id")
    void findById_NotValidCategoryId_ShouldThrowException() {
        when(categoryRepository.findById(INVALID_ID))
                .thenThrow(new EntityNotFoundException("Can't find category by id" + INVALID_ID));

        String message = "Can't find category by id" + INVALID_ID;
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.findById(INVALID_ID));

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Verify findAll() method works")
    void findAll_ValidPageable_ShouldReturnListOfCategoryDto() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categoryList = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categoryList, pageable, categoryList.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        List<CategoryDto> expected = List.of(categoryDto);
        List<CategoryDto> actual = categoryService.findAll(pageable);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify delete() method works")
    void delete_ValidCategoryId_ShouldSoftDeleteCategoryDtoById() {
        assertDoesNotThrow(() -> categoryRepository.findById(category.getId()));
    }

    @Test
    @DisplayName("Verify delete() method doesn't work with invalid id")
    void delete_NotValidCategoryId_ShouldThrowException() {
        doThrow(new EntityNotFoundException(
                "Category with id " + INVALID_ID + " is absent in DB"))
                .when(categoryRepository).deleteById(INVALID_ID);
        assertThrows(EntityNotFoundException.class, () -> categoryService.delete(INVALID_ID));
    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidCategoryId_ShouldReturnUpdatedCategoryDto() {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Adventure");
        categoryRequestDto.setDescription("This genre tell about traveling, dangerous etc.");
        Category adventureCategory = new Category();
        adventureCategory.setId(category.getId());
        adventureCategory.setName(categoryRequestDto.getName());
        adventureCategory.setDescription(categoryRequestDto.getDescription());
        CategoryDto adventureDto = new CategoryDto();
        adventureDto.setId(adventureCategory.getId());
        adventureDto.setName(adventureCategory.getName());
        adventureDto.setDescription(adventureCategory.getDescription());

        when(categoryRepository.findById(VALID_ID)).thenReturn(Optional.of(adventureCategory));
        when(categoryMapper.toEntity(adventureDto)).thenReturn(adventureCategory);
        when(categoryRepository.save(adventureCategory)).thenReturn(adventureCategory);
        when(categoryMapper.toDto(adventureCategory)).thenReturn(adventureDto);

        CategoryDto actual = categoryService.update(VALID_ID, adventureDto);
        assertEquals(adventureDto, actual);
    }
}
