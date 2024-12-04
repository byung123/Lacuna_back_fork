package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespConsultingUpperCategoryFilterDto {
    private int consultingUpperCategoryId;
    private String consultingUpperCategoryName;
}
