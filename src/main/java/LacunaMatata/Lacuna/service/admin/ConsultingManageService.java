package LacunaMatata.Lacuna.service.admin;

import LacunaMatata.Lacuna.dto.request.admin.Consulting.*;
import LacunaMatata.Lacuna.dto.response.admin.consulting.*;
import LacunaMatata.Lacuna.entity.consulting.ConsultingLowerCategory;
import LacunaMatata.Lacuna.entity.consulting.ConsultingSurveyInfo;
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
    public RespCountAndConsultingUpperCategoryListDto getUpperConsultingList() {
        List<ConsultingUpperCategory> consultingUpperCategoryList = consultingManageMapper.getConsultingCategoryList();
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
                insertCompletedImgPath = registerImgUrl(insertImgs.get(0), "consultingUpper/");
            }

            ConsultingUpperCategory consultingUpperCategory = ConsultingUpperCategory.builder()
                    .consultingUpperCategoryName(dto.getConsultingUpperCategoryName())
                    .consultingUpperCategoryDescription(dto.getConsultingUpperCategoryDescription())
                    .consultingUpperCategoryImg(insertCompletedImgPath)
                    .consultingUpperCategoryRegisterId(registerId)
                    .build();
            consultingManageMapper.saveConsultingUpperCategory(consultingUpperCategory);
        } catch (Exception e) {
            throw new Exception("컨설팅 상위분류를 등록하는 도중 문제가 발생했습니다. (서버오류)");
        }
    }

    // 컨설팅 상위 분류 수정 모달창 출력
    public RespConsultingUpperCategoryModifyDto getUpperConsulting(int upperId) {
        ConsultingUpperCategory consultingUpperCategory = consultingManageMapper.getConsultingUpperCategory(upperId);
        RespConsultingUpperCategoryModifyDto consultingUpperCategoryModify = RespConsultingUpperCategoryModifyDto.builder()
                .consultingUpperCategoryId(consultingUpperCategory.getConsultingUpperCategoryId())
                .consultingUpperCategoryName(consultingUpperCategory.getConsultingUpperCategoryName())
                .consultingUpperCategoryDescription(consultingUpperCategory.getConsultingUpperCategoryDescription())
                .consultingUpperCategoryImg(consultingUpperCategory.getConsultingUpperCategoryImg())
                .build();
        return consultingUpperCategoryModify;
    }

    // 컨설팅 상위 분류 항목 수정
    public void modifyUpperConsulting(ReqModifyUpperConsulingCategoryDto dto, int upperId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int registerId = principalUser.getId();

        try {
            /* 이미지 삭제 후 이미지 추가 */
            // 단계 : 1. 신규 등록, 삭제 공간 생성, 2. 이미지 경로 DB 삭제 및 DB 파일 삭제 3. 신규 데이터 등록

            // 1. 최종 수정될 imgPath 공간 생성
            String finalImgPath = dto.getPrevImgPath();

            // 2. 이미지 신규 등록할 공간 생성
            List<MultipartFile> insertImgs = dto.getInsertImgs();

            // 3. 이미지 삭제할 공간 생성
            String deleteImgPath = dto.getDeleteImgPath();

            // 4. 물리 파일 삭제
            if(deleteImgPath != null && !deleteImgPath.isEmpty()) {
                deleteImgUrl(deleteImgPath);
                finalImgPath = null;
            }

            // 이미지 등록
            // 1. 이미지 수정할 공간 생성
            if(insertImgs != null && !insertImgs.get(0).isEmpty()) {
                finalImgPath = registerImgUrl(insertImgs.get(0), "consultingUpper/");
            }

            ConsultingUpperCategory consultingUpperCategory = ConsultingUpperCategory.builder()
                    .consultingUpperCategoryId(dto.getConsultingUpperCategoryId())
                    .consultingUpperCategoryName(dto.getConsultingUpperCategoryName())
                    .consultingUpperCategoryDescription(dto.getConsultingUpperCategoryDescription())
                    .consultingUpperCategoryImg(finalImgPath)
                    .consultingUpperCategoryRegisterId(registerId)
                    .build();
            consultingManageMapper.modifyConsultingUpperCategory(consultingUpperCategory);

        } catch (Exception e) {
            throw new Exception("컨설팅 상위분류 수정 중 오류가 발생했습니다.");
        }
    }

    // 컨설팅 상위 분류 항목 단일 삭제
    public void deleteUpperConsulting(int upperId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        consultingManageMapper.deleteConsultingUpperCategory(upperId);
    }

    // 컨설팅 상위 분류 항목 복수개 삭제
    public void deleteUpperConsultingList(ReqDeleteConsultingUpperCategoryListDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        List<Integer> consultingUpperCategoryIdList = dto.getConsultingUpperCategoryIdList();
        consultingManageMapper.deleteConsultingUpperCategoryList(consultingUpperCategoryIdList);
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

    // 컨설팅 하위 분류 항목 등록
    public void registLowerConsulting(ReqRegistLowerConsultingCategoryDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int registerId = principalUser.getId();

        ConsultingLowerCategory consultingLowerCategory = ConsultingLowerCategory.builder()
                .consultingUpperCategoryId(dto.getConsultingUpperCategoryId())
                .consultingLowerCategoryName(dto.getConsultingLowerCategoryName())
                .consultingLowerCategoryRegisterId(registerId)
                .build();
        consultingManageMapper.saveConsultingLowerCategory(consultingLowerCategory);
    }

    // 컨설팅 하위 분류 수정 모달창 항목 출력
    public RespConsultingLowerCategoryModifyDto getLowerConsulting(int lowerId) {
        ConsultingLowerCategory lowerCategory = consultingManageMapper.getConsultingLowerCategory(lowerId);
        RespConsultingLowerCategoryModifyDto consultingLowerCategory = RespConsultingLowerCategoryModifyDto.builder()
                .consultingLowerCategoryId(lowerCategory.getConsultingLowerCategoryId())
                .consultingUpperCategoryName(lowerCategory.getConsultingUpperCategoryName())
                .consultingLowerCategoryName(lowerCategory.getConsultingLowerCategoryName())
                .build();
        return consultingLowerCategory;
    }

    // 컨설팅 하위 분류 항목 수정
    public void modifyLowerConsulting(ReqModifyLowerConsultingCategoryDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        ConsultingLowerCategory consultingLowerCategory = ConsultingLowerCategory.builder()
                .consultingLowerCategoryId(dto.getConsultingLowerCategoryId())
                .consultingLowerCategoryName(dto.getConsultingLowerCategoryName())
                .build();
        consultingManageMapper.modifyConsultingLowerCategory(consultingLowerCategory);
    }

    // 컨설팅 하위 분류 항목 단일 삭제
    public void deleteLowerConsulting(int lowerId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        consultingManageMapper.deleteConsultingLowerCategory(lowerId);
    }

    // 컨설팅 하위 분류 항목 복수개 삭제
    public void deleteLowerConsultingList(ReqDeleteConsultingLowerCategoryListDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        List<Integer> consultingLowerCategoryIdList = dto.getConsultingLowerCategoryIdList();
        consultingManageMapper.deleteConsultingLowerCategoryList(consultingLowerCategoryIdList);
    }

    // 컨설팅 설문지 목록 출력
    public RespCountAndConsultingSurveyInfoListDto getSurveyList(ReqGetConsultingServeyDto dto) {
        int startIndex = (dto.getPage() - 1) * dto.getLimit();
        Map<String, Object> params = Map.of(
        "startIndex", startIndex,
        "limit", dto.getLimit(),
        "searchValue", dto.getSearchValue() == null ? "" : dto.getSearchValue(),
        "option", dto.getOption(),
        "filter", dto.getFilter()
        );
        List<ConsultingSurveyInfo> consultingSurveyInfos = consultingManageMapper.getConsultingSurveyList(params);
        List<RespConsultingSurveyInfoListDto> consultingSurveyInfoList = new ArrayList<>();

        for(ConsultingSurveyInfo consultingSurveyInfo : consultingSurveyInfos) {
            RespConsultingSurveyInfoListDto respConsultingSurveyInfoListDto = RespConsultingSurveyInfoListDto.builder()
                    .consultingId(consultingSurveyInfo.getConsultingId())
                    .consultingCode(consultingSurveyInfo.getConsultingCode())
                    .consultingUpperCategoryName(consultingSurveyInfo.getConsultingUpperCategoryName())
                    .consultingLowerCategoryName(consultingSurveyInfo.getConsultingLowerCategoryName())
                    .consultingTitle(consultingSurveyInfo.getConsultingTitle())
                    .name(consultingSurveyInfo.getName())
                    .createDate(consultingSurveyInfo.getCreateDate())
                    .build();
            consultingSurveyInfoList.add(respConsultingSurveyInfoListDto);
        }

        int totalCount = consultingSurveyInfos.isEmpty() ? 0 : consultingSurveyInfos.get(0).getTotalCount();

        RespCountAndConsultingSurveyInfoListDto consultingSurvey = RespCountAndConsultingSurveyInfoListDto.builder()
                .totalCount(totalCount)
                .consultingSurveyInfoList(consultingSurveyInfoList)
                .build();

        return consultingSurvey;
    }

    // 컨설팅 설문지 컨설팅 설문지 등록 모달창 출력
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
