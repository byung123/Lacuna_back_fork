package LacunaMatata.Lacuna.service.user;

import LacunaMatata.Lacuna.dto.request.user.user.*;
import LacunaMatata.Lacuna.dto.response.user.user.*;
import LacunaMatata.Lacuna.entity.mbti.MbtiResult;
import LacunaMatata.Lacuna.entity.order.Order;
import LacunaMatata.Lacuna.entity.user.PasswordHistory;
import LacunaMatata.Lacuna.entity.user.User;
import LacunaMatata.Lacuna.exception.auth.NotMatchPasswordCheckException;
import LacunaMatata.Lacuna.exception.auth.NotMatchPasswordException;
import LacunaMatata.Lacuna.exception.auth.TokenValidExpiredException;
import LacunaMatata.Lacuna.exception.user.NotFoundMyMbtiResultException;
import LacunaMatata.Lacuna.exception.user.NotFoundMyOrderInfoException;
import LacunaMatata.Lacuna.exception.user.NotFoundUserException;
import LacunaMatata.Lacuna.repository.user.UserMapper;
import LacunaMatata.Lacuna.security.jwt.JwtProvider;
import LacunaMatata.Lacuna.security.principal.PrincipalUser;
import LacunaMatata.Lacuna.service.AuthService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Value("${file.path}")
    private String filePath;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 프로필 정보 (헤더부분) 출력
    public RespMyProfileHeaderDto getMyProfileHeader() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // authentication이 null이거나, principal이 PrincipalUser 인스턴스가 아닐 경우 비회원으로 간주하고 null 반환
        if (authentication == null || !(authentication.getPrincipal() instanceof PrincipalUser)) {
            return null;
        }
        PrincipalUser principalUser
                = (PrincipalUser) authentication.getPrincipal();
        int userId = principalUser.getId();

        try {
            User user = userMapper.findUserByUserId(userId);
            RespMyProfileHeaderDto myProfileHeader = RespMyProfileHeaderDto.builder()
                    .name(user.getName())
                    .username(user.getUsername())
                    .roleName(user.getRoleName())
                    .profileImg(user.getUserOptionalInfo().getProfileImg())
                    .build();
            return myProfileHeader;
        } catch (Exception e) {
            throw new NotFoundUserException("로그인한 유저의 정보를 찾을 수 없습니다. 서버에 문의 해주세요.");
        }
    }

    // 프로필 페이지 출력 정보
    public RespMyProfileDto getMyProfile() {
        PrincipalUser principalUser
                = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new TokenValidExpiredException("로그인 후 이용바랍니다.");
        }
        int userId = principalUser.getId();

        User user = userMapper.findUserByUserId(userId);
        if(user == null) {
            throw new NotFoundUserException("로그인한 유저의 정보를 찾을 수 없습니다. 서버에 문의 해주세요.");
        }

        String kakaoUrl = userMapper.getKakaoAddress();

        RespMyProfileDto respMyProfileDto = RespMyProfileDto.builder()
                .name(user.getName())
                .phoneNumber(user.getUserOptionalInfo().getPhoneNumber())
                .email(user.getEmail())
                .profileImg(user.getUserOptionalInfo().getProfileImg())
                .marketingReceiveAgreement(user.getUserOptionalInfo().getMarketingReceiveAgreement())
                .kakaoAddress(kakaoUrl)
                .build();
        return respMyProfileDto;
    }

    // 프로필 페이지 - 프로필 이미지 변경
    public void changeMyProfileImg(ReqModifyProfileImgDto dto) throws Exception {
        PrincipalUser principalUser
                = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new TokenValidExpiredException("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        int userId = principalUser.getId();
        String profileImg = dto.getProfileImg();

        Map<String, Object> params = Map.of(
                "userId", userId,
                "profileImg", profileImg
        );
        try {
            userMapper.modifyMyProfileImg(params);
        } catch (Exception e) {
            throw new Exception("이미지 변경 중 오류가 발생했습니다. 다시 시도해주세요");
        }
    }

    // 프로필 페이지 - 비밀번호 변경1
    @Transactional(rollbackFor = Exception.class)
    public Boolean passwordCheck(ReqPasswordCheckDto dto) {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new TokenValidExpiredException("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        int userId = principalUser.getId();
        User user = userMapper.findUserByUserId(userId);

        if(!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new NotMatchPasswordException("현재 사용하고 있는 비밀번호가 일치하지 않습니다.");
        }
        return true;
    }

    // 프로필 페이지 - 비밀번호 변경2
    public void passwordChange(ReqPasswordChangeDto dto) throws Exception {
        if(!dto.getPassword().equals(dto.getCheckPassword())) {
            throw new NotMatchPasswordCheckException("비밀번호가 일치하지 않습니다.");
        }

        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new TokenValidExpiredException("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int userId = principalUser.getId();

        String modifyPassword = passwordEncoder.encode(dto.getPassword());

        try {
            userMapper.modifyPassword(userId, modifyPassword);

            PasswordHistory passwordHistory = PasswordHistory.builder()
                    .historyUserId(userId)
                    .password(modifyPassword)
                    .build();
            userMapper.savePasswordHistory(passwordHistory);
        } catch (Exception e) {
            throw new Exception("비밀번호 수정 도중 오류가 발생했습니다. 잠시후 다시 시도해주세요.");
        }
    }

    // 프로필 페이지 - 내 연락처 바꾸기
    public void changePhoneNumber(ReqChangePhoneNumberDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new TokenValidExpiredException("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        int userId = principalUser.getId();
        Map<String, Object> params = Map.of(
                "userId", userId,
                "phoneNumber", dto.getPhoneNumber()
        );

        try {
            userMapper.modifyPhoneNumber(params);
        } catch (Exception e) {
            throw new Exception("정보 수정 도중 오류가 발생했습니다. 잠시후 다시 시도해주세요.");
        }
    }

    // 프로필 페이지 - 내 이메일 주소 변경하기 (메일 인증)
    public Boolean changeMyEmail(ReqChangeMyEmailDto dto) throws Exception {
        String toEmail = dto.getEmail();

        if(userMapper.findUserByEmail(dto.getEmail()) == null) {
            throw new Exception("이미 존재하는 이메일입니다.");
        }

        String emailToken = jwtProvider.generateEmailValidToken(toEmail);
        String bearerToken = "Bearer ".concat(emailToken);

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<div style='display:flex;justify-content:center;align-items:center;flex-direction:column;"
                + "width:400px'>");
        htmlContent.append("<h2>Lacuna 메일 주소 변경 이메일 인증 입니다.</h2>");
        htmlContent.append("<h3>아래 인증하기 버튼을 클릭해주세요</h3>");
        htmlContent.append("<form action='http://localhost:8080/api/v1/user/change/email' method='POST'>");
        htmlContent.append("<input type='hidden' name='_method' value='PUT' />");
        htmlContent.append("<input type='hidden' name='emailToken' value='");
        htmlContent.append(bearerToken);
        htmlContent.append("' />"); // 토큰 값 전달
        htmlContent.append("<button type='submit' style='padding:10px 20px;background-color:blue;color:white;border:none;'>인증하기</button>");
        htmlContent.append("</form>");
        htmlContent.append("</div>");

        return authService.send(toEmail, "Lacuna 메일 주소 변경 이메일 인증 ", htmlContent.toString());
    }

    // 프로필 페이지 - 내 이메일 주소 변경하기 (수정)
    public void changeMyEmail2(ReqMyEmailTokenDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new TokenValidExpiredException("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        int userId = principalUser.getId();
        String emailToken = dto.getEmailToken();

        Claims claims = jwtProvider.getClaim(emailToken);
        String email = (String) claims.get("toEmail");
        Map<String, Object> params = Map.of(
                "userId", userId,
                "email", email
        );

        try {
            userMapper.modifyMyEmail(params);
        } catch (Exception e) {
            throw new Exception("이메일 정보 수정 도중 오류가 발생했습니다. 잠시후 다시 시도해주세요.");
        }
    }

    // 프로필 페이지 - 마케팅 동의 설정 바꾸기
    public void changeMarketingAgreement(ReqChangeMarketingDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new TokenValidExpiredException("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        int userId = principalUser.getId();
        int marketingReceiveAgreement = dto.getMarketingReceiveAgreement();
        Map<String, Object> params = Map.of(
                "userId", userId,
                "marketingReceiveAgreement", marketingReceiveAgreement
        );

        try {
            userMapper.changeMarketingAgreement(params);
        } catch (Exception e) {
            throw new Exception("마케팅 동의 설정 도중 오류가 발생했습니다. 잠시후 다시 시도해주세요.");
        }
    }

    // 프로필페이지 - 회원 탈퇴
    @Transactional(rollbackFor = Exception.class)
    public void withdrawUser(ReqWithdrawUserDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new TokenValidExpiredException("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int userId = principalUser.getId();

        User user = userMapper.findUserByUserId(userId);

        String password = dto.getPassword();
        if(!user.getPassword().equals(password)) {
            throw new Exception("현재 비밀번호와 일치하지 않습니다. 다시 입력해주세요.");
        }

        try {
            userMapper.deleteUser(userId);
            userMapper.deleteUserOptionalInfo(userId);
            userMapper.deleteUserRoleMet(userId);
            userMapper.deleteOauthInfo(userId);
            // 나머지 정보는 그대로 보존
        } catch (Exception e) {
            throw new Exception("회원 탈퇴 과정중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }

    // 마이페이지 - mbti 결과
    public RespMyMbtiResultDto getMbtiResult() {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new TokenValidExpiredException("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        int userId = principalUser.getId();

        MbtiResult mbtiResult = userMapper.getMyMbtiResult(userId);
        if(mbtiResult == null) {
            throw new NotFoundMyMbtiResultException("피부 mbti 결과 정보가 존재하지 않습니다. 피부 mbti 설문 후 확인해주세요.");
        }

        RespMyMbtiResultDto myMbtiResultDto = RespMyMbtiResultDto.builder()
                .mbtiResultId(mbtiResult.getMbtiResultId())
                .mbtiResultCategoryName(mbtiResult.getMbtiResultCategoryName())
                .mbtiResultTitle(mbtiResult.getMbtiResultTitle())
                .mbtiResultSummary(mbtiResult.getMbtiResultSummary())
                .mbtiResultContent(mbtiResult.getMbtiResultContent())
                .mbtiResultImg(mbtiResult.getMbtiResultImg())
                .build();
        return myMbtiResultDto;
    }

    // 마이페이지 - 주문 정보 출력
    public List<RespMyOrderInfoDto> getMyOrderInfo(ReqGetMyOrderInfoDto dto) {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new TokenValidExpiredException("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        int userId = principalUser.getId();

        Map<String, Object> params = Map.of(
                "userId", userId,
                "startDate", dto.getStartDate() == null ? "1900-01-01" : dto.getStartDate(),
                "endDate", dto.getEndDate() == null ? "2200-12-31" : dto.getEndDate(),
                "searchValue", dto.getSearchValue() == null ? "" : dto.getSearchValue()
        );
        List<Order> myOrderList = userMapper.getMyOrderInfo(params);
        if(myOrderList == null) {
            throw new NotFoundMyOrderInfoException("회원님의 주문하신 상품이 존재하지 않습니다.");
        }

        List<RespMyOrderInfoDto> orderList = new ArrayList<>();

        for(Order order : myOrderList) {
            RespMyOrderInfoDto resp = RespMyOrderInfoDto.builder()
                    .orderId(order.getOrderId())
                    .create_date(order.getCreatedDate())
                    .productUpperCategoryName(order.getProductUpperCategoryName())
                    .productName(order.getOrderItemList().getProduct().getProductName())
                    .status(order.getStatus())
                    .quantity(order.getOrderItemList().getQuantity())
                    .priceAtPurchase(order.getOrderItemList().getPriceAtPurchase())
                    .build();
            orderList.add(resp);
        }
        return orderList;
    }

    // 프로필 페이지 - 결제 취소 공동
    public int cancelSystemPay(int orderId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new TokenValidExpiredException("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        try {
            userMapper.cancelSystemPay(orderId);
            int paymentId = userMapper.findPaymentByOrderId(orderId);
            return paymentId;
        } catch (Exception e) {
            throw new Exception("결제 취소 도중 오류가 발생했습니다. 관리자에게 문의 바랍니다.");
        }
    }

    // 프로필 페이지 - 주문 취소 (계좌이체)
    public void cancelMyOrder(int orderId) throws Exception {
        try {
            userMapper.cancelMyOrder(orderId);
        } catch (Exception e) {
            throw new Exception("주문 취소 도중 오류가 발생했습니다. 관리자에게 문의 바랍니다.");
        }
    }
}
