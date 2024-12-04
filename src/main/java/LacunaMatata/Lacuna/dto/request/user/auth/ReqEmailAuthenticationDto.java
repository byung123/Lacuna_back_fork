package LacunaMatata.Lacuna.dto.request.user.auth;

import lombok.Data;

@Data
public class ReqEmailAuthenticationDto {
    private String email;
    private String authenticationCode;
}
