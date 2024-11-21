package LacunaMatata.Lacuna.service.admin;

import LacunaMatata.Lacuna.dto.request.admin.mbti.*;
import LacunaMatata.Lacuna.dto.response.admin.mbti.*;
import LacunaMatata.Lacuna.entity.mbti.Mbti;
import LacunaMatata.Lacuna.entity.mbti.MbtiCategory;
import LacunaMatata.Lacuna.entity.mbti.MbtiOption;
import LacunaMatata.Lacuna.entity.mbti.MbtiResult;
import LacunaMatata.Lacuna.entity.product.ConsultingDetail;
import LacunaMatata.Lacuna.repository.admin.MbtiManageMapper;
import LacunaMatata.Lacuna.security.principal.PrincipalUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;

import javax.mail.Multipart;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MbtiManageService {

    @Autowired
    private MbtiManageMapper mbtiManageMapper;

    @Value("${file.path}")
    private String filePath;

    @Autowired
    private ObjectMapper objectMapper;

    // mbti 분류 카테고리 리스트 출력
    public RespCountAndMbtiCategoryDto getMbtiCategoryList() {

        List<MbtiCategory> mbtiCategoryList = mbtiManageMapper.getMbtiCategoryList();
        List<RespMbtiCategoryListDto> respMbtiCategoryListDtoList = new ArrayList<RespMbtiCategoryListDto>();
        for(MbtiCategory mbtiCategory : mbtiCategoryList){
            RespMbtiCategoryListDto respMbtiCategoryListDto = RespMbtiCategoryListDto.builder()
                    .mbtiCategoryId(mbtiCategory.getMbtiCategoryId())
                    .mbtiCategoryName(mbtiCategory.getMbtiCategoryName())
                    .name(mbtiCategory.getUser().getName())
                    .createdDate(mbtiCategory.getCreateDate())
                    .build();
            respMbtiCategoryListDtoList.add(respMbtiCategoryListDto);
        }
        int totalCount = mbtiCategoryList.isEmpty() ? 0 : mbtiCategoryList.get(0).getTotalCount();

        RespCountAndMbtiCategoryDto respCountAndMbtiCategoryDto = RespCountAndMbtiCategoryDto.builder()
                .totalCount(totalCount)
                .mbtiCategoryList(respMbtiCategoryListDtoList)
                .build();

        return respCountAndMbtiCategoryDto;
    }

    // MBTI 분류 카테고리 출력(필터용)
    public List<RespMbtiCategoryFilterDto> getMbtiCategoryFilterDto() {
        List<MbtiCategory> mbtiCategoryList = mbtiManageMapper.getMbtiCategoryFilter();
        List<RespMbtiCategoryFilterDto> mbtiCategoryFilterDto = new ArrayList<>();
        for(MbtiCategory mbtiCategory : mbtiCategoryList) {
            RespMbtiCategoryFilterDto categoryFilter = RespMbtiCategoryFilterDto.builder()
                    .mbtiCategoryId(mbtiCategory.getMbtiCategoryId())
                    .mbtiCategoryName(mbtiCategory.getMbtiCategoryName())
                    .build();
            mbtiCategoryFilterDto.add(categoryFilter);
        }
        return mbtiCategoryFilterDto;
    }

    // mbti 분류 카테고리 등록
    public void registMbtiCategory(ReqRegistMbtiCategoryDto dto) throws IOException, Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int userId = principalUser.getId();

        try {
            // 이미지 등록
            // 1. 이미지 신규 등록할 공간 생성
            List<MultipartFile> insertImgs = dto.getInsertImgs();
            String insertCompletedImgPath = null;

            // 2. 신규 이미지 저장
            if(insertImgs != null && !insertImgs.get(0).isEmpty()) {
                insertCompletedImgPath = registerImgUrl(insertImgs.get(0), "mbtiCategory/");
            }

            MbtiCategory mbtiCategory = MbtiCategory.builder()
                    .mbtiCategoryRegisterId(userId)
                    .mbtiCategoryName(dto.getMbtiCategoryName())
                    .mbtiCategoryTitle(dto.getMbtiCategoryTitle())
                    .mbtiCategoryDescription(dto.getMbtiCategoryDescription())
                    .mbtiCategoryImg(insertCompletedImgPath)
                    .build();
            mbtiManageMapper.saveMbtiCategory(mbtiCategory);
        } catch (IOException e) {
            throw new IOException("이미지 등록 중 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new Exception("mbti 설문 등록 중 오류가 발생했습니다.");
        }
    }

    // mbti 분류 카테고리  수정 모달창 출력
    public RespMbtiCategoryDto getMbtiCategory(int categoryId) {
        MbtiCategory mbtiCategory = mbtiManageMapper.findMbtiCategoryByCategoryId(categoryId);
        RespMbtiCategoryDto respMbtiCategory = RespMbtiCategoryDto.builder()
                .mbtiCategoryId(mbtiCategory.getMbtiCategoryId())
                .mbtiCategoryName(mbtiCategory.getMbtiCategoryName())
                .mbtiCategoryTitle(mbtiCategory.getMbtiCategoryTitle())
                .mbtiCategoryDescription(mbtiCategory.getMbtiCategoryDescription())
                .mbtiCategoryImg(mbtiCategory.getMbtiCategoryImg())
                .build();
        return respMbtiCategory;
    }

    // mbti 분류 카테고리 수정
    public void modifyMbtiCategory(ReqModifyMbtiCategoryDto dto, int mbtiCategoryId) throws IOException, Exception {
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
                finalImgPath = registerImgUrl(insertImgs.get(0), "mbtiCategory/");
            }

            MbtiCategory modifyMbtiCategory = MbtiCategory.builder()
                    .mbtiCategoryId(dto.getMbtiCategoryId())
                    .mbtiCategoryRegisterId(registerId)
                    .mbtiCategoryName(dto.getMbtiCategoryName())
                    .mbtiCategoryTitle(dto.getMbtiCategoryTitle())
                    .mbtiCategoryDescription(dto.getMbtiCategoryDescription())
                    .mbtiCategoryImg(finalImgPath)
                    .build();

            mbtiManageMapper.modifyMbtiCategory(modifyMbtiCategory);
        } catch (IOException e) {
            throw new IOException("이미지 등록 중 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new Exception("mbti 설문 등록 중 오류가 발생했습니다.");
        }
    }


    // mbti 분류 카테고리 삭제
    public void deleteMbtiCategory(int categoryId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        mbtiManageMapper.deleteMbtiCategory(categoryId);
    }

    // mbti 분류 카테고리 복수개 삭제
    public void deleteMbtiCategoryList(ReqDeleteMbtiCategoryListDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        List<Integer> mbtiCategoryIdList = dto.getMbtiCategoryIdList();
        mbtiManageMapper.deleteMbtiCategoryList(mbtiCategoryIdList);
    }

    // mbti 설문지 리스트 출력
    public RespCountAndMbtiQuestionDto getMbtiQuestionList(ReqGetMbtiQuestionListDto dto) {
        int startIndex = (dto.getPage() - 1) * dto.getLimit();
        Map<String, Object> params = Map.of(
                "filter", dto.getFilter(),
                "option", dto.getOption(),
                "searchValue", dto.getSearchValue() == null ? "" : dto.getSearchValue(),
                "startIndex", startIndex,
                "limit", dto.getLimit()
        );
        List<Mbti> mbtiQuestionList = mbtiManageMapper.getMbtiQuestionList(params);

        List<RespMbtiQuestionListDto> respMbtiQuestionList = new ArrayList<>();
        for(Mbti mbti : mbtiQuestionList) {
            RespMbtiQuestionListDto respMbtiQuestionListDto = RespMbtiQuestionListDto.builder()
                    .mbtiId(mbti.getMbtiId())
                    .mbtiCode(mbti.getMbtiCode())
                    .mbtiCategoryName(mbti.getMbtiCategoryName())
                    .mbtiTitle(mbti.getMbtiTitle())
                    .name(mbti.getUser().getName())
                    .createdDate(mbti.getCreateDate())
                    .build();
            respMbtiQuestionList.add(respMbtiQuestionListDto);
        }
        int totalCount = mbtiQuestionList.isEmpty() ? 0 : mbtiQuestionList.get(0).getTotalCount();

        RespCountAndMbtiQuestionDto respCountAndMbtiQuestionDto = RespCountAndMbtiQuestionDto.builder()
                .totalCount(totalCount)
                .mbtiQuestionList(respMbtiQuestionList)
                .build();

        return respCountAndMbtiQuestionDto;
    }

    // MBTI 설문지 등록 모달창 출력
    public List<RespMbtiCategoryFilterDto> mbtiQuestionRegistModal() {
        List<MbtiCategory> mbtiCategoryList  = mbtiManageMapper.getMbtiCategoryFilter();
        List<RespMbtiCategoryFilterDto> mbtiCategoryFilterDto = new ArrayList<>();
        for(MbtiCategory mbtiCategory : mbtiCategoryList) {
            RespMbtiCategoryFilterDto categoryFilter = RespMbtiCategoryFilterDto.builder()
                    .mbtiCategoryId(mbtiCategory.getMbtiCategoryId())
                    .mbtiCategoryName(mbtiCategory.getMbtiCategoryName())
                    .build();
            mbtiCategoryFilterDto.add(categoryFilter);
        }
        return mbtiCategoryFilterDto;
    }

    // mbti 설문지 항목 등록
    @Transactional(rollbackFor = Exception.class)
    public void registMbtiQuestion(ReqRegistMbtiQuestionDto dto) throws IOException, Exception {
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
            if(insertImgs != null && !insertImgs.get(0).isEmpty()) {
                insertCompletedImgPath = registerImgUrl(insertImgs.get(0), "mbti/");
            }

            Mbti mbti = Mbti.builder()
                    .mbtiCode(dto.getMbtiCode())
                    .mbtiCategoryId(dto.getMbtiCategoryId())
                    .mbtiRegisterId(registerId)
                    .mbtiTitle(dto.getMbtiTitle())
                    .mbtiImg(insertCompletedImgPath)
                    .build();

            mbtiManageMapper.saveMbti(mbti);

            List<ReqRegistOptionDto> optionList = objectMapper.readValue(dto.getOptions(), new TypeReference<>() {});

            Map<String, Object> params = Map.of(
                    "mbtiId", mbti.getMbtiId(),
                    "optionList", optionList
            );
            mbtiManageMapper.saveMbtiOption(params);
        } catch (IOException e) {
            throw new IOException("이미지 등록 중 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new Exception("mbti 설문 등록 중 오류가 발생했습니다.");
        }
    }

    // mbti 설문지 항목 수정 모달창 출력
    public RespMbtiQuestionDto getMbtiQuestion(int mbtiId) {
        RespMbtiQuestionDto mbtiQuestion = mbtiManageMapper.getMbtiQuestion(mbtiId);
        return mbtiQuestion;
    }

    // mbti 설문지 항목 모달 수정
    @Transactional(rollbackFor = Exception.class)
    public void modifyMbtiQuestion(ReqModifyMbtiQuestionDto dto, int mbtiId) throws IOException, Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
                finalImgPath = registerImgUrl(insertImgs.get(0), "mbti/");
            }

            Mbti mbti = Mbti.builder()
                    .mbtiId(dto.getMbtiId())
                    .mbtiCode(dto.getMbtiCode())
                    .mbtiCategoryId(dto.getMbtiCategoryId())
                    .mbtiRegisterId(registerId)
                    .mbtiTitle(dto.getMbtiTitle())
                    .mbtiImg(finalImgPath)
                    .build();

            mbtiManageMapper.modifyMbtiQuestion(mbti);

            mbtiManageMapper.deleteMbtiQuestionOptionList(dto.getMbtiId());

            List<ReqModifyMbtiOptionDto> optionList = objectMapper.readValue(dto.getOptions(), new TypeReference<>() {});
            Map<String, Object> params = Map.of(
                    "mbtiId", dto.getMbtiId(),
                    "optionList", optionList
            );
            mbtiManageMapper.saveMbtiOption(params);
        } catch (IOException e) {
            throw new IOException("이미지 등록 중 오류가 발생했습니다.");
        }
        catch (Exception e) {
            throw new Exception("mbti 설문지 수정 관정 중 오류가 발생했습니다");
        }
    }

    // mbti 설문지 항목 삭제
    public void deleteMbtiQuestion(int mbtiId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        mbtiManageMapper.deleteMbtiQuestion(mbtiId);
    }

    // mbti 설문지 항목 복수개 삭제
    public void deleteMbtiQuestionList(ReqDeleteMbtiQuestionDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        List<Integer> mbtiIdList = dto.getMbtiSurveyIdList();
        mbtiManageMapper.deleteMbtiQuestionList(mbtiIdList);
    }

    // mbti 설문 결과 리스트 출력
    public RespCountAndMbtiResultDto getMbtiResultList(ReqGetMbtiResultDto dto) {
        int startIndex = (dto.getPage() - 1) * dto.getLimit();
        Map<String, Object> params = Map.of(
                "searchValue", dto.getSearchValue() == null ? "" : dto.getSearchValue(),
                "startIndex", startIndex,
                "limit", dto.getLimit()
        );
        List<MbtiResult> mbtiResultList = mbtiManageMapper.getMbtiResultList(params);

        List<RespGetMbtiResultListDto> respGetMbtiResultListDtoList = new ArrayList<>();
        for(MbtiResult mbtiResult : mbtiResultList) {
            RespGetMbtiResultListDto respGetMbtiResultListDto = RespGetMbtiResultListDto.builder()
                    .mbtiResultId(mbtiResult.getMbtiResultId())
                    .mbtiResultCategoryName(mbtiResult.getMbtiResultCategoryName())
                    .mbtiResultTitle(mbtiResult.getMbtiResultTitle())
                    .mbtiResultStatus(mbtiResult.getMbtiResultStatus())
                    .name(mbtiResult.getName())
                    .createdDate(mbtiResult.getCreateDate())
                    .build();
            respGetMbtiResultListDtoList.add(respGetMbtiResultListDto);
        }
        int totalCount = mbtiResultList.isEmpty() ? 0 : mbtiResultList.get(0).getTotalCount();

        RespCountAndMbtiResultDto respCountAndMbtiResultDto = RespCountAndMbtiResultDto.builder()
                .totalCount(totalCount)
                .mbtiResultList(respGetMbtiResultListDtoList)
                .build();
        return respCountAndMbtiResultDto;
    }

    // mbti 설문 결과 항목 등록
    @Transactional(rollbackFor = Exception.class)
    public void registMbtiResult(ReqRegistMbtiResultDto dto) throws IOException {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int registerId = principalUser.getId();

        // 이미지 등록
        // 1. 이미지 신규 등록할 공간 생성
        List<MultipartFile> insertImgs = dto.getInsertImgs();
        String insertCompletedImgPath = null;

        // 2. 신규 이미지 저장
        if(insertImgs != null && !insertImgs.get(0).isEmpty()) {
            insertCompletedImgPath = registerImgUrl(insertImgs.get(0), "mbti/mbtiResult/");
        }

        MbtiResult mbtiResult = MbtiResult.builder()
                .mbtiResultRegisterId(registerId)
                .mbtiResultTitle(dto.getMbtiResultTitle())
                .mbtiResultCategoryName(dto.getMbtiResultCategoryName())
                .mbtiResultSummary(dto.getMbtiResultSummary())
                .mbtiResultContent(dto.getMbtiResultContent())
                .mbtiResultImg(insertCompletedImgPath)
                .mbtiResultStatus(dto.getMbtiResultStatus())
                .build();

        mbtiManageMapper.saveMbtiResult(mbtiResult);

//        // 1. 이미지 신규 등록할 공간 생성
//        List<MultipartFile> insertImgs = dto.getInsertImgs();
//        List<String> insertCompletedImgPaths = new ArrayList<>();
//
//        // 2. 신규 이미지 저장
//        if(insertImgs != null && !insertImgs.get(0).isEmpty()) {
//            for(MultipartFile insertFile: insertImgs) {
//                // Todo 디렉토리 경로 잘 확인해서 넣어야 함
//                insertCompletedImgPaths.add(registerImgUrl(insertFile, "mbti/mbtiResult"));
//            }
//            mbtiManageMapper.insertMbtiResultImgs(insertCompletedImgPaths, mbtiResult.getMbtiResultId());
//        }
    }

    // mbti 살문 결과 항목 수정 모달창 출력
    public RespGetMbtiResultDto getMbtiResultDto(int resultId) {
        MbtiResult mbtiResult = mbtiManageMapper.getMbtiResult(resultId);
        RespGetMbtiResultDto respGetMbtiResultDto = RespGetMbtiResultDto.builder()
                .mbtiResultId(mbtiResult.getMbtiResultId())
                .mbtiResultTitle(mbtiResult.getMbtiResultTitle())
                .mbtiResultCategoryName(mbtiResult.getMbtiResultCategoryName())
                .mbtiResultSummary(mbtiResult.getMbtiResultSummary())
                .mbtiResultContent(mbtiResult.getMbtiResultContent())
                .mbtiResultStatus(mbtiResult.getMbtiResultStatus())
                .mbtiResultImg(mbtiResult.getMbtiResultImg())
                .build();
        return respGetMbtiResultDto;
    }

    // mbti 설문 결과 항목 모달 수정
    @Transactional(rollbackFor = Exception.class)
    public void modifyMbtiResult(ReqModifyMbtiResultDto dto) throws IOException {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int registerId = principalUser.getId();

        /* 이미지 삭제 후 이미지 추가 */
        // 단계 : 1. 신규 등록, 삭제 공간 생성, 2. 이미지 경로 DB 삭제 및 DB 파일 삭제 3. 신규 데이터 등록

//        // 1. 이미지 신규 등록할 공간 생성
//        List<MultipartFile> insertImgs = dto.getInsertImgs();
//        List<String> insertCompletedImgPaths = new ArrayList<>();
//
//        // 1. 이미지 삭제할 공간 생성
//        List<Map<String, Object>> deleteImgs = dto.getDeleteMbtiResultImgs();
//        List<Long> deleteImgIds = deleteImgs.stream().map(deleteImg -> (Long) deleteImg.get("id")).collect(Collectors.toList());
//        List<String> deleteImgPaths = deleteImgs.stream().map(deleteImg -> (String) deleteImg.get("path")).collect(Collectors.toList());
//
//        // 2. 이미지 경로 DB 삭제 및 DB 파일 삭제
//        if(deleteImgs != null && !deleteImgs.get(0).isEmpty()) {
//            // 2-1. 이미지 경로 DB 삭제
//            mbtiManageMapper.deleteMbtiResultImgs(deleteImgIds);
//            // Todo foreach를 돌리는데, where 조건에 id in (deleteImgiIds);
//
//            // 2-2. 이미지 물리 파일 삭제
//            for(String deletePath: deleteImgPaths) {
//                deleteImgUrl(deletePath);
//            }
//        }
//
//        // 3. 신규 이미지 저장
//        if(insertImgs != null && !insertImgs.get(0).isEmpty()) {
//            for(MultipartFile insertFile: insertImgs) {
//                // Todo 디렉토리 경로 잘 확인해서 넣어야 함
//                insertCompletedImgPaths.add(registerImgUrl(insertFile, "mbti/mbtiResult"));
//            }
//            mbtiManageMapper.insertMbtiResultImgs(insertCompletedImgPaths, dto.getMbtiResultId());
//        }
//
//
//        String insertCompletedImgPath = null;
//        MultipartFile insertImg = dto.getInsertImg();
//
//        if(insertImg != null && insertImg.isEmpty()) {
//            insertCompletedImgPath = registerImgUrl(insertImg, "mbti/mbtiResult");
//        }

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
            finalImgPath = registerImgUrl(insertImgs.get(0), "mbti/mbtiResult/");
        }



        MbtiResult mbtiResult = MbtiResult.builder()
                .mbtiResultId(dto.getMbtiResultId())
                .mbtiResultRegisterId(registerId)
                .mbtiResultCategoryName(dto.getMbtiResultCategoryName())
                .mbtiResultTitle(dto.getMbtiResultTitle())
                .mbtiResultSummary(dto.getMbtiResultSummary())
                .mbtiResultContent(dto.getMbtiResultContent())
                .mbtiResultStatus(dto.getMbtiResultStatus())
                .mbtiResultImg(finalImgPath)
                .build();

        mbtiManageMapper.modifyMbtiResult(mbtiResult);
    }

    // mbti 설문 결과 항목 삭제
    public void deleteMbtiResult(int resultId) {
        mbtiManageMapper.deleteMbtiResult(resultId);
    }

    // mbti 설문 결과 복수개 삭제
    public void deleteMbtiResultList(ReqDeleteMbtiResultListDto dto) {
        List<Integer> mbtiResultIdList = dto.getMbtiResultIdList();
        mbtiManageMapper.deleteMbtiResultList(mbtiResultIdList);
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
