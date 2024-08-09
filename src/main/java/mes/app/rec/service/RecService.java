package mes.app.rec.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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
	
	@Autowired
	public RecService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	/*public String getRecData() {
		
		String encodedApiKey = apiKey;
		
		// URL 인코딩
*//*		String encodedApiKey = null;

		try {
			encodedApiKey = URLEncoder.encode(apiKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to encode API key");
		}*//*
		
		// 매주 화, 목요일 10:00 ~ 16:00 개장
		// 7일 조회
		LocalDate today = LocalDate.now();
		LocalDate startDay = today.minusDays(7);
		String avgPrice = null;
		
		for (LocalDate date = today; !date.isBefore(startDay); date = date.minusDays(1)) {
			String formattedDate = date.format(DateTimeFormatter.BASIC_ISO_DATE);
			
			// URI 생성
			URI uri = null;
			try {
				uri = new URI(apiEndpoint + "/getRecMarketInfo2" +
						"?serviceKey=" + encodedApiKey +
						"&pageNo=1" +
						"&numOfRows=1" +
						"&dataType=json" +
						"&bzDd=" + formattedDate); // 날짜 추가
			} catch (URISyntaxException e) {
				e.printStackTrace();
				continue; // 날짜가 유효하지 않은 경우, 다음 날짜로 넘어감
			}
			
			String response = restTemplate.getForObject(uri, String.class);
			
			try {
				// JSON 파싱
				int index = response.indexOf("landAvgPrc"); // 육지 평균값
				if (index != -1) {
					int start = response.indexOf(":", index) + 1;
					int end = response.indexOf(",", start);
					avgPrice = response.substring(start, end).trim();
					
					// 숫자 포맷팅
					double avgPriceDouble = Double.parseDouble(avgPrice);
					NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
					avgPrice = numberFormat.format(avgPriceDouble);
					
					System.out.println("최종 사용된 uri : " + uri );
					System.out.println("최종 사용된 날짜: " + formattedDate);
					break; // 유효한 데이터를 찾으면 루프 중단
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue; // JSON 파싱 실패 시, 다음 날짜로 넘어감
			}
			
			System.out.println("조회 시도된 uri : " + uri);
			System.out.println("조회 시도된 날짜 : " + formattedDate);
		}
		
		return avgPrice; // 가장 최신의 유효한 평균가격을 반환
	}*/
	
	@Cacheable(value = "recCache", key = "#root.method.name")
	public String getRecData() {
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
		return avgPrice;
	}
	
	private String fetchFromAPI(String formattedDate) throws URISyntaxException {
		URI uri = new URI(apiEndpoint + "/getRecMarketInfo2"
				+ "?serviceKey=" + apiKey
				+ "&pageNo=1"
				+ "&numOfRows=1"
				+ "&dataType=json"
				+ "&bzDd=" + formattedDate);
		return restTemplate.getForObject(uri, String.class);
	}
	
	private String parseResponse(String response) {
		int index = response.indexOf("landAvgPrc");
		if (index != -1) {
			int start = response.indexOf(":", index) + 1;
			int end = response.indexOf(",", start);
			String avgPrice = response.substring(start, end).trim();
			double avgPriceDouble = Double.parseDouble(avgPrice);
			NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
			return numberFormat.format(avgPriceDouble);
		}
		return null;
	}
}
