package LacunaMatata.Lacuna.dto.request.user.auth;

import lombok.Data;

@Data
public class ReqChangeNewPasswordDto {
    private String username;
    private String password;
    private String passwordCheck;
}
