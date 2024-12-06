package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RespConsultingLowerCategoryModifyDto {
    private int consultingLowerCategoryId;
    private String consultingUpperCategoryName;
    private String consultingLowerCategoryName;
}
