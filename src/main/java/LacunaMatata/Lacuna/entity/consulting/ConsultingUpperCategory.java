package LacunaMatata.Lacuna.entity.consulting;

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
public class ConsultingUpperCategory {
    private int consultingUpperCategoryId;
    private String consultingUpperCategoryName;
    private String consultingUpperCategoryDescription;
    private String consultingUpperCategoryImg;
    private int consultingUpperCategoryRegisterId;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    // 서브쿼리용
    private String name;
    private int totalCount;

    // 조인용
    private List<ConsultingLowerCategory> consultingLowerCategory;
}
