package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespConsultingSurveyOptionModifyDto {
    private String optionValue;
    private int optionScore;
}
