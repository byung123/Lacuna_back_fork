package LacunaMatata.Lacuna.dto.request.user.user;

import lombok.Data;

@Data
public class ReqPasswordChangeDto {
    private String password;
    private String checkPassword;
}
