package br.com.projetodifm.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.projetodifm.security.jwt.JwtAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationProvider authProvider;

    @Autowired
    private JwtAuthenticationFilter authFilter;

    @Autowired
    private LogoutHandler logoutHandler;

    @Value("${authorizeHttpRequests.permitAll}")
    private String requestAllowed;

    @Value("${authorizeHttpRequests.authenticated}")
    private String requestAuthenticated;

    @Value("${authorizeHttpRequests.denyAll}")
    private String requestDenied;

    @Value("${authorizeHttpRequests.adminsOnly}")
    private String adminsOnly;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorizeHttpRequests -> authorizeHttpRequests
                                .requestMatchers(requestAllowed.split(",")).permitAll()
                                .requestMatchers(requestAuthenticated.split(",")).hasAnyAuthority("ADMIN", "MANAGER", "COMMON_USER")
                                .requestMatchers(requestDenied.split(",")).denyAll()
                                .requestMatchers(adminsOnly.split(",")).hasAnyAuthority("ADMIN"))
                .cors(Customizer.withDefaults())
                .authenticationProvider(authProvider)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(
                                (request, response, authentication) ->
                                SecurityContextHolder.clearContext()));

        return http.build();
    }
}
