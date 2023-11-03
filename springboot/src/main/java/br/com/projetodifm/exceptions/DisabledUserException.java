package br.com.projetodifm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class DisabledUserException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public DisabledUserException(String msg){
        super(msg);
    }
}
