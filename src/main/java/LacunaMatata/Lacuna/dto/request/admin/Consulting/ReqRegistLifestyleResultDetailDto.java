package LacunaMatata.Lacuna.dto.request.admin.Consulting;

import lombok.Data;

@Data
public class ReqRegistLifestyleResultDetailDto {
    private String lifestyleResultType;
    private String lifestyleResultTitle;
    private String lifestyleResultContent;
    private int lifestyleResultScoreMax;
    private int lifestyleResultScoreMin;
}
