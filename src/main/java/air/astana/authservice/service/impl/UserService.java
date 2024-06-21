package air.astana.authservice.service.impl;

import air.astana.authservice.exceptions.GlobalException;
import air.astana.authservice.model.dto.request.AuthDto;
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
        return (UserDetails) userRepository.findByUsername(username)
                .orElseThrow(() -> new GlobalException("User not found by username", Map.of("username", username))
                );
    }

    public AuthDto getUserByUsername(String username) {
        return mapper.map(userRepository.findByUsername(username)
                        .orElseThrow(() -> new GlobalException("User not found by username", Map.of("username", username))),
                AuthDto.class);
    }

    public boolean checkCredentials(String username, String password) {
        // Получаем зашифрованный пароль из базы данных по имени пользователя
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return false; // Пользователь не найден
        }

        String encodedPassword = user.get().getPassword();

        // Сравниваем введенный пароль с зашифрованным паролем из базы данных
        return encoder.matches(password, encodedPassword);
    }
}
