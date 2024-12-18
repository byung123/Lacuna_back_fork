package LacunaMatata.Lacuna.service.admin;

import LacunaMatata.Lacuna.dto.request.admin.Consulting.*;
import LacunaMatata.Lacuna.dto.response.admin.consulting.*;
import LacunaMatata.Lacuna.entity.consulting.ConsultingLowerCategory;
import LacunaMatata.Lacuna.entity.consulting.ConsultingSurveyInfo;
import LacunaMatata.Lacuna.entity.consulting.ConsultingSurveyOption;
import LacunaMatata.Lacuna.entity.consulting.ConsultingUpperCategory;
import LacunaMatata.Lacuna.entity.lifestyle.LifestyleResult;
import LacunaMatata.Lacuna.entity.lifestyle.LifestyleResultDetail;
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
import java.time.LocalDateTime;
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

        List<Integer> consultingUpperCategoryIdList = dto.getUpperCategoryIdList();
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
        List<Integer> consultingLowerCategoryIdList = dto.getLowerCategoryIdList();
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

    // 컨설팅 설문지 등록 모달창 출력
    public List<RespConsultingRegistModalDto> getSurveyregisterModal() {
        List<ConsultingUpperCategory> consultingUpperCategoryList = consultingManageMapper.getConsultingCategoryList();

        List<RespConsultingRegistModalDto> consultingUpperCategory = new ArrayList<>();
        int consultingUpperId = 0;
        String consultingUpperName = "";

        for(ConsultingUpperCategory consultingUpper : consultingUpperCategoryList) {
            consultingUpperId = consultingUpper.getConsultingUpperCategoryId();
            consultingUpperName = consultingUpper.getConsultingUpperCategoryName();
            List<RespConsultingLowerCategoryListDto> consultingLowerCategory = new ArrayList<>();

            for(ConsultingLowerCategory consultingLower : consultingUpper.getConsultingLowerCategory()) {
                RespConsultingLowerCategoryListDto consultingLowerCategoryList = RespConsultingLowerCategoryListDto.builder()
                        .consultingLowerCategoryId(consultingLower.getConsultingLowerCategoryId())
                        .consultingLowerCategoryName(consultingLower.getConsultingLowerCategoryName())
                        .build();
                consultingLowerCategory.add(consultingLowerCategoryList);
            }
            RespConsultingRegistModalDto consultingRegistModal = RespConsultingRegistModalDto.builder()
                    .consultingUpperCategoryId(consultingUpperId)
                    .consultingUpperCategoryName(consultingUpperName)
                    .consultingLowerCategory(consultingLowerCategory)
                    .build();
            consultingUpperCategory.add(consultingRegistModal);
        }
        return consultingUpperCategory;
    }

    // 컨설팅 설문지 항목 등록
    @Transactional(rollbackFor = Exception.class)
    public void registConsultingSurvey(ReqRegistConsultingSurveyDto dto) throws Exception {
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
                insertCompletedImgPath = registerImgUrl(insertImgs.get(0), "consultingSurvey/");
            }

            ConsultingSurveyInfo consultingSurveyInfo = ConsultingSurveyInfo.builder()
                    .consultingSurveyUpperCategoryId(dto.getConsultingUpperCategoryId())
                    .consultingLowerCategoryId(dto.getConsultingLowerCategoryId())
                    .consultingCode(dto.getConsultingCode())
                    .consultingSurveyRegisterId(registerId)
                    .consultingTitle(dto.getConsultingTitle())
                    .consultingSubtitle(dto.getConsultingSubtitle())
                    .consultingImg(insertCompletedImgPath)
                    .build();
            consultingManageMapper.saveConsultingSurveyInfo(consultingSurveyInfo);

            // 선택지 옵션이 체크박스나 라디오일 때
            if(dto.getConsultingOptionType().equals("checkbox") || dto.getConsultingOptionType().equals("radio")) {
                List<ConsultingSurveyOption> consultingSurveyOptionList = new ArrayList<>();
                for(ReqConsultingSurveyOptionDto surveyOption : dto.getConsultingOption()) {
                    ConsultingSurveyOption consultingSurveyOption = ConsultingSurveyOption.builder()
                            .consultingId(consultingSurveyInfo.getConsultingId())
                            .consultingOptionType(dto.getConsultingOptionType())
                            .optionValue(surveyOption.getOptionValue())
                            .optionScore(surveyOption.getOptionScore())
                            .build();
                    consultingSurveyOptionList.add(consultingSurveyOption);
                }
                consultingManageMapper.saveConsultingSurveySelectOption(consultingSurveyOptionList);
            } else {
                ConsultingSurveyOption consultingSurveyOption = ConsultingSurveyOption.builder()
                        .consultingId(consultingSurveyInfo.getConsultingId())
                        .consultingOptionType(dto.getConsultingOptionType())
                        .build();
                consultingManageMapper.saveConsultingSurveyNonSelectOption(consultingSurveyOption);
            }
        } catch (Exception e) {
            throw new Exception("컨설팅 설문지를 등록하는 도중 문제가 발생했습니다. (서버오류)");
        }
    }

    // 컨설팅 설문지 항목 수정 모달창 출력
    public RespConsultingSurveyInfoModifyDto getConsultingSurvey(int consultingId) {
        ConsultingSurveyInfo consultingSurveyInfo = consultingManageMapper.getConsultingSurveyInfo(consultingId);
        List<ConsultingSurveyOption> consultingSurveyOptionList = consultingSurveyInfo.getConsultingSurveyOption();

        List<RespConsultingSurveyOptionModifyDto> consultingSurveyOption = new ArrayList<>();

        for(int i = 0; i < consultingSurveyOptionList.size(); i++) {
            RespConsultingSurveyOptionModifyDto consultingOptionList = RespConsultingSurveyOptionModifyDto.builder()
                    .optionValue(consultingSurveyOptionList.get(i).getOptionValue())
                    .optionScore(consultingSurveyOptionList.get(i).getOptionScore())
                    .build();
            consultingSurveyOption.add(consultingOptionList);
        }

        RespConsultingSurveyInfoModifyDto consultingModifySurveyInfo = RespConsultingSurveyInfoModifyDto.builder()
                .consultingId(consultingSurveyInfo.getConsultingId())
                .consultingCode(consultingSurveyInfo.getConsultingCode())
                .consultingUpperCategoryName(consultingSurveyInfo.getConsultingUpperCategoryName())
                .consultingLowerCategoryName(consultingSurveyInfo.getConsultingLowerCategoryName())
                .consultingTitle(consultingSurveyInfo.getConsultingTitle())
                .consultingSubtitle(consultingSurveyInfo.getConsultingSubtitle())
                .consultingImg(consultingSurveyInfo.getConsultingImg())
                .consultingOptionType(consultingSurveyOptionList.get(0).getConsultingOptionType())
                .consultingOption(consultingSurveyOption)
                .build();

        return consultingModifySurveyInfo;
    }

    // 컨설팅 설문지 항목 수정
    public void modifySurvey(ReqModifyConsultingSurveyInfoDto dto) throws Exception {
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
                finalImgPath = registerImgUrl(insertImgs.get(0), "consultingSurvey/");
            }

            int consultingId = dto.getConsultingId();

            // 컨설팅 설문 info 수정 - 공통
            ConsultingSurveyInfo modifyConsultingSurveyInfo = ConsultingSurveyInfo.builder()
                    .consultingId(consultingId)
                    .consultingCode(dto.getConsultingCode())
                    .consultingTitle(dto.getConsultingTitle())
                    .consultingSubtitle(dto.getConsultingSubtitle())
                    .consultingImg(finalImgPath)
                    .build();
            consultingManageMapper.modifyConsultingSurveyInfo(modifyConsultingSurveyInfo);

            consultingManageMapper.deleteConsultingSurveyOption(consultingId);

            // 선택지 옵션이 체크박스나 라디오일 때
            if(dto.getConsultingOptionType().equals("checkbox") || dto.getConsultingOptionType().equals("radio")) {
                List<ConsultingSurveyOption> consultingSurveyOptionList = new ArrayList<>();
                for(ReqModifyConsultingSurveyOptionDto surveyOption : dto.getConsultingOption()) {
                    ConsultingSurveyOption consultingSurveyOption = ConsultingSurveyOption.builder()
                            .consultingId(consultingId)
                            .consultingOptionType(dto.getConsultingOptionType())
                            .optionValue(surveyOption.getOptionValue())
                            .optionScore(surveyOption.getOptionScore())
                            .build();
                    consultingSurveyOptionList.add(consultingSurveyOption);
                }
                consultingManageMapper.saveConsultingSurveySelectOption(consultingSurveyOptionList);
            } else {
                ConsultingSurveyOption consultingSurveyOption = ConsultingSurveyOption.builder()
                        .consultingId(consultingId)
                        .consultingOptionType(dto.getConsultingOptionType())
                        .build();
                consultingManageMapper.saveConsultingSurveyNonSelectOption(consultingSurveyOption);
            }
        } catch (Exception e) {
            throw new Exception("컨설팅 설문지 수정 중 오류가 발생했습니다. 잠시후 다시 시도해주세요. (서버 오류)");
        }
    }

    // 컨설팅 설문지 항목 삭제
    public void deleteSurvey(int consultingId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        consultingManageMapper.deleteConsultingSurveyInfo(consultingId);
    }

    // 컨설팅 설문지 항목 복수개 삭제
    public void deleteSurveyList(ReqDeleteConsultingSurveyInfoListDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        List<Integer> consultingIdList = dto.getConsultingIdList();
        consultingManageMapper.deleteConsultingSurveyInfoList(consultingIdList);
    }

    // 컨설팅 결과지 목록 출력
    public RespCountAndLifestyleResultListDto getResultList(ReqGetLifeStyleSurveyResultListDto dto) {
        int startIndex = (dto.getPage() - 1) * dto.getLimit();
        Map<String, Object> params = Map.of(
            "startIndex", startIndex,
            "limit", dto.getLimit(),
            "searchValue", dto.getSearchValue() == null ? "" : dto.getSearchValue()
        );

        List<LifestyleResult> lifeStyleResults = consultingManageMapper.getLifeStyleResultList(params);
        List<RespLifestyleResultDto> lifestyleResultList = new ArrayList<>();
        for(LifestyleResult lifestyleResult : lifeStyleResults) {
            RespLifestyleResultDto lifestyle = RespLifestyleResultDto.builder()
                    .lifestyleResultId(lifestyleResult.getLifestyleResultId())
                    .consultingUpperCategoryName(lifestyleResult.getConsultingUpperCategoryName())
                    .lifestyleResultUnitTitle(lifestyleResult.getLifestyleResultUnitTitle())
                    .lifestyleResultStatus(lifestyleResult.getLifestyleResultStatus())
                    .name(lifestyleResult.getName())
                    .createDate(lifestyleResult.getCreateDate())
                    .build();
            lifestyleResultList.add(lifestyle);
        }

        RespCountAndLifestyleResultListDto lifestyleResultListDto = RespCountAndLifestyleResultListDto.builder()
                .totalCount(lifeStyleResults.get(0).getTotalCount())
                .lifestyleResult(lifestyleResultList)
                .build();
        return lifestyleResultListDto;
    }

    // 컨설팅 결과지 항목 등록
    public void registResult(ReqRegistLifestyleResultDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int registerId = principalUser.getId();

        LifestyleResult lifestyleResult = LifestyleResult.builder()
                .lifestyleResultConsultingUpperCategoryId(dto.getLifestyleResultConsultingUpperCategoryId())
                .lifestyleResultConsultingLowerCategoryId(dto.getLifestyleResultConsultingLowerCategoryId())
                .lifestyleResultUnitTitle(dto.getLifestyleResultUnitTitle())
                .lifestyleResultRegisterId(registerId)
                .lifestyleResultStatus(dto.getLifestyleResultStatus())
                .build();
        consultingManageMapper.saveLifestyleResult(lifestyleResult);

        List<ReqRegistLifestyleResultDetailDto> lifestyleResultDetail = dto.getLifestyleDetail();
        Map<String, Object> params = Map.of(
            "resultId", lifestyleResult.getLifestyleResultId(),
            "lifestyleResultDetailList", lifestyleResultDetail
        );
        consultingManageMapper.saveLifestyleResultDetail(params);
    }

    // 컨설팅 결과지 항목 수정 모달창 출력
    public RespLifestyleResultModifyModalDto getResult(int resultId) {
        LifestyleResult lifestyleResult = consultingManageMapper.getLifestyleResult(resultId);
        List<RespLifestyleResultDetailModifyModalDto> lifestyleResultDetail = new ArrayList<>();

        for(LifestyleResultDetail lifestyleDetail : lifestyleResult.getLifestyleResultDetail()) {
            RespLifestyleResultDetailModifyModalDto lifestyleDetailModify = RespLifestyleResultDetailModifyModalDto.builder()
                    .lifestyleResultDetailId(lifestyleDetail.getLifestyleResultDetailId())
                    .lifestyleResultType(lifestyleDetail.getLifestyleResultType())
                    .lifestyleResultTitle(lifestyleDetail.getLifestyleResultTitle())
                    .lifestyleResultContent(lifestyleDetail.getLifestyleResultContent())
                    .lifestyleResultScoreMin(lifestyleDetail.getLifestyleResultScoreMin())
                    .lifestyleResultScoreMax(lifestyleDetail.getLifestyleResultScoreMax())
                    .build();
            lifestyleResultDetail.add(lifestyleDetailModify);
        }

        RespLifestyleResultModifyModalDto lifestyleResultModify = RespLifestyleResultModifyModalDto.builder()
                .lifestyleResultId(lifestyleResult.getLifestyleResultId())
                .lifestyleResultUnitTitle(lifestyleResult.getLifestyleResultUnitTitle())
                .lifestyleResultConsultingUpperCategoryName(lifestyleResult.getConsultingUpperCategoryName())
                .lifestyleResultConsultingLowerCategoryName(lifestyleResult.getConsultingLowerCategoryName())
                .lifestyleResultStatus(lifestyleResult.getLifestyleResultStatus())
                .lifestyleDetail(lifestyleResultDetail)
                .build();
        return lifestyleResultModify;
    }

    // 컨설팅 결과지 항목 수정
    @Transactional(rollbackFor = Exception.class)
    public void modifyResult(ReqModifyLifestyleResultDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        LifestyleResult lifestyleResult = LifestyleResult.builder()
                .lifestyleResultId(dto.getLifestyleResultId())
                .lifestyleResultUnitTitle(dto.getLifestyleResultUnitTitle())
                .lifestyleResultStatus(dto.getLifestyleResultStatus())
                .build();
        consultingManageMapper.modifyLifestyleResult(lifestyleResult);

        List<ReqModifyLifestyleResultDetailDto> lifestyleDetail = dto.getLifestyleDetail();
        List<LifestyleResultDetail> modifyLifestyleDetail = new ArrayList<>();
        for(ReqModifyLifestyleResultDetailDto lifestyleDetailDto : lifestyleDetail) {
            LifestyleResultDetail lifestyleResultDetail = LifestyleResultDetail.builder()
                    .lifestyleResultDetailId(lifestyleDetailDto.getLifestyleResultDetailId())
                    .lifestyleResultTitle(lifestyleDetailDto.getLifestyleResultTitle())
                    .lifestyleResultContent(lifestyleDetailDto.getLifestyleResultContent())
                    .lifestyleResultScoreMin(lifestyleDetailDto.getLifestyleResultScoreMin())
                    .lifestyleResultScoreMax(lifestyleDetailDto.getLifestyleResultScoreMax())
                    .build();
            modifyLifestyleDetail.add(lifestyleResultDetail);
        }
        consultingManageMapper.modifyLifestyleResultDetail(modifyLifestyleDetail);
    }

    // 컨설팅 결과지 항목 삭제
    public void deleteResult(int resultId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        consultingManageMapper.deleteLifestyleResult(resultId);
    }

    // 컨설팅 결과지 항목 복수개 삭제
    public void deleteResultList(ReqDeleteLifestyleResultListDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        List<Integer> resultIdList = dto.getLifestyleResultIdList();
        consultingManageMapper.deleteLifestyleResultList(resultIdList);
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
