package br.com.projetodifm.data.vo.v1.admin;

import java.io.Serializable;

import br.com.projetodifm.util.ErrorMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionForUserVO implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private Long permissionId;
  
    @NotBlank(message = ErrorMessages.CONTENT_NOT_BLANK)
    @Email(message = ErrorMessages.INVALID_FIELD)
    private String userEmail;
}
