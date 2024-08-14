package mes.app;
import java.util.*;
import java.util.stream.Collectors;

public class UtilClass {

    // JSON 배열을 파싱하여 리스트로 변환하는 메서드
    public List<String> parseUserIds(String userid) {
        String cleanJson = userid.replaceAll("[\\[\\]\"]", "");
        return Arrays.asList(cleanJson.split(","));
    }

    public List<Integer> parseUserIdsToInt(String userid) {
        String cleanJson = userid.replaceAll("[\\[\\]\"]", ""); // 대괄호와 따옴표 제거
        return Arrays.stream(cleanJson.split(","))  // 쉼표로 분리
                .map(Integer::parseInt)       // 각 요소를 Integer로 변환
                .collect(Collectors.toList()); // 리스트로 수집
    }
}
