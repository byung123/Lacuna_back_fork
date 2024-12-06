package LacunaMatata.Lacuna.repository.admin;

import LacunaMatata.Lacuna.entity.consulting.ConsultingLowerCategory;
import LacunaMatata.Lacuna.entity.consulting.ConsultingSurveyInfo;
import LacunaMatata.Lacuna.entity.consulting.ConsultingUpperCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsulttingManageMapper {
    //* 컨설팅 상위 하위 카테고리 관련 mapper */
    // 1. 컨설팅 상위 카테고리 리스트 출력(페이지 리밋 제한 있음)_2024.12.04
    List<ConsultingUpperCategory> getConsultingCategoryList();
    // 2. 컨설팅 상위 카테고리 등록_2024.12.04
    int saveConsultingUpperCategory(ConsultingUpperCategory consultingUpperCategory);
    // 3. 컨설팅 상위 카테고리 수정 모달창 출력_2024.12.04
    ConsultingUpperCategory getConsultingUpperCategory(int upperId);
    // 4. 컨설팅 상위 카테고리 수정_2024.12.04
    int modifyConsultingUpperCategory(ConsultingUpperCategory consultingUpperCategory);
    // 5. 컨설팅 상위 카테고리 단일 삭제_2024.12.04
    int deleteConsultingUpperCategory(int upperId);
    // 6. 컨설팅 상위 카테고리 복수개 삭제_2024.12.04
    int deleteConsultingUpperCategoryList(List<Integer> upperCategoryIdList);
    // 7. 컨설팅 상위 카테고리 리스트 출력(필터용, 하위 카테고리 등록 모달창 출력)_2024.12.04
    List<ConsultingUpperCategory> getConsultingUpperFilter();
    // 8. 컨설팅 상위 카테고리에 따른 하위 카테고리 리스트 출력_2024.12.04
    List<ConsultingLowerCategory> getConsultingLowerCategoryList(int upperId);
    // 9. 컨설팅 하위 카테고리 등록_2024_12_06
    int saveConsultingLowerCategory(ConsultingLowerCategory consultingLowerCategory);
    // 10. 컨설팅 하위 카테고리 수정 모달창 출력_2024.12.06
    ConsultingLowerCategory getConsultingLowerCategory(int lowerId);
    // 11. 컨설팅 하위 카테고리 수정_2024.12.06
    int modifyConsultingLowerCategory(ConsultingLowerCategory consultingLowerCategory);
    // 12. 컨설팅 하위 카테고리 단일 삭제_2024.12.06
    int deleteConsultingLowerCategory(int upperId);
    // 13. 컨설팅 하위 카테고리 복수개 삭제_2024.12.06
    int deleteConsultingLowerCategoryList(List<Integer> lowerCategoryIdList);

    /** 컨설팅 설문지 관련 Mapper */
    // 1. 컨설팅 설문지 리스트 출력_2024.12.06
    List<ConsultingSurveyInfo> getConsultingSurveyList(Map<String, Object> params);

}