package mes.app.rec.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class RecService {
	
	@Value("${rec.api.endpoint}") // 보안을 위해 application.properties 에 저장해둠
	private String apiEndpoint;
	
	@Value("${api.key}")
	private String apiKey;
	
	// RestTemplate 을 사용하여 API 호출을 수행하고 응답받기
	private final RestTemplate restTemplate;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	public RecService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	
	/*
	* 다중 인스턴스 환경: 애플리케이션이 여러 인스턴스에서 동작하고 있고, 각 인스턴스가 독립적으로 API를 호출하고 있다면, 전체적으로 보았을 때 API 호출 제한을 초과할 수 있습니다. 이 경우 중앙 집중식 캐시 솔루션(예: Redis)을 사용하여 모든 인스턴스 간에 캐시를 공유할 수 있습니다. -> 나중에 활용*/
	@Cacheable(value = "recCache", key = "#root.method.name")
	public ResponseEntity<?> getRecData() {
		LocalDate today = LocalDate.now();
		LocalDate startDay = today.minusDays(7);
		String avgPrice = null;
		
		for (LocalDate date = today; !date.isBefore(startDay); date = date.minusDays(1)) {
			String formattedDate = date.format(DateTimeFormatter.BASIC_ISO_DATE);
			try {
				String response = fetchFromAPI(formattedDate);
				String price = parseResponse(response);
				if (price != null) {
					avgPrice = price;
					break;  // 유효한 데이터를 찾으면 루프 중단
				}
			} catch (URISyntaxException e) {
				System.err.println("URI syntax error: " + e.getMessage());
				continue;  // URI 생성 실패 시, 다음 날짜로 넘어감
			}
		}
		
		if (avgPrice != null) {
			return ResponseEntity.ok().body("{\"landAvgPrc\": \"" + avgPrice + "\"}"); // JSON 형태로 응답
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"No data found\"}");
		}
	}
	
	private String fetchFromAPI(String formattedDate) throws URISyntaxException {
		URI uri = new URI(apiEndpoint + "/getRecMarketInfo2"
				+ "?serviceKey=" + apiKey
				+ "&pageNo=1"
				+ "&numOfRows=1"
				+ "&dataType=json"
				+ "&bzDd=" + formattedDate);
		System.out.println("조회 시도된 uri : " + uri);
		return restTemplate.getForObject(uri, String.class);
	}
	
	private String parseResponse(String response) {
		try {
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");
			if (itemsNode.isArray() && itemsNode.size() > 0) {  // 배열이면서, 항목이 적어도 하나 이상 있는지 확인
				JsonNode landAvgPrcNode = itemsNode.get(0).path("landAvgPrc");
				if (!landAvgPrcNode.isMissingNode()) {
					double avgPriceDouble = landAvgPrcNode.asDouble();
					NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
					return numberFormat.format(avgPriceDouble);
				}
			}
		} catch (Exception e) {
			System.err.println("Failed to parse JSON response: " + e.getMessage());
		}
		return null;  // 유효한 데이터가 없거나 파싱에 실패한 경우
	}
}
