package LacunaMatata.Lacuna.controller.user;

import LacunaMatata.Lacuna.dto.request.user.user.*;
import LacunaMatata.Lacuna.dto.response.user.user.RespMyMbtiResultDto;
import LacunaMatata.Lacuna.dto.response.user.user.RespMyProfileDto;
import LacunaMatata.Lacuna.repository.user.UserMapper;
import LacunaMatata.Lacuna.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@Api(tags = {"사용자 - 사용자 관리 컨트롤러"})
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    // 상단 header 프로필 정보 출력(헤더)
    @ApiOperation(value = "헤더 - 헤더 정보 출력")
    @GetMapping("/profile/header")
    public ResponseEntity<?> getMyProfileHeader() {
        return ResponseEntity.ok().body(userService.getMyProfileHeader());
    }

    // 프로필 페이지 출력
    @ApiOperation(value = "MyPage - 프로필 페이지 출력")
    @GetMapping("/user/profile")
    public ResponseEntity<?> getMyProfile() {
        RespMyProfileDto myProfile = userService.getMyProfile();
        return ResponseEntity.ok().body(myProfile);
    }

    // 프로필 페이지 - 프로필 사진 바꾸기
    @ApiOperation(value = "MyPage - 프로필 사진 수정")
    @PutMapping("/user/change/profile/img")
    public ResponseEntity<?> changeMyProfileImg(@RequestBody ReqModifyProfileImgDto dto) throws IOException {
        userService.changeMyProfileImg(dto);
        return ResponseEntity.ok().body(true);
    }

    // 프로필 페이지 - 비밀번호 바꾸기1
    @PostMapping("/user/change/password")
    @ApiOperation(value = "MyPage - 비밀번호 변경1")
    public ResponseEntity<?> chekcPassword(@RequestBody ReqPasswordCheckDto dto) throws Exception {
        userService.passwordCheck(dto);
        return ResponseEntity.ok().body(true);
    }

    // 프로필페이지 - 비밀번호 바꾸기2
    @PutMapping("/user/change/password")
    public ResponseEntity<?> changePassword(@RequestBody ReqPasswordChangeDto dto) throws Exception {
        userService.passwordChange(dto);
        return ResponseEntity.ok().body(true);
    }

    // 프로필 페이지 - 내 연락처 바꾸기
    @PutMapping("/user/change/phone")
    @ApiOperation(value = "MyPage - 연락처 변경")
    public ResponseEntity<?> changeMyPhoneNumber(@RequestBody ReqChangePhoneNumberDto dto) {
        userService.changePhoneNumber(dto);
        return ResponseEntity.ok().body(true);
    }

    // 프로필페이지 - 내 이메일 주소 바꾸기 (메일 인증)
    @PostMapping("/user/change/email")
    @ApiOperation(value = "MyPage - 이메일 주소 변경 (인증)")
    public ResponseEntity<?> changeMyEmail(@RequestBody ReqChangeMyEmailDto dto) throws Exception {
        Boolean success = userService.changeMyEmail(dto);
        return ResponseEntity.ok().body(true);
    }

    // 프로필페이지 - 내 이메일 주소 바꾸기2
    @PutMapping("/user/change/email")
    @ApiOperation(value = "MyPage - 이메일 주소 변경 (수정)")
    public ResponseEntity<?> changeMyEmail2(@RequestBody ReqMyEmailTokenDto dto) {
        userService.changeMyEmail2(dto);
        return ResponseEntity.ok().body(true);
    }

    // 프로필페이지 - 마케팅 수신 동의 바꾸기
    @PutMapping("/user/change/marketing")
    @ApiOperation(value = "MyPage - 마케팅 수신 동의 수정")
    public ResponseEntity<?> changeMarketingAgreement(@RequestBody ReqChangeMarketingDto dto) {
        userService.changeMarketingAgreement(dto);
        return ResponseEntity.ok().body(true);
    }


    // 프로필 페이지 - 회원 탈퇴
    @DeleteMapping("/user/delete")
    @ApiOperation(value = "MyPage - 회원 탈뢰(계정 삭제)")
    public ResponseEntity<?> withdrawUser(@RequestBody ReqWithdrawUserDto dto) throws Exception {
        userService.withdrawUser(dto);
        return ResponseEntity.ok().body(true);
    }

    // 프로필 페이지 - 내 mbti 결과 보기
    @GetMapping("/user/mbti/result")
    @ApiOperation(value = "MyPage - MBTI 결과 보기")
    public ResponseEntity<?> getMbtiResult() {
        RespMyMbtiResultDto myMbtiResult = userService.getMbtiResult();
        return ResponseEntity.ok().body(myMbtiResult);
    }

    // 프로필 페이지 - 주문 내역 정보
    @GetMapping("/user/order/mypage")
    @ApiOperation(value = "MyPage - 주문 내역 정보 리스트 출력")
    public ResponseEntity<?> getMyOrderInfo(ReqGetMyOrderInfoDto dto) {
        return ResponseEntity.ok().body(userService.getMyOrderInfo(dto));
    }

    // 프로필 페이지 - 결제 취소 공동
    @PutMapping("/user/cancel/payment/{orderId}")
    @ApiOperation(value = "MyPage - 결제 취소")
    public ResponseEntity<?> cancelSystemPay(@PathVariable int orderId) throws Exception {
        return ResponseEntity.ok().body(userService.cancelSystemPay(orderId));
    }

    // 프로필 페이지 - 주문 취소 - 계좌이체
    @PutMapping("/user/cancel/order/{orderId}")
    @ApiOperation(value = "MyPage - 주문 취소")
    public ResponseEntity<?> cancelMyOrder(@PathVariable int orderId) {
        userService.cancelMyOrder(orderId);
        return ResponseEntity.ok().body(true);
    }
}
