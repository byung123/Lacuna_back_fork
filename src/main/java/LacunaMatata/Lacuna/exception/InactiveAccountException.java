package LacunaMatata.Lacuna.exception;

import lombok.Getter;

import java.util.Map;

public class InactiveAccountException extends RuntimeException {
    @Getter
    private Map<String, String> errorMessages;

    public InactiveAccountException(String s) {
        super("휴먼계정 예외처리");
        errorMessages = Map.of(
                "title", "휴먼계정 안내",
                "content", "고객님께서 지난 1년 동안 로그인 기록이 없어, 고객님의 계정이 휴먼 계정으로 전환되었습니다.\n" +
                        "휴먼 계정 상태에서는 일부 서비스 이용이 제한될 수 있으며, 계정 정보는 안전하게 보관되고 있습니다.",
                "subTitle", "계정 복구 방법",
                "subContent", "계정을 다시 활성화하시려면 본인인증이 필요합니다. 가입하신 이메일로 본인인증 메일을 발송하였습니다."
        );
    }
}
