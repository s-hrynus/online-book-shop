package mate.academy.service.category;

import java.util.List;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CategoryRequestDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto save(CategoryRequestDto categoryRequestDto);

    CategoryDto updateById(Long id, CategoryDto categoryDto);

    void deleteById(Long id);
}
