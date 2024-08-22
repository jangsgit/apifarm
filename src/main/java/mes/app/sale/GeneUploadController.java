package mes.app.sale;

import mes.app.sale.service.GeneUploadService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_RP320;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP320Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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
	@Transactional
	public AjaxResult saveGeneData(
			@RequestParam("upload_file") MultipartFile upload_file,
			@RequestParam("spworkcd") String spworkcd,
			@RequestParam("spworknm") String spworknm,
			@RequestParam("spcompcd") String spcompcd,
			@RequestParam("spcompnm") String spcompnm,
			@RequestParam("spplancd") String spplancd,
			@RequestParam("spplannm") String spplannm,
			@RequestParam(value = "date6", required = false) String date6,
			Authentication auth
	) throws FileNotFoundException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		
		AjaxResult result = new AjaxResult();
		try {
			User user = (User) auth.getPrincipal();
			
			String dateToUse = (date6 != null && !date6.isEmpty()) ? date6 : LocalDate.now().toString(); // date6 없으면 현재날짜 사용
			
			// Step 1: 엑셀 파일 읽기 (파일을 저장하지 않고 바로 읽기)
			List<List<String>> dataRows = geneUploadService.excel_read(upload_file);
			List<String> dates = dataRows.stream().map(row -> row.get(STANDDT_COL)).collect(Collectors.toList());
			
			// Step 2: 새 데이터 저장
			// 새로운 데이터를 저장하기 전에 기존 데이터를 삭제하는 대신, 새 데이터를 성공적으로 저장한 후 삭제 작업을 수행합니다.
			List<TB_RP320> entitiesToSave = dataRows.stream().map(row -> {
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
					try {
						entity.getClass().getMethod("setMevalue" + String.format("%02d", j + 1), BigDecimal.class)
								.invoke(entity, new BigDecimal(row.get(MEVALUE_LIST.get(j))));
					} catch (Exception e) {
						throw new RuntimeException("Error setting mevalue for " + j, e);
					}
				}
				entity.setMevaluet(new BigDecimal(row.get(MEVALUET_COL)));
				
				entity.setIndatem(LocalDate.parse(dateToUse)); // date6 값을 indatem에 설정
				
				return entity;
			}).collect(Collectors.toList());
			
			// Step 3: 기존 데이터 삭제 후 새로운 데이터 저장
			TB_RP320Repository.deleteAll();
			TB_RP320Repository.saveAll(entitiesToSave);
			
			result.success = true;
			result.message = "데이터가 성공적으로 저장되었습니다!";
			// 추출된 날짜 데이터를 클라이언트로 반환
			result.data = dates; // 날짜 목록을 클라이언트에 전송
		} catch (Exception e) {
			result.success = false;
			result.message = "An error occurred: " + e.getMessage();
			throw e; // 예외를 다시 던져 트랜잭션이 롤백되도록 함
		}
		
		return result;
	}
	
	@GetMapping("/read")
	public ResponseEntity<List<TB_RP320>> getAllData() {
//		List<TB_RP320> data = TB_RP320Repository.findAll();
		List<TB_RP320> data = TB_RP320Repository.findAll(Sort.by(Sort.Direction.ASC, "standdt", "powerid"));
		if (data.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
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
	@PatchMapping("/update")
	public ResponseEntity<AjaxResult> updateData(@RequestBody List<TB_RP320> updates, Authentication auth) {
		AjaxResult result = new AjaxResult();
		try {
			User user = (User) auth.getPrincipal();
			geneUploadService.updateGeneData(updates, user);
			
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