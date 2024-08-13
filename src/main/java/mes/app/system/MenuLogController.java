package mes.app.system;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mes.app.system.service.MenuLogService;
import mes.domain.model.AjaxResult;

@RestController
@RequestMapping("/api/system/menulog")
public class MenuLogController {

	@Autowired 
	private MenuLogService menuLogService;
	
	@GetMapping("/log_count")
	public AjaxResult getLogCount(
			@RequestParam("date_from") String dateFrom,
			@RequestParam("date_to") String dateTo,
			@RequestParam("cboMenu") String menuCode,
			@RequestParam("cboUser") String userPk ){
		
		List<Map<String, Object>> items = this.menuLogService.getLogCount(dateFrom,dateTo,menuCode,userPk);
		
		AjaxResult result = new AjaxResult();
		result.data = items;
		
		return result;
	}
	
	@GetMapping("/log_list")
	public AjaxResult getLogList(
			@RequestParam("date_from") String dateFrom,
			@RequestParam("date_to") String dateTo,
			@RequestParam("cboMenu") String menuCode,
			@RequestParam("cboUser") String userPk ){
		
		List<Map<String, Object>> items = this.menuLogService.getLogList(dateFrom,dateTo,menuCode,userPk);
		
		AjaxResult result = new AjaxResult();
		result.data = items;
		
		return result;
	}
	
	// 사용자 목록 조회
	@GetMapping("/user_list")
	public AjaxResult getUserList(){
		
		List<Map<String, Object>> items = this.menuLogService.getUserList();
		
		AjaxResult result = new AjaxResult();
		result.data = items;
		
		return result;
	}
	@GetMapping("/read")
	public AjaxResult getLogReadList(
			@RequestParam(value = "date_from", required = false) String dateFrom,
			@RequestParam(value = "date_to", required = false) String dateTo,
			@RequestParam(value = "cboMenu", required = false) String menuCode,
			@RequestParam(value = "cboUser", required = false) String userPk) {

		// 데이터 조회
		List<Map<String, Object>> items = this.menuLogService.getLogCount(dateFrom,dateTo,menuCode,userPk);

		// AjaxResult 객체 생성
		AjaxResult result = new AjaxResult();
		result.data = items;

		return result;
	}

	@GetMapping("/read2")
	public AjaxResult getLogReadList2(
			@RequestParam(value = "date_from", required = false) String dateFrom,
			@RequestParam(value = "date_to", required = false) String dateTo,
			@RequestParam(value = "cboMenu", required = false) String menuCode,
			@RequestParam(value = "cboUser", required = false) String userPk) {

		// 데이터 조회
		List<Map<String, Object>> items = this.menuLogService.getLogList(dateFrom,dateTo,menuCode,userPk);

		// AjaxResult 객체 생성
		AjaxResult result = new AjaxResult();
		result.data = items;

		return result;
	}



}
