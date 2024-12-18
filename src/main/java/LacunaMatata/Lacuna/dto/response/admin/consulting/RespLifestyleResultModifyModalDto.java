package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RespLifestyleResultModifyModalDto {
    private int lifestyleResultId;
    private String lifestyleResultUnitTitle;
    private String lifestyleResultConsultingUpperCategoryName;
    private String lifestyleResultConsultingLowerCategoryName;
    private int lifestyleResultStatus;
    private List<RespLifestyleResultDetailModifyModalDto> lifestyleDetail;
}
