package com.example.config;

import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.NoOpPasswordEncoder; // TEMPORARY ONLY
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		System.out.println("⚠️  Using NoOpPasswordEncoder (plain text passwords enabled)");
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/login", "/register", "/css/**", "/js/**")
				.permitAll().requestMatchers("/survey", "/survey/submit", "/questions/submit").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/api/users/**").authenticated()
				.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").successHandler(customAuthenticationSuccessHandler())
						.failureUrl("/login?error=true").permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true").permitAll()).csrf()
				.disable();

		return http.build();
	}

	/**
	 * Logs roles and redirects based on authority.
	 */
	@Bean
	public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
		return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			System.out.println(" Login success for user: " + authentication.getName());
			System.out.println(" Roles: ");
			for (GrantedAuthority authority : authorities) {
				System.out.println(" - " + authority.getAuthority());
				if (authority.getAuthority().equals("ROLE_ADMIN")) {
					System.out.println(" Redirecting to /admin/dashboard");
					response.sendRedirect("/admin/dashboard");
					return;
				}
			}
			System.out.println(" Redirecting to /dashboard");
			response.sendRedirect("/dashboard");
		};
	}
}
