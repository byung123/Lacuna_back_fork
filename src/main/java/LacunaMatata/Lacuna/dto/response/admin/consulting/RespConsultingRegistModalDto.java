package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RespConsultingRegistModalDto {
    private int consultingUpperCategoryId;
    private String consultingUpperCategoryName;

    private List<RespConsultingLowerCategoryListDto> consultingLowerCategory;
}
