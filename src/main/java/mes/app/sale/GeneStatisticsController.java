package mes.app.sale;

import mes.app.sale.service.GeneStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/sales/gene/statistics")
public class GeneStatisticsController {
	
	@Autowired
	private GeneStatisticsService geneStatisticsService;
	
	@GetMapping("/periodic")
	public ResponseEntity<Map<String, Object>> getPeriodicData(
			@RequestParam String startDate,
			@RequestParam String endDate,
			@RequestParam(required = false) String powerid) {
		Map<String, Object> result = geneStatisticsService.getPeriodicData(startDate, endDate, powerid);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/comparative")
	public ResponseEntity<Map<String, Object>> getComparativeData(
			@RequestParam String startDate,
			@RequestParam String endDate,
			@RequestParam(required = false) String powerid) {
		Map<String, Object> result = geneStatisticsService.getComparativeData(startDate, endDate, powerid);
		return ResponseEntity.ok(result);
	}
}
