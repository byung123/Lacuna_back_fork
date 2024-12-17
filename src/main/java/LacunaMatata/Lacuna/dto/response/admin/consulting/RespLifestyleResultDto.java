package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class RespLifestyleResultDto {
    private int lifestyleResultId;
    private String consultingUpperCategoryName;
    private String lifestyleResultUnitTitle;
    private int lifestyleResultStatus;
    private String name;
    private LocalDateTime createDate;
}
