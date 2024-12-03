package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class RespConsultingUpperListDto {
    private int consultingUpperCategoryId;
    private String consultingUpperCategoryName;
    private String name;
    private LocalDateTime createDate;
}
