package LacunaMatata.Lacuna.entity.lifestyle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LifestyleResult {
    private int lifestyleResultId;
    private int lifestyleResultConsultingUpperCategoryId;
    private int lifestyleResultConsultingLowerCategoryId;
    private String lifestyleResultUnitTitle;
    private int lifestyleResultRegisterId;
    private int lifestyleResultStatus;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    // 서브 쿼리용
    private String name;
    private String consultingUpperCategoryName;
    private String consultingLowerCategoryName;
    private int totalCount;

    // 조인용
    private List<LifestyleResultDetail> lifestyleResultDetail;
}
