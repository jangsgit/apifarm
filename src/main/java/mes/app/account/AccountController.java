package mes.app.account;

import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.Option;
import javax.transaction.Transactional;

import mes.app.account.service.TB_RP940_Service;
import mes.app.account.service.TB_RP945_Service;
import mes.domain.DTO.TB_RP940Dto;
import mes.domain.DTO.TB_RP945Dto;
import mes.domain.entity.UserCode;
import mes.domain.entity.UserGroup;
import mes.domain.repository.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestParam;

import mes.domain.entity.TB_RP940;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.security.CustomAuthenticationToken;
import mes.domain.security.Pbkdf2Sha256;
import mes.domain.services.AccountService;
import mes.domain.services.SqlRunner;


@RestController
public class AccountController {
	
	@Autowired
	AccountService accountService;
		
    @Autowired
    UserRepository userRepository;

	@Autowired
	UserCodeRepository userCodeRepository;
	
	@Autowired
	SqlRunner sqlRunner;

	@Autowired
	TB_RP940_Service tbRp940Service;

	@Autowired
	TB_RP945_Service tbRp945Service;


	@Autowired
	TB_RP940Repository tb_rp940Repository;

	@Autowired
	TB_RP945Repository tb_rp945Repository;


	@Resource(name="authenticationManager")
    private AuthenticationManager authManager;
    @Autowired
    private UserGroupRepository userGroupRepository;

	@GetMapping("/login")
    public ModelAndView loginPage(
    		HttpServletRequest request,
    		HttpServletResponse response,
    		HttpSession session, Authentication auth) {

		ModelAndView mv = new ModelAndView("login");		
		
		Map<String, Object> userInfo = new HashMap<String, Object>(); 
		Map<String, Object> gui = new HashMap<String, Object>();

		mv.addObject("userinfo", userInfo);

		mv.addObject("gui", gui);
		if(auth!=null) {
			SecurityContextLogoutHandler handler =  new SecurityContextLogoutHandler();
			handler.logout(request, response, auth);
		}
		
		return mv;
	}
	
	@GetMapping("/logout")
	public void logout(
			HttpServletRequest request
			, HttpServletResponse response) throws IOException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();		
		SecurityContextLogoutHandler handler =  new SecurityContextLogoutHandler();
		
		this.accountService.saveLoginLog("logout", auth);
		
		handler.logout(request, response, auth);
	    response.sendRedirect("/login");
	}

    @PostMapping("/login")
    public AjaxResult postLogin(
    		@RequestParam("username") final String username, 
    		@RequestParam("password") final String password,
    		final HttpServletRequest request) {
    	// 여기로 들어오지 않음.

		//List<TB_RP940> list = tb_rp940Repository.findAll();

		//System.out.print(list);


    	AjaxResult result = new AjaxResult();
    	
    	HashMap<String, Object> data = new HashMap<String, Object>();
    	result.data = data;
    	
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
		CustomAuthenticationToken auth = null;
		try{
			auth = (CustomAuthenticationToken)authManager.authenticate(authReq);
		}catch (AuthenticationException e){
			//e.printStackTrace();
			data.put("code", "NOUSER");
			return result;
		}

        if(auth!=null) {
        	User user = (User)auth.getPrincipal();
        	user.getActive();        	
        	data.put("code", "OK");        	
        	
        } else {
        	result.success=false;
        	data.put("code", "NOID");
        }
        
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", sc);
        
        return result;
    }	
    
    @GetMapping("/account/myinfo")
    public AjaxResult getUserInfo(Authentication auth){
    	User user = (User)auth.getPrincipal();
    	AjaxResult result = new AjaxResult();
    	
    	Map<String, Object> dicData = new HashMap<String, Object>();
    	dicData.put("login_id", user.getUsername());
    	dicData.put("name", user.getUserProfile().getName());    	
    	result.data = dicData;    	
    	return result;    	
    }
    
    @PostMapping("/account/myinfo/password_change")
    public AjaxResult userPasswordChange(
    		@RequestParam("name") final String name,
    		@RequestParam("loginPwd") final String loginPwd,
    		@RequestParam("loginPwd2") final String loginPwd2,    		
    		Authentication auth
    		) {
    	
    	User user = (User)auth.getPrincipal();
        AjaxResult result = new AjaxResult();
        
        if (StringUtils.hasText(loginPwd)==false | StringUtils.hasText(loginPwd2)==false) {
        	result.success=false;
        	result.message="The verification password is incorrect.";
        	return result;
        }
        
        if(loginPwd.equals(loginPwd2)==false) {        	
        	result.success=false;
        	result.message="The verification password is incorrect.";
        	return result;
        }
        
        user.setPassword(Pbkdf2Sha256.encode(loginPwd2));        
        //user.getUserProfile().setName(name);
        this.userRepository.save(user);
        
        String sql = """
        	update user_profile set 
        	"Name"=:name, _modified = now(), _modifier_id=:id 
        	where id=:id 
        """;
        
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("name", name);
        dicParam.addValue("id", user.getId());
        this.sqlRunner.execute(sql, dicParam);
        
        
        return result;
    }


	/***
	 *  아이디 중복 확인
	 * **/
	@PostMapping("/useridchk")
	public AjaxResult IdChk(@RequestParam("userid") final String userid){

		AjaxResult result = new AjaxResult();


		Optional<TB_RP940> rp940 =  tb_rp940Repository.findByUserid(userid);
		Optional<User> user = userRepository.findByUsername(userid);



		if(rp940.isPresent()){
			result.success = false;
			result.message = "이미 신청 완료하였습니다.";
			return result;

		}
		if(!user.isPresent()){

			result.success = true;
			result.message = "사용할 수 있는 계정입니다.";
			return result;

		}else {
			result.success = false;
			result.message = "중복된 계정이 존재합니다.";
			return result;
		}


	}

	/**권한신청**/
	@PostMapping("/Register/save")
	@Transactional
	public AjaxResult RegisterUser(
			@RequestParam(value = "agency") String agency,
			@RequestParam(value = "agencyDepartment") String agencyDepartment,
			@RequestParam(value = "level", required = false) String level,
			@RequestParam(value = "name") String name,
			@RequestParam(value = "tel", required = false) String tel,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "id") String id,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "authType") String authType,
			@RequestParam(value = "spworkcd") String spworkcd,
			@RequestParam(value = "spcompcd") String spcompcd,
			@RequestParam(value = "spplancd") String spplancd,
			@RequestParam(value = "reason") String reason,
			@RequestParam(value = "firstText") String firstText,
			@RequestParam(value = "secondText") String secondText,
			@RequestParam(value = "thirdText") String thirdText,
			@RequestParam(value = "authTypeText") String authTypeText
			){

			AjaxResult result = new AjaxResult();

			try {
					//클라에서 동적으로 값이 넘어와서 몇개인지 모름, 그래서 쉼표구분자로 리스트형태로 분개해서 서버에서 노가다 뛰어여한다.
					List<String> spworkList = Arrays.asList(spworkcd.split(","));
					List<String> spcompList = Arrays.asList(spcompcd.split(","));
					List<String> spplanList = Arrays.asList(spplancd.split(","));

					List<String> firstTextList = Arrays.asList(firstText.split(","));
					List<String> secondTextList = Arrays.asList(secondText.split(","));
					List<String> thirdTextList = Arrays.asList(thirdText.split(","));


					TB_RP940Dto dto = TB_RP940Dto.builder()
						.agency(agency)
						.agencyDepartment(agencyDepartment)
						.authType(authType)
							.authgrpnm(authTypeText)
							.appflag("N")
						.email(email)
						.id(id)
						.level(level)
						.tel(tel.replaceAll("-",""))
						.name(name)
						.password(Pbkdf2Sha256.encode(password))
						.reason(reason)
						.build();

				tbRp940Service.save(dto);

				//신청순번의 최대값을 구한후 +1을 하고 문자열로 바꿔줌
				//이거 반복문 안에 넣으면 db호출이 너무 많다.
				String RawAskSeq = tb_rp945Repository.findMaxAskSeq();
				RawAskSeq = (RawAskSeq != null) ? RawAskSeq : "0";

				int AskSeqInt = Integer.parseInt(RawAskSeq) + 1;


				for(int i=0; i<spworkList.size(); i++){


					String askseq = String.format("%03d", AskSeqInt);

					TB_RP945Dto dto2 = TB_RP945Dto.builder()
							.userid(id)
							.askseq(askseq)
							.spworkcd(spworkList.get(i))
							.spcompcd(spcompList.get(i))
							.spplancd(spplanList.get(i))
							.spworknm(firstTextList.get(i))
							.spcompnm(secondTextList.get(i))
							.spplannm(thirdTextList.get(i))
							.build();

					tbRp945Service.save(dto2);

					AskSeqInt++;
				}

				result.success = true;
				result.message = "신청이 완료되었습니다.";
				return result;

			} catch(Exception e){
				System.out.println(e);

				result.success = false;
				result.message = "에러가발생하였습니다.";
				return result;
			}

	}

	@GetMapping("/user-codes/parent")
	public List<UserCode> getUserCodeByParentId(@RequestParam Integer parentId){
		return userCodeRepository.findByParentId(parentId);
	}

	@GetMapping("/user-auth/type")
	public List<UserGroup> getUserAuthTypeAll(){
		return userGroupRepository.findAll();

	}

	@GetMapping("/user-codes/code")
	public List<UserCode> getUserCodesByCode(@RequestParam String code, @RequestParam String value) {

		List<UserCode> list = userCodeRepository.findByCodeAndValue(code, value);  //TODO: 이거 PK도 아닌데 여러개의 값이 나온다면 어떡하지? 근데 웬만해서는 단일값만 나올것이지만 에러처리는 해야할듯?


		return userCodeRepository.findByParentId(list.get(0).getId());
	}


    
}