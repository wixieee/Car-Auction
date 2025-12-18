package edu.lpnu.auction.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for {@link edu.lpnu.auction.model.User}
 */
@Data
public class LoginRequest {
    @Email(message = "Невірний формат email")
    @NotBlank(message = "Email не може бути пустим")
    private String email;

    @NotBlank(message = "Пароль не може бути пустим")
    private String password;
}
