package LacunaMatata.Lacuna.service.admin;


import LacunaMatata.Lacuna.dto.request.admin.product.*;
import LacunaMatata.Lacuna.dto.response.admin.product.*;
import LacunaMatata.Lacuna.entity.product.*;
import LacunaMatata.Lacuna.repository.admin.ProductManageMapper;
import LacunaMatata.Lacuna.security.principal.PrincipalUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductManageService {

    @Autowired
    private ProductManageMapper productManageMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${file.path}")
    private String filePath;

    // 상품 상위 분류 리스트 출력
    public RespCountAndUpperProductDto getProductUpperCategory() {
        List<ProductUpperCategory> productUpperCategoryList = productManageMapper.getProductUpperCategoryList();
        List<RespUpperProductCategoryListDto> productUpperCategory = new ArrayList<RespUpperProductCategoryListDto>();
        for(ProductUpperCategory productUpperCategoryDto : productUpperCategoryList) {
            RespUpperProductCategoryListDto respUpperProductCategoryListDto = RespUpperProductCategoryListDto.builder()
                    .productUpperCategoryId(productUpperCategoryDto.getProductUpperCategoryId())
                    .productUpperCategoryName(productUpperCategoryDto.getProductUpperCategoryName())
                    .name(productUpperCategoryDto.getUser().getName())
                    .createdDate(productUpperCategoryDto.getCreateDate())
                    .build();
            productUpperCategory.add(respUpperProductCategoryListDto);
        }
        int totalCount = productUpperCategoryList.isEmpty() ? 0 : productUpperCategoryList.get(0).getTotalCount();
        RespCountAndUpperProductDto respCountAndUpperProductDto = RespCountAndUpperProductDto.builder()
                .totalCount(totalCount)
                .productUpperCategoryList(productUpperCategory)
                .build();

        return respCountAndUpperProductDto;
    }

    // 상품 상위 분류 카테고리 등록
    public void registProductUpperCategory(ReqRegistUpperProductCategoryDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        int registerId = principalUser.getId();
        ProductUpperCategory productUpperCategory = ProductUpperCategory.builder()
                .productUpperCategoryName(dto.getProductUpperCategoryName())
                .productUpperCategoryRegisterId(registerId)
                .build();

        try {
            productManageMapper.saveProductUpperCategory(productUpperCategory);
        } catch (Exception e) {
            throw new Exception("상품 상위 분류를 등록하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 상위 분류 카테고리 항목 출력
    public RespUpperProductCategoryDto getProductUpper(int upperId) {
        ProductUpperCategory respUpperCategory = productManageMapper.getproductUpperDto(upperId);
        RespUpperProductCategoryDto upperCategory = RespUpperProductCategoryDto.builder()
                .productUpperCategoryId(respUpperCategory.getProductUpperCategoryId())
                .productUpperCategoryName(respUpperCategory.getProductUpperCategoryName())
                .build();
        return upperCategory;
    }

    // 상품 상위 분류 카테고리 수정
    public void modifyProductUpperCategory(ReqModifyUpperProductDto dto, int upperId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        int registerId = principalUser.getId();
        ProductUpperCategory productUpperCategory = ProductUpperCategory.builder()
                .productUpperCategoryId(dto.getProductUpperCategoryId())
                .productUpperCategoryName(dto.getProductUpperCategoryName())
                .productUpperCategoryRegisterId(registerId)
                .build();

        try {
            productManageMapper.modifyProductUpperCategory(productUpperCategory);
        } catch (Exception e) {
            throw new Exception("상품 상위 분류를 수정하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 상위 분류 카테고리 단일 삭제
    public void deleteProductUpperCategory(int upperId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        try {
            productManageMapper.deleteUpperProductCategory(upperId);
        } catch (Exception e) {
            throw new Exception("상품 상위 분류를 삭제하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 상위 분류 카테고리 복수개 삭제
    public void deleteProductUpperCategoryList(ReqDeleteUpperProductCategoryListDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        List<Integer> upperIdList = dto.getUpperCategoryIdList();

        try {
            productManageMapper.deleteUpperProductCategoryList(upperIdList);
        } catch (Exception e) {
            throw new Exception("상품 상위 분류를 삭제하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 하위 분류 리스트 출력
    public RespCountAndLowerProductDto getProductlowerCategory(int upperId) {
        List<ProductLowerCategory> productLowerCategoryList = productManageMapper.getProductLowerCategoryList(upperId);
        List<RespLowerProductCategoryListDto> productLowerCategory = new ArrayList<RespLowerProductCategoryListDto>();
        for(ProductLowerCategory productLowerCategoryDto : productLowerCategoryList) {
            RespLowerProductCategoryListDto respLowerProductCategoryListDto = RespLowerProductCategoryListDto.builder()
                    .productLowerCategoryId(productLowerCategoryDto.getProductLowerCategoryId())
                    .productLowerCategoryName(productLowerCategoryDto.getProductLowerCategoryName())
                    .name(productLowerCategoryDto.getUser().getName())
                    .createdDate(productLowerCategoryDto.getCreateDate())
                    .build();
            productLowerCategory.add(respLowerProductCategoryListDto);
        }
        int totalCount = productLowerCategoryList.isEmpty() ? 0 : productLowerCategoryList.get(0).getTotalCount();

        RespCountAndLowerProductDto respCountAndLowerProductDto = RespCountAndLowerProductDto.builder()
                .totalCount(totalCount)
                .productLowerCategoryList(productLowerCategory)
                .build();

        return respCountAndLowerProductDto;
    }

    //상품 하위 분류 등록 모달창 출력
    public List<RespUpperProductCategoryDto> lowerRegistCategoryModal() {
        List<ProductUpperCategory> productUpperCategoryList = productManageMapper.getProductUpperCategoryList();
        List<RespUpperProductCategoryDto> respProductUpperCategoryList = new ArrayList<>();
        for (ProductUpperCategory productUpperCategory : productUpperCategoryList) {
            RespUpperProductCategoryDto respUpperProductCategoryDto = RespUpperProductCategoryDto.builder()
                    .productUpperCategoryId(productUpperCategory.getProductUpperCategoryId())
                    .productUpperCategoryName(productUpperCategory.getProductUpperCategoryName())
                    .build();
            respProductUpperCategoryList.add(respUpperProductCategoryDto);
        }
        return respProductUpperCategoryList;
    }

    // 상품 하위 분류 카테고리 등록
    public void registProductlowerCategory(ReqRegistLowerProductCategoryDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int registerId = principalUser.getId();

        ProductLowerCategory productLowerCategory = ProductLowerCategory.builder()
                .productLowerCategoryName(dto.getProductLowerCategoryName())
                .productLowerCategoryRegisterId(registerId)
                .productUpperCategoryId(dto.getProductUpperCategoryId())
                .build();

        try {
            productManageMapper.saveProductLowerCategory(productLowerCategory);
        } catch (Exception e) {
            throw new Exception("상품 하위 분류를 등록하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 하위 분류 카테고리 수정 모달창 출력
    public RespLowerProductCategoryDto getProductLower(int lowerId) {
        ProductLowerCategory respLowerCategory = productManageMapper.getProductLowerDto(lowerId);
        RespLowerProductCategoryDto lowerCategory = RespLowerProductCategoryDto.builder()
                .productLowerCategoryId(respLowerCategory.getProductLowerCategoryId())
                .productUpperCategoryId(respLowerCategory.getProductUpperCategoryId())
                .productLowerCategoryName(respLowerCategory.getProductLowerCategoryName())
                .productUpperCategoryName(respLowerCategory.getProductUpperCategoryName())
                .build();
        System.out.println(lowerCategory);
        return lowerCategory;
    }

    // 상품 하위 분류 카테고리 수정
    public void modifyProductlowerCategory(ReqModifyLowerProductCategoryDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int registerId = principalUser.getId();

        ProductLowerCategory productLowerCategory = ProductLowerCategory.builder()
                .productLowerCategoryId(dto.getProductLowerCategoryId())
                .productLowerCategoryName(dto.getProductLowerCategoryName())
                .productLowerCategoryRegisterId(registerId)
                .build();

        try {
            productManageMapper.modifyProductLowerCategory(productLowerCategory);
        } catch (Exception e) {
            throw new Exception("상품 하위 분류를 수정하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 하위 분류 카테고리 삭제
    public void deleteProductlowerCategory(int lowerId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        try {
            productManageMapper.deleteProductLowerCategory(lowerId);
        } catch (Exception e) {
            throw new Exception("상품 하위 분류를 삭제하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 하위 분류 카테고리 복수개 삭제
    public void deleteProductlowerCategoryList(ReqDeleteLowerProductCategoryListDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        List<Integer> lowerIdList = dto.getLowerCategoryIdList();

        try {
            productManageMapper.deleteProductLowerCategoryList(lowerIdList);
        } catch (Exception e) {
            throw new Exception("상품 하위 분류를 삭제하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 리스트 출력
    public RespCountAndProductDto getProducts(ReqGetProductListDto dto) {
        int startIndex = (dto.getPage() - 1) * dto.getLimit();
        Map<String, Object> params = Map.of(
                "filter", dto.getFilter(),
                "option", dto.getOption(),
                "searchValue", dto.getSearchValue() == null ? "" : dto.getSearchValue(),
                "startIndex", startIndex,
                "limit", dto.getLimit()
        );
        List<Product> productList = productManageMapper.getProductList(params);
        List<RespProductListDto> respProductListDtoList = new ArrayList<RespProductListDto>();
        for(Product product : productList) {
            RespProductListDto respProductListDto = RespProductListDto.builder()
                    .productId(product.getProductId())
                    .productCode(product.getProductCode())
                    .productUpperCategoryName(product.getProductUpperCategory().getProductUpperCategoryName())
                    .productName(product.getProductName())
                    .price(product.getPrice())
                    .promotionPrice(product.getPromotionPrice())
                    .name(product.getUser().getName())
                    .createdDate(product.getCreateDate())
                    .build();
            respProductListDtoList.add(respProductListDto);
        }
        int totalCount = productList.isEmpty() ? 0 : productList.get(0).getTotalCount();

        RespCountAndProductDto respCountAndProductDto = RespCountAndProductDto.builder()
                .totalCount(totalCount)
                .productList(respProductListDtoList)
                .build();

        return respCountAndProductDto;
    }

    // 상품 등록 모달창 출력
    public RespProductRegistModalDto getRegistModal() {
        List<RespUpperProductCategoryAndLowerDto> productUpperAndLower = productManageMapper.getProductUpperAndLowerCategoryList();
        List<ConsultingContent> consultingContent = productManageMapper.getConsultingContent();
        RespProductRegistModalDto respProductRegistModalDto = RespProductRegistModalDto.builder()
                .upperProductCategoryAndLower(productUpperAndLower)
                .productConsultingContentList(consultingContent)
                .build();
        return respProductRegistModalDto;
    }

    // 상품 등록
    @Transactional(rollbackFor = Exception.class)
    public void registProduct(ReqRegistProductDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int registeId = principalUser.getId();

        // 이미지 등록
        // 1. 이미지 신규 등록할 공간 생성
        List<MultipartFile> insertImgs = dto.getInsertImgs();
        String insertCompletedImgPath = null;

        try {
            // 2. 신규 이미지 저장
            if(insertImgs != null && !insertImgs.get(0).isEmpty()) {
                insertCompletedImgPath = registerImgUrl(insertImgs.get(0), "product/");
            }

            Product product = Product.builder()
                    .productProductUpperCategoryId(dto.getProductUpperCategoryId())
                    .productLowerCategoryId(dto.getProductLowerCategoryId())
                    .productCode(dto.getProductCode())
                    .productName(dto.getProductName())
                    .subtitle(dto.getSubtitle())
                    .price(BigDecimal.valueOf(dto.getPrice()))
                    .promotionPrice(BigDecimal.valueOf(dto.getPromotionPrice()))
                    .productImg(insertCompletedImgPath)
                    .productRegisterId(registeId)
                    .description(dto.getDescription())
                    .etc(dto.getEtc())
                    .build();

            productManageMapper.saveProduct(product);

            // Todo 상품 세부 정보 컨설팅 분류에 맞게 넣는거 알아서 짜봐여. (dto, enttiy 확인 및 수정 필요
            // Todo insert 된 id는 useGenerator 사용하면 build한 엔티티 변수에 들어있는 것을 사용하면 됨 (위 이미지 넣은 방법 참조)
            switch (dto.getProductUpperCategoryId()) {
                case 1:
                    // ConsultingContent 내용 들어갈 곳
                    List<ConsultingDetail> consultingDetails = objectMapper.readValue(dto.getConsultingContent(), new TypeReference<>() {});

                    productManageMapper.saveConsultingDetail(consultingDetails.stream().map(consultingDetail -> {
                        consultingDetail.setConsultingDetailProductId(product.getProductId());
                        return consultingDetail;
                    }).collect(Collectors.toList()));
                    break;
                case 2:
                    CosmeticDetail cosmeticDetail = CosmeticDetail.builder()
                            .cosmeticDetailProductId(product.getProductId())
                            .volume(dto.getVolume())
                            .ingredient(dto.getIngredient())
                            .skinType(dto.getSkinType())
                            .effect(dto.getEffect())
                            .manufacture(dto.getManufacture())
                            .productUrl(dto.getProductUrl())
                            .build();
                    productManageMapper.saveCosmeticDetail(cosmeticDetail);
                    break;
            }
        } catch (Exception e) {
            throw new Exception("상품을 등록하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 항목 출력(수정 모달창) -> ProductDetailAspect 로 넘겨줌
    public RespProductDetailDto getProductDetail(int productId) {
        Product product = productManageMapper.getProduct(productId);
        RespProductDetailDto productDetail = RespProductDetailDto.builder()
                .productId(product.getProductId())
                .productUpperCategoryId(product.getProductUpperCategory().getProductUpperCategoryId())
                .productUpperCategoryName(product.getProductUpperCategory().getProductUpperCategoryName())
                .productLowerCategoryName(product.getProductLowerCategory().getProductLowerCategoryName())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .subtitle(product.getSubtitle())
                .price(product.getPrice())
                .promotionPrice(product.getPromotionPrice())
                .productImg(product.getProductImg())
                .description(product.getDescription())
                .etc(product.getEtc())
                .build();
        System.out.println(productDetail);
        return productDetail;
    }

    // 상품 수정
    @Transactional(rollbackFor = Exception.class)
    public void modifyProduct(ReqModifyProductDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int registerId = principalUser.getId();

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

        try {
            // 이미지 등록
            // 1. 이미지 수정할 공간 생성
            if(insertImgs != null && !insertImgs.get(0).isEmpty()) {
                finalImgPath = registerImgUrl(insertImgs.get(0), "product/");
            }

            Product product = Product.builder()
                    .productId(dto.getProductId())
                    .productProductUpperCategoryId(dto.getProductUpperCategoryId())
                    .productLowerCategoryId(dto.getProductLowerCategoryId())
                    .productCode(dto.getProductCode())
                    .productName(dto.getProductName())
                    .subtitle(dto.getSubtitle())
                    .price(BigDecimal.valueOf(dto.getPrice()))
                    .promotionPrice(BigDecimal.valueOf(dto.getPromotionPrice()))
                    .productImg(finalImgPath)
                    .productRegisterId(registerId)
                    .description(dto.getDescription())
                    .etc(dto.getEtc())
                    .build();
            productManageMapper.modifyProduct(product);

            if(product.getProductProductUpperCategoryId() == 1) {

                // Todo 나중에 확인 필요
                List<ConsultingDetail> consultingDetails = objectMapper.readValue(dto.getConsultingContent(), new TypeReference<>() {});
                productManageMapper.modifyConsultingDetail(consultingDetails);
            }

            if(product.getProductProductUpperCategoryId() == 2) {
                CosmeticDetail cosmeticDetail = CosmeticDetail.builder()
                        .cosmeticDetailId(dto.getCosmeticDetailId())
                        .cosmeticDetailProductId(dto.getCosmeticDetailProductId())
                        .volume(dto.getVolume())
                        .ingredient(dto.getIngredient())
                        .skinType(dto.getSkinType())
                        .effect(dto.getEffect())
                        .manufacture(dto.getManufacture())
                        .productUrl(dto.getProductUrl())
                        .build();
                productManageMapper.modifyCosmeticDetail(cosmeticDetail);
            }
        } catch (Exception e) {
            throw new Exception("상품을 수정하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 삭제
    public void deleteProduct(int productId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        try {
            productManageMapper.deleteProduct(productId);
        } catch (Exception e) {
            throw new Exception("상품을 삭제하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 복수개 삭제
    public void deleteProductList(ReqDeleteProductDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        List<Integer> productIdList =  dto.getProductIdList();

        try {
            productManageMapper.deleteProductList(productIdList);
        } catch (Exception e) {
            throw new Exception("상품을 삭제하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 상품 상위 카테고리 분류 출력(필터용)
    public List<RespUpperProductCategoryDto> getUpperProductFilter() {
        List<ProductUpperCategory> upperFilter = productManageMapper.getProductUpperCategoryList();
        List<RespUpperProductCategoryDto> upperFilterList = new ArrayList<>();
        for(ProductUpperCategory productUpperCategory : upperFilter) {
            RespUpperProductCategoryDto upperFilter2 = RespUpperProductCategoryDto.builder()
                    .productUpperCategoryId(productUpperCategory.getProductUpperCategoryId())
                    .productUpperCategoryName(productUpperCategory.getProductUpperCategoryName())
                    .build();
            upperFilterList.add(upperFilter2);
        }
        return upperFilterList;
    }

    // 상품 하위 카테고리 분류 출력(필터용)
    public List<RespLowerProductCategoryDto> getLowerProductFilter(int upperId) {
        List<ProductLowerCategory> lowerFilter = productManageMapper.getProductLowerCategoryList(upperId);
        List<RespLowerProductCategoryDto> lowerFilterList = new ArrayList<RespLowerProductCategoryDto>();
        for(ProductLowerCategory productLowerCategory : lowerFilter) {
            RespLowerProductCategoryDto lowerFilter2 = RespLowerProductCategoryDto.builder()
                    .productLowerCategoryId(productLowerCategory.getProductLowerCategoryId())
                    .productLowerCategoryName(productLowerCategory.getProductLowerCategoryName())
                    .build();
            lowerFilterList.add(lowerFilter2);
        }
        return lowerFilterList;
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
