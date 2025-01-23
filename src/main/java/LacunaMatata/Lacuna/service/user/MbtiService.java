package LacunaMatata.Lacuna.service.user;

import LacunaMatata.Lacuna.dto.request.user.mbti.ReqMbtiAnswerDto;
import LacunaMatata.Lacuna.dto.request.user.mbti.ReqMbtiQuestionDto;
import LacunaMatata.Lacuna.dto.request.user.mbti.ReqMbtiSurveyDto;
import LacunaMatata.Lacuna.dto.response.user.mbti.*;
import LacunaMatata.Lacuna.entity.mbti.MbtiResponse;
import LacunaMatata.Lacuna.entity.mbti.MbtiResult;
import LacunaMatata.Lacuna.entity.mbti.MbtiSkinConcern;
import LacunaMatata.Lacuna.repository.user.MbtiMapper;
import LacunaMatata.Lacuna.security.principal.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MbtiService {

    @Autowired
    private MbtiMapper mbtiMapper;

    @Autowired
    private StatisticService statisticService;

    // 설문지 기본정보(피부고민 리스트) 출력
    public List<MbtiSkinConcern> getMbtiSkinProblemList() {
        List<MbtiSkinConcern> mbtiSkinConcernList = mbtiMapper.getMbtiSkinConcernList();
        return mbtiSkinConcernList;
    }

    // mbti 설문지 출력
    public List<RespMbtiSurveyDto> getmbtiSurvey() {
        List<RespMbtiSurveyDto> mbtiCategoryList = mbtiMapper.getMbtiSurvey();
        return mbtiCategoryList;
    }

    // mbti 설문 답안 등록
    public int submitMbti(ReqMbtiAnswerDto dto, HttpServletRequest request) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        int gender = dto.getGender();
        Date birthDate = dto.getBirth();
        int skinProblemOne = dto.getSkinProblemOne();
        int skinProblemTwo = dto.getSkinProblemTwo();

        int mbtiResultId = caculateMbtiResult(dto);

        List<MbtiResponse> mbtiResponseList = new ArrayList<>();

        // authentication이 null이거나, principal이 PrincipalUser 인스턴스가 아닐 경우 비회원으로 간주하고 null 반환
        if (authentication == null || !(authentication.getPrincipal() instanceof PrincipalUser)) {
            HttpSession session = request.getSession();
            String sessionId = session.getId();

            for(ReqMbtiSurveyDto surveyDto : dto.getMbtiResult()) {
                int mbtiCategoryId = surveyDto.getMbtiCategoryId();

                for(ReqMbtiQuestionDto questionDto : surveyDto.getMbti()) {
                    MbtiResponse mbtiResponse = MbtiResponse.builder()
                            .sessionId(sessionId)
                            .gender(gender)
                            .birth(birthDate)
                            .skinProblemOne(skinProblemOne)
                            .skinProblemTwo(skinProblemTwo)
                            .responseMbtiCategoryId(mbtiCategoryId)
                            .responseMbtiId(questionDto.getMbtiId())
                            .mbtiOptionId(questionDto.getOptionId())
                            .mbtiResultId(mbtiResultId)
                            .build();
                    mbtiResponseList.add(mbtiResponse);
                }
            }

            try {
                mbtiMapper.saveNonUserMbtiSurvey(mbtiResponseList);
                return mbtiResultId;
            } catch (Exception e) {
                throw new Exception("mbti 결과정보를 등록하는 도중 오류가 발생했습니다. (서버 오류)");
            }
        }

        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        for(ReqMbtiSurveyDto surveyDto : dto.getMbtiResult()) {
            int mbtiCategoryId = surveyDto.getMbtiCategoryId();

            for(ReqMbtiQuestionDto questionDto : surveyDto.getMbti()) {
                MbtiResponse mbtiResponse = MbtiResponse.builder()
                        .mbtiResponseUserId(principalUser.getId())
                        .gender(gender)
                        .birth(birthDate)
                        .skinProblemOne(skinProblemOne)
                        .skinProblemTwo(skinProblemTwo)
                        .responseMbtiCategoryId(mbtiCategoryId)
                        .responseMbtiId(questionDto.getMbtiId())
                        .mbtiOptionId(questionDto.getOptionId())
                        .mbtiResultId(mbtiResultId)
                        .build();
                mbtiResponseList.add(mbtiResponse);
            }
        }

        try {
            mbtiMapper.saveUserMbtiSurvey(mbtiResponseList);
        } catch (Exception e) {
            throw new Exception("mbti 결과정보를 등록하는 도중 오류가 발생했습니다. (서버 오류)");
        }

        // 등록 후 결과값 반환
        return mbtiResultId;
    }

    // mbti 설문 결과 출력
    public RespMbtiStatusAndResultDto getMbtiResult(int mbtiResultId) {
        MbtiResult mbtiSurveyResult = mbtiMapper.getMbtiSurveyResult(mbtiResultId);
        RespMbtiSurveyResultDto mbtiResult = RespMbtiSurveyResultDto.builder()
                .mbtiResultId(mbtiSurveyResult.getMbtiResultId())
                .mbtiResultCategoryName(mbtiSurveyResult.getMbtiResultCategoryName())
                .mbtiResultTitle(mbtiSurveyResult.getMbtiResultTitle())
                .mbtiResultSummary(mbtiSurveyResult.getMbtiResultSummary())
                .mbtiResultContent(mbtiSurveyResult.getMbtiResultContent())
                .mbtiResultImg(mbtiSurveyResult.getMbtiResultImg())
                .totalSubject(mbtiSurveyResult.getTotalSubject())
                .subject(mbtiSurveyResult.getSubject())
                .build();

        RespMbtiStatusAndResultDto statusAndResult = RespMbtiStatusAndResultDto.builder()
                .mbtiResultStatus(mbtiSurveyResult.getMbtiResultStatus())
                .mbtiResult(mbtiResult)
                .build();

        return statusAndResult;
    }

    private int caculateMbtiResult(ReqMbtiAnswerDto dto) {
        List<ReqMbtiSurveyDto> mbtiResult = dto.getMbtiResult();
        List<RespMbtiCategoryIdDto> mbtiOptionScoreList = mbtiMapper.getMbtiOptionScore(mbtiResult);

        List<String> mbtiResultType = new ArrayList<>();
        for(RespMbtiCategoryIdDto mbtiCategory : mbtiOptionScoreList) {

            int mbtiCategoryId = mbtiCategory.getMbtiCategoryId();
            int totalScore = 0;
            switch (mbtiCategoryId) {
                case 1:
                    for(RespMbtiOptionIdDto mbtiOption : mbtiCategory.getMbti()) {
                        int optionScore = mbtiOption.getOptionScore();
                        totalScore += optionScore;
                    }
                    if(totalScore > 0) {
                        mbtiResultType.add("D");
                    } else {
                        mbtiResultType.add("O");
                    }
                    break;
                case 2:
                    for(RespMbtiOptionIdDto mbtiOption : mbtiCategory.getMbti()) {
                        int optionScore = mbtiOption.getOptionScore();
                        totalScore += optionScore;
                    }
                    if(totalScore > 0) {
                        mbtiResultType.add("R");
                    } else {
                        mbtiResultType.add("S");
                    }
                    break;
                case 3:
                    for(RespMbtiOptionIdDto mbtiOption : mbtiCategory.getMbti()) {
                        int optionScore = mbtiOption.getOptionScore();
                        totalScore += optionScore;
                    }
                    if(totalScore > 0) {
                        mbtiResultType.add("N");
                    } else {
                        mbtiResultType.add("P");
                    }
                    break;
                case 4:
                    for(RespMbtiOptionIdDto mbtiOption : mbtiCategory.getMbti()) {
                        int optionScore = mbtiOption.getOptionScore();
                        totalScore += optionScore;
                    }
                    if(totalScore > 0) {
                        mbtiResultType.add("T");
                    } else {
                        mbtiResultType.add("W");
                    }
                    break;
                default:
                    break;
            }
        }
        String mbtiResultName = "";
        for(int i = 0; i < mbtiResultType.size(); i++) {
            String resultType = mbtiResultType.get(i);
            mbtiResultName = mbtiResultName.concat(resultType);
        }
        int mbtiResultId = mbtiMapper.getMbtiResultId(mbtiResultName);

        return mbtiResultId;
    }
}
