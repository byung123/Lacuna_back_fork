package LacunaMatata.Lacuna.entity.consulting;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsultingStatusMet {
    private int consultingStatusMetId;
    private int consultingStatusMemberId; // consultingMemberInfo 테이블 consultingMemberId 외래키
    private int consultingStatusId; // consultingStatus 테이블 consultingStatusId 외래키
    private LocalDateTime createDate;
}
