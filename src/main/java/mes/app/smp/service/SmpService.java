package mes.app.smp.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import mes.domain.DTO.SmpDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SmpService {
	
	@Value("${smp.api.endpoint}")
	private String apiEndpoint;
	
	@Value("${api.key}")
	private String apiKey;
	
	private final RestTemplate restTemplate;
	
	private final XmlMapper xmlMapper = new XmlMapper();  // XmlMapper 인스턴스 생성
	
	@Autowired
	public SmpService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate; // 생성자 주입
	}
	
	/*// RestTemplate 을 사용하여 XML 응답을 SmpDto 객체로 자동 매핑
	public SmpDto getSmpData() {
		LocalDateTime now = LocalDateTime.now();
		String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		int hour = now.getHour();
		
		// URI 생성
		URI uri = null;
		try {
			uri = new URI(apiEndpoint + "/getSmp1hToday"
					+ "?areaCd=1"
					+ "&serviceKey=" + apiKey
					+ "&tradeDay=" + formattedDate
					+ "&tradeHour=" + hour);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException("URI syntax is incorrect: " + e.getMessage());
		}
		
		// API 호출 및 결과 변환
		SmpDto response = restTemplate.getForObject(uri, SmpDto.class);
		
		System.out.println("smp URI: " + uri);
		
		return response; // RestTemplate을 통해 API 호출 결과를 SmpDto로 변환
		
	}*/
	
	
	public ResponseEntity<String> getCurrentSmp() {
		LocalDateTime now = LocalDateTime.now();
		String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		int hour = now.getHour();
		
		String uriString = apiEndpoint + "/getSmp1hToday?areaCd=1&serviceKey=" + apiKey +
				"&tradeDay=" + formattedDate + "&tradeHour=" + hour;
		
		try {
			URI uri = new URI(uriString);
			
			System.out.println("Constructed URI: " + uri);
			
			// RestTemplate가 리디렉션을 따르도록 설정
			ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
			
			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				SmpDto dtoResponse = xmlMapper.readValue(response.getBody(), SmpDto.class);
				String currentSmp = findCurrentSmp(dtoResponse, hour);
				return ResponseEntity.ok(currentSmp);
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("API call failed: " + response.getStatusCode());
			}
		} catch (URISyntaxException | IOException e) {
			return ResponseEntity.badRequest().body("Error in accessing the API: " + e.getMessage());
		}
	}
	
	private String findCurrentSmp(SmpDto dto, int hour) {
		System.out.println("Finding SMP for Hour: " + hour);
		
		if (dto != null && dto.getBody() != null && dto.getBody().getItems() != null) {
			System.out.println("Items found: " + dto.getBody().getItems().getItems().size());
			
			return dto.getBody().getItems().getItems().stream()
					.filter(item -> Integer.parseInt(item.getTradeHour()) == hour)
					.findFirst()
					.map(item -> String.format("%.2f", item.getSmp()))  // 여기서 item -> String.format 사용
					.orElse("No data for current hour");
		}
		
		System.out.println("No valid data found in DTO");
//		return "No valid data found";
		return "로딩중";
	}
}