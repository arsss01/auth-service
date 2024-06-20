package air.astana.auth_service.config.security;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
