//package mes.app.weather;
//
//import mes.app.weather.service.WeatherService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/weather")
//public class WeatherController {
//
//	@Autowired
//	private WeatherService weatherService;
//
//	@GetMapping("/current")
//	public ResponseEntity<?> getCurrentWeather() {
//		return weatherService.getWeatherData();
//	}
//}
