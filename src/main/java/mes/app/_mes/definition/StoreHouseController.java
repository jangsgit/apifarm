package mes.app.definition;

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

import mes.app.definition.service.StoreHouseService;
import mes.domain.entity.StoreHouse;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.repository.StorehouseRepository;


@RestController
@RequestMapping("/api/definition/storehouse")
public class StoreHouseController {

	@Autowired
	StorehouseRepository StorehouseRepository;
	
	@Autowired
	private StoreHouseService StoreHouseService;
	
	// 창고 목록 조회
	@GetMapping("/read")
	public AjaxResult getStorehouseList(
			@RequestParam("storehouse_name") String storehouseName,
			HttpServletRequest request) {
		
		List<Map<String,Object>> items = this.StoreHouseService.getStorehouseList(storehouseName);
		
		AjaxResult result = new AjaxResult();
        result.data = items;        				
        
		return result;
	}
	
	// 창고 상세정보 조회
	@GetMapping("/detail")
	public AjaxResult getStorehouseDetail(
			@RequestParam("storehouse_id") int storehouseId,
			HttpServletRequest request) {
		Map<String,Object> item = this.StoreHouseService.getStorehouseDetail(storehouseId);
		
		AjaxResult result = new AjaxResult();
		result.data = item;
		
		return result;
	}
	
	//창고 정보 저장
	@PostMapping("/save")
	public AjaxResult saveStorehouse(
			@RequestParam(value="id", required=false) Integer id,
			@RequestParam(value="storehouse_type") String storehouseType,
			@RequestParam(value="storehouse_code") String storehouseCode,
			@RequestParam(value="storehouse_name") String storehouseName,
			@RequestParam(value="factory_id") Integer factoryId,
			@RequestParam(value="description") String description,
			HttpServletRequest request,
			Authentication auth ) {
		
		AjaxResult result = new AjaxResult();
		
		User user = (User)auth.getPrincipal();
		
		StoreHouse storehouse = null;
		
		if (id ==null) {
			storehouse = new StoreHouse();
		}else {
			storehouse = this.StorehouseRepository.getStoreHouseById(id);
		}
		
		boolean codeChk = this.StorehouseRepository.getByCode(storehouseCode).isEmpty();
		
		if (storehouseCode.equals(storehouse.getCode()) == false && codeChk == false) {
			result.success = false;
			result.message="중복된 코드명 입니다.";
			return result;
		}
		
		boolean nameChk = this.StorehouseRepository.getByName(storehouseName).isEmpty();
		
		if (storehouseName.equals(storehouse.getName()) == false && nameChk == false) {
			result.success = false;
			result.message="중복된 창고명 입니다.";
			return result;
		}
		
		storehouse.setHouseType(storehouseType);
		storehouse.setCode(storehouseCode);
		storehouse.setName(storehouseName);
		storehouse.setFactory_id(factoryId);
		storehouse.setDescription(description);
		storehouse.set_audit(user);
		
		storehouse = this.StorehouseRepository.save(storehouse);
		
        result.data=storehouse;
		return result;
	}
	
	// 창고 정보 삭제
	@PostMapping("/delete")
	public AjaxResult deleteStorehouse(@RequestParam("id") Integer id) {
		
		this.StorehouseRepository.deleteById(id);
		
		AjaxResult result = new AjaxResult();
		
		return result;
	}
	
}
