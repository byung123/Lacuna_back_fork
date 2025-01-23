package LacunaMatata.Lacuna.service.user;

import LacunaMatata.Lacuna.dto.request.user.auth.ReqAuthEmailDto;
import LacunaMatata.Lacuna.dto.request.user.purchase.ReqOrderConsultingProductDto;
import LacunaMatata.Lacuna.dto.request.user.purchase.ReqOrderConsultingProductItemDto;
import LacunaMatata.Lacuna.dto.response.user.purchase.consultingProductDetail.RespConsultingProductDetailDto;
import LacunaMatata.Lacuna.dto.response.user.purchase.consultingProductList.RespConsultingListDto;
import LacunaMatata.Lacuna.dto.response.user.purchase.consultingProductList.RespConsultingProductDto;
import LacunaMatata.Lacuna.dto.response.user.purchase.consultingProductList.RespProductLowerCategoryDto;
import LacunaMatata.Lacuna.dto.response.user.purchase.consultingProductList.RespProductUpperCategoryDto;
import LacunaMatata.Lacuna.entity.order.Order;
import LacunaMatata.Lacuna.entity.order.OrderItem;
import LacunaMatata.Lacuna.entity.order.Payment;
import LacunaMatata.Lacuna.entity.product.Product;
import LacunaMatata.Lacuna.entity.user.User;
import LacunaMatata.Lacuna.exception.user.NotMatchPriceException;
import LacunaMatata.Lacuna.exception.user.NotSelectItemException;
import LacunaMatata.Lacuna.repository.user.PurchaseMapper;
import LacunaMatata.Lacuna.security.principal.PrincipalUser;
import LacunaMatata.Lacuna.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private AuthService authService;

    @Autowired
    private PurchaseMapper purchaseMapper;

    // 회원 컨설팅 상품 목록 출력
    public List<RespConsultingListDto> getConsultingProductList() {
        RespProductUpperCategoryDto productUpperCategory = purchaseMapper.getConsultingProductList();
        List<RespConsultingListDto> consultingProductList = new ArrayList<>();
        for(RespProductLowerCategoryDto resp : productUpperCategory.getProductLowerCategory()) {
            for(RespConsultingProductDto consulting : resp.getConsultingProduct()) {
                RespConsultingListDto consultingProduct = RespConsultingListDto.builder()
                        .productId(consulting.getProductId())
                        .productName(consulting.getProductName())
                        .subtitle(consulting.getSubtitle())
                        .price(consulting.getPrice())
                        .promotionPrice(consulting.getPromotionPrice())
                        .description(consulting.getDescription())
                        .build();
                consultingProductList.add(consultingProduct);
            }
        }
        return consultingProductList;
    }

    // 회원 컨설팅 상품 항목 자세히 보기
    public RespConsultingProductDetailDto getConsultingProductDetail(int productId) {
        RespConsultingProductDetailDto consultingProduct = purchaseMapper.getConsultingProductDetail(productId);
        return consultingProduct;
    }

    // 회원 컨설팅 상품 구매하기 누르기 - 시스템 결제 계좌이체 동일
    @Transactional(rollbackFor = Exception.class)
    public void orderConsultingProduct(ReqOrderConsultingProductDto dto) throws Exception {
        PrincipalUser principalUser =
                (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        int userId = principalUser.getId();

        List<String> productStringIdList = dto.getProducts().stream().map(product -> product.getId()).collect(Collectors.toList());
        List<Integer> productIdList = productStringIdList.stream().filter(productId -> productId != null && !productId.trim().isEmpty())
                .map(productId -> productId.replace("\"", ""))
                .map(Integer::parseInt).collect(Collectors.toList());
        if(productIdList.isEmpty()) {
            throw new NotSelectItemException("선택하신 상품이 없습니다. 상품을 선택해주세요.");
        }

        List<Product> products = purchaseMapper.findProductByProductId(productIdList);

        List<ReqOrderConsultingProductItemDto> orderProductItemList = dto.getProducts();
        List<Integer> quantityList = orderProductItemList.stream().map(orderProduct -> orderProduct.getQuantity()).collect(Collectors.toList());
        List<BigDecimal> priceList = products.stream().map(product -> product.getPromotionPrice() == null ?  product.getPrice() : product.getPromotionPrice())
                .collect(Collectors.toList());
        BigDecimal totalPrice = BigDecimal.ZERO;

        for(int i = 0; i < priceList.size(); i++) {
            BigDecimal multiplyPrice = priceList.get(i).multiply(BigDecimal.valueOf(quantityList.get(i)));
            totalPrice = totalPrice.add(multiplyPrice);
        }

        // DB에 저장되어 있는 가격정보를 합한 값과 사용자가 주문한 가격이 다르면 오류
        if(!totalPrice.equals(dto.getTotalAmount())) {
            throw new NotMatchPriceException("주문 가격 오류");
        }

        try {
            // 주문 정보 저장
            Order order = Order.builder()
                    .orderUserId(userId)
                    .totalAmount(dto.getTotalAmount())
                    .status(dto.getPaymentStatus())
                    .build();
            purchaseMapper.saveOrder(order);

            // 주문 정보 안에 각각의 주문 item 목록들 저장
            List<OrderItem> orderItemList = new ArrayList<>();

            for(ReqOrderConsultingProductItemDto item : orderProductItemList ) {
                OrderItem orderItem = OrderItem.builder()
                        .orderId(order.getOrderId())
                        .orderProductId(Integer.parseInt(item.getId() != null && !item.getId().trim().isEmpty() ? item.getId().replace("\"", "") : "0"))
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getAmount())
                        .build();
                orderItemList.add(orderItem);
            }
            purchaseMapper.saveOrderItem(orderItemList);

            // Payment 정보 저장

            if(!dto.getPaymentMethod().equals("transfer")) {
                Payment payment = Payment.builder()
                        .paymentOrderId(order.getOrderId())
                        .paymentApproveId(dto.getPaymentApproveId())
                        .paymentMethod(dto.getPaymentMethod())
                        .paymentStatus(dto.getPaymentStatus())
                        .amount(dto.getTotalAmount())
                        .build();
                purchaseMapper.savePayment(payment);
            } else {
                LocalDateTime now = LocalDateTime.now();
                String paymentApproveId = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                Payment payment = Payment.builder()
                        .paymentOrderId(order.getOrderId())
                        .paymentApproveId(paymentApproveId)
                        .paymentMethod(dto.getPaymentMethod())
                        .paymentStatus(dto.getPaymentStatus())
                        .amount(dto.getTotalAmount())
                        .build();
                purchaseMapper.savePayment(payment);
            }

            // 결제 등급 수정
            User user = purchaseMapper.findUserByUserId(userId);

            if(user.getMaxRoleId() < 4) {
                List<Integer> roleIdList = new ArrayList<>();
                if(user.getMaxRoleId() == 2) {
                    for(int i = 3; i < 5; i++) {
                        roleIdList.add(i);
                    }
                    Map<String, Object> params = Map.of(
                            "userId", userId,
                            "roleIdList", roleIdList
                    );
                    purchaseMapper.saveUserRoleMet(params);
                    purchaseMapper.modifyUserRoleMet(params);
                } else {
                    roleIdList.add(4);
                    Map<String, Object> params = Map.of(
                            "userId", userId,
                            "roleIdList", roleIdList
                    );
                    purchaseMapper.saveUserRoleMet(params);
                    purchaseMapper.modifyUserRoleMet(params);
                }
            }

            String toEmail = dto.getEmail();

            // 이메일 입력 받지 않으면 메서드 종료
            if(toEmail == null) {
                return;
            }

            List<String> productNameList = products.stream().map(product -> product.getProductName()).collect(Collectors.toList());

            // 이메일
            LocalDateTime now = LocalDateTime.now();
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<div style='display:flex;justify-content:flex-start;align-items:center;flex-direction:column;"
                    + "width:400px'>");
            htmlContent.append("<h2>Lacuna 회원님이 주문하신 상품의 목록 정보 입니다.</h2>");
            htmlContent.append("<h3>주문 상품</h3>");
            htmlContent.append("<h4>주문 ID: ");
            htmlContent.append(order.getOrderId());
            htmlContent.append("</h4>");
            htmlContent.append("<h4>주문자명: ");
            htmlContent.append(user.getName());
            htmlContent.append("</h4>");
            htmlContent.append("<h4>주문 금액: ");
            htmlContent.append(dto.getTotalAmount());
            htmlContent.append("원</h4>");
            htmlContent.append("<h4>주문 상태: ");
            htmlContent.append(dto.getPaymentStatus());
            htmlContent.append("</h4>");
            htmlContent.append("<h4>주문일: ");
            htmlContent.append(now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")));
            htmlContent.append("</h4>");
            htmlContent.append("<h4>주문 상세</h4>");
            htmlContent.append("<div style='display:flex;justify-content:center;align-items:center;flex-direction:column;"
                    + "width:100%'>");
            for(int i = 0; i < products.size(); i++) {
                htmlContent.append("<h4>상품   ");
                htmlContent.append(productNameList.get(i));
                htmlContent.append("   수량   ");
                htmlContent.append(quantityList.get(i));
                htmlContent.append("   가격   ");
                htmlContent.append(priceList.get(i));
                htmlContent.append("원");
                htmlContent.append("</h4>");
                htmlContent.append("</div>");
            }
            htmlContent.append("</div>");

            authService.send(toEmail, "Lacuna 컨설팅 주문 정보 이메일 발송 ", htmlContent.toString());
        } catch (Exception e) {
            throw new Exception("주문하는 도중 오류가 발생했습니다. 잠시후 다시 시도해주세요. (서버 오류)");
        }
    }
}
