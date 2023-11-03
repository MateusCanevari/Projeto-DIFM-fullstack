package br.com.projetodifm.services;

import br.com.projetodifm.controller.AuthController;
import br.com.projetodifm.data.vo.v1.security.LoginRequestVO;
import br.com.projetodifm.data.vo.v1.security.RegisterRequestVO;
import br.com.projetodifm.data.vo.v1.security.TokenVO;
import br.com.projetodifm.exceptions.*;
import br.com.projetodifm.model.Links;
import br.com.projetodifm.model.Permission;
import br.com.projetodifm.model.User;
import br.com.projetodifm.model.token.Token;
import br.com.projetodifm.model.token.TokenType;
import br.com.projetodifm.repositories.LinkRepository;
import br.com.projetodifm.repositories.PermissionRepository;
import br.com.projetodifm.repositories.TokenRepository;
import br.com.projetodifm.repositories.UserRepository;
import br.com.projetodifm.util.BuildEmail;
import br.com.projetodifm.util.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class AuthServices {

    @Autowired
    private UserRepository repository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtServices service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AbstractEmailService emailService;

    @SuppressWarnings("rawtypes")
    @Transactional
    public ResponseEntity register(RegisterRequestVO request) {

        List<Permission> permissions = new ArrayList<>();

        permissions.add(permissionRepository.findById(3L)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND)));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .permissions(permissions)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(false)
                .build();

        if (repository.existsByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber()))
            throw new ConflictException(ErrorMessages.EMAIL_OR_PHONENUMBER_CONFLICT);

        repository.save(user);

        var tokenVO = service.createToken(user);

        saveUserToken(user, tokenVO);

        emailService.sendEmailVerification(user.getEmail(),
                BuildEmail.buildConfirmUserEmail(user.getFirstName(),
                        createVerificationLink(tokenVO.getAccessToken())));

        return ResponseEntity.ok(tokenVO);
    }

    @SuppressWarnings("rawtypes")
    @Transactional(noRollbackFor = DisabledUserException.class)
    public ResponseEntity login(LoginRequestVO request) {
        try {

            var email = request.getEmail();
            var password = request.getPassword();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            var user = repository.findByEmail(email).orElseThrow();

            var tokenVO = service.createToken(user);

            revokeAllUsersTokens(user);
            saveUserToken(user, tokenVO);

            return ResponseEntity.ok(tokenVO);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(ErrorMessages.BAD_CREDENTIALS);
        } catch (Exception e){
            if(repository.findByEmail(request.getEmail()).isPresent()){
                var user = repository.findByEmail(request.getEmail()).orElseThrow();
                if(!user.isEnabled()){
                    var token = tokenRepository.findValidTokenByUserId(user.getId()).orElseThrow();
                    resendEmailVerificationLink(token.getToken());
                }
            }
            throw new DisabledUserException(ErrorMessages.CONFIRM_EMAIL);
        }
    }

    @SuppressWarnings("rawtypes")
    @Transactional
    public ResponseEntity refreshToken(String email, String refreshToken) {

        if(refreshToken.startsWith("Bearer ")){
            refreshToken = refreshToken.substring("Bearer ".length());
        }

        var user = repository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));

        var tokenVO = service.refreshToken(refreshToken, user);

        var token = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(InvalidJwtAuthenticationException::new);

        token.setToken(tokenVO.getAccessToken());
        token.setRefreshToken(tokenVO.getRefreshToken());

        tokenRepository.save(token);

        return ResponseEntity.ok(tokenVO);
    }

    @SuppressWarnings("rawtypes")
    @Transactional
    public ResponseEntity emailVerificationLink(String accessToken){

        var user = repository.findByToken(accessToken).orElseThrow();
        var token = tokenRepository.findByToken(accessToken).orElseThrow();
        var link = linkRepository.findByURLAndUserId(accessToken, user.getId()).orElseThrow();

        if (token.isVerified()){
            throw new IllegalStateException("email already verified");
        }

        if(link.getExpireAt() == null || link.getExpireAt().isBefore(LocalDateTime.now())){
            resendEmailVerificationLink(token.getToken());
            throw new IllegalStateException("Link has expired, another verification link has been sent to your email");
        }

        user.setEnabled(true);
        token.setVerified(true);

        repository.save(user);
        tokenRepository.save(token);

        linkRepository.delete(link);

        return ResponseEntity.noContent().build();
    }

    @SuppressWarnings("rawtypes")
    @Transactional
    public ResponseEntity resendEmailVerificationLink(String token){

        var user = repository.findByToken(token).orElseThrow();
        var link = createVerificationLink(token);

        if(user.getEnabled()){
            throw new IllegalStateException("email already verified");
        }

        emailService.sendEmailVerification(user.getEmail(),
                BuildEmail.buildConfirmUserEmail(user.getFirstName(), link));

        return ResponseEntity.noContent().build();
    }

    private void revokeAllUsersTokens(User user){
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId())
                .orElseThrow(InvalidJwtAuthenticationException::new);

        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);

        var invalidUserTokens = tokenRepository.findAllInvalidTokensByUser(user.getId())
                .orElseThrow(InvalidJwtAuthenticationException::new);

        if (invalidUserTokens.size() >= 5) // Limpa os tokens do usuario a cada 5 registros
           tokenRepository.deleteAll(invalidUserTokens);

    }

    private void saveUserToken(User user, TokenVO tokenVO){
        var token = Token.builder()
                .user(user)
                .createAt(LocalDateTime.now())
                .token(tokenVO.getAccessToken())
                .refreshToken(tokenVO.getRefreshToken())
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .verified(user.getEnabled())
                .build();

        tokenRepository.save(token);
    }

    private String createVerificationLink(String token){
        var link = linkTo(methodOn(AuthController.class).EmailVerificationLink(token)); // link

        var user = repository.findByToken(token).orElseThrow();

        if (linkRepository.findByURLAndUserId(token, user.getId()).isEmpty()){
            var links = Links.builder()
                    .pageName("Verify Link")
                    .link(link.toString())
                    .comment("Email verification link")
                    .createAt(LocalDateTime.now())
                    .expireAt(LocalDateTime.now().plusMinutes(15))
                    .user(user)
                    .build();

            linkRepository.save(links);
        }

        var links = linkRepository.findByURLAndUserId(token, user.getId()).orElseThrow();

        links.setExpireAt(LocalDateTime.now().plusMinutes(15));

        linkRepository.save(links);

        return links.getLink();
    }

}
