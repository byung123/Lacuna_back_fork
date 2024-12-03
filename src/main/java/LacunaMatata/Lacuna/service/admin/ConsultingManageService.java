package LacunaMatata.Lacuna.service.admin;

import LacunaMatata.Lacuna.dto.request.admin.Consulting.ReqGetConsultingLowerCategoryListDto;
import LacunaMatata.Lacuna.dto.request.admin.Consulting.ReqGetConsultingUpperCategoryListDto;
import LacunaMatata.Lacuna.dto.response.admin.consulting.RespConsultingUpperListDto;
import LacunaMatata.Lacuna.dto.response.admin.consulting.RespCountAndConsultingUpperCategoryListDto;
import LacunaMatata.Lacuna.entity.consulting.ConsultingLowerCategory;
import LacunaMatata.Lacuna.entity.consulting.ConsultingUpperCategory;
import LacunaMatata.Lacuna.repository.admin.ConsulttingManageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ConsultingManageService {

    @Autowired
    private ConsulttingManageMapper consultingManageMapper;

    // 컨설팅 상위 분류 목록 출력
    public RespCountAndConsultingUpperCategoryListDto getUpperConsultingList(ReqGetConsultingUpperCategoryListDto dto) {
        int startIndex = (dto.getPage() - 1) * dto.getLimit();
        Map<String, Object> params = Map.of(
            "startIndex", startIndex,
            "limit", dto.getLimit()
        );
        List<ConsultingUpperCategory> consultingUpperCategoryList = consultingManageMapper.getConsultingCategoryList(params);
        List<RespConsultingUpperListDto> consultingUpperCategorys = new ArrayList<>();

        for (ConsultingUpperCategory consultingUpperCategory : consultingUpperCategoryList) {
            RespConsultingUpperListDto consultingUpperAndLower = RespConsultingUpperListDto.builder()
                    .consultingUpperCategoryId(consultingUpperCategory.getConsultingUpperCategoryId())
                    .consultingUpperCategoryName(consultingUpperCategory.getConsultingUpperCategoryName())
                    .name(consultingUpperCategory.getName())
                    .createDate(consultingUpperCategory.getCreateDate())
                    .build();
            consultingUpperCategorys.add(consultingUpperAndLower);
        }
        int totalCount = consultingUpperCategoryList.isEmpty() ? 0 : consultingUpperCategoryList.get(0).getTotalCount();
        RespCountAndConsultingUpperCategoryListDto countAndCounsultingUpperCategory = RespCountAndConsultingUpperCategoryListDto.builder()
                .totalCount(totalCount)
                .consultingUpperCategory(consultingUpperCategorys)
                .build();
        return countAndCounsultingUpperCategory;
    }

    // 컨설팅 상위 분류 항목 출력(필터)
    public void getUpperConsultingFilter() {

    }

    // 컨설팅 상위 분류 항목 등록
    public void registUpperConsulting() {

    }

    // 컨설팅 상위 분류 항목 출력
    public void getUpperConsulting() {

    }

    // 컨설팅 상위 분류 항목 수정
    public void modifyUpperConsulting() {

    }

    // 컨설팅 상위 분류 항목 삭제
    public void deleteUpperConsulting() {

    }

    // 컨설팅 상위 분류 항목 복수개 삭제
    public void deleteUpperConsultingList() {

    }

    // 컨설팅 하위 분류 목록 출력
    public String getLowerConsultingList(ReqGetConsultingLowerCategoryListDto dto) {
        int startIndex = (dto.getPage() - 1) * dto.getLimit();
        Map<String, Object> params = Map.of(
            "startIndex", startIndex,
                "limit", dto.getLimit()
        );
//        List<ConsultingLowerCategory> consultingLowerCategoryList = consultingManageMapper.getConsultingLowerCategoryList()
        return "s";
    }

    // 컨설팅 하위 분류 항목 출력 (필터)
    public void getLowerConsultingFilter() {

    }

    // 컨설팅 하위 분류 항목 등록
    public void registLowerConsulting() {

    }

    // 컨설팅 하위 분류 항목 출력
    public void getLowerConsulting() {

    }

    // 컨설팅 하위 분류 항목 수정
    public void modifyLowerConsulting() {

    }

    // 컨설팅 하위 분류 항목 삭제
    public void deleteLowerConsulting() {

    }

    // 컨설팅 하위 분류 항목 복수개 삭제
    public void deleteLowerConsultingList() {

    }

    // 컨설팅 설문지 목록 출력
    public void getSurveyList() {

    }

    // 컨설팅 설문지 선택지 타입 항목 출력
    public void getSurveyOption() {

    }

    // 컨설팅 설문지 항목 등록
    public void registSurvey() {

    }

    // 컨설팅 설문지 항목 출력
    public void getSurvey() {

    }

    // 컨설팅 설문지 항목 수정
    public void modifySurvey() {

    }

    // 컨설팅 설문지 항목 삭제
    public void deleteSurvey() {

    }

    // 컨설팅 설문지 항목 복수개 삭제
    public void deleteSurveyList() {

    }

    // 컨설팅 결과지 목록 출력
    public void getResultList() {

    }

    // 컨설팅 결과지 항목 등록
    public void registResult() {

    }

    // 컨설팅 결과지 항목 출력
    public void getResult() {

    }

    // 컨설팅 결과지 항목 수정
    public void modifyResult() {

    }

    // 컨설팅 결과지 항목 삭제
    public void deleteResult() {

    }

    // 컨설팅 결과지 항목 복수개 삭제
    public void deleteResultList() {

    }
}
