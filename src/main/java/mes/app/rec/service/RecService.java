package mes.app.rec.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mes.domain.entity.UserCode;
import mes.domain.repository.UserCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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

	@Autowired
	private UserCodeRepository userCodeRepository;
	
	@Scheduled(cron = "0 0 4 * * WED,FRI") // 매주 수요일과 금요일 새벽 4시에 실행
	public void updateRecData() {
		LocalDateTime now = LocalDateTime.now();
		LocalDate lastRecDay = getLastRecDay(now.toLocalDate());
		String formattedDate = lastRecDay.format(DateTimeFormatter.BASIC_ISO_DATE);
		
		try {
			String uriString = apiEndpoint + "/getRecMarketInfo2"
					+ "?serviceKey=" + apiKey
					+ "&pageNo=1"
					+ "&numOfRows=1"
					+ "&dataType=json"
					+ "&bzDd=" + formattedDate;
			
			URI uri = new URI(uriString);
			String response = restTemplate.getForObject(uri, String.class);
			String avgPrice = parseRecPrice(response);  // 평균 가격을 String으로 변환
			
			System.out.println("REC URI: " + uri);
			
			// REC 단가 업데이트
			Optional<UserCode> optionalRec = userCodeRepository.findById(161);
			optionalRec.ifPresent(code -> {
				code.setValue(avgPrice);
				userCodeRepository.save(code);
//				System.out.println("REC 단가 저장 성공: " + avgPrice);
			});
			
			// 업데이트 시간 기록
			Optional<UserCode> optionalTime = userCodeRepository.findById(162);
			optionalTime.ifPresent(code -> {
				code.setValue(LocalDateTime.now().toString());
				userCodeRepository.save(code);
//				System.out.println("업데이트 시간 저장 성공");
			});
			
			System.out.println("REC 정보가 업데이트 되었습니다. REC 단가: " + avgPrice + ", 업데이트 시간: " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		} catch (URISyntaxException e) {
			System.err.println("Error fetching REC data: " + e.getMessage());
		}
	}
	
	private LocalDate getLastRecDay(LocalDate currentDate) {
		DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
		
		if (dayOfWeek == DayOfWeek.WEDNESDAY) {
			return currentDate.minusDays(1); // 화요일로 설정
		} else if (dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.MONDAY) {
			return currentDate.with(DayOfWeek.THURSDAY); // 가장 최근의 목요일로 설정
		} else if (dayOfWeek == DayOfWeek.TUESDAY) {
			return currentDate.minusDays(4); // 바로 직전의 목요일로 설정
		} else if (dayOfWeek == DayOfWeek.THURSDAY) {
			return currentDate.minusDays(2); // 화요일로 설정
		}
		
		return currentDate;
	}
	
	
	private String parseRecPrice(String response) {
		try {
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode itemNode = rootNode.path("response").path("body").path("items").path("item").get(0);
			
			if (itemNode != null) {
				double recValue = itemNode.path("landAvgPrc").asDouble();
				return String.format("%.2f", recValue); // 포맷팅된 평균 가격 반환
			}
		} catch (Exception e) {
			System.err.println("Failed to parse JSON response: " + e.getMessage());
		}
		return "데이터없음"; // 유효한 데이터가 없을 경우 "데이터없음"을 반환
	}
}

// 즉시 실행되도록 수동으로 호출 버전
	/*public void updateRecData() {
		LocalDateTime today = LocalDateTime.now();
		String avgPrice = null;
		String businessDay = null;
		
		try {
			// 현재 날짜부터 최대 일주일 전까지 조회
			for (int i = 0; i < 7; i++) {
				LocalDateTime targetDate = today.minusDays(i);
				String formattedDate = targetDate.format(DateTimeFormatter.BASIC_ISO_DATE);
				String response = fetchFromAPI(formattedDate);
				JsonNode result = parseResponse(response);
				
				// 결과가 있으면 데이터를 추출하고 루프를 종료
				if (result != null && !result.isEmpty()) {
					avgPrice = result.path("landAvgPrc").asText();
					businessDay = result.path("bzDd").asText();
					System.out.println("Parsed avgPrice: " + avgPrice);  // 추가된 로그
					System.out.println("Parsed businessDay: " + businessDay);  // 추가된 로그
					break;
				}
			}
			
			if (avgPrice != null) {
				// REC 단가 업데이트
				Optional<UserCode> optionalRec = userCodeRepository.findById(161);
				String finalAvgPrice = avgPrice;
				optionalRec.ifPresent(code -> {
					code.setValue(finalAvgPrice);
					userCodeRepository.save(code);
					System.out.println("REC 단가 저장 성공: " + finalAvgPrice);
				});
				
				// 업데이트 시간 기록
				Optional<UserCode> optionalTime = userCodeRepository.findById(162);
				optionalTime.ifPresent(code -> {
					code.setValue(LocalDateTime.now().toString());
					userCodeRepository.save(code);
					System.out.println("업데이트 시간 저장 성공");
				});
				
				System.out.println("REC 정보가 업데이트되었습니다. REC 단가: " + avgPrice + ", 영업일: " + businessDay + ", 업데이트 시간: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			} else {
				System.err.println("REC 정보를 찾을 수 없습니다.");
			}
		} catch (URISyntaxException e) {
			System.err.println("Error fetching REC data: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Unexpected error: " + e.getMessage());
			e.printStackTrace(); // 예외의 스택 트레이스를 콘솔에 출력
		}
	}
	
	
	private String fetchFromAPI(String formattedDate) throws URISyntaxException {
		URI uri = new URI(apiEndpoint + "/getRecMarketInfo2"
				+ "?serviceKey=" + apiKey
				+ "&pageNo=1"
				+ "&numOfRows=1"
				+ "&dataType=json"
				+ "&bzDd=" + formattedDate);
		
		System.out.println("REC URI: " + uri);
		
		return restTemplate.getForObject(uri, String.class);
	}
	
	private JsonNode parseResponse(String response) {
		try {
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode itemNode = rootNode.path("response").path("body").path("items").path("item").get(0);
			return itemNode;
		} catch (Exception e) {
			System.err.println("Failed to parse JSON response: " + e.getMessage());
			return null;
		}
	}*/
