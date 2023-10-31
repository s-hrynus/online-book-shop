package mate.academy.dto.cartitem;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateRequestDto {
    @Min(value = 0)
    private int quantity;
}
