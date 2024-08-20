package mes.app.sale;

import mes.app.sale.service.GeneUploadService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_RP320;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP320Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gene/upload")
public class GeneUploadController {


//	try catch문!!!!
	
	@Autowired
	TB_RP320Repository TB_RP320Repository;
	
	@Autowired
	private GeneUploadService geneUploadService;
	
	@Autowired
	Settings settings;
	
	// 엑셀 컬럼 순서 정의
	private static final int STANDDT_COL = 0; // 기준일자
	private static final int POWERID_COL = 1; // 발전기ID
	private static final int POWERNM_COL = 2; // 발전기명
	private static final int CHARGEDV_COL = 3; // 충전/방전
	private static final List<Integer> MEVALUE_LIST = Arrays.asList(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27); // 계량값 01시부터 24시
	private static final int MEVALUET_COL = 28; // 계량값 합계
	
	
	// 엑셀 업로드
	@PostMapping("/upload_save")
//	@Transactional
	public AjaxResult saveGeneData(
			@RequestParam("upload_file") MultipartFile upload_file,
			@RequestParam("spworkcd") String spworkcd,
			@RequestParam("spworknm") String spworknm,
			@RequestParam("spcompcd") String spcompcd,
			@RequestParam("spcompnm") String spcompnm,
			@RequestParam("spplancd") String spplancd,
			@RequestParam("spplannm") String spplannm,
			Authentication auth
	) throws FileNotFoundException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		
		User user = (User) auth.getPrincipal();
		
		// Step 1: 기존 데이터 삭제
		TB_RP320Repository.deleteAll();
		
		// Step 2: 엑셀 파일 저장 및 읽기
		String uploadFilename = geneUploadService.saveUploadedFile(upload_file);
		List<List<String>> dataRows = geneUploadService.excel_read(uploadFilename);
		
		// Step 3: 새 데이터 저장
		for (int i = 0; i < dataRows.size(); i++) {
			List<String> row = dataRows.get(i);
			TB_RP320 entity = new TB_RP320();
			
			// 사용자 정보 설정
			entity.setInuserid(user.getUsername());  // 사용자 아이디
			entity.setInusernm(user.getFirst_name() + " " + user.getLast_name());  // 사용자 전체 이름
			
			entity.setSpworkcd(spworkcd);
			entity.setSpworknm(spworknm);
			entity.setSpcompcd(spcompcd);
			entity.setSpcompnm(spcompnm);
			entity.setSpplancd(spplancd);
			entity.setSpplannm(spplannm);
			
			entity.setStanddt(row.get(STANDDT_COL));
			entity.setPowerid(row.get(POWERID_COL));
			entity.setPowernm(row.get(POWERNM_COL));
			entity.setChargedv(row.get(CHARGEDV_COL));
			
			for (int j = 0; j < MEVALUE_LIST.size(); j++) {
				entity.getClass().getMethod("setMevalue" + String.format("%02d", j + 1), BigDecimal.class)
						.invoke(entity, new BigDecimal(row.get(MEVALUE_LIST.get(j))));
			}
			entity.setMevaluet(new BigDecimal(row.get(MEVALUET_COL)));
			
			TB_RP320Repository.save(entity);
		}
		
		return new AjaxResult();
	}
	
	@GetMapping("/read")
	public ResponseEntity<List<TB_RP320>> getAllData() {
		List<TB_RP320> data = TB_RP320Repository.findAll();
		return ResponseEntity.ok(data);
	}
	
	// 발전기코드 옵션 동적 생성
	@GetMapping("/powerids")
	public ResponseEntity<List<String>> getPowerIds() {
		List<TB_RP320> data = TB_RP320Repository.findAll();
		List<String> powerIds = data.stream()
				.map(TB_RP320::getPowerid)
				.distinct()
				.collect(Collectors.toList());
		return ResponseEntity.ok(powerIds);
	}
	
	// 검색
	@GetMapping("/search")
	public ResponseEntity<List<TB_RP320>> searchGeneData(
			@RequestParam String startdt,
			@RequestParam String enddt,
			@RequestParam(required = false) String powerid) {
		
		List<TB_RP320> results = TB_RP320Repository.searchGeneData(startdt, enddt, powerid);
		return ResponseEntity.ok(results);
	}
	
	
	// 수정
	// PostMapping, PutMapping(멱등성), PatchMapping 중 PatchMapping 사용
	@PatchMapping("/update")
	public ResponseEntity<AjaxResult> updateData(@RequestBody List<TB_RP320> updates) {
		AjaxResult result = new AjaxResult();
		try {
			for (TB_RP320 update : updates) {
				update.setUpdatem(LocalDate.now());  // 현재 날짜로 설정
				TB_RP320Repository.save(update);  // JpaRepository의 save 메소드는 ID가 존재하면 merge (업데이트)를 수행합니다.
			}
			result.success = true;
			result.message = "Data updated successfully!";
		} catch (Exception e) {
			result.success = false;
			result.message = "Failed to update data: " + e.getMessage();
		}
		return ResponseEntity.ok(result);
	}
	
	// 삭제
	@DeleteMapping("/deleteAll")
	public ResponseEntity<AjaxResult> deleteAllGeneData() {
		AjaxResult result = new AjaxResult();
		try {
			TB_RP320Repository.deleteAll();
			result.success = true;
			result.message = "All data deleted successfully!";
		} catch (Exception e) {
			result.success = false;
			result.message = "Failed to delete data: " + e.getMessage();
		}
		return ResponseEntity.ok(result);
	}
	
}