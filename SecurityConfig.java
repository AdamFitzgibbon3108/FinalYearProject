package com.example.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

@Configuration
@EnableMethodSecurity //  Enables @PreAuthorize in controllers
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Secure password hashing
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                //  Allow open access to login, register, and public static files
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                .requestMatchers("/survey", "/survey/submit", "/questions/submit").permitAll()
                
                //  Restrict /admin/** routes to ADMIN role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                //  Allow authenticated users access to user-related operations
                .requestMatchers("/api/users/**").authenticated()

                //  All other routes require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")  // Custom login page
                .successHandler(customAuthenticationSuccessHandler()) //  Redirects based on role
                .failureUrl("/login?error=true")  //  Handle login failure
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")  //  Define logout endpoint
                .logoutSuccessUrl("/login?logout=true")  //  Redirect after logout
                .permitAll()
            )
            .csrf().disable(); //  Disabled for now (Enable in production)

        return http.build();
    }

    /**
     * Custom Success Handler: Redirect users based on their role.
     */
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    response.sendRedirect("/admin/dashboard");
                    return;
                }
            }
            response.sendRedirect("/dashboard"); // Default for normal users
        };
    }
}
