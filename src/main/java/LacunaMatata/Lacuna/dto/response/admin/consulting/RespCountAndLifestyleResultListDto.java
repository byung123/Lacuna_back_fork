package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RespCountAndLifestyleResultListDto {
    private int totalCount;
    private List<RespLifestyleResultDto> lifestyleResult;
}
