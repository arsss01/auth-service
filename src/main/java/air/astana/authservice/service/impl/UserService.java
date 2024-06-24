package air.astana.authservice.service.impl;

import air.astana.authservice.exceptions.GlobalException;
import air.astana.authservice.model.dto.UserDto;
import air.astana.authservice.model.entity.User;
import air.astana.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        return (UserDetails) getUserByUsername(username);
    }

    public UserDto getUserByUsername(String username) {
        log.info("Getting user by username: {}", username);
        return mapper.map(userRepository.findByUsername(username)
                        .orElseThrow(() -> new GlobalException("User not found by username", Map.of("username", username))),
                UserDto.class);
    }

    public boolean checkCredentials(String username, String password) {
        log.info("Credentials matching is started");

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.error("User is not found");
            return false;
        }

        String encodedPassword = user.get().getPassword();

        log.info("Process of comparing a password with an encrypted password from a database");
        return encoder.matches(password, encodedPassword);
    }
}
