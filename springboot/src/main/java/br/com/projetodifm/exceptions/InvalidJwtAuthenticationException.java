package br.com.projetodifm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.projetodifm.util.ErrorMessages;

@ResponseStatus(HttpStatus.FORBIDDEN) 
public class InvalidJwtAuthenticationException extends AuthenticationException{

    private static final long serialVersionUID = 1L;

    public InvalidJwtAuthenticationException() {
        super(ErrorMessages.INVALID_TOKEN);
    }

    public InvalidJwtAuthenticationException(String msg) {
        super(msg);
    }
    
}