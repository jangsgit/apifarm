package mes.app.test;

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

import mes.app.test.service.TestMasterGroupService;
import mes.domain.entity.TestMasterGroup;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TestMasterGroupRepository;
import mes.domain.services.CommonUtil;

@RestController
@RequestMapping("/api/test/test_master_group")
public class TestMasterGroupController {
	
	@Autowired
	TestMasterGroupRepository testMasterGroupRepository;
	
	@Autowired
	private TestMasterGroupService testMasterGroupService;
	
	@GetMapping("/read")
	public AjaxResult getTestMasterGroupList(
			@RequestParam(value="test_grp_name", required=false) String testGroupName,
			@RequestParam(value="test_class", required=false) String testClass,
			HttpServletRequest request) {
		List <Map<String, Object>> items = this.testMasterGroupService.getTestMasterGroupList(testGroupName, testClass);
		
		AjaxResult result = new AjaxResult();
		result.data = items;
		
		return result;
	}
	
	
	@PostMapping("/save")
	public AjaxResult saveTestMasterGroup(
			@RequestParam("list") String list,
			//@RequestParam(value="id", required=false) Integer id,
			//@RequestParam(value="Name") String name, 
			//@RequestParam(value="TestClass", required=false) String testClass,
			HttpServletRequest request,
			Authentication auth	) {
		List<Map<String,Object>> items = CommonUtil.loadJsonListMap(list);
		Map<String,Object> item = items.get(0);
		Integer id = (Integer)item.get("id");
		String name = (String)item.get("test_grp_name");
		String testClass = (String)item.get("test_class");
		User user = (User)auth.getPrincipal();
		System.out.println(item);
		
		TestMasterGroup tmg = null;
		if (id == null) {
			tmg = new TestMasterGroup();
		} else {
			tmg = this.testMasterGroupRepository.getTestMasterGroupById(id);
		}
		tmg.setName(name);
		tmg.setTestclass(testClass);
		tmg.set_audit(user);
		tmg = this.testMasterGroupRepository.save(tmg);
		AjaxResult result = new AjaxResult();
		result.data = tmg;
		return result;
	}
	
	@PostMapping("/delete")
	public AjaxResult deleteEquipment(@RequestParam("id") Integer id) {
		this.testMasterGroupRepository.deleteById(id);
		AjaxResult result = new AjaxResult();
		return result;
	}
	
	
}
