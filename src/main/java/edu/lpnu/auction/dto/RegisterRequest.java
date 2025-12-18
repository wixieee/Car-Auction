package edu.lpnu.auction.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for {@link edu.lpnu.auction.model.User}
 */
@Data
public class RegisterRequest{
    @NotBlank(message = "Ім'я не може бути пустим")
    private String firstName;

    @NotBlank(message = "Прізвище не може бути пустим")
    private String lastName;

    @NotNull(message = "Дата народження не може бути пуста")
    @Past(message = "Дата народження повинна бути в минулому")
    private LocalDate birthDate;

    @Email(message = "Невірний формат email")
    @NotBlank(message = "Email не може бути пустим")
    private String email;

    @Size(min = 8, message = "Мінімальна довжина паролю 8 символів")
    @NotBlank(message = "Пароль не може бути пустим")
    private String password;

    @NotBlank(message = "Підтвердження паролю не може бути пустим")
    private String passwordConfirm;
}