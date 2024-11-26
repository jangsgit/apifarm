package mes.app.sale;

import mes.app.sale.service.GeneStatisticsService;
import mes.app.sale.service.GeneUploadService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_RP320;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP320Repository;
import mes.domain.services.SqlRunner;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/sales/gene")
public class GeneUploadController {
	
	@Autowired
	SqlRunner sqlRunner;
	
	@Autowired
	TB_RP320Repository TB_RP320Repository;
	
	@Autowired
	private GeneUploadService geneUploadService;
	
	@Autowired
	private GeneStatisticsService geneStatisticsService;
	
	@Autowired
	Settings settings;
	
	// 엑셀 컬럼 순서 정의
	private static final int STANDDT_COL = 0; // 날짜
	private static final int POWERID_COL = 1; // 발전기ID
	private static final int POWERNM_COL = 2; // 발전기명
	private static final int POWTIME_COL = 3; // 시간
	private static final int SMPAMT_COL = 4; // SMP
	private static final int EMAMT_COL = 5; // 긴급정산상한가격
	private static final int MEVALUE_COL = 6; // 거래량
	private static final int FEEAMT_COL = 7; // 정산금
	private static final int AREAAMT_COL = 8; // 지역자원시설세
	private static final int OUTAMT_COL = 9; // 배출권정산금
	private static final int RPSAMT_COL = 10; // RPS
	private static final int DIFAMT_COL = 11; // 차액정산금
	private static final int SUMAMT_COL = 12; // 최종정산금
	
	// 엑셀 업로드 및 저장
	@PostMapping("/upload")
	@Transactional
	public AjaxResult saveGeneData(@RequestParam("upload_file") MultipartFile upload_file,
								   @RequestParam("spworkcd") String spworkcd,
								   @RequestParam("spworknm") String spworknm,
								   @RequestParam("spcompcd") String spcompcd,
								   @RequestParam("spcompnm") String spcompnm,
								   @RequestParam("spplancd") String spplancd,
								   @RequestParam("spplannm") String spplannm,
								   Authentication auth)
			throws FileNotFoundException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		
		AjaxResult result = new AjaxResult();
		
		try {
			User user = (User) auth.getPrincipal();
			
			// Step 1: 엑셀 파일 읽기 (파일을 저장하지 않고 바로 읽기)
			List<List<String>> dataRows = geneUploadService.excel_read(upload_file);
			List<TB_RP320> entitiesToSave = new ArrayList<>();
			
			// Step 2: 저장할 데이터 준비
			for (int i = 1; i < dataRows.size(); i++) {
				List<String> row = dataRows.get(i);
				if (row.isEmpty() || row.get(STANDDT_COL).isEmpty()) continue; // 빈 줄 건너뛰기
				
				// 엑셀에서 값 추출
//				String powtimeStr = row.get(POWTIME_COL); // 엑셀에서 powtime을 문자열로 가져옴
//				int powtime = parseIntegerSafely(powtimeStr); // 정수로 변환
//				String standdt = convertExcelDate(row.get(STANDDT_COL));
				String standdt = convertExcelDate(row.get(STANDDT_COL));
				int powtime = parseIntegerSafely(row.get(POWTIME_COL));
				
				// 기존 레코드가 있는지 확인
				TB_RP320 entity = TB_RP320Repository
						.findBySpworkcdAndSpcompcdAndSpplancdAndStanddtAndPoweridAndPowtime(
								spworkcd, spcompcd, spplancd, standdt, row.get(POWERID_COL), powtime)
						.orElse(new TB_RP320());
				
				entity.setSpworkcd(spworkcd);
				entity.setSpcompcd(spcompcd);
				entity.setSpplancd(spplancd);
				
				// 나머지 필드들 설정 (엑셀 데이터를 가져와서 엔티티에 설정)
				entity.setStanddt(standdt);
				entity.setPowerid(row.get(POWERID_COL));
				entity.setPowtime(powtime);
				entity.setPowernm(row.get(POWERNM_COL));
				entity.setSmpamt(parseIntegerSafely(row.get(SMPAMT_COL)));   // SMP
				entity.setEmamt(parseIntegerSafely(row.get(EMAMT_COL)));     // 긴급정산상한가격
				entity.setMevalue(parseDoubleSafely(row.get(MEVALUE_COL))); // 거래량
				entity.setFeeamt(parseIntegerSafely(row.get(FEEAMT_COL)));   // 정산금
				entity.setAreaamt(parseIntegerSafely(row.get(AREAAMT_COL))); // 지역자원시설세
				entity.setOutamt(parseIntegerSafely(row.get(OUTAMT_COL)));   // 배출권정산금
				entity.setRpsamt(parseIntegerSafely(row.get(RPSAMT_COL)));   // RPS
				entity.setDifamt(parseIntegerSafely(row.get(DIFAMT_COL)));   // 차액정산금
				entity.setSumamt(parseIntegerSafely(row.get(SUMAMT_COL)));   // 최종정산금
				
				// BaseEntity 필드 설정 (사용자 정보를 기반으로 설정)
				entity.setSpworknm(spworknm);
				entity.setSpcompnm(spcompnm);
				entity.setSpplannm(spplannm);
				
				entity.setInuserid(user.getUsername()); // 사용자 아이디
				entity.setInusernm(user.getFirst_name() + " " + user.getLast_name()); // 사용자 전체 이름
				
				entitiesToSave.add(entity); // 저장할 엔티티 리스트에 추가
			}
			
			// 모든 데이터 저장
			TB_RP320Repository.saveAll(entitiesToSave);
			result.success = true;
			result.message = "데이터가 성공적으로 저장되었습니다!";
		} catch (Exception e) {
			result.success = false;
			result.message = "오류 발생: " + e.getMessage();
			e.printStackTrace(); // 예외를 로그로 출력하여 트랜잭션 오류 확인
			throw e; // 트랜잭션 롤백을 위해 예외 재발생
		}
		return result;
	}
	
	private double parseDoubleSafely(String value) {
		if (value == null || value.trim().isEmpty()) {
			return 0.0;
		}
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}
	
	// 문자열을 안전하게 정수로 변환
	private int parseIntegerSafely(String value) {
		if (value == null || value.trim().isEmpty()) {
			return 0; // 빈 값일 경우 0으로 설정
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return 0; // 정수 변환 실패 시 0으로 설정
		}
	}
	
	// 엑셀 날짜 처리
	private String convertExcelDate(String value) {
		if (value == null || value.trim().isEmpty()) {
			return "";
		}
		try {
			// 엑셀에서 날짜가 숫자로 읽힌 경우
			double excelDateNumber = Double.parseDouble(value);
			// 엑셀 날짜 형식을 LocalDate로 변환 (엑셀에서 1900-01-01부터 일 수를 계산)
			LocalDate date = LocalDate.of(1900, 1, 1).plusDays((long) excelDateNumber - 2);
			// 하이픈을 제외한 "yyyyMMdd" 형식으로 반환
			return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		} catch (NumberFormatException e) {
			// 이미 문자열로 되어 있을 경우 하이픈을 제거한 상태로 반환
			return value.replace("-", "");
		}
	}
	
	
	@GetMapping("/read")
	public ResponseEntity<List<TB_RP320>> getAllData() {
		List<TB_RP320> data = TB_RP320Repository.findAll(Sort.by(Sort.Direction.ASC, "standdt", "powerid"));
		if (data.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		
		// 날짜에 하이픈을 추가하여 클라이언트로 반환
		data.forEach(entity -> entity.setStanddt(formatDateWithHyphens(entity.getStanddt())));
		
		return ResponseEntity.ok(data);
	}
	
	// yyyyMMdd 형식을 yyyy-MM-dd 형식으로 변환
	private String formatDateWithHyphens(String date) {
		if (date != null && date.length() == 8) {
			return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6);
		}
		return date; // 날짜 형식이 잘못된 경우 그대로 반환
	}
	
	// 발전기코드 옵션 동적 생성
	@GetMapping("/powerids")
	public ResponseEntity<List<String>> getPowerIds() {
		List<String> powerIds = TB_RP320Repository.findDistinctPowerIds();
		return ResponseEntity.ok(powerIds);
	}
	
	// 등록 일자 목록
	@GetMapping("/regYMList")
	public ResponseEntity<List<String>> getRegYMList() {
		List<String> regYMList = TB_RP320Repository.findDistinctYearMonths();
		return ResponseEntity.ok(regYMList);
	}
	
	// tab3 ============================================================
	
	// 시간별 조회
	@GetMapping("/latest")
	public ResponseEntity<List<TB_RP320>> getLatestData() {
		String latestDate = TB_RP320Repository.findLatestDate();
		if (latestDate == null) {
			return ResponseEntity.noContent().build();
		}
		List<TB_RP320> data = TB_RP320Repository.findByStanddt(latestDate);
		data.forEach(entity -> entity.setStanddt(formatDateWithHyphens(entity.getStanddt())));
		return ResponseEntity.ok(data);
	}
	
	// tab4 ============================================================
//	@GetMapping("/periodicData")
//	public ResponseEntity<Map<String, List<Map<String, Object>>>> getPeriodicData(
//			@RequestParam("startDate") String startDate,
//			@RequestParam("endDate") String endDate) {
//
//		System.out.println("Received request with startDate: " + startDate + ", endDate: " + endDate);
//
//		if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
//			// 날짜가 제공되지 않은 경우, 데이터베이스의 최소/최대 날짜를 사용
//			startDate = TB_RP320Repository.findMinDate();
//			endDate = TB_RP320Repository.findMaxDate();
//
//			System.out.println("Using default date range: " + startDate + " to " + endDate);
//		}
//
//		Map<String, List<Map<String, Object>>> result = new HashMap<>();
//		result.put("monthly", getMonthlyData(startDate, endDate));
//		result.put("quarterly", getQuarterlyData(startDate, endDate));
//		result.put("halfYearly", getHalfYearlyData(startDate, endDate));
//		result.put("yearly", getYearlyData(startDate, endDate));
//
////		System.out.println("Returning data with " + monthlyData.size() + " entries");
//
//		return ResponseEntity.ok(result);
//	}
	@GetMapping("/periodicData")
	public ResponseEntity<Map<String, List<Map<String, Object>>>> getPeriodicData(
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) {
		
		System.out.println("Received request with startDate: " + startDate + ", endDate: " + endDate);
		
		if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
			// 날짜가 제공되지 않은 경우, 데이터베이스의 최소/최대 날짜를 사용
			startDate = TB_RP320Repository.findMinDate();
			endDate = TB_RP320Repository.findMaxDate();
			
			System.out.println("Using default date range: " + startDate + " to " + endDate);
		}
		
		// 날짜 형식 변환
		String formattedStartDate = startDate.replace("-", "") + "01";
		String formattedEndDate = endDate.replace("-", "") + "31";
		
		System.out.println("Formatted date range: " + formattedStartDate + " to " + formattedEndDate);
		
		Map<String, List<Map<String, Object>>> result = new HashMap<>();
		result.put("monthly", getMonthlyData(formattedStartDate, formattedEndDate));
		result.put("quarterly", getQuarterlyData(formattedStartDate, formattedEndDate));
		result.put("halfYearly", getHalfYearlyData(formattedStartDate, formattedEndDate));
		result.put("yearly", getYearlyData(formattedStartDate, formattedEndDate));
		
		// 결과 로깅
//		System.out.println("Monthly data: " + result.get("monthly"));
//		System.out.println("Quarterly data: " + result.get("quarterly"));
//		System.out.println("Half-yearly data: " + result.get("halfYearly"));
//		System.out.println("Yearly data: " + result.get("yearly"));
		
		return ResponseEntity.ok(result);
	}
	
	// 월별
	private List<Map<String, Object>> getMonthlyData(String startDate, String endDate) {
		List<Object[]> rawData = TB_RP320Repository.getMonthlyData(startDate, endDate);
		Map<String, Map<String, Object>> result = new LinkedHashMap<>();
		Set<String> powerNames = new LinkedHashSet<>();
		
		for (Object[] row : rawData) {
			String month = (String) row[0];
			String powernm = (String) row[1];
			Double value = ((Number) row[2]).doubleValue();
			
			powerNames.add(powernm);
			
			result.putIfAbsent(month, new LinkedHashMap<>());
			result.get(month).put("Period", month);  // 여기서 'Month' 대신 'Period' 사용
			result.get(month).put(powernm, value);
		}
		
		// Ensure all months have entries for all power names
		for (Map<String, Object> monthData : result.values()) {
			for (String powernm : powerNames) {
				monthData.putIfAbsent(powernm, 0.0);
			}
		}
		
		return new ArrayList<>(result.values());
	}
	
	// 분기별
	private List<Map<String, Object>> getQuarterlyData(String startDate, String endDate) {
		List<Object[]> rawData = TB_RP320Repository.getQuarterlyData(startDate, endDate);
		Map<String, Map<String, Object>> result = new LinkedHashMap<>();
		Set<String> powerNames = new LinkedHashSet<>();
		
		for (Object[] row : rawData) {
			int year = ((Number) row[0]).intValue();
			int quarter = ((Number) row[1]).intValue();
			String powernm = (String) row[2];
			Double value = ((Number) row[3]).doubleValue();
			
			String key = year + "-Q" + quarter;
			powerNames.add(powernm);
			
			result.putIfAbsent(key, new LinkedHashMap<>());
			result.get(key).put("Period", key);
			result.get(key).put(powernm, value);
		}
		
		// Ensure all periods have entries for all power names
		for (Map<String, Object> periodData : result.values()) {
			for (String powernm : powerNames) {
				periodData.putIfAbsent(powernm, 0.0);
			}
		}
		
		return new ArrayList<>(result.values());
	}
	
	// 반기별
	private List<Map<String, Object>> getHalfYearlyData(String startDate, String endDate) {
		List<Object[]> rawData = TB_RP320Repository.getHalfYearlyData(startDate, endDate);
		Map<String, Map<String, Object>> result = new LinkedHashMap<>();
		Set<String> powerNames = new LinkedHashSet<>();
		
		for (Object[] row : rawData) {
			int year = ((Number) row[0]).intValue();
			int half = ((Number) row[1]).intValue();
			String powernm = (String) row[2];
			Double value = ((Number) row[3]).doubleValue();
			
			String key = year + "-H" + half; // 예: 2023-H1, 2023-H2
			powerNames.add(powernm);
			
			result.putIfAbsent(key, new LinkedHashMap<>());
			result.get(key).put("Period", key);
			result.get(key).put(powernm, value);
		}
		
		// 모든 기간에 대해 모든 powernm이 존재하도록 보장
		for (Map<String, Object> periodData : result.values()) {
			for (String powernm : powerNames) {
				periodData.putIfAbsent(powernm, 0.0);
			}
		}
		
		return new ArrayList<>(result.values());
	}
	
	
	// 연도별
	private List<Map<String, Object>> getYearlyData(String startDate, String endDate) {
		List<Object[]> rawData = TB_RP320Repository.getYearlyData(startDate, endDate);
		Map<String, Map<String, Object>> result = new LinkedHashMap<>();
		Set<String> powerNames = new LinkedHashSet<>();
		
		for (Object[] row : rawData) {
			int year = ((Number) row[0]).intValue();
			String powernm = (String) row[1];
			Double value = ((Number) row[2]).doubleValue();
			
			String key = String.valueOf(year);
			powerNames.add(powernm);
			
			result.putIfAbsent(key, new LinkedHashMap<>());
			result.get(key).put("Period", key); // 'Period' 키를 사용
			// result.get(key).put("Year", key); // 만약 이 라인이 있다면 제거하세요
			result.get(key).put(powernm, value);
		}
		
		// 모든 발전기명에 대해 값이 없을 경우 0.0으로 초기화
		for (Map<String, Object> periodData : result.values()) {
			for (String powernm : powerNames) {
				periodData.putIfAbsent(powernm, 0.0);
			}
		}
		
		return new ArrayList<>(result.values());
	}
	
	
	//  최소/최대 연월 반환
	@GetMapping("/dateRange")
	public ResponseEntity<Map<String, String>> getDateRange() {
		String minDate = TB_RP320Repository.findMinDate();
		String maxDate = TB_RP320Repository.findMaxDate();
		
		Map<String, String> dateRange = new HashMap<>();
		dateRange.put("minDate", minDate);
		dateRange.put("maxDate", maxDate);
		
		return ResponseEntity.ok(dateRange);
	}
	
	// tab5 ============================================================
	@GetMapping("/tab5Data")
	public ResponseEntity<Map<String, List<Map<String, Object>>>> getTab5Data() {
		try {
			Map<String, List<Map<String, Object>>> result = new HashMap<>();
			result.put("yoy", getYoYData());
			result.put("qoq", getQoQData());
			result.put("mom", getMoMData());
			result.put("ytd", getYTDData());
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace(); // 서버 로그에 스택 트레이스 출력
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	private List<Map<String, Object>> getYoYData() {
		List<Object[]> rawData = TB_RP320Repository.getMonthlyDataForYoYAndMoM();
		return transformMonthlyData(rawData);
	}
	
	private List<Map<String, Object>> getQoQData() {
		List<Object[]> rawData = TB_RP320Repository.getQuarterlyDataForQoQ();
		return transformQuarterlyData(rawData);
	}
	
	private List<Map<String, Object>> getMoMData() {
		List<Object[]> rawData = TB_RP320Repository.getMonthlyDataForYoYAndMoM();
		return transformMonthlyData(rawData);
	}
	
	private List<Map<String, Object>> getYTDData() {
		List<Object[]> rawData = TB_RP320Repository.getMonthlyDataForYTD();
		return transformMonthlyData(rawData);
	}
	
	private List<Map<String, Object>> transformMonthlyData(List<Object[]> rawData) {
		Map<String, Map<String, Object>> result = new LinkedHashMap<>();
		Set<String> powerNames = new LinkedHashSet<>();
		
		for (Object[] row : rawData) {
			String month = (String) row[0];
			String powernm = (String) row[1];
			Double value = ((Number) row[2]).doubleValue();
			
			powerNames.add(powernm);
			
			result.putIfAbsent(month, new LinkedHashMap<>());
			result.get(month).put("Period", month);
			result.get(month).put(powernm, value);
		}
		
		for (Map<String, Object> monthData : result.values()) {
			for (String powernm : powerNames) {
				monthData.putIfAbsent(powernm, 0.0);
			}
		}
		
		return new ArrayList<>(result.values());
	}
	
	private List<Map<String, Object>> transformQuarterlyData(List<Object[]> rawData) {
		Map<String, Map<String, Object>> result = new LinkedHashMap<>();
		Set<String> powerNames = new LinkedHashSet<>();
		
		for (Object[] row : rawData) {
			int year = ((Number) row[0]).intValue();
			int quarter = ((Number) row[1]).intValue();
			String powernm = (String) row[2];
			Double value = ((Number) row[3]).doubleValue();
			
			String period = year + "-Q" + quarter;
			powerNames.add(powernm);
			
			result.putIfAbsent(period, new LinkedHashMap<>());
			result.get(period).put("Period", period);
			result.get(period).put(powernm, value);
		}
		
		for (Map<String, Object> periodData : result.values()) {
			for (String powernm : powerNames) {
				periodData.putIfAbsent(powernm, 0.0);
			}
		}
		
		return new ArrayList<>(result.values());
	}


//	private List<Map<String, Object>> getQuarterlyData(String startDate, String endDate, String powerid) {
//		// 분기별 데이터 쿼리 및 처리 로직
//	}
//
//	private List<Map<String, Object>> getHalfYearlyData(String startDate, String endDate, String powerid) {
//		// 반기별 데이터 쿼리 및 처리 로직
//	}
//
//	private List<Map<String, Object>> getYearlyData(String startDate, String endDate, String powerid) {
//		// 연도별 데이터 쿼리 및 처리 로직
//	}
	
	// 검색 (tab1, tab2)
	@GetMapping("/search")
	public ResponseEntity<List<TB_RP320>> searchData(@RequestParam String startdt,
													 @RequestParam String enddt,
													 @RequestParam(required = false) String powerid) {
		List<TB_RP320> data = TB_RP320Repository.searchGeneData(startdt, enddt, powerid);
		
		// 검색된 데이터에 대해서도 날짜 형식을 변환
		data.forEach(entity -> entity.setStanddt(formatDateWithHyphens(entity.getStanddt())));
		
		return ResponseEntity.ok(data);
	}
	
	// 검색 (tab3)
	@GetMapping("/searchByDate")
	public ResponseEntity<List<TB_RP320>> searchGeneDataByDate(
			@RequestParam String date,
			@RequestParam(required = false) String powerid) {
		
		// 검색 쿼리 실행
		List<TB_RP320> results = TB_RP320Repository.searchGeneDataByDate(date, powerid);
		
		// 검색된 데이터의 날짜 형식을 yyyy-MM-dd 형식으로 변환
		results.forEach(entity -> entity.setStanddt(formatDateWithHyphens(entity.getStanddt())));
		
		return ResponseEntity.ok(results);
	}
	
	
	// =====
	
	
	
	// 수정
	@PatchMapping("/update")
	public ResponseEntity<AjaxResult> updateData(@RequestBody List<TB_RP320> updates, Authentication auth) {
		AjaxResult result = new AjaxResult();
		try {
			User user = (User) auth.getPrincipal();
			geneUploadService.updateGeneData(updates, user);
			
			result.success = true;
			result.message = "데이터가 성공적으로 업데이트되었습니다!";
		} catch (Exception e) {
			result.success = false;
			result.message = "데이터 업데이트 실패: " + e.getMessage();
		}
		return ResponseEntity.ok(result);
	}
	
	// 삭제
	/*@DeleteMapping("/delete")
	public ResponseEntity<AjaxResult> deleteGeneDataByDate(@RequestParam String INDATEM) {
		AjaxResult result = new AjaxResult();
		try {
			// String으로 전달된 날짜 값을 LocalDate로 변환
			LocalDate INDATEMDate = LocalDate.parse(INDATEM);
			
			// 해당 날짜의 시작과 끝 시간 계산
			LocalDateTime startOfDay = INDATEMDate.atStartOfDay();
			LocalDateTime endOfDay = INDATEMDate.atTime(23, 59, 59);
			
			// INDATEM을 사용하여 데이터 검색
			List<TB_RP320> dataToDelete = TB_RP320Repository.findByIndatemBetween(startOfDay, endOfDay);
			
			if (!dataToDelete.isEmpty()) {
				TB_RP320Repository.deleteAll(dataToDelete);
				result.success = true;
				result.message = "등록일자: " + INDATEM + " 데이터가 삭제되었습니다.";
			} else {
				result.success = false;
				result.message = "선택한 날짜에 대한 데이터가 없습니다.";
			}
		} catch (Exception e) {
			result.success = false;
			result.message = "데이터 삭제 실패: " + e.getMessage();
		}
		return ResponseEntity.ok(result);
	}*/
	

	
	// ============================================================================
	// 발전기명 옵션 동적 생성
	/*@GetMapping("/generatornames")
	public ResponseEntity<List<String>> getGeneratorNames() {
		List<String> generatorNames = TB_RP320Repository.findDistinctGeneratorNames();
		return ResponseEntity.ok(generatorNames);
	}*/
	
	// 시간별 조회 데이터 필터링 메소드 추가
	/*private List<Map<String, Object>> getHourlyData(String date, String powerid, int startHour, int endHour) {
		List<Map<String, Object>> rawData = TB_RP320Repository.searchHourlyData(date, powerid);
		
		// 필요한 시간대만 남기도록 데이터 필터링
		List<Map<String, Object>> filteredData = rawData.stream().map(row -> {
			Map<String, Object> filteredRow = new HashMap<>();
			filteredRow.put("period", row.get("period"));
			filteredRow.put("powernm", row.get("powernm"));
			
			for (int i = startHour; i <= endHour; i++) {
				String hourKey = String.format("hour%02d", i);
				filteredRow.put(hourKey, row.get(hourKey));
			}
			
			return filteredRow;
		}).collect(Collectors.toList());
		
		return filteredData;
	}
	
	
	// 기간별 조회 검색
	*//*@GetMapping("/periodSearch")
	public ResponseEntity<List<?>> searchByPeriod(
			@RequestParam String periodType,
			@RequestParam String startdt,
			@RequestParam(required = false) String enddt,
			@RequestParam(required = false) String powerid,
			@RequestParam(required = false) String startHour,
			@RequestParam(required = false) String endHour) {
		
		List<?> results;
		
		switch (periodType.toLowerCase()) {
			case "hourly":
				// 시간별 검색 로직
				int start = startHour != null ? Integer.parseInt(startHour) : 1;
				int end = endHour != null ? Integer.parseInt(endHour) : 24;
				results = getHourlyData(startdt, powerid, start, end);
				break;
			case "monthly", "quarterly", "halfyearly":
				results = TB_RP320Repository.searchMonthlyData(startdt, enddt, powerid);
				break;
			case "yearly":
				int year = Integer.parseInt(startdt.substring(0, 4)); // 연도만 추출하여 정수로 변환
				results = TB_RP320Repository.searchYearlyData(year, powerid);
				break;
			default:
				throw new IllegalArgumentException("Invalid period type: " + periodType);
		}
		
		return ResponseEntity.ok(results);
	}*//*
	
	@GetMapping("/dateRange")
	public ResponseEntity<Map<String, String>> getDateRange() {
		Map<String, String> dateRange = new HashMap<>();
		
		// 데이터베이스에서 최소 날짜와 최대 날짜를 가져옵니다.
		String startDate = TB_RP320Repository.findMinDate();
		String endDate = TB_RP320Repository.findMaxDate();
		
		dateRange.put("startdt", startDate);
		dateRange.put("enddt", endDate);
		
		return ResponseEntity.ok(dateRange);
	}*/
	
	// 연도 목록 동적 생성
	/*@GetMapping("/distinctYears")
	public ResponseEntity<List<String>> getDistinctYears() {
		List<String> years = TB_RP320Repository.findDistinctYears();
		return ResponseEntity.ok(years);
	}*/
	
}