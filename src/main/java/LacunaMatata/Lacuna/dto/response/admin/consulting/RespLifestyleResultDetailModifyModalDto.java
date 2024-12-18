package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespLifestyleResultDetailModifyModalDto {
    private int lifestyleResultDetailId;
    private String lifestyleResultType;
    private String lifestyleResultTitle;
    private String lifestyleResultContent;
    private int lifestyleResultScoreMin;
    private int lifestyleResultScoreMax;
}
