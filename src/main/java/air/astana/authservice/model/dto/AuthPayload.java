package air.astana.authservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthPayload {
    private Integer id;
    private String username;
    private RoleDto role;
    private String access_token;
}
