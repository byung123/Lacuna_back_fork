package LacunaMatata.Lacuna.entity.consulting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultingLowerCategory {
    private int consultingLowerCategoryId;
    private int consultingUpperCategoryId;
    private String consultingLowerCategoryName;
    private int consultingLowerCategoryRegisterId;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    // 서브쿼리용
    private String name;
    private int totalCount;
}
