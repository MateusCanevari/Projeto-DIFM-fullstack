package br.com.projetodifm.exceptions;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionsResponseValidation implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private LocalDateTime timestamp;
    private Set<String> message;
    private String details;
}
