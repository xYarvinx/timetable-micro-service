package com.example.timetablemicroservice.config;


import com.example.timetablemicroservice.dto.Error;
import com.example.timetablemicroservice.dto.ErrorResponse;
import com.example.timetablemicroservice.dto.TokenValidationResponse;
import com.example.timetablemicroservice.service.RabbitService;
import com.example.timetablemicroservice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;


@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final RabbitService rabbitService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = JwtUtil.extractToken(request);
        if (token != null) {
            try {
                TokenValidationResponse validationResponse = rabbitService.sendTokenValidationRequest(token);
                if (!validationResponse.isValid()) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
                Authentication authentication = new UsernamePasswordAuthenticationToken(validationResponse.getCorrelationId(),null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error occurred");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(Error.builder()
                        .message(message)
                        .build())
                .build();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

}
