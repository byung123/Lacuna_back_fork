package LacunaMatata.Lacuna.controller.user;

import LacunaMatata.Lacuna.dto.request.user.mbti.ReqMbtiAnswerDto;
import LacunaMatata.Lacuna.dto.response.user.mbti.RespMbtiStatusAndResultDto;
import LacunaMatata.Lacuna.dto.response.user.mbti.RespMbtiSurveyResultDto;
import LacunaMatata.Lacuna.service.user.MbtiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/mbti/survey")
@Api(tags = {"사용자 - MBTI 컨트롤러"})
public class MbtiController {

    @Autowired
    private MbtiService mbtiService;

    // mbti 설문지 기본 정보(피부고민 리스트) 출력
    @GetMapping("/question/skinproblem")
    @ApiOperation(value = "MBTI 설문지 - 피부고민 리스트 출력")
    public ResponseEntity<?> getMbtiSkinProblem() {
        return ResponseEntity.ok().body(mbtiService.getMbtiSkinProblemList());
    }

    // mbti 설문지 출력
    @GetMapping("/question")
    @ApiOperation(value = "MBTI 설문지 - MBTI 설문지 리스트 출력")
    public ResponseEntity<?> getMbtiSurvey() {
        return ResponseEntity.ok().body(mbtiService.getmbtiSurvey());
    }

    // mbti 설문 답안 등록
    @PostMapping("/submit")
    @ApiOperation(value = "MBTI 설문지 - 설문 응답 등록")
    public ResponseEntity<?> submitMbti(@RequestBody ReqMbtiAnswerDto dto, HttpServletRequest request) throws Exception {
        int mbtiResultId = mbtiService.submitMbti(dto, request);
        return ResponseEntity.ok().body(mbtiResultId);
    }

    // mbti 설문 결과 출력
    @GetMapping("/result/{resultId}")
    @ApiOperation(value = "MBTI 결과지 - MBTI 결과 출력")
    public ResponseEntity<?> getMbtiResult(@PathVariable int resultId) {
        RespMbtiStatusAndResultDto mbtiResult = mbtiService.getMbtiResult(resultId);
        if(mbtiResult.getMbtiResultStatus() == 2) {
            return ResponseEntity.ok().body(null);
        }
        return ResponseEntity.ok().body(mbtiResult.getMbtiResult());
    }
}
