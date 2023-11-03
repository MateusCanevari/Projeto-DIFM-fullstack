package br.com.projetodifm.data.vo.v1.security;

import java.io.Serializable;

import br.com.projetodifm.util.ErrorMessages;
import br.com.projetodifm.validation.constraints.Name;
import br.com.projetodifm.validation.constraints.Password;
import br.com.projetodifm.validation.constraints.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = ErrorMessages.CONTENT_NOT_BLANK)
    @Name(message = ErrorMessages.INVALID_FIELD)
    private String firstName;
    @NotBlank(message = ErrorMessages.CONTENT_NOT_BLANK)
    @Name(message = ErrorMessages.INVALID_FIELD)
    private String lastName;
    @NotBlank(message = ErrorMessages.CONTENT_NOT_BLANK)
    @Email(message = ErrorMessages.INVALID_FIELD)
    private String email;
    @NotBlank(message = ErrorMessages.CONTENT_NOT_BLANK)
    @Password(message = ErrorMessages.INVALID_FIELD)
    private String password;
    @NotBlank(message = ErrorMessages.CONTENT_NOT_BLANK)
    @PhoneNumber(message = ErrorMessages.INVALID_FIELD)
    private String phoneNumber;
    @NotBlank(message = ErrorMessages.CONTENT_NOT_BLANK)
    private String gender;
}
