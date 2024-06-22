package air.astana.authservice.service.impl;

import air.astana.authservice.config.security.jwt.JwtUtils;
import air.astana.authservice.exceptions.GlobalException;
import air.astana.authservice.model.RoleCode;
import air.astana.authservice.model.dto.UserDto;
import air.astana.authservice.model.dto.request.AuthDto;
import air.astana.authservice.model.dto.response.AuthPayload;
import air.astana.authservice.model.entity.User;
import air.astana.authservice.repository.RoleRepository;
import air.astana.authservice.repository.UserRepository;
import air.astana.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper mapper;

    public void registration(AuthDto authDto) {
        log.info("Registration process is started");
        if (authDto.getUsername() != null) {
            if (userRepository.existsByUsername(authDto.getUsername())) {
                throw new GlobalException("Введенный вами username уже зарегистрирован. Войдите в систему", Map.of("username", authDto.getUsername()));
            }
            String passwordEncoder = encoder.encode(authDto.getPassword());
            User user = mapper.map(authDto, User.class);
            user.setPassword(passwordEncoder);
            user.setRole(roleRepository.findByCode(RoleCode.USER)
                    .orElseThrow(() -> new GlobalException("Роль USER не найдена")));
            userRepository.save(user);
            log.info("Registration process finished successfully");
        }
    }

    @Override
    public AuthPayload loginByUsername(AuthDto authDto) {
        log.info("Login process is started");
        UserDto userDto = userService.getUserByUsername(authDto.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDto.getUsername(), authDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return generateTokens(userDto);
    }

    private AuthPayload generateTokens(UserDto userDto) {
        String access_jwt = jwtUtils.generateAccessToken(userDto);
        log.info("Login process finished successfully");
        return AuthPayload.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .role(userDto.getRole())
                .access_token(access_jwt)
                .build();
    }
}
