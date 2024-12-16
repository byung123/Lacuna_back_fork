package LacunaMatata.Lacuna.dto.request.admin.Consulting;

import lombok.Data;

import java.util.List;

@Data
public class ReqDeleteConsultingLowerCategoryListDto {
    private List<Integer> lowerCategoryIdList;
}
