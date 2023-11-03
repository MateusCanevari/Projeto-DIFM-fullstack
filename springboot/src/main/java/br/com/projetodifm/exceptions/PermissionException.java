package br.com.projetodifm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PermissionException extends RuntimeException{
    
    private static final long serialVersionUID = 1L;

    public PermissionException(String msg){
        super(msg);
    }
}
