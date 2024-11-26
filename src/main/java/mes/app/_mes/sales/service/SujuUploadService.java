package mes.app.sales.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import mes.domain.repository.SujuRepository;
import mes.domain.services.CommonUtil;
import mes.domain.services.SqlRunner;

@Service
public class SujuUploadService {

	@Autowired
	SqlRunner sqlRunner;
	
	@Autowired
	SujuRepository SujuRepository;
	
	
	// 수주 업로드 내역 조회 
	public List<Map<String, Object>> getSujuUploadList() {
		
		MapSqlParameterSource dicParam = new MapSqlParameterSource();
		// data_kind : 'sales', 'delivery'
		/*dicParam.addValue("date_kind", date_kind);
		dicParam.addValue("start", start);
		dicParam.addValue("end", end);*/

		String sql = """
			select 
           	*
            from suju_bulk2 sb
            where 1 = 1
			""";

		/*if (date_kind.equals("sales")) {
			sql += """
				and sb."JumunDate" between :start and :end
                order by sb."JumunDate" desc
				""";
		} else {
			sql +="""
				and sb."DueDate" between :start and :end
                order by sb."DueDate" desc
				""";
		}*/
		
		List<Map<String, Object>> itmes = this.sqlRunner.getRows(sql, dicParam);
		
		return itmes;
	}

	public List<Map<String, Object>> getSujuUploadList_sale_list(String start, String end) {

		MapSqlParameterSource dicParam = new MapSqlParameterSource();
		// data_kind : 'sales', 'delivery'
		/*dicParam.addValue("date_kind", date_kind);
		dicParam.addValue("start", start);
		dicParam.addValue("end", end);*/

		String sql = """
			select 
           	*
            from suju_bulk2 sb
            where 1 = 1
            and sb."date" between :start and :end
            order by sb."date" desc
			""";
		dicParam.addValue("start", start);
		dicParam.addValue("end", end);
		/*if (date_kind.equals("sales")) {
			sql += """
				and sb."JumunDate" between :start and :end
                order by sb."JumunDate" desc
				""";
		} else {
			sql +="""
				and sb."DueDate" between :start and :end
                order by sb."DueDate" desc
				""";
		}*/

		List<Map<String, Object>> itmes = this.sqlRunner.getRows(sql, dicParam);

		return itmes;
	}


	public List<List<String>> excel_read(String filename) throws IOException{
		List<List<String>> all_rows = new ArrayList<>();

		FileInputStream file = new FileInputStream(filename);
		XSSFWorkbook wb = new XSSFWorkbook(file);
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFCell jumunNumCell = null;

		for (int i=1; i<=sheet.getLastRowNum(); i++) {
			XSSFRow row = sheet.getRow(i);
			List<String> value_list = new ArrayList<>();

			for (int j=0; j<row.getLastCellNum(); j++) {
				XSSFCell cell = row.getCell(j);
				jumunNumCell = row.getCell(0);

				if (jumunNumCell == null || jumunNumCell.getCellType() == CellType.BLANK)
					break;

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
			if (jumunNumCell == null || jumunNumCell.getCellType() == CellType.BLANK)
				break;
			all_rows.add(value_list);
		}

		wb.close();

		return all_rows;
	}


	public Map<String, Object> getUserGroup(String number){
		String sql = """
				select * from suju_bulk2 where number = :number
				""";
		MapSqlParameterSource dicParam = new MapSqlParameterSource();
		dicParam.addValue("number", number);

		Map<String, Object> item = this.sqlRunner.getRow(sql, dicParam);
		return item;

	}



	
}
