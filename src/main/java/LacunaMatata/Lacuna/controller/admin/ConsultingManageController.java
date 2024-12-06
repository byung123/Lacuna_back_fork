package LacunaMatata.Lacuna.controller.admin;

import LacunaMatata.Lacuna.dto.request.admin.Consulting.*;
import LacunaMatata.Lacuna.service.admin.ConsultingManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/************************************************
 * version: 1.0.4                               *
 * author: 정령우                                *
 * description: AdminMbtiController() - 세팅     *
 * createDate: 2024-10-30                       *
 * updateDate: 2024-10-30                       *
 ************************************************/

@RestController
@RequestMapping("/api/v1/admin/consulting")
@Api(tags = {"관리자 - 컨설팅관리 컨트롤러 - 아직 미구현..(TBC)"})
public class ConsultingManageController {

    @Autowired
    private ConsultingManageService consultingManageService;

    // 컨설팅 상위 분류 목록 출력
    @GetMapping("/upper/list")
    @ApiOperation(value = "getUpperConsultingListApi")
    public ResponseEntity<?> getUpperConsultingListApi(ReqGetConsultingUpperCategoryListDto dto) {
        return ResponseEntity.ok().body(consultingManageService.getUpperConsultingList(dto));
    }

    // 컨설팅 상위 분류 항목 출력(필터)
    @GetMapping("/upper/filter")
    @ApiOperation(value = "getUpperConsultingListFilterApi")
    public ResponseEntity<?> getUpperConsultingListFilter() {
        return ResponseEntity.ok().body(consultingManageService.getUpperConsultingFilter());
    }

    // 컨설팅 상위 분류 항목 등록
    @PostMapping("/upper/regist")
    @ApiOperation(value = "registUpperConsultingApi")
    public ResponseEntity<?> registUpperConsulting(@ModelAttribute ReqRegistUpperConsultingCategoryDto dto) throws Exception {
        consultingManageService.registUpperConsulting(dto);
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 상위 분류 수정 모달창 출력
    @GetMapping("/upper/{upperId}")
    @ApiOperation(value = "getUpperConsultingApi")
    public ResponseEntity<?> getUpperConsulting(@PathVariable int upperId) {
        return ResponseEntity.ok().body(consultingManageService.getUpperConsulting(upperId));
    }

    // 컨설팅 상위 분류 항목 수정
    @PostMapping("/upper/modify/{upperId}")
    @ApiOperation(value = "modifyUpperConsultingApi")
    public ResponseEntity<?> modifyUpperConsulting(@ModelAttribute ReqModifyUpperConsulingCategoryDto dto, @PathVariable int upperId) throws Exception {
        consultingManageService.modifyUpperConsulting(dto, upperId);
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 상위 분류 항목 삭제
    @DeleteMapping("/upper/delete/{upperId}")
    @ApiOperation(value = "deleteUpperConsultingApi")
    public ResponseEntity<?> deleteUpperConsulting(@PathVariable int upperId) throws Exception {
        consultingManageService.deleteUpperConsulting(upperId);
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 상위 분류 항목 복수개 삭제
    @DeleteMapping("/upper/delete")
    @ApiOperation(value = "deleteUpperConsultingListApi")
    public ResponseEntity<?> deleteUpperConsultingList(@RequestBody ReqDeleteConsultingUpperCategoryListDto dto) throws Exception {
        consultingManageService.deleteUpperConsultingList(dto);
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 하위 분류 목록 출력
    @GetMapping("/lower/list/{upperId}")
    public ResponseEntity<?> getLowerConsultingListApi(@PathVariable int upperId) {
        return ResponseEntity.ok().body(consultingManageService.getLowerConsultingList(upperId));
    }

    // 컨설팅 하위 분류 항목 출력 (필터)
    @GetMapping("/lower/list/filter/{upperId}")
    public ResponseEntity<?> getLowerConsultingListFilter(@PathVariable int upperId) {
        consultingManageService.getLowerConsultingFilter();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 하위 분류 항목 등록
    @PostMapping("/lower/regist")
    public ResponseEntity<?> registLowerConsulting(@RequestBody ReqRegistLowerConsultingCategoryDto dto) throws Exception {
        consultingManageService.registLowerConsulting(dto);
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 하위 분류 수정 모달창 출력
    @GetMapping("/lower/{lowerId}")
    public ResponseEntity<?> getLowerConsulting(@PathVariable int lowerId) {
        return ResponseEntity.ok().body(consultingManageService.getLowerConsulting(lowerId));
    }

    // 컨설팅 하위 분류 항목 수정
    @PutMapping("/lower/modify/{lowerId}")
    public ResponseEntity<?> modifyLowerConsulting(@PathVariable int lowerId, @RequestBody ReqModifyLowerConsultingCategoryDto dto) throws Exception {
        consultingManageService.modifyLowerConsulting(dto);
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 하위 분류 항목 삭제
    @DeleteMapping("/lower/delete/{upperId}")
    public ResponseEntity<?> deleteLowerConsulting(@PathVariable int lowerId) throws Exception {
        consultingManageService.deleteLowerConsulting(lowerId);
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 하위 분류 항목 복수개 삭제
    @DeleteMapping("/lower/delete")
    public ResponseEntity<?> deleteLowerConsultingList(@RequestBody ReqDeleteConsultingLowerCategoryListDto dto) throws Exception {
        consultingManageService.deleteLowerConsultingList(dto);
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 설문지 목록 출력
    @GetMapping("/survey/list")
    public ResponseEntity<?> getSurveyConsultingListApi() {
        consultingManageService.getSurveyList();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 설문지 선택지 타입 항목 출력
    @GetMapping("/survey/option/filter/{consultingId}")
    public ResponseEntity<?> getSurveyConsultingOptionFilter(@PathVariable int consultingId) {
        consultingManageService.getSurveyOption();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 설문지 항목 등록
    @PostMapping("survey/regist")
    public ResponseEntity<?> registSurveyConsulting() {
        consultingManageService.registSurvey();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 설문지 항목 출력
    @GetMapping("/survey/{consultingId}")
    public ResponseEntity<?> getSurveyConsulting(@PathVariable int consultingId) {
        consultingManageService.getSurvey();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 설문지 항목 수정
    @PutMapping("/survey/modify")
    public ResponseEntity<?> modifySurveyConsulting() {
        consultingManageService.modifySurvey();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 설문지 항목 삭제
    @DeleteMapping("/survey/delete/{consultingId}")
    public ResponseEntity<?> deleteSurveyConsulting(@PathVariable int consultingId) {
        consultingManageService.deleteSurvey();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 설문지 항목 복수개 삭제
    @DeleteMapping("/survey/delete")
    public ResponseEntity<?> deleteSurveyConsultingList() {
        consultingManageService.deleteSurveyList();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 결과지 목록 출력
    @GetMapping("/survey/result/list")
    public ResponseEntity<?> getResultConsultingListApi() {
        consultingManageService.getResultList();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 결과지 항목 등록
    @PostMapping("/survey/result/regist")
    public ResponseEntity<?> registResultConsulting() {
        consultingManageService.registResult();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 결과지 항목 출력
    @GetMapping("/survey/result/{resultId}")
    public ResponseEntity<?> getResultConsulting(@PathVariable int resultId) {
        consultingManageService.getResult();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 결과지 항목 수정
    @PutMapping("/survey/result/modify/{resultId}")
    public ResponseEntity<?> modifyResultConsulting( ) {
        consultingManageService.modifyResult();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 결과지 항목 삭제
    @DeleteMapping("/survey/result/delete/{resultId}")
    public ResponseEntity<?> deleteResultConsulting(@PathVariable int resultId) {
        consultingManageService.deleteResult();
        return ResponseEntity.ok().body(true);
    }

    // 컨설팅 결과지 항목 복수개 삭제
    @DeleteMapping("/survey/result/delete")
    public ResponseEntity<?> deleteResultConsultingList() {
        consultingManageService.deleteResultList();
        return ResponseEntity.ok().body(true);
    }
}
