package LacunaMatata.Lacuna.dto.request.user.auth;

import lombok.Data;

import java.util.Date;

@Data
public class ReqGeneralSignupDto {
    private String username;
    private String password;
    private String passwordCheck;
    private Date birthDate;
    private String email;
    private String name;
    private int gender;
    private String phoneNumber;
    private String address;
    private int marketingReceiveAgreement;
    private int thirdPartyInfoSharingAgreement;
    private int useConditionAgreement;
}
