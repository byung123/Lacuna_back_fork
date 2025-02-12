package LacunaMatata.Lacuna.service;

import LacunaMatata.Lacuna.dto.request.user.auth.*;
import LacunaMatata.Lacuna.dto.response.user.auth.RespAgreementInfoDto;
import LacunaMatata.Lacuna.dto.response.user.auth.RespFindUsernameDto;
import LacunaMatata.Lacuna.entity.Setting;
import LacunaMatata.Lacuna.entity.user.*;
import LacunaMatata.Lacuna.exception.InactiveAccountException;
import LacunaMatata.Lacuna.exception.auth.*;
import LacunaMatata.Lacuna.repository.user.UserMapper;
import LacunaMatata.Lacuna.security.ip.IpUtils;
import LacunaMatata.Lacuna.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;


/************************************
 * version: 1.0.5                   *
 * author: 손경태                    *
 * description: AuthService         *
 * createDate: 2024-10-17           *
 * updateDate: 2024-10-21           *
 ***********************************/
@Service
public class AuthService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired private IpUtils ipUtils;

    @Autowired private JwtProvider jwtProvider;

    @Autowired private UserMapper userMapper;

    @Autowired private BCryptPasswordEncoder passwordEncoder;

    // 일반 회원 가입
    @Transactional(rollbackFor = Exception.class)
    public void signup(ReqGeneralSignupDto dto) throws Exception {

        try {
            User user = User.builder()
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .name(dto.getName())
                    .socialLoginType(1)
                    .build();
            userMapper.saveUser(user);

            UserOptionalInfo userOptionalInfo = UserOptionalInfo.builder()
                    .userId(user.getUserId())
                    .birthDate(dto.getBirthDate())
                    .gender(dto.getGender())
                    .phoneNumber(dto.getPhoneNumber())
                    .address(dto.getAddress())
                    .marketingReceiveAgreement(dto.getMarketingReceiveAgreement())
                    .thirdPartyInfoSharingAgreement(dto.getThirdPartyInfoSharingAgreement())
                    .useConditionAgreement(dto.getUseConditionAgreement())
                    .build();
            userMapper.saveUserOptionalInfo(userOptionalInfo);

            List<Integer> roleIdList = new ArrayList<>();
            roleIdList.add(1);
            roleIdList.add(2);
            Map<String, Object> params = Map.of(
                    "userId", user.getUserId(),
                    "roleIdList", roleIdList
            );
            userMapper.saveUserRoleMet(params);

            EmailAuthentication emailAuthentication = userMapper.findAuthenticationCodeByEmail(dto.getEmail());
            if(!emailAuthentication.getEmail().equals(dto.getEmail())) {
                throw new Exception("인증받은 이메일주소와 회원가입 시의 이메일 주소가 다릅니다. 인증받은 이메일 주소를 기입해주세요.");
            }

            userMapper.deleteEmailAuthentication(dto.getEmail());

        } catch (Exception e) {
            throw new Exception("회원가입 도중 오류가 발생했습니다.");
        }
    }

    // 일반 로그인
    @Transactional(rollbackFor = Exception.class)
    public String signin(HttpServletRequest request, ReqGeneralSigninDto dto) throws Exception {
        User user = userMapper.findUserByUsername(dto.getUsername());

        // 계정 비활성화인 경우
        if(user.getLastLoginDate().isBefore(LocalDateTime.now().minusYears(1))) {
            throw new InactiveAccountException("휴면계정입니다. 이메일 인증으로 휴면 계정을 복구하시고 이용해주세요.");
        }

        try {
            // 토큰 가져오기
            int roleId = user.getUserRoleMets().stream().map(ur -> ur.getRoleId())
                    .max(Comparator.naturalOrder()).orElse(2);
            String roleName = userMapper.findUserRoleByRoleId(roleId).getRoleName();

            String accessToken = jwtProvider.generateAccessToken(user.getUserId(), roleName);

            // ip 가져오기
            String loginIp = IpUtils.getClientIp(request);
            // 로그인 정보에 로그인 시간과 ip 저장하기
            LoginHistory loginHistory = LoginHistory.builder()
                    .loginUserId(user.getUserId())
                    .loginIp(loginIp)
                    .build();
            userMapper.saveLoginHistory(loginHistory);

            return accessToken;
        } catch (Exception e) {
            throw new Exception("로그인 도중 오류가 발생하였습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    // 오어스 회원가입
    @Transactional(rollbackFor = Exception.class)
    public void oauthSignup(ReqOauthSignupDto dto) throws Exception {

        try {
            User originUser = userMapper.findUserByEmail(dto.getEmail());

            // 오어스 계정이 없는 경우 바로 회원가입 시키기
            if(originUser == null) {
                User user = User.builder()
                        .username(dto.getUsername())
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .name(dto.getName())
                        .socialLoginType(2)
                        .build();
                userMapper.saveUser(user);

                UserOptionalInfo userOptionalInfo = UserOptionalInfo.builder()
                        .userId(user.getUserId())
                        .birthDate(dto.getBirthDate())
                        .gender(dto.getGender())
                        .phoneNumber(dto.getPhoneNumber())
                        .address(dto.getAddress())
                        .marketingReceiveAgreement(dto.getMarketingReceiveAgreement())
                        .thirdPartyInfoSharingAgreement(dto.getThirdPartyInfoSharingAgreement())
                        .useConditionAgreement(dto.getUseConditionAgreement())
                        .build();
                userMapper.saveUserOptionalInfo(userOptionalInfo);

                SocialLogin socialLogin = SocialLogin.builder()
                        .socialUserId(user.getUserId())
                        .socialId(dto.getSocialId())
                        .provider(dto.getProvider())
                        .build();
                userMapper.saveOauthInfo(socialLogin);

                List<Integer> roleIdList = new ArrayList<>();
                roleIdList.add(1);
                roleIdList.add(2);
                Map<String, Object> params = Map.of(
                        "userId", user.getUserId(),
                        "roleIdList", roleIdList
                );
                userMapper.saveUserRoleMet(params);
            }

            // 기존 회원가입을 오어스로 하지 않았는데 이메일을 오어스로 등록해놨을 경우 통합 회원가입 처리하기
            if(originUser != null && originUser.getSocialLoginType() == 1) {
                userMapper.modifySocialLoginType(originUser.getUserId());
                SocialLogin socialLogin = SocialLogin.builder()
                        .socialUserId(originUser.getUserId())
                        .socialId(dto.getSocialId())
                        .provider(dto.getProvider())
                        .build();
                userMapper.saveOauthInfo(socialLogin);
            }

            if(originUser != null && originUser.getSocialLoginType() == 2) {
                throw new ExistSocialLoginInfoException("해당 소셜 아이디는 이미 가입된 계정입니다. 소셜 계정으로 바로 로그인 부탁드립니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("회원가입 도중 오류가 발생하였습니다. 잠시 후 이용 부탁드립니다. (서버 오류)");
        }
    }

    // 회원가입할 때 이용약관, 마케팅 정보 불러오기
    public List<RespAgreementInfoDto> getAgreementInfo() throws Exception {
        try {
            List<Setting> agreementInfo = userMapper.getAgreementInfoList();
            List<RespAgreementInfoDto> agreementInfoList = new ArrayList<>();

            for(int i = 0; i < agreementInfo.size(); i++) {
                RespAgreementInfoDto agreement = RespAgreementInfoDto.builder()
                        .settingId(agreementInfo.get(i).getSettingId())
                        .dataType(agreementInfo.get(i).getDataType())
                        .value(agreementInfo.get(i).getValue())
                        .build();
                agreementInfoList.add(agreement);
            }
            return agreementInfoList;
        } catch (Exception e) {
            throw new Exception("약관 정보를 불러오는 도중 서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }

    }

    // username 중복 되는 지 검사 -> AuthAspect로 들어감
    public Boolean isDuplicateUsername(String username) {
        User user = userMapper.findUserByUsername(username);

        if(user == null) {
            return false;
        }
        return true;
    }

    // username의 비밃번호를 틀렸을 때 검사 -> AuthAspect로 들어감
    public Boolean isDifferentPassword(ReqGeneralSigninDto dto) {
        User user = userMapper.findUserByUsername(dto.getUsername());

        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return true;
        }
        return false;
    }

    // email 중복되는지 검사 -> AuthAspect로 들어감
    public Boolean isDuplicateEmail(String email) {
        User user = userMapper.findUserByEmail(email);

        if(user == null) {
            return false;
        }
        return true;
    }

    public Boolean sendAuthEmail(ReqAuthEmailDto dto) throws Exception {
        String toEmail = dto.getToEmail();
        if(isDuplicateEmail(toEmail)) {
            throw new Exception("해당 이메일 주소는 이미 등록된 이메일입니다. 다른 이메일 주소를 입력해주세요.");
        }

        StringBuilder code = generateAuthenticationCode(6);
        String authenticationCode = code.toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        try {
            Map<String, Object> params = Map.of(
                    "email", toEmail,
                    "verificationCode", authenticationCode,
                    "expirationTime", expirationTime
            );
            userMapper.saveEmailAuthentication(params);
        } catch (Exception e) {
            throw new Exception("이메일 인증 과정 중 오류가 발생했습니다. 잠시후 다시 시도해주세요.");
        }

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<div style='display:flex;justify-content:center;align-items:center;flex-direction:column;width:400px'>");
        htmlContent.append("<h2>Lacuna 회원가입 이메일 인증 입니다.</h2>");
        htmlContent.append("<h3>아래 인증번호를 이메일 인증번호 입력란에 기입해주시길 바랍니다.</h3>");
        htmlContent.append("<h3>");
        htmlContent.append(authenticationCode);
        htmlContent.append("</h3>");
        htmlContent.append("</div>");

        return  send(toEmail, "Lacuna 회원가입 이메일 인증 ", htmlContent.toString());
    }

    public Boolean emailAuthentication(ReqEmailAuthenticationDto dto) throws Exception {
        String email = dto.getEmail();
        EmailAuthentication emailAuthentication = userMapper.findAuthenticationCodeByEmail(email);

        if(emailAuthentication.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new Exception("이메일 인증 시간이 만료되었습니다. 다시 시도해주세요.");
        }

        if(!emailAuthentication.getVerificationCode().equals(dto.getAuthenticationCode())) {
            throw new Exception("인증코드가 일치하지 않습니다. 다시 확인해주세요.");
        }

        try {
            userMapper.modifyEmailVerified(email);
        } catch (Exception e) {
            throw new Exception("이메일 인증 과정 중 오류가 발생했습니다. 다시 시도 부탁드립니다.");
        }

        return true;
    }

//    public String validToken(String emailValidtoken) throws Exception {
//        try {
//            // 만료시간이 지나면 못 꺼낼 것임 -> 지나명 validFail 리턴
//            jwtProvider.getClaim(emailValidtoken);
//
//            // 시간이 유효하면 success 리턴
//            return "success";
//        } catch (Exception e) {
//            return "validFail";
//        }
//    }

    // 아이디 찾기
    public RespFindUsernameDto findUsername(ReqFindUsernameDto dto) throws EmailNotFoundException {
        String toEmail = dto.getEmail();
        Map<String, Object> params = Map.of(
            "email", toEmail,
            "birth", dto.getBirth(),
            "name", dto.getName()
        );

        User user = userMapper.findUserByNameEmailBirth(params);
        if(user == null) {
            throw new EmailNotFoundException("입력하신 정보와 일치하는 사용자를 찾을 수 없습니다. 입력하신 정보를 확인해주세요");
        }

        List<Setting> adminEmailAndPhone = userMapper.getAdminEmailAndPhone();

        String adminEmail = adminEmailAndPhone.stream().filter(setting ->
                setting.getDataType().equals("Email")).map(Setting::getValue).findFirst().orElse(null);
        String adminPhone = adminEmailAndPhone.stream().filter(setting ->
                setting.getDataType().equals("Phone")).map(Setting::getValue).findFirst().orElse(null);

        String username = user.getUsername();
        String maskingUsername = maskingInfo(username);

        RespFindUsernameDto  respFindUsernameDto = RespFindUsernameDto.builder()
                .username(maskingUsername)
                .email(adminEmail)
                .phone(adminPhone)
                .build();

        return respFindUsernameDto;
    }

    // 비밀번호 찾기1 - 인증코드 보내기
    @Transactional(rollbackFor = Exception.class)
    public void findPassword(ReqFindPasswordDto dto) throws Exception {
        String toEmail = dto.getEmail();
        Map<String, Object> params = Map.of(
            "username", dto.getUsername(),
            "email", toEmail
        );
        User user = userMapper.findUserByUsernameEmail(params);
        if(user == null) {
            throw new UsernameNotFoundException("해당 계정의 정보를 찾을 수 없습니다. 입력하신 정보를 다시 한 번 확인하세요.");
        }

        StringBuilder code = generateAuthenticationCode(10);
        String authenticationCode = code.toString();
        Map<String, Object> params2 = Map.of(
            "userId", user.getUserId(),
            "authenticationCode", authenticationCode
        );

        try {
            userMapper.updateAuthenticationCode(params2);
        } catch (Exception e) {
            throw new Exception("인증 과정 중 오류가 발생했습니다. 다시 시도 부탁드립니다.");
        }


        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<div style='display:flex;justify-content:center;align-items:center;flex-direction:column;"
                + "width:400px'>");
        htmlContent.append("<h2>Lacuna 사용자 인증코드 안내</h2>");
        htmlContent.append("<h3>회원님의 임시 인증코드는");
        htmlContent.append(authenticationCode);
        htmlContent.append("입니다.</h3>");
        htmlContent.append("</div>");

        send(toEmail, "Lacuna 비밀번호 찾기 인증코드", htmlContent.toString());
    }

    // 비밀번호 찾기2 - 인증코드 확인
    public Boolean checkAuthenticationCode(ReqCheckAuthenticationDto dto) {
        User user = userMapper.checkAuthenticationCode(dto.getUsername());
        if(user == null) {
            throw new UsernameNotFoundException("입력하신 계정의 정보가 없습니다. 다시 한 번 확인 부탁드립니다.");
        }

        if(!user.getAuthenticationCode().equals(dto.getAuthenticationCode())) {
            throw new NotMatchAuthenticationException("인증번호 불일치");
        }

        return true;
    }

    // 비밀번호 찾기3 - 새 비밀번호로 저장하기
    @Transactional(rollbackFor = Exception.class)
    public void changeNewPassword(ReqChangeNewPasswordDto dto) throws Exception {
        String username = dto.getUsername();
        String password = dto.getPassword();
        User user = userMapper.findUserByUsername(username);
        if(passwordEncoder.matches(password, user.getPassword())) {
            throw new IsPresentPasswordException("현재 사용하고 있는 비밀번호와 일치합니다. 새로운 비밀번호를 입력 바랍니다.");
        }

        if(!password.equals(dto.getPasswordCheck())) {
            throw new NotMatchPasswordCheckException("비밀번호가 일치하지 않습니다. 다시 확인 부탁드립니다.");
        }

        String encoingPassword = passwordEncoder.encode(password);

        Map<String, Object> params = Map.of(
            "username", username,
            "password", encoingPassword
        );
        try {
            userMapper.modifyNewPassword(params);
            PasswordHistory passwordHistory = PasswordHistory.builder()
                    .historyUserId(user.getUserId())
                    .password(encoingPassword)
                    .build();
            userMapper.savePasswordHistory(passwordHistory);
        } catch (Exception e) {
            throw new Exception("비밀번호를 바꾸는 도중 서버에서 오류가 발생했습니다. 잠시 후에 이용해주시길 바랍니다.");
        }
    }

    private String maskingInfo(String info) {
        if (info == null || info.length() < 3) {
            // 너무 짧은 ID는 마스킹하지 않음
            return info;
        }

        int length = info.length();
        int start = length / 3; // ID 길이의 1/3 지점
        int end = start + 2;   // 마스킹할 2글자

        // ID가 짧으면 끝까지 마스킹
        if (end > length) {
            end = length;
        }

        // 마스킹 처리
        String maskedInfo = info.substring(0, start)
                + "*".repeat(end - start)
                + info.substring(end);

        return maskedInfo;
    }

    private StringBuilder generateAuthenticationCode(int passwordLength) {
        // 임시 비밀번호 생성
        String tempCharacter = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom randomNumber = new SecureRandom();
        StringBuilder tempCode = new StringBuilder();

        for(int i = 0; i < passwordLength; i++) {
            int index = randomNumber.nextInt(tempCharacter.length());
            tempCode.append(tempCharacter.charAt(index));
        }

        return tempCode;
    }

    //
    public Boolean send(String toEmail, String subject, String htmlContent) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            message.setText(htmlContent, "utf-8", "html");

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new FailSendEmailException("이메일 전송 도중 오류가 발생했습니다. 다시 시도해주세요.");
        }

        return true;
    }

    // 인증 성공 화면
    public String successView() {
        StringBuilder sb = new StringBuilder();

        sb.append("<html>");
        sb.append("<body>");
        sb.append("<script>");
        sb.append("alert('인증이 완료되었습니다');");
        sb.append("</script>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

    // 인증 실패 화면
    public String errorView(String message) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html>");
        sb.append("<body>");
        sb.append("<div style=\"text-align:center;\">");
        sb.append("<h2>");
        sb.append(message);
        sb.append("</h2>");
        // onclick 소문자로 해야함
        sb.append("<button onclick='window.close()'>닫기</button>");
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }
}
