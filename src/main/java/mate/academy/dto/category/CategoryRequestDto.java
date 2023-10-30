package mate.academy.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class CategoryRequestDto {
    @NotNull
    @Size(min = 1)
    private String name;
    @NotNull
    private String description;
}
