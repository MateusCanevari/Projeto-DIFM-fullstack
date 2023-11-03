package br.com.projetodifm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.projetodifm.util.ErrorMessages;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmailNotFoundException extends UsernameNotFoundException{

    public EmailNotFoundException(String email) {
        super(String.format(ErrorMessages.EMAIL_NOT_FOUND, email));
    }
    
}
