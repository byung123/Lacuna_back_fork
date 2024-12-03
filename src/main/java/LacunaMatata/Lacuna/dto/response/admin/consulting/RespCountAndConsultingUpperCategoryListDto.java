package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RespCountAndConsultingUpperCategoryListDto {
    private int totalCount;
    private List<RespConsultingUpperListDto> consultingUpperCategory;
}
