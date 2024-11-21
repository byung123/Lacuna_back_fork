package LacunaMatata.Lacuna.controller.user;

import LacunaMatata.Lacuna.dto.request.user.commoninfo.ReqServiceCountInfoDto;
import LacunaMatata.Lacuna.dto.response.admin.settinginfo.RespSettingInfoDto;
import LacunaMatata.Lacuna.service.user.CommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@Api(tags = "사용자 - 공용 컨트롤러(Setting 정보 출력, 이용 통계수 증가)")
public class CommonController {

    @Autowired
    private CommonService commonService;

    // 사용자 페이지에 Setting 정보 불러오기
    @GetMapping("/setting/info")
    @ApiOperation(value = "세팅 - 세팅 정보 출력(이용약관, 연락처 등등)")
    public ResponseEntity<?> getSettingInfo() {
        List<RespSettingInfoDto> settingInfo = commonService.getSettingInfo();
        return ResponseEntity.ok().body(settingInfo);
    }

    @GetMapping("/setting/bankinfo")
    @ApiOperation(value = "getBankInfoApi")
    public ResponseEntity<?> getBankInfo() {
        List<RespSettingInfoDto> settingBankInfo = commonService.getSettingBankInfo();
        return ResponseEntity.ok().body(settingBankInfo);
    }

    @PostMapping("/service/counting")
    @ApiOperation(value = "통계 - 서비스 이용(조회)수 증가")
    public ResponseEntity<?> getCountIndex(@RequestBody ReqServiceCountInfoDto dto) {
        commonService.getCountIntroPage(dto);
        return ResponseEntity.ok().body(true);
    }
}
