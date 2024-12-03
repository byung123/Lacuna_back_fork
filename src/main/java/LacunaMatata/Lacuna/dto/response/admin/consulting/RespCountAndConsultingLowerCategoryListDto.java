package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RespCountAndConsultingLowerCategoryListDto {
    private int totalCount;
    private List<RespConsultingLowerListDto> consultingLowerCategory;
}
