package LacunaMatata.Lacuna.service.admin;

import LacunaMatata.Lacuna.dto.request.admin.order.ReqApprovePaymentAccountDto;
import LacunaMatata.Lacuna.dto.request.admin.order.ReqCancelOrderAccountDto;
import LacunaMatata.Lacuna.dto.request.admin.order.ReqDeleteOrderListDto;
import LacunaMatata.Lacuna.dto.request.admin.order.ReqGetOrderListDto;
import LacunaMatata.Lacuna.dto.response.admin.order.*;
import LacunaMatata.Lacuna.entity.order.Order;
import LacunaMatata.Lacuna.entity.order.OrderItem;
import LacunaMatata.Lacuna.entity.order.Payment;
import LacunaMatata.Lacuna.repository.admin.OrderManageMapper;
import LacunaMatata.Lacuna.security.principal.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderManageService {

    @Autowired
    private OrderManageMapper orderManageMapper;

    // 회원 주문 정보 리스트 출력
    public RespCountAndOderListDto getOrderList(ReqGetOrderListDto dto) {
        int startIndex = (dto.getPage() - 1) * dto.getLimit();
        Map<String, Object> params = Map.of(
                "filter", dto.getFilter(),
                "option", dto.getOption(),
                "searchValue", dto.getSearchValue() == null ? "" : dto.getSearchValue(),
                "startIndex", startIndex,
                "limit", dto.getLimit()
        );
        List<Order> orderList = orderManageMapper.getOrderList(params);
        List<RespGetOrderListDto> respGetOrderListDtos = new ArrayList<>();
        for(Order order : orderList) {
            RespGetOrderListDto respGetOrderListDto = RespGetOrderListDto.builder()
                    .orderId(order.getOrderId())
                    .paymentId(order.getPaymentId())
                    .name(order.getName())
                    .roleName(order.getRoleName())
                    .totalAmount(order.getTotalAmount())
                    .createdDate(order.getCreatedDate())
                    .status(order.getStatus())
                    .build();
            respGetOrderListDtos.add(respGetOrderListDto);
        }
        int totalCount = orderList.isEmpty() ? 0 : orderList.get(0).getTotalCount();

        RespCountAndOderListDto respCountAndOderListDto = RespCountAndOderListDto.builder()
                .totalCount(totalCount)
                .orderList(respGetOrderListDtos)
                .build();

        return respCountAndOderListDto;
    }

    // 회원 주문 정보 상태 출력(필터)
    public List<RespGetOrderStatusFilterDto> getOrderStatusFilter() {
        List<Order> orderFilter = orderManageMapper.getOrderStatusFilter();
        List<RespGetOrderStatusFilterDto> orderStatusFilter = new ArrayList<>();
        for(Order order : orderFilter) {
            RespGetOrderStatusFilterDto orderFilter2 = RespGetOrderStatusFilterDto.builder()
                    .orderId(order.getOrderId())
                    .statusName(order.getStatus())
                    .build();
            orderStatusFilter.add(orderFilter2);
        }
        return orderStatusFilter;
    }

    // 회원 주문 항목 상세 출력
    public RespGetOrderDetailDto getOrderDetail(int orderId) {
        RespGetOrderDetailDto orderDetail = orderManageMapper.findOrderById(orderId);
        return orderDetail;
    }

    // 회원 결제 항목 상세 출력
    public RespGetPaymentDetailDto getPaymentDetail(int paymentId) {
        Payment payment = orderManageMapper.getPaymentDetail(paymentId);
        RespGetPaymentDetailDto respGetPaymentDetailDto = RespGetPaymentDetailDto.builder()
                .paymentId(payment.getPaymentId())
                .paymentApproveId(payment.getPaymentApproveId())
                .name(payment.getName())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .createdDate(payment.getCreatedDate())
                .build();
        return respGetPaymentDetailDto;
    }

    // 회원 결제 취소하기 (결제 취소하기) - 계좌 시스템 공통
    @Transactional(rollbackFor = Exception.class)
    public void cancelSystemOrder(int paymentId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        try {
            orderManageMapper.cancelSystemPayment(paymentId);
            orderManageMapper.cancelSystemOrder(paymentId);
        } catch (Exception e) {
            throw new Exception("회원님의 결제 항목을 취소하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

//    // 주문 수정 - 회원 결제 취소하기 (계좌 이체)    // 시스템 결제와 동일
//    @Transactional(rollbackFor = Exception.class)
//    public void cancelAccountOrder(ReqCancelOrderAccountDto dto) {
//        int orderId = dto.getOrderId();
//        LocalDateTime now = LocalDateTime.now();
//        String paymentApproveId = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//        Map<String, Object> params = Map.of(
//                "orderId", orderId,
//                "paymentApproveId", paymentApproveId
//        );
////        orderManageMapper.cancelAccountPayment(params); // 없애기로함
////        orderManageMapper.cancelAccountOrder(orderId); // 없애기로함
//        orderManageMapper.cancelSystemPayment(orderId);
//    }

    // 주문 수정 - 회원 결제 승인하기 (계좌 이체)
    @Transactional(rollbackFor = Exception.class)
    public void approveAccountOrder(int orderId ,ReqApprovePaymentAccountDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        BigDecimal amount = dto.getAmount();
//        LocalDateTime now = LocalDateTime.now();
//        String paymentApproveId = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Payment payment = orderManageMapper.getPaymentAmount(orderId);

        if(!payment.getAmount().equals(amount)) {
            throw new Exception("결제 금액이 맞지 않습니다.");
        }

        try {
            orderManageMapper.approveAccountPayment(orderId);
            orderManageMapper.approveAccountOrder(orderId);
        } catch (Exception e) {
            throw new Exception("회원님의 결제 항목을 취소하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 회원 주문 항목 삭제
    public void deleteOrder(int orderId) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }

        try {
            orderManageMapper.deleteOrder(orderId);
        } catch (Exception e) {
            throw new Exception("회원님의 주문 항목을 삭제하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }

    // 회원 주문 항목 복수개 삭제
    public void deleteOrderList(ReqDeleteOrderListDto dto) throws Exception {
        PrincipalUser principalUser = (PrincipalUser)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principalUser == null) {
            throw new Exception("로그인 시간이 만료되었습니다. 다시 로그인 후 이용해주시기 바랍니다.");
        }
        List<Integer> orderIdList = dto.getOrderIdList();

        try {
            orderManageMapper.deleteOrderList(orderIdList);
        } catch (Exception e) {
            throw new Exception("회원님의 주문 항목을 삭제하는 도중 오류가 발생했습니다. (서버 오류)");
        }
    }
}
