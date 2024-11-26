package mes.app.sale;

import mes.app.sale.service.GeneStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/sales/gene/statistics")
public class GeneStatisticsController {
	
	private static final Logger logger = LoggerFactory.getLogger(GeneStatisticsController.class);
	
	@Autowired
	private GeneStatisticsService geneStatisticsService;
	
	@GetMapping("/periodic")
	public ResponseEntity<?> getPeriodicData(
			@RequestParam String startDate,
			@RequestParam String endDate,
			@RequestParam(required = false) String powerid) {
		try {
			logger.info("Received request for periodic data: startDate={}, endDate={}, powerid={}", startDate, endDate, powerid);
			Map<String, Object> result = geneStatisticsService.getPeriodicData(startDate, endDate, powerid);
			logger.info("Periodic data retrieved successfully");
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException e) {
			logger.error("Invalid input: {}", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			logger.error("Error retrieving periodic data", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while processing your request");
		}
	}
	
	@GetMapping("/comparative")
	public ResponseEntity<?> getComparativeData(
			@RequestParam String startDate,
			@RequestParam String endDate,
			@RequestParam(required = false, defaultValue = "all") String powerid) {
		try {
			logger.info("Received request for comparative data: startDate={}, endDate={}, powerid={}", startDate, endDate, powerid);
			Map<String, Object> result = geneStatisticsService.getComparativeData(startDate, endDate, powerid);
			logger.info("Comparative data retrieved successfully");
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException e) {
			logger.error("Invalid input: {}", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			logger.error("Error retrieving comparative data", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while processing your request");
		}
	}
}
