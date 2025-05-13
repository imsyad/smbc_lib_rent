package com.smbc.library.rent_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.smbc.library.rent_service.filter.JwtFilter;
import com.smbc.library.rent_service.security.handler.ForbiddenEntryPoint;
import com.smbc.library.rent_service.security.handler.UnauthorizedEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final JwtFilter jwtFilter;
	private final UnauthorizedEntryPoint unauthorizedEntryPoint;
	private final ForbiddenEntryPoint forbiddenEntryPoint;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(
						auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyRequest()
								.authenticated())
				.exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedEntryPoint)
						.accessDeniedHandler(forbiddenEntryPoint))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}
}
