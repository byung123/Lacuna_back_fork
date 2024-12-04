package LacunaMatata.Lacuna.repository.admin;

import LacunaMatata.Lacuna.entity.consulting.ConsultingLowerCategory;
import LacunaMatata.Lacuna.entity.consulting.ConsultingUpperCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsulttingManageMapper {
    //* 컨설팅 상위 하위 카테고리 관련 mapper */
    // 1. 컨설팅 상위 카테고리 리스트 출력(페이지 리밋 제한 있음)_2024.12.04
    List<ConsultingUpperCategory> getConsultingCategoryList(Map<String, Object> params);
    // 2. 컨설팅 상위 카테고리 등록_2024.12.04
    int saveConsultingUpperCategory(ConsultingUpperCategory consultingUpperCategory);
    // 3. 컨설팅 상위 카테고리 수정 모달창 출력_2024.12.04
    ConsultingUpperCategory getConsultingUpperCategory(int upperId);

    // 6. 컨설팅 상위 카테고리 리스트 출력(필터용)_2024.12.04
    List<ConsultingUpperCategory> getConsultingUpperFilter();
    // 7. 컨설팅 상위 카테고리에 따른 하위 카테고리 리스트 출력_2024.12.04
    List<ConsultingLowerCategory> getConsultingLowerCategoryList(int upperId);
}