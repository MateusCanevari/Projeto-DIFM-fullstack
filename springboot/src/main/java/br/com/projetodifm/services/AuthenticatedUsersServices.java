package br.com.projetodifm.services;

import br.com.projetodifm.model.User;
import br.com.projetodifm.util.ErrorMessages;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUsersServices {

    public static User getAuthenticatedUser(){
        try {
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e){
            throw new UsernameNotFoundException(ErrorMessages.USER_NOT_FOUND);
        }
    }
}
