package air.astana.authservice.service.impl;

import air.astana.authservice.exceptions.GlobalException;
import air.astana.authservice.model.dto.UserDto;
import air.astana.authservice.model.entity.User;
import air.astana.authservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserService userService;

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
    public void getUserByUsername_UserExists() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        UserDto userDto = new UserDto();
        userDto.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(mapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.getUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository).findByUsername(username);
        verify(mapper).map(user, UserDto.class);
    }

    @Test
    public void getUserByUsername_UserNotFound() {
        String username = "testuser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        GlobalException thrown = assertThrows(GlobalException.class, () -> {
            userService.getUserByUsername(username);
        });

        assertEquals("User not found by username", thrown.getMessage());
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void checkCredentials_UserFound_ValidPassword() {
        String username = "testuser";
        String rawPassword = "testpassword";
        String encodedPassword = "$2a$10$z7T/nB7B/W2iE0Skt7uXxuBp0qJFpz0B6H7F/qe0fRlzYdN6Kjkdi";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(encoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        boolean result = userService.checkCredentials(username, rawPassword);

        assertTrue(result);
        verify(userRepository).findByUsername(username);
        verify(encoder).matches(rawPassword, encodedPassword);
    }

    @Test
    public void checkCredentials_UserFound_InvalidPassword() {
        String username = "testuser";
        String rawPassword = "testpassword";
        String encodedPassword = "$2a$10$z7T/nB7B/W2iE0Skt7uXxuBp0qJFpz0B6H7F/qe0fRlzYdN6Kjkdi";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(encoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        boolean result = userService.checkCredentials(username, rawPassword);

        assertFalse(result);
        verify(userRepository).findByUsername(username);
        verify(encoder).matches(rawPassword, encodedPassword);
    }

    @Test
    public void checkCredentials_UserNotFound() {
        String username = "testuser";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        boolean result = userService.checkCredentials(username, password);

        assertFalse(result);
        verify(userRepository).findByUsername(username);
        verify(encoder, never()).matches(anyString(), anyString());
    }
}
