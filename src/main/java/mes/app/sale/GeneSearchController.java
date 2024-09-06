package mes.app.sale;

import mes.app.sale.service.GeneSearchService;
import mes.config.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/genesearch")
public class GeneSearchController {
	
	@Autowired
	mes.domain.repository.TB_RP320Repository TB_RP320Repository;
	
	@Autowired
	private GeneSearchService geneSearchService;
	
	@Autowired
	Settings settings;
	
	// YoY
	@GetMapping("comparison/yoy")
	public List<Map<String, Object>> getYearOverYearData(@RequestParam String powerid,
														 @RequestParam String startYear,
														 @RequestParam String endYear) {
		return geneSearchService.getYoYComparisonData(powerid, startYear, endYear);
	}
	
	// QoQ
	@GetMapping("comparison/qoq")
	public List<Map<String, Object>> getQuarterOverQuarterData(@RequestParam String powerid,
															   @RequestParam String startYear,
															   @RequestParam String endYear) {
		return geneSearchService.getQoQComparisonData(powerid, startYear, endYear);
	}
	
	// MoM
	@GetMapping("comparison/mom")
	public List<Map<String, Object>> getMonthOverMonthData(@RequestParam String powerid,
														   @RequestParam String startYear,
														   @RequestParam String endYear) {
		return geneSearchService.getMoMComparisonData(powerid, startYear, endYear);
	}
	
	// YTD
	@GetMapping("comparison/ytd")
	public List<Map<String, Object>> getYearToDateData(@RequestParam String powerid,
													   @RequestParam String startDate,
													   @RequestParam String endDate) {
		return geneSearchService.getYTDComparisonData(powerid, startDate, endDate);
	}
	
}
