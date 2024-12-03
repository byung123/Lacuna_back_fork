package LacunaMatata.Lacuna.dto.response.admin.consulting;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class RespConsultingLowerListDto {
    private int consultingLowerCategoryId;
    private String consultingLowerCategoryName;
    private String name;
    private LocalDateTime createDate;
}
