package LacunaMatata.Lacuna.dto.response.user.auth;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RespFindUsernameDto {
    private String username;
    private String email;
    private String phone;
}
