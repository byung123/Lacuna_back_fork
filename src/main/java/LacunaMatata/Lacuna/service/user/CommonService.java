package LacunaMatata.Lacuna.service.user;

import LacunaMatata.Lacuna.dto.request.user.commoninfo.ReqServiceCountInfoDto;
import LacunaMatata.Lacuna.dto.response.admin.settinginfo.RespSettingInfoDto;
import LacunaMatata.Lacuna.entity.Setting;
import LacunaMatata.Lacuna.repository.user.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonService {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private StatisticService statisticService;

    // 설정(약관 email, phone, sns 주소 등) 정보 출력
    public List<RespSettingInfoDto> getSettingInfo() {
        List<Setting> settings = commonMapper.getSettingInfoList();
        List<RespSettingInfoDto> respSettingInfo = new ArrayList<RespSettingInfoDto>();
        for(Setting setting : settings) {
            RespSettingInfoDto respSettingInfoDto = RespSettingInfoDto.builder()
                    .settingId(setting.getSettingId())
                    .dataType(setting.getDataType())
                    .value(setting.getValue())
                    .build();
            respSettingInfo.add(respSettingInfoDto);
        }
        return respSettingInfo;
    }

    // 설정(은행 정보) 정보 출력
    public List<RespSettingInfoDto> getSettingBankInfo() {
        List<Setting> settings = commonMapper.getSettingBankInfoList();
        List<RespSettingInfoDto> respSettingInfo = new ArrayList<RespSettingInfoDto>();
        for(Setting setting : settings) {
            RespSettingInfoDto respSettingInfoDto = RespSettingInfoDto.builder()
                    .settingId(setting.getSettingId())
                    .dataType(setting.getDataType())
                    .value(setting.getValue())
                    .build();
            respSettingInfo.add(respSettingInfoDto);
        }
        return respSettingInfo;
    }

    // 페이지 정보 눌렀을 때 1증가
    public void getCountIntroPage(ReqServiceCountInfoDto dto) {
        statisticService.plusServiceCount(dto);
    }
}
