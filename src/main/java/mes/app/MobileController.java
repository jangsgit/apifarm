package mes.app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 모바일 메뉴 컨트롤러
@Controller
@RequestMapping("/mobile")
public class MobileController {

    @GetMapping("/ticket-list")
    public String ticketList(Model model) {
        model.addAttribute("currentPage", "ticket-list");
        return "mobile/ticket-list"; // "mobile/ticket-list.html"로 매핑
    }

    @GetMapping("/ticket-register")
    public String ticketRegister(Model model) {
        model.addAttribute("currentPage", "ticket-register");
        return "mobile/ticket-register"; // "mobile/ticket-register.html"로 매핑
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