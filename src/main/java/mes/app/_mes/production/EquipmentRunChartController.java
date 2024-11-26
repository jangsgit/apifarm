package mes.app.production;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mes.app.production.service.EquipmentRunChartService;
import mes.domain.entity.EquRun;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.repository.EquRunRepository;
import mes.domain.services.SqlRunner;

@RestController
@RequestMapping("/api/production/equipment_run_chart")
public class EquipmentRunChartController {
	
	@Autowired
	SqlRunner sqlRunner;
	
	@Autowired
	EquipmentRunChartService equipmentRunChartService;
	
	@Autowired
	EquRunRepository equRunRepository;

	// 차트 searchMainData
	@GetMapping("/read")
	public AjaxResult getEquipmentRunChart(
			@RequestParam(value="date_from", required=false) String date_from, 
    		@RequestParam(value="date_to", required=false) String date_to,
    		@RequestParam(value="id", required=false) Integer id,
    		@RequestParam(value="runType", required=false) String runType,
			HttpServletRequest request) {
		
		List<Map<String, Object>> items = this.equipmentRunChartService.getEquipmentRunChart(date_from, date_to, id, runType);      
        AjaxResult result = new AjaxResult();
        result.data = items;        
		return result;
	}
	
	// 차트 fillData
	@GetMapping("/readData")
	public AjaxResult getEquipmentRunChart(
    		@RequestParam(value="id", required=false) Integer id,
    		@RequestParam(value="runType", required=false) String runType,
			HttpServletRequest request) {
		
		List<Map<String, Object>> items = this.equipmentRunChartService.getEquipmentRunChart(null, null, id, runType);      
        AjaxResult result = new AjaxResult();
        result.data = items;        
		return result;
	}
	
	// saveData
	@PostMapping("/addData")
	public AjaxResult addDataEquipmentRunChart (
			@RequestParam(value="id", required=false) Integer id,
			@RequestParam(value="Equipment_id", required=false) Integer Equipment_id,
			@RequestParam(value="start_date", required=false) String start_date,
			@RequestParam(value="StartTime", required=false) String StartTime,
			@RequestParam(value="end_date", required=false) String end_date,
			@RequestParam(value="EndTime", required=false) String EndTime,
			@RequestParam(value="RunState", required=false) String RunState,
			@RequestParam(value="Description", required=false) String Description,
			@RequestParam(value="StopCause_id", required=false) Integer StopCause_id,
			HttpServletRequest request,
			Authentication auth) {
		
		AjaxResult result = new AjaxResult();

		User user = (User)auth.getPrincipal();
		
		Timestamp startDate = Timestamp.valueOf(start_date + ' ' + StartTime + ":00");
		Timestamp endDate = Timestamp.valueOf(end_date + ' ' + EndTime + ":00");
		
		EquRun er = null;
		
		if (id==null) {
			er = new EquRun();
		} else {
			er = this.equRunRepository.getEquRunById(id);
		}
		
		er.setEquipmentId(Equipment_id);
		er.setStartDate(startDate);
		er.setEndDate(endDate);
		er.setRunState(RunState);
		er.setDescription(Description);
		er.setStopCauseId(StopCause_id);
		er.set_audit(user);
		this.equRunRepository.save(er);
		
		result.success = true;
		result.message = "저장하였습니다.";
		result.data = er.getId();
	    return result;
	}
	
	// delDataBind
	@PostMapping("/delData")
	public AjaxResult deleteEquipmentRunChart(
			@RequestParam("id") Integer id) {
		
		this.equRunRepository.deleteById(id);
		AjaxResult result = new AjaxResult();
		return result;
	}
}
