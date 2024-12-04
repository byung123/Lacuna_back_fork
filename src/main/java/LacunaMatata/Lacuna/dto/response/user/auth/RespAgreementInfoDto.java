package LacunaMatata.Lacuna.dto.response.user.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespAgreementInfoDto {
    private int settingId;
    private String dataType;
    private String value;
}
