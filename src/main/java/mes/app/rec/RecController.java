package mes.app.rec;

import mes.app.rec.service.RecService;
import mes.domain.entity.UserCode;
import mes.domain.repository.UserCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/rec")
public class RecController {

	@Autowired
	private RecService recService;
	
	@Autowired
	private UserCodeRepository userCodeRepository;
	
	@GetMapping("/recAverage")
	public ResponseEntity<?> getRecAverage() {
		// REC 단가가 저장된 UserCode를 가져옴 (ID 161)
		Optional<UserCode> recCode = userCodeRepository.findById(161);
		if (recCode.isPresent()) {
			return ResponseEntity.ok(recCode.get().getValue());
		} else {
			// 해당 ID의 UserCode가 존재하지 않는 경우, 404 Not Found 상태를 반환
			return ResponseEntity.notFound().build();
		}
		
		// 수동으로 업데이트 메서드 호출 버전
		/*recService.updateRecData(); 
		Optional<UserCode> recCode = userCodeRepository.findById(161); // 업데이트된 REC 단가 가져오기
		if (recCode.isPresent()) {
			return ResponseEntity.ok(Map.of("message", "REC data updated manually", "value", recCode.get().getValue()));
		} else {
			return ResponseEntity.ok(Map.of("message", "REC data updated manually", "value", "No data"));
		}*/
	}
}
