package LacunaMatata.Lacuna.controller;

import LacunaMatata.Lacuna.aspect.annotation.user.AuthAop;
import LacunaMatata.Lacuna.dto.request.user.auth.*;
import LacunaMatata.Lacuna.exception.auth.EmailNotFoundException;
import LacunaMatata.Lacuna.exception.auth.UsernameNotFoundException;
import LacunaMatata.Lacuna.service.AuthService;
import LacunaMatata.Lacuna.service.TokenService;
import LacunaMatata.Lacuna.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/************************************
 * version: 1.0.3                   *
 * author: 손경태                    *
 * description: accessToken()       *
 * createDate: 2024-10-11           *
 * updateDate: 2024-11-05           *
 ***********************************/
@Slf4j
@RestController
@Api(tags = "인증 - 인증 관련 컨트롤러(토큰, 로그인 등)")
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    // 토큰 인증 요청
    @GetMapping("/access")
    @ApiOperation(value = "토큰 - 토큰 액세스 검증")
    public ResponseEntity<?> accessToken(ReqAccessTokenDto dto) {
        tokenService.isValidToken(dto.getAccessToken());
        return ResponseEntity.ok().body(true);
    }

    // 일반 로그인
    @PostMapping("/signin")
    @ApiOperation(value = "로그인 - 일반 로그인")
    @AuthAop
    public ResponseEntity<?> signin(HttpServletRequest request, @RequestBody ReqGeneralSigninDto dto, BindingResult bindingResult) throws Exception {
        String accessToken = authService.signin(request, dto);
        String bearerToken = "Bearer ".concat(accessToken);

        return ResponseEntity.ok().body(bearerToken);
    }

    // 회원가입 시 이용약관, 마케팅 정보 불러오기
    @GetMapping("/signup/agreementinfo")
    @ApiOperation(value = "getAgreementInfoApi")
    ResponseEntity<?> getAgreementInfo() {
        return ResponseEntity.ok().body(authService.getAgreementInfo());
    }

    // 일반 회원가입
    @PostMapping("/signup")
    @ApiOperation(value = "회원가입 - 일반 회원가입")
    @AuthAop
    public ResponseEntity<?> signup(@Valid @RequestBody ReqGeneralSignupDto dto, BindingResult bindingResult) throws Exception {
        authService.signup(dto);
        return ResponseEntity.ok().body(true);
    }

    // 오어스 회원가입
    @ApiOperation(value = "회원가입 - oauth2 회원가입")
    @PostMapping("/oauth2user/signup")
    @AuthAop
    public ResponseEntity<?> oauthSignup(@RequestBody ReqOauthSignupDto dto, BindingResult bindingResult) throws Exception {
        authService.oauthSignup(dto);
        return ResponseEntity.ok().body(true);
    }

    // 회원가입 시 이메일 인증(인증 메일 보내기) 1
    @PostMapping("/email")
    @ApiOperation(value = "인증 - (회원가입)이메일 인증1 (인증)")
    public ResponseEntity<?> sendAuthEmail(@RequestBody ReqAuthEmailDto dto) throws Exception {
        authService.sendAuthEmail(dto);
        return ResponseEntity.ok().body(true);
    }

    // 회원가입시 이메일 인증(인증코드 확인) 2
    @PostMapping("/email/authentication")
    @ApiOperation(value = "인증 - (회원가입)이메일 인증2 (인증코드 확인)")
    public ResponseEntity<?> emailValid(@RequestBody ReqEmailAuthenticationDto dto) throws Exception {
        return ResponseEntity.ok().body(authService.emailAuthentication(dto));
    }

    // 사용자 아이디 찾기
    @PostMapping("/find/id")
    @ApiOperation(value = "찾기 - 사용자 ID 찾기")
    public ResponseEntity<?> findUsername(@RequestBody ReqFindUsernameDto dto) throws EmailNotFoundException {
        return ResponseEntity.ok().body(authService.findUsername(dto));
    }

    // 사용자 비밀번호 찾기 - 인증코드 전송
    @PostMapping("/find/password/authentication")
    @ApiOperation(value = "찾기 - 사용자 PW 찾기 - 인증코드 전송")
    public ResponseEntity<?> findPassword(@RequestBody ReqFindPasswordDto dto) throws UsernameNotFoundException {
        authService.findPassword(dto);
        return ResponseEntity.ok().body(true);
    }

    // 사용자 비밀번호 찾기 - 인증코드 인증
    @PostMapping("/find/password/checkcode")
    @ApiOperation(value = "찾기 - 사용자 PW 찾기 - 인증코드 인증")
    public ResponseEntity<?> checkAuthenticationCode(@RequestBody ReqCheckAuthenticationDto dto) {
        return ResponseEntity.ok().body(authService.checkAuthenticationCode(dto));
    }

    // 사용자 비밀번호 찾기 - 새 비밀번호 입력
    @PutMapping("/find/password/newpassword")
    @ApiOperation(value = "찾기 - 사용자 PW 찾기 - 인증코드 인증")
    public ResponseEntity<?> changeNewPassword(@RequestBody ReqChangeNewPasswordDto dto) {
        authService.changeNewPassword(dto);
        return ResponseEntity.ok().body(true);
    }
}
