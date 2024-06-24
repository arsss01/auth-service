package air.astana.authservice.config.security;

import air.astana.authservice.exceptions.UnauthorizedException;
import air.astana.authservice.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        Object credentials = authentication.getCredentials();

        if (credentials == null || credentials.toString().isEmpty()) {
            return new UsernamePasswordAuthenticationToken(username, null);
        } else {
            String password = credentials.toString();
            boolean authenticationSuccess = userService.checkCredentials(username, password);
            if (authenticationSuccess) {
                return new UsernamePasswordAuthenticationToken(username, password);
            } else {
                throw new UnauthorizedException("Не верно введен пароль");
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
