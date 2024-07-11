package mes.app.sale.service;

import mes.domain.repository.TB_RP320Repository;
import mes.domain.services.CommonUtil;
import mes.domain.services.SqlRunner;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeneUploadService {

	@Autowired
	SqlRunner sqlRunner;

	@Autowired
	TB_RP320Repository TB_RP320Repository;

//	 엑셀 파일에 데이터 형식이나 값의 오류가 있으니까,
//	 각 셀을 읽을 때 유효성 검사를 추가하거나,
//	 특정 필드(예: 날짜, 숫자)의 형식이 올바른지 확인하는 로직을 추가하기
	
	public List<List<String>> excel_read(String filename) throws IOException{
		List<List<String>> all_rows = new ArrayList<>();
		
		FileInputStream file = new FileInputStream(filename);
		XSSFWorkbook wb = new XSSFWorkbook(file);
		XSSFSheet sheet = wb.getSheetAt(0);
		
		for (int i=1; i<=sheet.getLastRowNum(); i++) {
			XSSFRow row = sheet.getRow(i);
			List<String> value_list = new ArrayList<>();
			
			for (int j=0; j<row.getLastCellNum(); j++) {
				XSSFCell cell = row.getCell(j);
				
				
				if(cell != null) {
					if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						value_list.add(new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue()).strip());
					} else if(cell.getCellType() == CellType.NUMERIC){
						value_list.add(CommonUtil.tryString(cell.getNumericCellValue()).strip() );
					}else {
						value_list.add(cell.getStringCellValue().strip());
					}
				}
			}
			all_rows.add(value_list);
		}
		
		wb.close();
		
		return all_rows;
	}


}
