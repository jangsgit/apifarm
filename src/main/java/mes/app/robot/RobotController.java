package mes.app.robot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class RobotController {

    @Autowired
    private MqttService mqttService;

    @Autowired
    private WebSocketService webSocketService;

    @GetMapping("/robot-data")
    public String getRobotData(@RequestParam String robotId) {
        // MQTT로 데이터를 요청하는 메시지 전송
        String payload = "{ \"request\": \"robotData\", \"robotId\": \"" + robotId + "\" }";
        mqttService.publishMessage("Platform", payload);
        return "Request sent to MQTT broker.";
    }
}