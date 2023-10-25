package mate.academy.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(
        @NotNull
        @Size(min = 1)
        String name,
        String description
) {
}
