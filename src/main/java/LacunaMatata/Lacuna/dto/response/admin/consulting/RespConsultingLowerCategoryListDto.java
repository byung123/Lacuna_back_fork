package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespConsultingLowerCategoryListDto {
    private int consultingLowerCategoryId;
    private String consultingLowerCategoryName;
}
