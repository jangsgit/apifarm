//package mes.app.weather.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class WeatherService {
//
//	@Value("${weather.api.endpoint}")
//	private String apiEndpoint;
//
//	@Value("${api.key}")
//	private String apiKey;
//
//	private final RestTemplate restTemplate;
//
//	@Autowired
//	public WeatherService(RestTemplate restTemplate) {
//		this.restTemplate = restTemplate;
//	}
//
//
///*	❍단기예보
//- Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
//- API 제공 시간(~이후) : 02:10, 05:10, 08:10, 11:10, 14:10, 17:10, 20:10, 23:10*/
//
//	//TODO : 시간 오류 추후 수정 (오전 9시일 경우 0500 데이터로 조회됨)
//	private String determineBaseTime(LocalDateTime now) {
//		int hour = now.getHour();
//		int minute = now.getMinute();
//
//		if (minute >= 10) {
//			if (hour >= 23) return "2300";
//			else if (hour >= 20) return "2000";
//			else if (hour >= 17) return "1700";
//			else if (hour >= 14) return "1400";
//			else if (hour >= 11) return "1100";
//			else if (hour >= 8) return "0800";
//			else if (hour >= 5) return "0500";
//			else return "0200";
//		} else {
//			if (hour >= 23) return "2000";
//			else if (hour >= 20) return "1700";
//			else if (hour >= 17) return "1400";
//			else if (hour >= 14) return "1100";
//			else if (hour >= 11) return "0800";
//			else if (hour >= 8) return "0500";
//			else return "0200";
//		}
//	}
//
//
//	public ResponseEntity<?> getWeatherData() {
//		LocalDateTime now = LocalDateTime.now();
//		String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//		String time = determineBaseTime(now);
//
//		// 초단기실황 조회
//		ResponseEntity<?> currentWeather = fetchWeatherData("/getUltraSrtNcst", date, time, "current");
//		// 단기예보 조회
//		ResponseEntity<?> forecastData = fetchWeatherData("/getVilageFcst", date, time, "forecast");
//
//		return combineData(currentWeather, forecastData);
//	}
//
//
//	private ResponseEntity<?> fetchWeatherData(String servicePath, String date, String time, String dataSource) {
//
//		// 1. 먼저 현재 시간으로 데이터 요청
//		try {
//			URI uri = new URI(apiEndpoint + servicePath +
//					"?serviceKey=" + apiKey +
//					"&pageNo=1" +
//					"&numOfRows=10" +
//					"&dataType=json" +
//					"&base_date=" + date +
//					"&base_time=" + time +
//					// 대구 달서구 신당동 위치 (위치 추가되면 수정해)
//					"&nx=87" +
//					"&ny=90");
//
//			System.out.println("날씨 uri (현재 시간): " + uri);
//
//			String response = restTemplate.getForObject(uri, String.class);
//			ResponseEntity<?> parsedResponse = parseWeatherData(response, dataSource);
//
//			if (parsedResponse.getStatusCode().is2xxSuccessful()) {
//				return parsedResponse;
//			}
//
//			// 2. 데이터가 없는 경우, 이전 시간으로 조정하여 재요청
//			System.out.println("현재 시간의 데이터가 없어 이전 시간으로 조정 중...");
//
//			// 한 시간 이전 시간으로 조정
//			LocalDateTime newDateTime = LocalDateTime.parse(date + time, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
//			newDateTime = newDateTime.minusHours(1);
//			date = newDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//			time = newDateTime.format(DateTimeFormatter.ofPattern("HH00"));
//
//			uri = new URI(apiEndpoint + servicePath +
//					"?serviceKey=" + apiKey +
//					"&pageNo=1" +
//					"&numOfRows=10" +
//					"&dataType=json" +
//					"&base_date=" + date +
//					"&base_time=" + time +
//					"&nx=87" +
//					"&ny=90");
//
//			System.out.println("날씨 uri (이전 시간): " + uri);
//
//			response = restTemplate.getForObject(uri, String.class);
//			parsedResponse = parseWeatherData(response, dataSource);
//
//			return parsedResponse;
//
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			return ResponseEntity.badRequest().body("URI syntax error");
//		}
//	}
//
//	private ResponseEntity<?> parseWeatherData(String response, String dataSource) {
//		try {
//			ObjectMapper mapper = new ObjectMapper();
//			JsonNode root = mapper.readTree(response);
//			JsonNode items = root.path("response").path("body").path("items").path("item");
//
//			Map<String, String> result = new HashMap<>();
//			for (JsonNode item : items) {
//				String category = item.path("category").asText();
//				String value;
//				if ("forecast".equals(dataSource)) {
//					value = item.path("fcstValue").asText();  // 단기예보에서는 fcstValue 사용
//				} else {
//					value = item.path("obsrValue").asText();  // 초단기실황에서는 obsrValue 사용
//				}
//				result.put(category, value);
////				System.out.println("Parsed " + category + ": " + value);
//			}
//
////			System.out.println("최종 데이터 : " + result);
//			return ResponseEntity.ok(result);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.badRequest().body("Failed to parse weather data");
//		}
//	}
//
//	private ResponseEntity<?> combineData(ResponseEntity<?> weatherData, ResponseEntity<?> forecastData) {
//		Map<String, String> weatherResult = (Map<String, String>) weatherData.getBody();
//		Map<String, String> forecastResult = (Map<String, String>) forecastData.getBody();
//
////		weatherResult.putAll(forecastResult); // 강수확률 정보를 포함시키는 로직
//
//		// 예보 데이터의 'fcstValue'를 사용하여 실황 데이터의 'obsrValue'를 업데이트
//		forecastResult.forEach((key, value) -> {
//			if (weatherResult.containsKey(key)) {
//				// 예보값이 비어있지 않다면 업데이트
//				if (!value.isEmpty()) {
//					weatherResult.put(key, value);
//				}
//			} else {
//				// 키가 존재하지 않으면 새로 추가
//				weatherResult.put(key, value);
//			}
//		});
//
//		return ResponseEntity.ok(weatherResult);
//	}
//
//}