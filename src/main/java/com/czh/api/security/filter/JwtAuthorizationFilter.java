package com.czh.api.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import com.czh.api.security.config.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.AntPathRequestMatcherProvider;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.czh.api.security.JwtAuthorizationManager;
import com.czh.api.security.authentication.JwtAuthorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    JwtAuthorizationManager jwtAuthorizationManager;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("jwt auth  -->" + request.getRequestURI());

        AntPathRequestMatcherProvider provider = new AntPathRequestMatcherProvider(s -> s);

        for (String pattern : SecurityConfiguration.writeList) {
            RequestMatcher requestMatcher = provider.getRequestMatcher(pattern);
            if (requestMatcher.matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String jwttokenString = request.getHeader("Authorization");
        if (jwttokenString != null && jwttokenString.startsWith("Bearer ")) {
            jwttokenString = jwttokenString.replace("Bearer ", "");
            JwtAuthorization jwtAuthorization = new JwtAuthorization(jwttokenString);
            jwtAuthorizationManager.verify(() -> jwtAuthorization, null);
        }
        filterChain.doFilter(request, response);
    }
}
