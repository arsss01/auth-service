package air.astana.authservice.model.dto.request;

import air.astana.authservice.model.dto.RoleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto {
    private Integer id;
    private String username;
    private String password;
    private RoleDto role;
}
