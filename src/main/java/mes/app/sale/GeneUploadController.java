package mes.app.sale;

import mes.app.sale.service.GeneUploadService;
import mes.config.Settings;
import mes.domain.entity.actasEntity.TB_RP320;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP320Repository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gene/upload")
public class GeneUploadController {
	
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
	
	@PostMapping("/upload_save")
//	@Transactional
	public AjaxResult saveGeneData(@RequestParam("upload_file") MultipartFile upload_file, MultipartHttpServletRequest multipartRequest, Authentication auth) throws FileNotFoundException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		
		// Step 1: 기존 데이터 삭제
		TB_RP320Repository.deleteAll();
		
		// Step 2: 엑셀 파일 저장 및 읽기
		String uploadFilename = geneUploadService.saveUploadedFile(upload_file);
		List<List<String>> dataRows = geneUploadService.excel_read(uploadFilename);
		
		
		// Step 3: 새 데이터 저장
		for (List<String> row : dataRows) {
			TB_RP320 entity = new TB_RP320();
			entity.setStanddt(row.get(STANDDT_COL));
			entity.setPowerid(row.get(POWERID_COL));
			entity.setPowernm(row.get(POWERNM_COL));
			entity.setChargedv(row.get(CHARGEDV_COL));
			for (int i = 0; i < MEVALUE_LIST.size(); i++) {
				entity.getClass().getMethod("setMevalue" + String.format("%02d", i + 1), BigDecimal.class)
						.invoke(entity, new BigDecimal(row.get(MEVALUE_LIST.get(i))));
			}
			entity.setMevaluet(new BigDecimal(row.get(MEVALUET_COL)));

//			pk들 임의의 값 설정
			entity.setSpworkcd("def");
			entity.setSpcompcd("def");
			entity.setSpplancd("def");
			
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
	
}