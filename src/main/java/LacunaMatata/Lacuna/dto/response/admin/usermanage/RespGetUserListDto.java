package LacunaMatata.Lacuna.dto.response.admin.usermanage;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Data
public class RespGetUserListDto {
    private int userId;
    private String roleName;
    private String username;
    private String password;
    private String name;
    private int gender;
    private Date birthDate;
    private LocalDateTime createdDate;
    private LocalDateTime loginTime;
    private String loginIp;
    private String email;
    private String inactive; // 휴면 계정 여부
}