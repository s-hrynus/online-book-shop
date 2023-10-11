package mate.academy.dto.cartitem;

public record CartItemRequestDto(
        Long bookId,
        int quantity
) {
}
