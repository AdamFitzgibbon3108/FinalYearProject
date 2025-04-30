package com.example.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.model.User;
import com.example.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public MyUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Retrieve user or throw error
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("❌ User not found: " + username));

		// Log the user details for debugging
		System.out.println("✅ Loaded user: " + username);
		System.out.println("🔐 Password (from DB): " + user.getPassword());
		System.out.println("🔒 Roles: " + user.getRoles());

		// Build authorities
		Set<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).collect(Collectors.toSet());

		// Return plain text password with {noop} prefix
		return org.springframework.security.core.userdetails.User.withUsername(username).password(user.getPassword())

				.authorities(authorities).build();
	}
}
