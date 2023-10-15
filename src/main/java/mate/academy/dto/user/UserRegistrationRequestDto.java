package mate.academy.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import mate.academy.validation.FieldMatch;
import mate.academy.validation.Password;

@FieldMatch(first = "password", second = "repeatPassword")
public record UserRegistrationRequestDto(
        @NotNull
        @Email
        String email,
        @NotNull
        @Password
        String password,
        @NotNull
        String repeatPassword,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        String shippingAddress
) {
}
