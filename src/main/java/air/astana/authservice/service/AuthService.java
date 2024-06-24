package air.astana.authservice.service;

import air.astana.authservice.model.dto.AuthRequestDto;
import air.astana.authservice.model.dto.AuthPayload;

public interface AuthService {
    void registration(AuthRequestDto authDto);

    AuthPayload loginByUsername(AuthRequestDto authDto);
}
