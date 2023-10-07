package mate.academy.dto.user;

import jakarta.validation.constraints.NotNull;
import mate.academy.validation.Email;
import mate.academy.validation.Password;

public record UserLoginRequestDto(
        @NotNull
        @Email
        String email,
        @NotNull
        @Password
        String password
){
}
