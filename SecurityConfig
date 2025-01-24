package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt for password hashing
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll() // Allow login and register pages without authentication
                .anyRequest().authenticated() // Protect all other routes
            )
            .formLogin(form -> form
                .loginPage("/login") // Custom login page
                .defaultSuccessUrl("/dashboard", true) // Redirect to dashboard after successful login
                .failureUrl("/login?error=true") // Redirect to login page with error on failure
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // Define the logout URL
                .logoutSuccessUrl("/login?logout=true") // Redirect to login page with logout success message
                .permitAll()
            )
            .csrf().disable(); // Disable CSRF for simplicity in development; enable it in production

        return http.build();
    }
}
