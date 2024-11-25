package LacunaMatata.Lacuna.entity.user;

import LacunaMatata.Lacuna.security.principal.PrincipalUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/************************************
 * version: 1.0.2                   *
 * author: 손경태                    *
 * description: User                *
 * createDate: 2024-10-10           *
 * updateDate: 2024-10-10           *
 ***********************************/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private int userId;
    private String username;
    private String email;
    private String password;
    private String name;
    private int socialLoginType;
    private LocalDateTime passwordLastChanged;
    private LocalDateTime lastLoginDate;
    private String authenticationCode;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    private int roleId;
    private String roleName;
    private int totalCount;
    private String inactive;
    private String loginIp;
    private int maxRoleId;

    // 매핑 목적
    private Set<UserRoleMet> userRoleMets;
    private UserOptionalInfo userOptionalInfo;
    private LoginHistory loginHistory;

    public PrincipalUser toPrincipal() {
        return PrincipalUser.builder()
                .id(userId)
                .username(username)
                .password(password)
                .userRoleMets(userRoleMets)
                .build();
    }
}
