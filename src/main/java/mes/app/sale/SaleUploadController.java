package mes.app.sale;

import mes.app.sale.service.SaleUploadService;
import mes.config.Settings;
import mes.domain.DTO.TB_RP510Dto;
import mes.domain.entity.TB_RP510;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP510Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/sales")
public class SaleUploadController {
	
	@Autowired
	TB_RP510Repository TB_RP510Repository;
	
	@Autowired
	private SaleUploadService saleUploadService;
	
	@Autowired
	Settings settings;
	
	@Autowired
	SqlRunner sqlRunner;
	
	
	// 매출 정보 조회: 날짜 범위가 주어지면 해당 범위에 맞는 데이터를 반환
	@GetMapping("/fetch")
	public ResponseEntity<List<TB_RP510>> fetchSalesData(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
		if (startDate != null && endDate != null) {
			List<TB_RP510> sales = saleUploadService.fetchAllSalesByDateRange(startDate, endDate);
			return ResponseEntity.ok(sales);
		} else {
			List<TB_RP510> sales = saleUploadService.fetchAllSales();
			return ResponseEntity.ok(sales);
		}
	}
	
	
	// 매출 정보 저장
	@PostMapping("/upload")
	public ResponseEntity<AjaxResult> saveSalesInfo(@RequestBody TB_RP510Dto dto, Authentication auth) throws ParseException {
		AjaxResult result = new AjaxResult();
		
		try {
			saleUploadService.saveSalesInfo(dto);
			result.success = true;
//			result.message = "성공적으로 저장되었습니다.";
		} catch (Exception e) {
			result.success = false;
			result.message = e.getMessage();
		}
		return ResponseEntity.ok(result);
	}
	
	
	// 수정
	@PutMapping("/update/{salesym}")
	public ResponseEntity<AjaxResult> updateSalesInfo(@PathVariable String salesym, @RequestBody TB_RP510Dto dto) {
		AjaxResult result = new AjaxResult();
		try {
			saleUploadService.updateSalesInfo(salesym, dto);
			result.success = true;
			result.message = "매출 정보가 성공적으로 수정되었습니다.";
		} catch (Exception e) {
			result.success = false;
			result.message = "수정 실패: " + e.getMessage();
		}
		return ResponseEntity.ok(result);
	}
	
	// 삭제
	@DeleteMapping("/delete/{salesym}")
	public ResponseEntity<AjaxResult> deleteSalesInfo(@PathVariable String salesym) {
		AjaxResult result = new AjaxResult();
		try {
			saleUploadService.deleteSalesInfo(salesym);
			result.success = true;
			result.message = "Data deleted successfully!";
		} catch (Exception e) {
			result.success = false;
			result.message = "Failed to delete data: " + e.getMessage();
		}
		return ResponseEntity.ok(result);
	}
}
