package air.astana.authservice.service.impl;

import air.astana.authservice.config.security.jwt.JwtUtils;
import air.astana.authservice.exceptions.GlobalException;
import air.astana.authservice.model.dto.request.AuthDto;
import air.astana.authservice.model.dto.response.AuthPayload;
import air.astana.authservice.model.entity.User;
import air.astana.authservice.repository.UserRepository;
import air.astana.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper mapper;

    public void registration(AuthDto authDto) {
        if (authDto.getUsername() != null) {
            if (userRepository.existsByUsername(authDto.getUsername())) {
                throw new GlobalException("Введенный вами username уже зарегистрирован. Войдите в систему", Map.of("username", authDto.getUsername()));
            }
            String passwordEncoder = encoder.encode(authDto.getPassword());
            User user = mapper.map(authDto, User.class);
            user.setPassword(passwordEncoder);
            userRepository.save(user);
        }
    }

    @Override
    public AuthPayload loginByUsername(AuthDto authDto) {
        AuthDto auth = userService.getUserByUsername(authDto.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDto.getUsername(), authDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return generateTokens(auth);
    }

    private AuthPayload generateTokens(AuthDto auth) {
        String access_jwt = jwtUtils.generateAccessToken(auth);

        return AuthPayload.builder()
                .id(auth.getId())
                .username(auth.getUsername())
                .role(auth.getRole())
                .access_token(access_jwt)
                .build();
    }
}
