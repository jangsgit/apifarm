package mes.app.account;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mes.app.account.service.TB_RP940_Service;
import mes.domain.DTO.TB_RP940Dto;
import mes.domain.DTO.TB_RP945Dto;
import mes.domain.repository.TB_RP940Repository;
import mes.domain.repository.TB_RP945Repository;
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
import mes.domain.repository.UserRepository;
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
	SqlRunner sqlRunner;

	@Autowired
	TB_RP940_Service tbRp940Service;

	@Autowired
	TB_RP940Repository tb_rp940Repository;

	@Autowired
	TB_RP945Repository tb_rp945Repository;


	@Resource(name="authenticationManager")
    private AuthenticationManager authManager;
	
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

		List<TB_RP940> list = tb_rp940Repository.findAll();

		System.out.print(list);




    	AjaxResult result = new AjaxResult();
    	
    	HashMap<String, Object> data = new HashMap<String, Object>();
    	result.data = data;
    	
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
        CustomAuthenticationToken auth = (CustomAuthenticationToken)authManager.authenticate(authReq);
        
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

		if(!rp940.isPresent()){

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
	public AjaxResult RegisterUser(
			@RequestParam(value = "agency") String agency,
			@RequestParam(value = "agencyDepartment") String agencyDepartment,
			@RequestParam(value = "level", required = false) String level,
			@RequestParam(value = "name") String name,
			@RequestParam(value = "tel", required = false) String tel,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "id") String id,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "passwordchk") String passwordchk,
			@RequestParam(value = "authType") String authType,
			@RequestParam(value = "val") String val,
			@RequestParam(value = "reason") String reason, Authentication auth){

			AjaxResult result = new AjaxResult();
 			//StringUtils.hasText: 파라미터문자가 공백을 제외하고 길이가 1이상인 경우인지 따진다. null 받지 않는다.


			//dto에 담는다 . TODO: 근데 컬럼명하고 파라미터명을 다르게 한 것은 보안때문에 작명을 다르게 함. 그리고 controller에서 entity 매핑보다는 dto가 더 안전하다
			// TODO: 로그인할때만 이런식으로 할것.
			try {
					TB_RP940Dto dto = TB_RP940Dto.builder()
						.agency(agency)
						.agencyDepartment(agencyDepartment)
						.authType(authType)
						.email(email)
						.id(id)
						.level(level)
						.tel(tel)
						.name(name)
						.password(Pbkdf2Sha256.encode(password))
						.reason(reason)
						.build();

				String spcompnm = "";
				String spworknm = "";
				String spwordcd = "";

				switch (val) {
					case "dgsan01":
						spwordcd = "dg";
						spcompnm = "성서산단";
						spworknm = "대구";
						break;
					case "dlsan01":
						spwordcd = "dl";
						spcompnm = "대관령산단";
						spworknm = "대관령";
						break;
					case "dksan01":
						spwordcd = "dk";
						spcompnm = "대덕산단";
						spworknm = "대관령";
						break;
				}

				//신청순번의 최대값을 구한후 +1을 하고 문자열로 바꿔줌
				String RawAskSeq = tb_rp945Repository.findMaxAskSeq();
				int AskSeqInt = Integer.parseInt(RawAskSeq) + 1;
				String askseq = String.format("%03d", AskSeqInt);


				TB_RP945Dto dto2 = TB_RP945Dto.builder()
						.userid(id)
						.askseq(askseq)
						.spworkcd(spwordcd)
						.spcompcd(val)
						.spworknm(spworknm)
						.spcompnm(spcompnm)
						.build();

				tbRp940Service.save(dto, dto2);

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
    
}