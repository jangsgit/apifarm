package mes.app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 모바일 메뉴 컨트롤러
@Controller
@RequestMapping("/mobile")
public class MobileController {


    @GetMapping("/farm-list01")
    public String farmList01(Model model) {
        model.addAttribute("currentPage", "farm-list01");
        return "mobile/farm-list01"; // 하이팜컨트롤러
    }


    @GetMapping("/farm-list02")
    public String farmList02(Model model) {
        model.addAttribute("currentPage", "farm-list02");
        return "mobile/farm-list02"; // 하이팜상태보기
    }

    @GetMapping("/farm-list03")
    public String farmList03(Model model) {
        model.addAttribute("currentPage", "farm-list03");
        return "mobile/farm-list03"; // 버섯컨트롤
    }

    @GetMapping("/farm-list04")
    public String farmList04(Model model) {
        model.addAttribute("currentPage", "farm-list04");
        return "mobile/farm-list04"; // 운영정보
    }

    @GetMapping("/farm-list05")
    public String farmList05(Model model) {
        model.addAttribute("currentPage", "farm-list05");
        return "mobile/farm-list05"; // 스케줄정보
    }

    @GetMapping("/current-status")
    public String currentStatusPage(Model model) {
        model.addAttribute("currentPage", "current-status");
        return "mobile/current-status"; // Return the Thymeleaf view name
    }

    @GetMapping("/fsr-register")
    public String fsrRegister() {
        return "mobile/fsr-register"; // "mobile/fsr-register.html"로 매핑
    }

    @GetMapping("/fsr-search")
    public String fsrSearch() {
        return "mobile/fsr-search"; // "mobile/fsr-search.html"로 매핑
    }

    @GetMapping("/user-info")
    public String userInfo() {
        return "mobile/user-info"; // "mobile/user-info.html"로 매핑
    }
}