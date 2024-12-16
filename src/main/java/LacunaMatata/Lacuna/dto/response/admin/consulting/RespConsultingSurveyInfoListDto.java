package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class RespConsultingSurveyInfoListDto {
    private int consultingId;
    private String consultingCode;
    private String consultingUpperCategoryName;
    private String consultingLowerCategoryName;
    private String consultingTitle;
    private String name;
    private LocalDateTime createDate;
}
