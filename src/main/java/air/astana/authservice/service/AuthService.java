package air.astana.authservice.service;

import air.astana.authservice.model.dto.request.AuthDto;
import air.astana.authservice.model.dto.response.AuthPayload;

public interface AuthService {

     void registration(AuthDto authDto);

     AuthPayload loginByUsername(AuthDto authDto);
}
