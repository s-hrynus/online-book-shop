package mate.academy.dto.cartitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequestDto(
        @NotNull
        Long bookId,
        @NotNull
        @Min(value = 1)
        int quantity
) {
}
