package LacunaMatata.Lacuna.dto.request.admin.usermanage;

import lombok.Data;

@Data
public class ReqModifyUserDto {
    private int userId;
    private int roleId;
    private String email;
    private String password;
    private String passwordCheck;
    private String phoneNumber;
}
