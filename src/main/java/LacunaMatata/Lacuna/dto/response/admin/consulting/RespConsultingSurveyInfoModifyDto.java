package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RespConsultingSurveyInfoModifyDto {
    private int consultingId;
    private String consultingCode;
    private String consultingUpperCategoryName;
    private String consultingLowerCategoryName;
    private String consultingTitle;
    private String consultingSubtitle;
    private String consultingImg;
    private String consultingOptionType;

    private List<RespConsultingSurveyOptionModifyDto> consultingOption;
}
