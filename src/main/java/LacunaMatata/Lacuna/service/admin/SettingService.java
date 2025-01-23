package LacunaMatata.Lacuna.service.admin;

import LacunaMatata.Lacuna.dto.request.admin.settinginfo.ReqModifySettingInfoDto;
import LacunaMatata.Lacuna.dto.response.admin.settinginfo.RespSettingInfoDto;
import LacunaMatata.Lacuna.entity.Setting;
import LacunaMatata.Lacuna.repository.admin.SettingMapper;
import LacunaMatata.Lacuna.security.principal.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingService {

    @Autowired
    private SettingMapper settingMapper;

    // 설정(약관 email, phone, sns 주소 등) 정보 출력
    public List<RespSettingInfoDto> getSettingInfo() {
        List<Setting> settings = settingMapper.getSettingInfoList();
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

    // 설정(약관 email, phone, sns 주소 등) 정보 수정
    public void modifySettingInfo(ReqModifySettingInfoDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int modifySettingId = dto.getSettingId();
        String modifySettingValue = dto.getValue();

        try {
            settingMapper.modifySettingInfo(modifySettingValue, modifySettingId);
        } catch (Exception e) {
            throw new Exception("관리자 정보 수정 중 오류가 발생했습니다. (서버 오류)");
        }
    }
}