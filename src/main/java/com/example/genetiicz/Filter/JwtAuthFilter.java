package com.example.genetiicz.Filter;
import com.example.genetiicz.Controller.AuthController;
import com.example.genetiicz.Service.JwtService;
import com.example.genetiicz.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthFilter(HandlerExceptionResolver handlerExceptionResolver, JwtService jwtService, UserService userService) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
        this.userService = userService;
    }


    /*
    So this video helped me a lot: https://www.youtube.com/watch?v=uZGuwX3St_c

     */

    // TODO: CRITICAL - invalid JWT tokens are swallowed silently and request passes through
    // Fix: throw 401 not Authenticated when token is invalid instead of allowing request to continue, found this by entering three integeres or characters,
    // but when the bearer token is empty,the server is rejecting as it should - but it should reject each requests that doesn't store THE WHOLE JWT token.

    @Override
    protected void doFilterInternal (@NotNull HttpServletRequest request,
                                      @NotNull HttpServletResponse response,
                                      @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userService.loadUserByUsername(userEmail); //Userservice implements UserDetailsService so we say instead that this value should be on userService.

                if(jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken userAuthToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    userAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(userAuthToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token, please log in again");
        }
    }
}
