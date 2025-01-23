package LacunaMatata.Lacuna.controller.admin;

import LacunaMatata.Lacuna.aspect.annotation.Log;
import LacunaMatata.Lacuna.aspect.annotation.admin.ProductDetailAop;
import LacunaMatata.Lacuna.dto.request.admin.product.*;
import LacunaMatata.Lacuna.dto.response.admin.product.*;
import LacunaMatata.Lacuna.service.admin.ProductManageService;
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
 * description: AdminProductController() - 세팅  *
 * createDate: 2024-10-16                       *
 * updateDate: 2024-10-16                       *
 ************************************************/
@RestController
@Slf4j
@Api(tags = "관리자 - 상품 관리 컨트롤러")
@RequestMapping("/api/v1/admin/product")
public class ProductManageController {

    @Autowired
    private ProductManageService productManageService;

    // 상품 상위 분류 리스트 출력 - 완료
    @GetMapping("/upper/list")
    @ApiOperation(value = "상품 상위 분류 - 상위 분류 리스트 출력")
    public ResponseEntity<?> getUpperProductList() {
        RespCountAndUpperProductDto respCountAndUpperProductDto =
                productManageService.getProductUpperCategory();
        return ResponseEntity.ok().body(respCountAndUpperProductDto);
    }

    // 상품 상위 분류 카테고리 등록
    @PostMapping("/upper/regist")
    @ApiOperation(value = "상품 상위 분류 - 상위 분류 등록")
    public ResponseEntity<?> registUpperProduct(@RequestBody ReqRegistUpperProductCategoryDto dto) throws Exception {
        productManageService.registProductUpperCategory(dto);
        return ResponseEntity.ok().body(true);
    }

    // 상품 상위 분류 항목 출력
    @GetMapping("/upper/{upperId}")
    @ApiOperation(value = "상품 상위 분류 - 상위 분류 수정 모달창 출력")
    public ResponseEntity<?> getUpperProduct(@PathVariable int upperId) {
        RespUpperProductCategoryDto respUpperCategory = productManageService.getProductUpper(upperId);
        return ResponseEntity.ok().body(respUpperCategory);
    }

    // 상품 상위 분류 카테고리 수정
    @PutMapping("/upper/modify/{upperId}")
    @ApiOperation(value = "상품 상위 분류 - 상위 분류 수정")
    public ResponseEntity<?> modifyUpperProduct(@RequestBody ReqModifyUpperProductDto dto, @PathVariable int upperId) throws Exception {
        productManageService.modifyProductUpperCategory(dto, upperId);
        return ResponseEntity.ok().body(true);
    }

    // 상품 상위 분류 카테고리 삭제
    @DeleteMapping("/upper/delete/{upperId}")
    @ApiOperation(value = "상품 상위 분류 - 상위 분류 단일 삭제")
    public ResponseEntity<?> deleteUpperProduct(@PathVariable int upperId) throws Exception {
        productManageService.deleteProductUpperCategory(upperId);
        return ResponseEntity.ok().body(true);
    }

    // 상품 상위 분류 카테고리 복수개 삭제
    @DeleteMapping("/upper/delete")
    @ApiOperation(value = "상품 상위 분류 - 상위 분류 복수 삭제")
    public ResponseEntity<?> deleteUpperProductList(@RequestBody ReqDeleteUpperProductCategoryListDto dto) throws Exception {
        productManageService.deleteProductUpperCategoryList(dto);
        return ResponseEntity.ok().body(true);
    }

    // 상품 하위 분류 리스트 출력
    @GetMapping("/lower/list/{upperId}")
    @ApiOperation(value = "상품 하위 분류 - 하위 분류 리스트 출력")
    public ResponseEntity<?> getLowerProductList(@PathVariable int upperId) {
        RespCountAndLowerProductDto respCountAndLowerProductDto =
                productManageService.getProductlowerCategory(upperId);
        return ResponseEntity.ok().body(respCountAndLowerProductDto);
    }

    // 상품 하위 분류 등록 모달창 출력
    @GetMapping("/lower/regist/modal")
    @ApiOperation(value = "상품 하위 분류 - 하위 분류 등록 모달창 출력")
    public ResponseEntity<?> getLowerCategiryRegistModal() {
        List<RespUpperProductCategoryDto> productUpperCategoryList = productManageService.lowerRegistCategoryModal();
        return ResponseEntity.ok().body(productUpperCategoryList);
    }

    // 상품 하위 분류 카테고리 등록
    @PostMapping("/lower/regist")
    @ApiOperation(value = "상품 하위 분류 - 하위 분류 등록")
    public ResponseEntity<?> registLowerProduct(@RequestBody ReqRegistLowerProductCategoryDto dto) throws Exception {
        System.out.println("요청 들어옴?" + dto);
        productManageService.registProductlowerCategory(dto);
        return ResponseEntity.ok().body(true);
    }

    // 상품 하위 분류 카테고리 수정 모달창 출력
    @GetMapping("/lower/{lowerId}")
    @ApiOperation(value = "상품 하위 분류 - 하위 분류 수정 모달창 출력")
    public ResponseEntity<?> getLowerProduct(@PathVariable int lowerId) {
        RespLowerProductCategoryDto respLowerCategory = productManageService.getProductLower(lowerId);
        return ResponseEntity.ok().body(respLowerCategory);
    }

    // 상품 하위 분류 카테고리 수정
    @PutMapping("/lower/modify/{lowerId}")
    @ApiOperation(value = "상품 하위 분류 - 하위 분류 수정")
    public ResponseEntity<?> modifyLowerProduct(@RequestBody ReqModifyLowerProductCategoryDto dto, @PathVariable int lowerId) throws Exception {
        productManageService.modifyProductlowerCategory(dto);
        return ResponseEntity.ok().body(true);
    }

    // 상품 하위 분류 카테고리 삭제
    @DeleteMapping("/lower/delete/{lowerId}")
    @ApiOperation(value = "상품 하위 분류 - 하위 분류 단일 삭제")
    public ResponseEntity<?> deleteLowerProduct(@PathVariable int lowerId) throws Exception {
        productManageService.deleteProductlowerCategory(lowerId);
        return ResponseEntity.ok().body(true);
    }

    // 상품 하위 분류 카테고리 복수개 삭제
    @DeleteMapping("/lower/delete")
    @ApiOperation(value = "상품 하위 분류 - 하위 분류 복수 삭제")
    public ResponseEntity<?> deleteLowerProductList(@RequestBody ReqDeleteLowerProductCategoryListDto dto) throws Exception {
        productManageService.deleteProductlowerCategoryList(dto);
        return ResponseEntity.ok().body(true);
    }

    // 상품 리스트 출력
    @GetMapping("/list")
    @ApiOperation(value = "상품 - 상품 리스트 출력")
    public ResponseEntity<?> getProductList(ReqGetProductListDto dto) {
        RespCountAndProductDto respCountAndProductDto = productManageService.getProducts(dto);
    return ResponseEntity.ok().body(respCountAndProductDto);
    }

    // 상품 등록 모달창 출력
    @GetMapping("/regist/modal")
    @ApiOperation(value = "상품 - 상품 등록 모달창 출력")
    public ResponseEntity<?> getRegistModal() {
        return ResponseEntity.ok().body(productManageService.getRegistModal());
    }

    // 상품 등록
    @PostMapping("/regist")
    @ApiOperation(value = "상품 - 상품 등록")
    public ResponseEntity<?> registProduct(@ModelAttribute ReqRegistProductDto dto) throws Exception {
        productManageService.registProduct(dto);
        return ResponseEntity.ok().body(true);
    }

    // 상품 수정 모달창 출력
    @ProductDetailAop
    @GetMapping("/{productId}")
    @ApiOperation(value = "상품 - 상품 수정 모달창 출력")
    public ResponseEntity<?> getProduct(@PathVariable int productId) {
        return ResponseEntity.ok().body(productManageService.getProductDetail(productId));
    }

    // 상품 수정
    @PostMapping("/modify/{productId}")
    @ApiOperation(value = "상품 - 상품 수정")
    public ResponseEntity<?> modifyProduct(@ModelAttribute ReqModifyProductDto dto, @PathVariable int productId) throws Exception {
        productManageService.modifyProduct(dto);
        return ResponseEntity.ok().body(true);
    }

    // 상품 삭제
    @DeleteMapping("/delete/{productId}")
    @ApiOperation(value = "상품 - 상품 단일 삭제")
    public ResponseEntity<?> deleteProduct(@PathVariable int productId) throws Exception {
        productManageService.deleteProduct(productId);
        return ResponseEntity.ok().body(true);
    }

    // 상품 복수개 삭제
    @DeleteMapping("/delete")
    @ApiOperation(value = "상품 - 상품 복수 삭제")
    public ResponseEntity<?> deleteProductList(@RequestBody ReqDeleteProductDto dto) throws Exception {
        productManageService.deleteProductList(dto);
        return ResponseEntity.ok().body(true);
    }

    // 상품 상위 분류 출력(필터용)
    @GetMapping("/upper/filter")
    @ApiOperation(value = "상품 상위 분류 - 상품 상위 분류 출력(필터)")
    public ResponseEntity<?> getUpperProductFilter() {
        List<RespUpperProductCategoryDto> upperFilter = productManageService.getUpperProductFilter();
        return ResponseEntity.ok().body(upperFilter);
    }

    // 상품 하위 분류 출력(필터용)
    @GetMapping("/lower/filter/{upperId}")
    @ApiOperation(value = "상품 하위 분류 - 상품 하위 분류 출력(필터)")
    public ResponseEntity<?> getLowerProductFilter(@PathVariable int upperId) {
        List<RespLowerProductCategoryDto> lowerFilter = productManageService.getLowerProductFilter(upperId);
        return ResponseEntity.ok().body(lowerFilter);
    }
}
