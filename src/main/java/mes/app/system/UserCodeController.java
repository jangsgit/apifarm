package mes.app.system;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mes.app.system.service.UserCodeService;
import mes.domain.entity.User;
import mes.domain.entity.UserCode;
import mes.domain.model.AjaxResult;
import mes.domain.repository.UserCodeRepository;


@RestController
@RequestMapping("/api/system/code")
public class UserCodeController {
	
	
	@Autowired
	private UserCodeService codeService;

	@Autowired
	UserCodeRepository userCodeRepository;
	
	@GetMapping("/read")
	public AjaxResult getCodeList(
			@RequestParam("txtCode") String txtCode
			) {
		
		List<Map<String, Object>> items = this.codeService.getCodeList(txtCode);
		AjaxResult result = new AjaxResult();
		
		result.data = items;
		return result;
	}
	
	@GetMapping("/detail")
	public AjaxResult getCode(@RequestParam("id") int id) {
		Map<String, Object> item = this.codeService.getCode(id);
		
		AjaxResult result = new AjaxResult();
		result.data = item;
		return result;
	}
	
	@PostMapping("/save")
	public AjaxResult saveCode(
			@RequestParam(value="id", required=false) Integer id,
			@RequestParam("name") String value,
			@RequestParam("code") String code,
			@RequestParam(value="parent_id" , required=false) Integer parent_id,
			@RequestParam("description") String description,
			HttpServletRequest request,
			Authentication auth) {
		User user = (User)auth.getPrincipal();
		
		UserCode c = null;
		
		if(id == null) {
			c = new UserCode();
		} else {
			c = this.userCodeRepository.getUserCodeById(id);
		}
		c.setValue(value);
		c.setCode(code);
		c.setDescription(description);
		c.setParentId(parent_id);
		c.set_audit(user);
		
		c = this.userCodeRepository.save(c);
		
		AjaxResult result = new AjaxResult();
		result.data = c;
		
		return result;
	}
	
	@PostMapping("/delete")
	@Transactional
	public AjaxResult deleteCode(@RequestParam("id") Integer id) {
		this.userCodeRepository.deleteById(id);
		AjaxResult result = new AjaxResult();
		
		return result;
	}

	
}