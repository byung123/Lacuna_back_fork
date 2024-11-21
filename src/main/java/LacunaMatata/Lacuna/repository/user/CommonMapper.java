package LacunaMatata.Lacuna.repository.user;

import LacunaMatata.Lacuna.entity.Setting;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommonMapper {

    // 1. 세팅(이용약관, 관리자 전화번호, 이용약관 등) 정보 불러오기_2024.11.10
    List<Setting> getSettingInfoList();
    // 2. 세팅(은행 정보) 정보 불러오기
    List<Setting> getSettingBankInfoList();
}
