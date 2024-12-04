package LacunaMatata.Lacuna.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailAuthentication {
    private Long emailAuthenticationId;
    private String email;
    private String verificationCode;
    private LocalDateTime expirationTime;
    private LocalDateTime createdTime;
    private int isVerified;
}
