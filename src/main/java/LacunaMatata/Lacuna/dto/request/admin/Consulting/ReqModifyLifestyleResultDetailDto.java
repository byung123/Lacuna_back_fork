package LacunaMatata.Lacuna.dto.request.admin.Consulting;

import lombok.Data;

@Data
public class ReqModifyLifestyleResultDetailDto {
    private int lifestyleResultDetailId;
    private String lifestyleResultTitle;
    private String lifestyleResultContent;
    private int lifestyleResultScoreMin;
    private int lifestyleResultScoreMax;
}
