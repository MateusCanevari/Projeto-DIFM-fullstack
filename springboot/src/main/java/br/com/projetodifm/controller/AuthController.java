package br.com.projetodifm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.projetodifm.data.vo.v1.security.LoginRequestVO;
import br.com.projetodifm.data.vo.v1.security.RegisterRequestVO;
import br.com.projetodifm.services.AuthServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Endpoint")
public class AuthController {

    @Autowired
    private AuthServices services;

    @SuppressWarnings("rawtypes")
    @Operation(summary = "Authenticates a user and returns a token")
    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequestVO request) {
        return services.login(request);
    }

    @SuppressWarnings("rawtypes")
    @Operation(summary = "Register a user and returns a token")
    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody @Valid RegisterRequestVO request) {
        return services.register(request);
    }

    @SuppressWarnings("rawtypes")
	@Operation(summary = "Refresh token for authenticated user and returns a token")
	@PutMapping(value = "/refresh/{email}")
    public ResponseEntity refreshToken(@PathVariable(value = "email") String email, 
        @RequestHeader("Authorization") String refreshToken) {
        return services.refreshToken(email, refreshToken);
    }

    @SuppressWarnings("rawtypes")
    @GetMapping(value = "/emailVerification")
    public ResponseEntity EmailVerificationLink(@RequestParam(value = "token", defaultValue = "null") String token){
        return services.emailVerificationLink(token);
    }

    @SuppressWarnings("rawtypes")
    @GetMapping(value = "/resendEmailVerification")
    public ResponseEntity resendEmailVerificationLink(@RequestParam(value = "token", defaultValue = "null") String token){
        return services.resendEmailVerificationLink(token);
    }

}
