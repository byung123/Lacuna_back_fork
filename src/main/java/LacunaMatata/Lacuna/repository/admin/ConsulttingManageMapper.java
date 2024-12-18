package LacunaMatata.Lacuna.repository.admin;

import LacunaMatata.Lacuna.entity.consulting.ConsultingLowerCategory;
import LacunaMatata.Lacuna.entity.consulting.ConsultingSurveyInfo;
import LacunaMatata.Lacuna.entity.consulting.ConsultingSurveyOption;
import LacunaMatata.Lacuna.entity.consulting.ConsultingUpperCategory;
import LacunaMatata.Lacuna.entity.lifestyle.LifestyleResult;
import LacunaMatata.Lacuna.entity.lifestyle.LifestyleResultDetail;
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
    // 2. 컨설팅 설문지 항목 등록 모달창 출력_2024.12.16 - 위에 컨설팅 상위 하위 관련 1번꺼 사용
    // 3-1. 컨설팅 설문지 항목 등록_2024.12.16
    int saveConsultingSurveyInfo(ConsultingSurveyInfo consultingSurveyInfo);
    // 3-2. 컨설팅 설문지 옵션 항목 등록1(옵션이 radio, checkbox 일 때 - 선택항목 일때)_2024.12.06
    int saveConsultingSurveySelectOption(List<ConsultingSurveyOption> consultingSurveyOption);
    // 3-3. 컨설팅 설문지 옵션 항목 등록2(옵션이 img, wvm, text 등 일 때 - 경로나 직접 입력 받는 것일때)_2024.12.06
    int saveConsultingSurveyNonSelectOption(ConsultingSurveyOption consultingSurveyOption);
    // 4. 컨설팅 설문지 항목 수정 모달창 출력_2024.12.16
    ConsultingSurveyInfo getConsultingSurveyInfo(int consultingId);
    // 5-1. 컨설팅 설문지 수정1 - 컨설팅 설문 정보 수정_2024.12.17
    int modifyConsultingSurveyInfo(ConsultingSurveyInfo consultingSurveyInfo);
    // 5-2. 컨섩팅 설문지 수정2 - 컨설팅 설문 옵션 목록 삭제_2024.12.17
    int deleteConsultingSurveyOption(int consultingId);
    // 6. 컨설팅 설문지 항목 단일 삭제_2024.12.17
    int deleteConsultingSurveyInfo(int consultingId);
    // 7. 컨설팅 설문지 항목 복수개 삭제_2024.12.17
    int deleteConsultingSurveyInfoList(List<Integer> consultingIdList);

    /** 컨설팅 결과지(라이프스타일) 관련 Mapper */
    // 1. 컨설팅 결과지 리스트 출력_2024.12.17
    List<LifestyleResult> getLifeStyleResultList(Map<String, Object> params);
    // 2-1. 컨설팅 결과지 항목 등록 - 라이프스타일 결과 항목 등록_2024.12.17
    int saveLifestyleResult(LifestyleResult lifestyleResult);
    // 2-2. 컨설팅 결과지 항목 등록 - 라이프스타일 결과 디테일 항목 등록_2024.12.17
    int saveLifestyleResultDetail(Map<String, Object> params);
    // 3. 컨설팅 결과지 항목 수정 모달창 출력_2024.12.18
    LifestyleResult getLifestyleResult(int resultId);
    // 4-1. 컨설팅 결과지 항목 수정_2024.12.18
    int modifyLifestyleResult(LifestyleResult lifestyleResult);
    // 4-2. 컨설팅 결과지 디테일 항목 수정_2024.12.18
    int modifyLifestyleResultDetail(List<LifestyleResultDetail> lifestyleDetailList);
    // 5. 컨설팅 결돠지 항목 단일 삭제_2024.12.18
    int deleteLifestyleResult(int resultId);
    // 6. 컨설팅 결과지 항목 복수개 삭제_2024.12.18
    int deleteLifestyleResultList(List<Integer> resultIdList);
}