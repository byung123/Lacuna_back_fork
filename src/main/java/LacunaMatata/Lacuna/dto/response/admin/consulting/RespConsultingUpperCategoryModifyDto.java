package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespConsultingUpperCategoryModifyDto {
    private int consultingUpperCategoryId;
    private String consultingUpperCategoryName;
    private String consultingUpperCategoryDescription;
    private String consultingUpperCategoryImg;
}
