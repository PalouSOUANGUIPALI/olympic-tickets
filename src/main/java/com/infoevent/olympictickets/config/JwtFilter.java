package com.infoevent.olympictickets.config;


import com.infoevent.olympictickets.entity.Jwt;
import com.infoevent.olympictickets.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtService jwtService;

    public JwtFilter(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,  HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token;
        Jwt tokenDansLaBDD = null;
        String username = null;
        boolean isTokenExpired = true;


        final String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer ")){
            token = authorization.substring(7);
            tokenDansLaBDD = this.jwtService.tokenByValue(token);
            isTokenExpired = jwtService.isTokenExpired(token);
            username = jwtService.extractUsername(token);
        }

        if(
                !isTokenExpired
                        && tokenDansLaBDD.getUser().getEmail().equals(username)
                        && SecurityContextHolder.getContext().getAuthentication() == null
        ) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
