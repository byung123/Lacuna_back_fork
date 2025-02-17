package LacunaMatata.Lacuna.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

    @Value("${server.env}")
    private String env;
    @Value("${server.port}")
    private String serverPort;
    @Value("${server.serverAddress}")
    private String serverAddress;


    // healthCheck 테스트용
    @GetMapping("/hc")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> responseData = new HashMap<>();
        responseData.put("serverName", serverAddress);
        responseData.put("serverPort", serverPort);
        responseData.put("env", env);

        return ResponseEntity.ok(responseData);
    }

    // 실제 healthCheck 체크
    @GetMapping("/env")
    public ResponseEntity<?> getEnv() {
        return ResponseEntity.ok(env);
    }

    // 실제 healthCheck 체크
    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.ok("테스트1");
    }
}
