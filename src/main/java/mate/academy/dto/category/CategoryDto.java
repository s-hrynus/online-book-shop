package mate.academy.dto.category;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
}
