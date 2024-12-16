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
public class ConsultingSurveyInfo {
    private int consultingId;
    private int consultingSurveyUpperCategoryId;
    private int consultingLowerCategoryId;
    private String consultingCode;
    private int consultingSurveyRegisterId;
    private String consultingTitle;
    private String consultingSubtitle;
    private String consultingImg;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    // 서브쿼리용
    private int totalCount;
    private String name;
    private String consultingUpperCategoryName;
    private String consultingLowerCategoryName;

    // 조인용
    private List<ConsultingSurveyOption> consultingSurveyOption;
}
