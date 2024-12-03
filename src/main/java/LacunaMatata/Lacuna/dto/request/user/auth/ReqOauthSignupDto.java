package LacunaMatata.Lacuna.dto.request.user.auth;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ReqOauthSignupDto {
    // 직접 입력받을 내용
    private String username;
    private String password;
    private String checkPassword;
    private String name;
    private Date birthDate;
    private String phoneNumber;
    private int gender;
    private String address;
    private int marketingReceiveAgreement;
    private int thirdPartyInfoSharingAgreement;
    private int useConditionAgreement;

    // oauth2로부터 받을 내용
    private String email;
    private String socialId; // oauth2 클라이언트 id 받을 곳
    private String provider; // oauth2 제공사
}
