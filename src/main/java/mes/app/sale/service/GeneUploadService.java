package mes.app.sale.service;

import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_RP310;
import mes.domain.entity.actasEntity.TB_RP320;
import mes.domain.entity.actasEntity.TB_RP320_Id;
import mes.domain.repository.TB_RP310Repository;
import mes.domain.repository.TB_RP320Repository;
import mes.domain.services.SqlRunner;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeneUploadService {
	
	@Autowired
	SqlRunner sqlRunner;
	
	@Autowired
	TB_RP320Repository TB_RP320Repository;
	
	@Autowired
	TB_RP310Repository TB_RP310Repository;
	
	@Autowired
	Settings settings;

//	 엑셀 파일에 데이터 형식이나 값의 오류가 있으니까,
//	 각 셀을 읽을 때 유효성 검사를 추가하거나,
//	 특정 필드(예: 날짜, 숫자)의 형식이 올바른지 확인하는 로직을 추가하기
	
	// 엑셀 파일 데이터 읽기 메소드
	public List<List<String>> excel_read(MultipartFile file) throws IOException {
		List<List<String>> all_rows = new ArrayList<>();
		XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
		XSSFSheet sheet = wb.getSheetAt(0);
		
		for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 첫 번째 행은 헤더로 가정하고 1부터 시작
			XSSFRow row = sheet.getRow(i);
			List<String> value_list = new ArrayList<>();
			
			if (row == null) {
				continue; // 빈 행 건너뛰기
			}
			
			for (int j = 0; j < row.getLastCellNum(); j++) {
				XSSFCell cell = row.getCell(j);
				String cellValue = "";
				
				if (cell != null) {
					cell.setCellType(CellType.STRING); // 모든 셀을 문자열로 변환
					
					cellValue = cell.getStringCellValue().trim();
					
					// 숫자인 경우 소수점 제거
					if (cellValue.matches("\\d+\\.0")) {
						cellValue = cellValue.substring(0, cellValue.length() - 2);
					}
				}
				value_list.add(cellValue);
			}
			all_rows.add(value_list);
		}
		
		wb.close();
		return all_rows;
	}
	
	// 업로드된 파일 저장 메소드
	/*public String saveUploadedFile(MultipartFile file) throws IOException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();
		String formattedDate = dtf.format(now);
		String uploadPath = settings.getProperty("file_temp_upload_path");
		String filename = formattedDate + "_" + file.getOriginalFilename();
		String fullPath = uploadPath + filename;
		
		File outputFile = new File(fullPath);
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			fos.write(file.getBytes());
		}
		return fullPath;
	}*/
	
	// 데이터 삭제 메소드
	/*public void deleteGeneData(TB_RP320_Id id) {
		TB_RP320Repository.deleteById(id);
	}*/
	
	
	// 수정
	@Transactional
	public void updateGeneData(List<TB_RP320> updates, User currentUser) {
		for (TB_RP320 update : updates) {
			
			// 복합 키 생성
			TB_RP320_Id id = new TB_RP320_Id(
					update.getSpworkcd(),
					update.getSpcompcd(),
					update.getSpplancd(),
					update.getStanddt(),
					update.getPowerid(),
					update.getPowtime()
			);
			
			// 기존 데이터 가져오기
			TB_RP320 existingData = TB_RP320Repository.findById(id).orElse(null);
			
			if (existingData != null && !existingData.equals(update)) {
				// 수정 이력 기록 (TB_RP310)
				TB_RP310 history = new TB_RP310();
				history.setSpworkcd(existingData.getSpworkcd());
				history.setSpworknm(existingData.getSpworknm());
				history.setSpcompcd(existingData.getSpcompcd());
				history.setSpcompnm(existingData.getSpcompnm());
				history.setSpplancd(existingData.getSpplancd());
				history.setSpplannm(existingData.getSpplannm());
				history.setStanddt(existingData.getStanddt());
				history.setUserid(currentUser.getUsername());
				history.setUsernm(currentUser.getFirst_name() + " " + currentUser.getLast_name());
				history.setCreartdt(LocalDateTime.now()); // 생성일시
				history.setPowerid(existingData.getPowerid());
				history.setPowernm(existingData.getPowernm());
				history.setUptyn("Y"); // 업데이트 여부 'Y'로 설정
				
				TB_RP310Repository.save(history);
				
				// 기존 데이터의 수정자 정보 업데이트
				existingData.setInuserid(currentUser.getUsername());
				existingData.setInusernm(currentUser.getFirst_name() + " " + currentUser.getLast_name());
				existingData.setUpdatem(LocalDate.now());
				
				// 필요한 필드 업데이트
				existingData.setPowernm(update.getPowernm());
				existingData.setPowtime(update.getPowtime());
				existingData.setSmpamt(update.getSmpamt());
				existingData.setEmamt(update.getEmamt());
				existingData.setMevalue(update.getMevalue());
				existingData.setFeeamt(update.getFeeamt());
				existingData.setAreaamt(update.getAreaamt());
				existingData.setOutamt(update.getOutamt());
				existingData.setRpsamt(update.getRpsamt());
				existingData.setDifamt(update.getDifamt());
				existingData.setSumamt(update.getSumamt());
				existingData.setINDATEM(update.getINDATEM());
				
				// TB_RP320 테이블 업데이트
				TB_RP320Repository.save(existingData);
			}
		}
	}
}