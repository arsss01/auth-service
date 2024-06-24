package air.astana.authservice.service.impl;

import air.astana.authservice.config.security.jwt.JwtUtils;
import air.astana.authservice.exceptions.GlobalException;
import air.astana.authservice.model.RoleCode;
import air.astana.authservice.model.dto.AuthPayload;
import air.astana.authservice.model.dto.AuthRequestDto;
import air.astana.authservice.model.dto.RoleDto;
import air.astana.authservice.model.dto.UserDto;
import air.astana.authservice.model.entity.Role;
import air.astana.authservice.model.entity.User;
import air.astana.authservice.repository.RoleRepository;
import air.astana.authservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void registration_Successful() {
        AuthRequestDto authDto = new AuthRequestDto("testuser", "password");

        Role role = new Role(1, RoleCode.USER, null);

        User user = new User();
        user.setUsername(authDto.getUsername());
        user.setPassword(authDto.getPassword());
        user.setRole(role);

        when(userRepository.existsByUsername(authDto.getUsername())).thenReturn(false);
        when(encoder.encode(authDto.getPassword())).thenReturn("encodedPassword");
        when(mapper.map(authDto, User.class)).thenReturn(user);
        when(roleRepository.findByCode(RoleCode.USER)).thenReturn(Optional.of(role));

        authServiceImpl.registration(authDto);

        verify(userRepository).save(user);
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    public void registration_UsernameAlreadyExists() {
        AuthRequestDto authDto = new AuthRequestDto("testuser", "password");

        when(userRepository.existsByUsername(authDto.getUsername())).thenReturn(true);

        GlobalException thrown = assertThrows(GlobalException.class, () -> {
            authServiceImpl.registration(authDto);
        });

        assertEquals("Введенный вами username уже зарегистрирован. Войдите в систему", thrown.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void loginByUsername_Successful() {
        AuthRequestDto authDto = new AuthRequestDto("testuser", "password");

        RoleDto roleDto = new RoleDto(1, RoleCode.USER);

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setRole(roleDto);
        userDto.setUsername(authDto.getUsername());

        when(userService.getUserByUsername(authDto.getUsername())).thenReturn(userDto);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(jwtUtils.generateAccessToken(userDto)).thenReturn("accessToken");

        AuthPayload authPayload = authServiceImpl.loginByUsername(authDto);

        assertEquals("accessToken", authPayload.getAccess_token());
        assertEquals(userDto.getUsername(), authPayload.getUsername());
        verify(jwtUtils).generateAccessToken(userDto);
    }

    @Test
    public void loginByUsername_InvalidCredentials() {
        AuthRequestDto authDto = new AuthRequestDto("testuser", "wrongpassword");
        UserDto userDto = new UserDto();
        userDto.setUsername(authDto.getUsername());
        userDto.setId(1);
        userDto.setRole(new RoleDto());

        when(userService.getUserByUsername(authDto.getUsername())).thenReturn(userDto);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            authServiceImpl.loginByUsername(authDto);
        });

        assertEquals("Invalid credentials", thrown.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
