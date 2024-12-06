package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RespCountAndConsultingSurveyInfoListDto {
    private int totalCount;
    private List<RespConsultingSurveyInfoListDto> consultingSurveyInfoList;
}
