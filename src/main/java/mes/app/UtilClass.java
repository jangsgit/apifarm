package mes.app;
import java.util.*;

public class UtilClass {

    // JSON 배열을 파싱하여 리스트로 변환하는 메서드
    public List<String> parseUserIds(String userid) {
        String cleanJson = userid.replaceAll("[\\[\\]\"]", "");
        return Arrays.asList(cleanJson.split(","));
    }
}
