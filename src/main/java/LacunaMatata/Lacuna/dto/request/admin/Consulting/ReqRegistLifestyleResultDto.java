package LacunaMatata.Lacuna.dto.request.admin.Consulting;

import lombok.Data;

import java.util.List;

@Data
public class ReqRegistLifestyleResultDto {
    private String lifestyleResultUnitTitle;
    private int lifestyleResultConsultingUpperCategoryId;
    private int lifestyleResultConsultingLowerCategoryId;
    private int lifestyleResultStatus;

    private List<ReqRegistLifestyleResultDetailDto> lifestyleDetail;
}
