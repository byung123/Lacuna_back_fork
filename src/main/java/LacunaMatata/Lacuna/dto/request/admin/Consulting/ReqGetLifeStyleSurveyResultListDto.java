package LacunaMatata.Lacuna.dto.request.admin.Consulting;

import lombok.Data;

@Data
public class ReqGetLifeStyleSurveyResultListDto {
    private String searchValue;
    private int page;
    private int limit;
}
