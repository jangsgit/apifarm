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
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
	public List<List<String>> excel_read(String filename) throws IOException{
		
		System.out.println("Reading Excel file: " + filename);
		
		List<List<String>> all_rows = new ArrayList<>();
		
		FileInputStream file = new FileInputStream(filename);
		XSSFWorkbook wb = new XSSFWorkbook(file);
		XSSFSheet sheet = wb.getSheetAt(0);
		
		System.out.println("getLastRowNum" + sheet.getLastRowNum());
		
		
		for (int i=1; i<=sheet.getLastRowNum(); i++) {
			XSSFRow row = sheet.getRow(i);
			List<String> value_list = new ArrayList<>();
			
			for (int j=0; j<row.getLastCellNum(); j++) {
				XSSFCell cell = row.getCell(j);
				String cellValue = "";
				
//				System.out.println("Cell[" + i + ", " + j + "]: Type=" + cell.getCellType());
				
				if (cell != null) {
					if (cell.getCellType() == CellType.NUMERIC) {
						if (DateUtil.isCellDateFormatted(cell)) {
							// 날짜 포맷으로 처리하고 yyyy-MM-dd 형식의 문자열로 변환
							cellValue = cell.getDateCellValue().toInstant()
									.atZone(ZoneId.systemDefault())
									.toLocalDate()
									.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						} else {
							// 숫자 포맷으로 처리
							cellValue = new BigDecimal(cell.getNumericCellValue()).toPlainString();
						}
					} else if (cell.getCellType() == CellType.STRING) {
						cellValue = cell.getStringCellValue().trim();
					}
				}
				value_list.add(cellValue);
			}
			all_rows.add(value_list);
			
			
		}
		
//		System.out.println("all_rows" + all_rows);
		
		
		wb.close();
		
		return all_rows;
	}
	
	
	// 업로드된 파일 저장 메소드
	public String saveUploadedFile(MultipartFile file) throws IOException {
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
	}
	
	
	// 데이터 삭제 메소드
	public void deleteGeneData(TB_RP320_Id id) {
		TB_RP320Repository.deleteById(id);
	}
	
	
	// 수정
	@Transactional
	public void updateGeneData(List<TB_RP320> updates, User currentUser) {
		for (TB_RP320 update : updates) {
			
			// 기존 데이터 가져오기
			TB_RP320 existingData = TB_RP320Repository.findById(new TB_RP320_Id(update.getStanddt(), update.getPowerid())).orElse(null);
			
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
				history.setCreartdt(LocalDateTime.now());
				history.setPowerid(existingData.getPowerid());
				history.setPowernm(existingData.getPowernm());
				history.setUptyn("Y"); // 업데이트 여부 'Y'로 설정
				
				TB_RP310Repository.save(history);
				
				// 기존 데이터의 등록자 정보를 수정자로 업데이트
				update.setInuserid(currentUser.getUsername());
				update.setInusernm(currentUser.getFirst_name() + " " + currentUser.getLast_name());
				update.setUpdatem(LocalDate.now());
				
				// TB_RP320 테이블 업데이트
				TB_RP320Repository.save(update);
			}
		}
	}
	
	
}