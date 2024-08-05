package mes.app.robot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

// mqtt 테스트용
@Component
public class MqttPublisher {

    private String brokerUrl = "tcp://broker.hivemq.com:1883";
    private String clientId = "javaMqttPublisher";
    private String topic = "Platform";
    private MqttClient client;

//    public MqttPublisher() {
//        try {
//            client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
//            MqttConnectOptions connOpts = new MqttConnectOptions();
//            connOpts.setCleanSession(true);
//            client.connect(connOpts);
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Scheduled(fixedRate = 60000)  // 60초마다 메시지 발행
//    public void publishTestMessage() {
//        try {
//            // 다양한 로봇 ID를 생성
//            String[] robotIds = {"nc0001", "nc0002", "nc0003"};
//            for (String robotId : robotIds) {
//                String payload = createTestPayload(robotId);
//                MqttMessage message = new MqttMessage(payload.getBytes());
//                message.setQos(2);
//                client.publish(topic, message);
//                System.out.println("Test message sent: " + payload);
//            }
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }

    private String createTestPayload(String robotId) {
        return "{"
                + "\"transactionId\": \"" + UUID.randomUUID().toString() + "\","
                + "\"robotId\":\"" + robotId + "\","
                + "\"macAddress\" : \"127.0.0.1\","
                + "\"floorId\" : \"f1\","
                + "\"mapId\": \"office_f1\","
                + "\"slamStatus\": \"\","
                + "\"localizationStatus\": \"OK\","
                + "\"localizationRawData\": \"\","
                + "\"emergencyStatus\": \"RELEASED\","
                + "\"moveStatus\": \"STOP\","
                + "\"robotStatus\": \"ready\","
                + "\"batteryStatus\": \"50\","
                + "\"batteryStatusVoltage\": \"23.5\","
                + "\"schedulingID\": \"sch210705143015\","
                + "\"schedulingGoalNo\": \"\","
                + "\"positionName\": \"HOME\","
                + "\"position\": { \"x\": 0.0, \"y\": 0.0, \"degree\": 0.0 },"
                + "\"speed\": 0.0,"
                + "\"statistics\": { \"cumulativeTravelDistance\": 120, \"cumulativeTravelTime\": 300, \"cumulativeUpTime\": 400 },"
                + "\"moduleStatus\": ["
                + "{ \"key\": \"driver_module\", \"value\": \"OK\", \"category\": \"DBOT\" },"
                + "{ \"key\": \"3dlidar_module\", \"value\": \"OK\", \"category\": \"3D\" },"
                + "{ \"key\": \"actuation_module\", \"value\": \"OK\", \"category\": \"DBOT\" },"
                + "{ \"key\": \"navigation_module\", \"value\": \"OK\", \"category\": \"NAV\" },"
                + "{ \"key\": \"slam_module\", \"value\": \"\", \"category\": \"SLAM\" },"
                + "{ \"key\": \"docking_module\", \"value\": \"OK\", \"category\": \"DOCKING\" }"
                + "]"
                + "}";
    }
}