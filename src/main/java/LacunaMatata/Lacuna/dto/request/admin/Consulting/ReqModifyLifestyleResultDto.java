package LacunaMatata.Lacuna.dto.request.admin.Consulting;

import lombok.Data;

import java.util.List;

@Data
public class ReqModifyLifestyleResultDto {
    private int lifestyleResultId;
    private String lifestyleResultUnitTitle;
    private int lifestyleResultStatus;
    private List<ReqModifyLifestyleResultDetailDto> lifestyleDetail;
}
