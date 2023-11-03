package br.com.projetodifm.exceptions;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionsResponse implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
