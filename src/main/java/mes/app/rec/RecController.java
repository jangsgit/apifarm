package mes.app.rec;

import mes.app.rec.service.RecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rec")
public class RecController {
	
	@Autowired
	private RecService recService;
	
	@GetMapping("/recAverage")
	public String getRecInfo() {
		return recService.getRecData();
	}
}
