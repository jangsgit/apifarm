package mes.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import mes.app.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import mes.config.Settings;
import mes.domain.GUIConfiguration;
import mes.domain.entity.MenuUseLog;
import mes.domain.entity.User;
import mes.domain.model.GUIData;
import mes.domain.repository.MenuUseLogRepository;
import mes.domain.services.SqlRunner;

@Controller
public class GuiController {
	
	@Autowired
	Settings settings;
	
	@Autowired
	SqlRunner sqlRunner;
	
	@Autowired
	MenuUseLogRepository menuUseLogRepository;

	@Autowired
	UserService userService;
	
	private ModelAndView getView(String gui, String templateName, User user, MultiValueMap<String, String> allRequestParams) {
		ModelAndView mv = new ModelAndView();                                  //ModelAndView 객체 생성
        GUIData guiData= GUIConfiguration.getGUIInfo(gui);					   //
        if (guiData!=null) {
			String userid = user.getUsername();
        	String username = user.getUserProfile().getName();
//			List<Map<String, Object>> sandanList = userService.getUserSandanList(userid);

        	for(String k : allRequestParams.keySet()){
        	     mv.addObject(k, allRequestParams.get(k).get(0));
        	}
        	
            // get 파라미터를 뷰에 넘겨줘야함
        	//mv.addAllObjects(allRequestParams);
        	
        	String templatePath = guiData.templates.get(templateName); //GuiData에서 templateName이 가지는 실제 값에 해당하는 value를 가져옴
        	mv.setViewName(templatePath); //가져온 templatePath를 ModelAndView객체에 넣어준다. 여기서 templatePath는 사용자에게 보여줄 html화면 위치를 반환하여 해당 html을 보여준다.

			mv.addObject("userid", userid);
    		mv.addObject("username", username);  //ModelAndView객체에 여러가지의 오브젝트값을 키, 밸류 형태로 넣어준다.
    		mv.addObject("userinfo", user);    		
    		mv.addObject("gui_code", gui);
    		mv.addObject("template_key", templateName);
//			mv.addObject("sandanList", sandanList);
    		
    		String mqtt_host = settings.getProperty("mqtt_host");  //application.properties에 명시되어 있는 mqtt_host 속성의 값을 가져옴
    		String mqtt_web_port = settings.getProperty("mqtt_web_port"); //이하 동문
    		String hmi_topic = settings.getProperty("hmi_topic"); //이하 동문
    		mv.addObject("mqtt_host", mqtt_host);  //가져와서 ModelAndView객체에 넣어준다.
    		mv.addObject("mqtt_web_port", mqtt_web_port);
    		mv.addObject("hmi_topic", hmi_topic);

    		// 권한처리 루틴 시작
    		// 메뉴별 권한 조회, sql문을 명시적으로 실행함으로서 DB에서 화면에 대한 권한자격을 가져옴
    		Map<String,Object> map = new HashMap<String,Object>();
    		
			MapSqlParameterSource dicParam = new MapSqlParameterSource();			
			dicParam.addValue("MenuCode", gui);
			dicParam.addValue("UserGroupId", user.getUserProfile().getUserGroup().getId());
			
			String sql = """
						select "AuthCode" from user_group_menu ugm
						where ugm."MenuCode" = :MenuCode and "UserGroup_id" = :UserGroupId
						""";
			
			map = this.sqlRunner.getRow(sql, dicParam);  //sql실행
			
			String active = null;
			
			boolean read_flag = false; 
			boolean write_flag = false;
			
			if (map != null) {
				active = map.get("AuthCode").toString();  //쿼리 실행 결과에서 AuthCode(권한코드)를 가져와서 active 변수에 할당해준다.
				
				// 권한처리
				if(active.equals("RW")) {               //RW면 읽기,쓰기 가능
					read_flag = true;
					write_flag = true;
				} else if (active.equals("R")) {       //R이면 read만 가능
					read_flag = true;
					write_flag = false;
				} else if (active.equals("W")) {        //w면 writer만 가능
					read_flag = false;
					write_flag = true;
				} else {
					read_flag = false;
					write_flag = false;
				}
			} else {										//권한이 없으면 읽기만 가능
				read_flag = true;
				write_flag = false;
			}
			
			// 권한 셋팅
    		mv.addObject("read_flag", read_flag);
    		mv.addObject("write_flag", write_flag);   //ModelAndView객체에 넣어준다.
    		
            // 메뉴 접속 로그 기록 저장
            if ("default".equals(templateName)) {				// 파라미터로 받은 templateName의 값이 default일 경우 로그를 기록하는 로직 실행
            	try {
    	        	MenuUseLog menuUseLog = new MenuUseLog();
    	        	menuUseLog.setMenuCode(gui);
    	        	menuUseLog.setUserId(user.getId());
    	        	menuUseLog.set_audit(user);
    	        	
    	            this.menuUseLogRepository.save(menuUseLog);
            	} catch(Exception ex) {
            		System.out.println("MenuUseLog save" + ex);
            	}
            }
            
        }
        else {
        	//404 page 이동
        	mv.setViewName("errors/404");
        }       
        
		return mv;    //view로 ModelAndView객체를 리턴해준다.
	}
	
	@GetMapping("/gui/{gui}")
	public ModelAndView pageGUI(
			@PathVariable("gui")String gui, 
			@RequestParam MultiValueMap<String, String> allRequestParams
			) {
		
        SecurityContext sc = SecurityContextHolder.getContext();
        Authentication auth = sc.getAuthentication();         
        User user = (User)auth.getPrincipal();        
        //System.out.println(user.getUsername());		
        

        String templateName = "default";       
        ModelAndView mv = this.getView(gui, templateName, user, allRequestParams);
		return mv;
	}
	
	@GetMapping("/gui/{gui}/{template}")
	public ModelAndView pageGUITemplate(
			@PathVariable("gui")String gui, 
			@PathVariable("template")Optional<String> template,
			@RequestParam MultiValueMap<String, String> allRequestParams,
			@RequestParam(value = "checkdt", required = false) String checkdt,
			Authentication auth
			) {	
        
        User user = (User)auth.getPrincipal();        		
        
        String templateName = "default";
        if(template.isPresent()) {
        	templateName = template.get();
        }
        
        ModelAndView mv = this.getView(gui, templateName, user, allRequestParams);
		mv.addObject("checkdt", checkdt);
		return mv;
	}
	
	@GetMapping("/page/{folder}/{template}")
	public ModelAndView pageTemplatePathView(
			@PathVariable("folder")String folder,
			@PathVariable("template")String template,
			Authentication auth
			) {
		User user = (User)auth.getPrincipal();
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("userinfo", user);
		
		String viewName = String.format("/page/%s/%s", folder, template);
		mv.setViewName(viewName);		
		
		return mv;
		
	}

	@GetMapping("/test")
	public String testPage(){
		return "/test";
	}
}