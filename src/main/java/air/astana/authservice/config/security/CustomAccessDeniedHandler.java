package air.astana.authservice.config.security;

import air.astana.authservice.exceptions.ApiError;
import air.astana.authservice.model.dto.request.AuthDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            AuthDto jwtUser = (AuthDto) auth.getPrincipal();
            log.warn("User: {} attempted to access protected URL: {}", jwtUser.getUsername(), request.getRequestURI());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ApiError errorResponse = new ApiError(
                HttpStatus.FORBIDDEN,
                "Access denied!",
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
