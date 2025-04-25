package com.smbc.library.rent_service.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.smbc.library.rent_service.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            log.debug("jwt token: {}", jwt);
            Object userLogin = jwtUtil.claimValue(jwt, "userLogin");

            if (userLogin instanceof Map<?, ?> userLoginMap) {
                Object username = userLoginMap.get("username");
                Object userId = userLoginMap.get("userId");
                Object memberId = userLoginMap.get("memberId");

                if (username != null) {
                    request.setAttribute("username", username.toString());
                    request.setAttribute("memberId", memberId.toString());
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username.toString(), null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

                if (userId != null) {
                    request.setAttribute("userId", userId.toString());
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
