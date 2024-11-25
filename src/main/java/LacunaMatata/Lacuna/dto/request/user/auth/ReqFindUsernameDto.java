package LacunaMatata.Lacuna.dto.request.user.auth;

import lombok.Data;

import java.util.Date;

@Data
public class ReqFindUsernameDto {
    private String name;
    private String birth;
    private String email;
}
