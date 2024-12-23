package LacunaMatata.Lacuna.entity.consulting;

import LacunaMatata.Lacuna.entity.order.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsultingMemberInfo {
    private int consultingMemberId;
    private int consultingMemberUserId; // user테이블 userId 외래키
    private int consultingMemberPurchaseInfoId; // purchaseInfo 테이블 purchaseId 외래키
    private LocalDateTime createDate;

    // 서브쿼리용
    private String name; // 사용자 이름
    private LocalDateTime lastLoginDate;
    private Date birthDate;
    private int gender;

    // 조인용
    private List<Order> order;

}
