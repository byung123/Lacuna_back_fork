package LacunaMatata.Lacuna.dto.request.admin.Consulting;

import lombok.Data;

import java.util.List;

@Data
public class ReqDeleteConsultingSurveyInfoListDto {
    private List<Integer> consultingIdList;
}
