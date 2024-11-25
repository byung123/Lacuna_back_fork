package LacunaMatata.Lacuna.dto.request.user.auth;

import lombok.Data;

@Data
public class ReqCheckAuthenticationDto {
    private String username;
    private String authenticationCode;
}
