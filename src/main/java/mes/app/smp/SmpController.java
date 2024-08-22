package mes.app.smp;

import mes.app.smp.service.SmpService;
import mes.domain.entity.UserCode;
import mes.domain.repository.UserCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/smp")
public class SmpController {

	@Autowired
	private SmpService smpService;
	
	@Autowired
	private UserCodeRepository userCodeRepository;
	
	@GetMapping("/smpCurrent")
	public ResponseEntity<?> getCurrentSmp() {
		Optional<UserCode> smpCode = userCodeRepository.findById(158);  // ID 158은 SMP 값을 저장하는 코드 ID
		if (smpCode.isPresent()) {
			return ResponseEntity.ok(smpCode.get().getValue());
		} else {
			// 해당 ID의 UserCode가 존재하지 않는 경우, 404 Not Found 상태를 반환
			return ResponseEntity.notFound().build();
		}
	}
}