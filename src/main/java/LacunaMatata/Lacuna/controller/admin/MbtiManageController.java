package LacunaMatata.Lacuna.controller.admin;

import LacunaMatata.Lacuna.dto.request.admin.mbti.*;
import LacunaMatata.Lacuna.dto.response.admin.mbti.*;
import LacunaMatata.Lacuna.service.admin.MbtiManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/************************************************
 * version: 1.0.4                               *
 * author: 손경태                                *
 * description: AdminMbtiController() - 세팅     *
 * createDate: 2024-10-16                       *
 * updateDate: 2024-10-27                       *
 ************************************************/
@RestController
@RequestMapping("/api/v1/admin/mbti")
@Api(tags = {"관리자 - MBTI 관리 컨트롤러"})
@Slf4j
public class MbtiManageController {

    @Autowired
    private MbtiManageService mbtiManageService;

    // MBTI 분류 카테고리 리스트 출력
    @ApiOperation(value = "MBTI 분류 카테고리 - 리스트 출력") // test 완료(이상 없음)
    @GetMapping("/survey/category/list")
    public ResponseEntity<?> getMbtiCategoryList() {
        RespCountAndMbtiCategoryDto respCountAndMbtiCategoryDto = mbtiManageService.getMbtiCategoryList();
        return ResponseEntity.ok().body(respCountAndMbtiCategoryDto);
    }

    // MBTI 분류 카테고리 등록
    @ApiOperation(value = "MBTI 분류 카테고리 - 등록") // test 완료 (이상 없음)
    @PostMapping("/survey/category/regist")
    public ResponseEntity<?> registMbtiCategory(@ModelAttribute ReqRegistMbtiCategoryDto dto) throws IOException, Exception {
        mbtiManageService.registMbtiCategory(dto);
        return ResponseEntity.ok().body(true);
    }

    //MBTI 분류 카테고리 수정 모달창 조회
    @ApiOperation(value = "MBTI 분류 카테고리 - 수정 모달창 출력")
    @GetMapping("/survey/category/{categoryId}") // test 완료 (이상 없음)
    public ResponseEntity<?> getMbtiCategory(@PathVariable int categoryId) {
        RespMbtiCategoryDto mbtiCategory = mbtiManageService.getMbtiCategory(categoryId);
        return ResponseEntity.ok().body(mbtiCategory);
    }

    //MBTI 분류 카테고리 출력(필터용)
    @GetMapping("/survey/category/filter")
    @ApiOperation(value = "MBTI 분류 카테고리 - 필터 출력") // test 완료 (이상 없음)
    public ResponseEntity<?> getMbtiCategoryListFilter() {
        List<RespMbtiCategoryFilterDto> mbtiCategoryList = mbtiManageService.getMbtiCategoryFilterDto();
        return ResponseEntity.ok().body(mbtiCategoryList);
    }

    // MBTI 분류 카테고리 수정
    @ApiOperation(value = "MBTI 분류 카테고리 - 수정")
    @PostMapping("/survey/category/modify/{categoryId}") // test 완료 (이상 없음)
    public ResponseEntity<?> modifyMbtiCategory(@ModelAttribute ReqModifyMbtiCategoryDto dto, @PathVariable int categoryId) throws IOException, Exception {
        mbtiManageService.modifyMbtiCategory(dto, categoryId);
        return ResponseEntity.ok().body(true);
    }

    // MBTI 분류 카테고리 삭제
    @ApiOperation(value = "MBTI 분류 카테고리 - 단일 삭제")
    @DeleteMapping("/survey/category/delete/{categoryId}") // test ㅇ놔료(이상 없음)
    public ResponseEntity<?> deleteMbtiCategory(@PathVariable int categoryId) throws Exception {
        mbtiManageService.deleteMbtiCategory(categoryId);
        return ResponseEntity.ok().body(true);
    }

    // MBTI 분류 카테고리 복수개 삭제
    @ApiOperation(value = "MBTI 분류 카테고리 - 복수 삭제")
    @DeleteMapping("/survey/category/delete")
    public ResponseEntity<?> deleteMbtiCategoryList(@RequestBody ReqDeleteMbtiCategoryListDto dto) throws Exception {
        mbtiManageService.deleteMbtiCategoryList(dto);
        return ResponseEntity.ok().body(true);
    }

    // MBTI 설문 항목 리스트 출력
    @ApiOperation(value = "MBTI 설문지 - 리스트 출력") // test 완료 (이상 없음)
    @GetMapping("/question/list")
    public ResponseEntity<?> getMbtiQuestionList(ReqGetMbtiQuestionListDto dto) {
        RespCountAndMbtiQuestionDto respCountAndMbtiQuestionDto = mbtiManageService.getMbtiQuestionList(dto);
        return ResponseEntity.ok().body(respCountAndMbtiQuestionDto);
    }

    // MBTI 설문 등록 모달창 출력
    @GetMapping("/question/regist/modal")
    @ApiOperation(value = "MBTI 설문지 - 등록 모달창 출력") // test 완료 (이상 없음)
    public ResponseEntity<?> mbtiQuestionRegistModal() {
        return ResponseEntity.ok().body(mbtiManageService.mbtiQuestionRegistModal());
    }

    // MBTI 설문 항목 등록
    @ApiOperation(value = "MBTI 설문지 - 등록") // 이싱 있음
    @PostMapping("/question/regist")
    public ResponseEntity<?> registMbtiQuestion(@ModelAttribute ReqRegistMbtiQuestionDto dto) throws IOException, Exception {
        mbtiManageService.registMbtiQuestion(dto);
        return ResponseEntity.ok().body(true);
    }

    // MBTI 설문 항목 수정 모달창 출력
    @ApiOperation(value = "MBTI 설문지 - 수정 모달창 출력") // test 완료 (이상 없음)
    @GetMapping("/question/{mbtiId}")
    public ResponseEntity<?> getMbtiQuestion(@PathVariable int mbtiId) {
        RespMbtiQuestionDto mbtiQuestion = mbtiManageService.getMbtiQuestion(mbtiId);
        return ResponseEntity.ok().body(mbtiQuestion);
    }

    // MBTI 설문 항목 모달 수정
    @ApiOperation(value = "MBTI 설문지 - 수정") // 이상 있음
    @PostMapping("/question/modify/{mbtiId}")
    public ResponseEntity<?> modifyMbtiQuestion(@ModelAttribute ReqModifyMbtiQuestionDto dto, @PathVariable int mbtiId) throws IOException, Exception {
        mbtiManageService.modifyMbtiQuestion(dto, mbtiId);
        return ResponseEntity.ok().body(true);
    }

    // MBTI 설문 항목 삭제
    @ApiOperation(value = "MBTI 설문지 - 단일 삭제")
    @DeleteMapping("/question/delete/{mbtiId}")
    public ResponseEntity<?> deleteMbtiQuestion(@PathVariable int mbtiId) throws Exception {
        mbtiManageService.deleteMbtiQuestion(mbtiId);
        return ResponseEntity.ok().body(true);
    }

    // MBTI 설문 항목 복수개 삭제
    @ApiOperation(value = "MBTI 설문지 - 복수 삭제")
    @DeleteMapping("/question/delete")
    public ResponseEntity<?> deleteMbtiQuestionList(@RequestBody ReqDeleteMbtiQuestionDto dto) throws Exception {
        mbtiManageService.deleteMbtiQuestionList(dto);
        return ResponseEntity.ok().body(true);
    }

    // MBTI 설문 결과 리스트 출력
    @ApiOperation(value = "MBTI 결과지 - 리스트 출력") // test 완료 (이상 없음)
    @GetMapping("/result/list")
    public ResponseEntity<?> getMbtiResultList(ReqGetMbtiResultDto dto) {
        RespCountAndMbtiResultDto respCountAndMbtiResultDto = mbtiManageService.getMbtiResultList(dto);
        return ResponseEntity.ok().body(respCountAndMbtiResultDto);
    }

    // MBTI 설문 결과 등록
    @ApiOperation(value = "MBTI 결과지 - 등록")
    @PostMapping("/result/regist")
    public ResponseEntity<?> registMbtiResult(@ModelAttribute ReqRegistMbtiResultDto dto) throws IOException {
        mbtiManageService.registMbtiResult(dto);
        return ResponseEntity.ok().body(true);
    }

    // MBTI 설문 결과 항목 수정 모달창 출력
    @ApiOperation(value = "MBTI 결과지 -수정 모달창 출력")
    @GetMapping("/result/{resultId}")
    public ResponseEntity<?> getMbtiResult(@PathVariable int resultId) {
        RespGetMbtiResultDto mbtiResultDto = mbtiManageService.getMbtiResultDto(resultId);
        return ResponseEntity.ok().body(mbtiResultDto);
    }

    // MBTI 설문 결과 항목 수정
    @ApiOperation(value = "MBTI 결과지 - 수정")
    @PostMapping("/result/modify/{resultId}")
    public ResponseEntity<?> modifyMbtiResult(@ModelAttribute ReqModifyMbtiResultDto dto, @PathVariable int resultId) throws IOException {
        mbtiManageService.modifyMbtiResult(dto);
        return ResponseEntity.ok().body(true);
    }

    // MBTI 설문 결과 삭제
    @ApiOperation(value = "MBTI 결과지 - 단일 삭제")
    @DeleteMapping("/result/delete/{resultId}")
    public ResponseEntity<?> deleteMbtiResult(@PathVariable int resultId) {
        mbtiManageService.deleteMbtiResult(resultId);
        return ResponseEntity.ok().body(true);
    }

    // MBTI 설문 결과 복수개 삭제
    @ApiOperation(value = "MBTI 결과지 - 복수 삭제")
    @DeleteMapping("/result/delete")
    public ResponseEntity<?> deleteMbtiResultList(@RequestBody ReqDeleteMbtiResultListDto dto) {
        mbtiManageService.deleteMbtiResultList(dto);
        return ResponseEntity.ok().body(true);
    }
}
