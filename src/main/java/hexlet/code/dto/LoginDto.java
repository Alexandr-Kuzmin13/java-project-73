package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    private static final int MIN = 3;
    private static final int MAX = 100;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size (min = MIN, max = MAX)
    private String password;
}
