package LacunaMatata.Lacuna.service.admin;

import LacunaMatata.Lacuna.dto.request.admin.Consulting.ReqGetConsultingUpperCategoryListDto;
import LacunaMatata.Lacuna.dto.request.admin.Consulting.ReqRegistUpperConsultingCategoryDto;
import LacunaMatata.Lacuna.dto.response.admin.consulting.*;
import LacunaMatata.Lacuna.entity.consulting.ConsultingLowerCategory;
import LacunaMatata.Lacuna.entity.consulting.ConsultingUpperCategory;
import LacunaMatata.Lacuna.repository.admin.ConsulttingManageMapper;
import LacunaMatata.Lacuna.security.principal.PrincipalUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ConsultingManageService {

    @Autowired
    private ConsulttingManageMapper consultingManageMapper;

    @Value("${file.path}")
    private String filePath;

    @Autowired
    private ObjectMapper objectMapper;

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

    // 컨설팅 상위 분류 항목 등록
    @Transactional(rollbackFor = Exception.class)
    public void registUpperConsulting(ReqRegistUpperConsultingCategoryDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int registerId = principalUser.getId();

        try {
            // 이미지 등록
            // 1. 이미지 신규 등록할 공간 생성
            List<MultipartFile> insertImgs = dto.getInsertImgs();
            String insertCompletedImgPath = null;

            // 2. 신규 이미지 저장
            if (insertImgs != null && !insertImgs.get(0).isEmpty()) {
                insertCompletedImgPath = registerImgUrl(insertImgs.get(0), "mbti/");
            }

            ConsultingUpperCategory consultingUpperCategory = ConsultingUpperCategory.builder()
                    .consultingUpperCategoryName(dto.getConsultingUpperCategoryName())
                    .consultingUpperCategoryDescription(dto.getConsultingUpperCategoryDescription())
                    .consultingUpperCategoryImg(insertCompletedImgPath)
                    .consultingUpperCategoryRegisterId(registerId)
                    .build();
            consultingManageMapper.saveConsultingUpperCategory(consultingUpperCategory);
        } catch (Exception e) {
            throw new Exception("상품을 등록하는 도중 문제가 발생했습니다. (서버오류)");
        }
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
    public RespCountAndConsultingLowerCategoryListDto getLowerConsultingList(int upperId) {
        List<ConsultingLowerCategory> consultingLowerCategoryList = consultingManageMapper.getConsultingLowerCategoryList(upperId);
        List<RespConsultingLowerListDto> consultingLower = new ArrayList<>();
        for(ConsultingLowerCategory consultingLowerCategory : consultingLowerCategoryList) {
            RespConsultingLowerListDto respLowerCategory = RespConsultingLowerListDto.builder()
                    .consultingLowerCategoryId(consultingLowerCategory.getConsultingLowerCategoryId())
                    .consultingLowerCategoryName(consultingLowerCategory.getConsultingLowerCategoryName())
                    .name(consultingLowerCategory.getName())
                    .createDate(consultingLowerCategory.getCreateDate())
                    .build();
            consultingLower.add(respLowerCategory);
        }

        int totalCount = consultingLowerCategoryList.isEmpty() ? 0 : consultingLowerCategoryList.get(0).getTotalCount();
        RespCountAndConsultingLowerCategoryListDto countAndLowerCategory = RespCountAndConsultingLowerCategoryListDto.builder()
                .totalCount(totalCount)
                .consultingLowerCategory(consultingLower)
                .build();

        return countAndLowerCategory;
    }

    // 컨설팅 상위 분류 항목 출력(필터)
    public List<RespConsultingUpperCategoryFilterDto> getUpperConsultingFilter() {
        List<ConsultingUpperCategory> consultingUpperCategoryList = consultingManageMapper.getConsultingUpperFilter();
        List<RespConsultingUpperCategoryFilterDto> consultingUpperFilter = new ArrayList<>();

        for(ConsultingUpperCategory consultingUpper : consultingUpperCategoryList) {
            RespConsultingUpperCategoryFilterDto consultingUpperCategory = RespConsultingUpperCategoryFilterDto.builder()
                    .consultingUpperCategoryId(consultingUpper.getConsultingUpperCategoryId())
                    .consultingUpperCategoryName(consultingUpper.getConsultingUpperCategoryName())
                    .build();
            consultingUpperFilter.add(consultingUpperCategory);
        }
        return consultingUpperFilter;
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

    public String registerImgUrl(MultipartFile img, String dirName ) throws IOException {
        String originalFilenameAndExtension = img.getOriginalFilename();
        String imgName = UUID.randomUUID() + "_" + originalFilenameAndExtension;
//        System.out.println("originalFilename: " + imgName);
        // Todo 디렉토리 경로 잘 확인해서 넣어야 함
        File directory = new File(filePath + dirName);
        if(!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(filePath + dirName + imgName);
        img.transferTo(file);

        return dirName + imgName;
    }

    public void deleteImgUrl(String imgUrl) {
        File file = new File(imgUrl);
        if(file.exists()) {
            file.delete();
        }
    }
}
