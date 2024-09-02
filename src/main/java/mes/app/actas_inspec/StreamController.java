package mes.app.actas_inspec;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class StreamController {


    @GetMapping("/stream")
    public String streamPage(){
        return "rtsp";
    }
}
