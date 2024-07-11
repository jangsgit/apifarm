package mes.app.sale;

import mes.app.sale.service.GeneUploadService;
import mes.config.Settings;
import mes.domain.model.AjaxResult;
import mes.domain.services.SqlRunner;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/gene/upload")
public class GeneUploadController {
	
//	@Autowired
//	TB_RP320Repository TB_RP320Repository;
	
	@Autowired
	private GeneUploadService geneUploadService;
	
	@Autowired
	Settings settings;
	
	@Autowired
	SqlRunner sqlRunner;
	
	
	/*
	 * 엑셀 컬럼 순서
	 * (엑셀 파일에서 데이터를 읽을 때 사용되는 각 열(컬럼)의 인덱스를 지정)
	 *
	 * #standdt #기준일자
	 * #powerid #발전기ID
	 * #powernm #발전기명
	 * #chargedv #충전/방전
	 * #mevalue01 #계량값01시
	 * #mevalue02 #계량값02시
	 * #mevalue03 #계량값03시
	 * #mevalue04 #계량값04시
	 * #mevalue05 #계량값05시
	 * #mevalue06 #계량값06시
	 * #mevalue07 #계량값07시
	 * #mevalue08 #계량값08시
	 * #mevalue09 #계량값09시
	 * #mevalue10 #계량값10시
	 * #mevalue11 #계량값11시
	 * #mevalue12 #계량값12시
	 * #mevalue13 #계량값13시
	 * #mevalue14 #계량값14시
	 * #mevalue15 #계량값15시
	 * #mevalue16 #계량값16시
	 * #mevalue17 #계량값17시
	 * #mevalue18 #계량값18시
	 * #mevalue19 #계량값19시
	 * #mevalue20 #계량값20시
	 * #mevalue21 #계량값21시
	 * #mevalue22 #계량값22시
	 * #mevalue23 #계량값23시
	 * #mevalue24 #계량값24시
	 * #mevaluet #계량값합계
	 *
	 * */
	
	
	// 발전량 엑셀 업로드
	@PostMapping("/upload_save")
	public AjaxResult saveGeneData(@RequestParam(value = "upload_file") MultipartFile upload_file,
								   MultipartHttpServletRequest multipartRequest,
								   Authentication auth) throws FileNotFoundException, IOException {
		
		//User user = (User)auth.getPrincipal();
		
		int standdt_col = 0; // 일자 standdt
		int powerid_col = 1; // 발전기아이디(발전기코드) powerid
		int powernm_col = 2; // 발전기명 powernm
		int chargedv_col = 3; // 충전or방전 chargedv
		List<Integer> mevalueList = Arrays.asList(
				4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27
		); // mevalue01시부터 24시까지의 데이터 열 인덱스
		int mevaluet_col = 28; // 합계 mevaluet
		
		
		// 파일 저장 및 읽기
		// 업로드된 파일을 서버의 지정된 위치에 저장
		// 파일명은 현재 날짜와 시간을 포함하여 중복을 방지하고, 파일이 이미 존재하면 삭제 후 새로 저장
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();
		String formattedDate = dtf.format(now);
		String upload_filename = settings.getProperty("file_temp_upload_path") + formattedDate + "_" + upload_file.getOriginalFilename();
		
		if(new File(upload_filename).exists()){
			new File(upload_filename).delete();
		}
		try (FileOutputStream destication = new FileOutputStream(upload_filename)) {
			destication.write(upload_file.getBytes());
		}
		
		// 데이터 읽기 및 파싱
		// 업로드 된 엑셀 파일을 읽어서 각 행의 데이터를  List<List<String>> 형태로 변환
		List<List<String>> gene_file = this.geneUploadService.excel_read(upload_filename);
		Map<String, Object> error_items = new HashMap<String, Object>();
		
		// timelist 변수는 엑셀 파일에서 읽은 각 행의 시간별 계량값들을 저장하는 리스트
		// 엑셀 파일의 각 행에서 시간별 계량값을 추출하여 리스트로 만드는 과정임
		for(int i=0; i < gene_file.size(); i++){
			
			List<String> timelist = new ArrayList<>();
			List<String> row = gene_file.get(i);
			
			/* 엑셀 파일에서 읽은 데이터 중에서 숫자 데이터를 정수로 변환.
			 value2는 엑셀에서 읽은 데이터의 특정 열의 값을 의미
			 이를 double 타입으로 파싱한 후에 정수로 변환
			 그리고 이 정수 값을 다시 문자열로 변환하여 stringvalue2에 저장 */
			// 근데 난 필요 없음
//			double value2 = Double.parseDouble(row.get(number_col));
//			int integerValue2 = (int) value2;
//			String stringvalue2 = String.valueOf(integerValue2);
//			String number = String.valueOf(stringvalue2);
			
			String standdt = row.get(standdt_col);
			String powerid = row.get(powerid_col);
			String powernm = row.get(powernm_col);
			String chargedv = row.get(chargedv_col);
			for(int j=0; j < mevalueList.size(); j++){
				double value = Double.parseDouble(row.get(mevalueList.get(j)));
				int integerValue = (int) value;
				String stringvalue = String.valueOf(integerValue);
				timelist.add(stringvalue);
			}
			String mevaluet = row.get(mevaluet_col);
			
			MapSqlParameterSource paramMap = new MapSqlParameterSource();
//			paramMap.addValue("number", number);
			paramMap.addValue("standdt", standdt);
			paramMap.addValue("powerid", powerid);
			paramMap.addValue("powernm", powernm);
			paramMap.addValue("chargedv", chargedv);
			paramMap.addValue("mevalue01", timelist.get(0));
			paramMap.addValue("mevalue02", timelist.get(1));
			paramMap.addValue("mevalue03", timelist.get(2));
			paramMap.addValue("mevalue04", timelist.get(3));
			paramMap.addValue("mevalue05", timelist.get(4));
			paramMap.addValue("mevalue06", timelist.get(5));
			paramMap.addValue("mevalue07", timelist.get(6));
			paramMap.addValue("mevalue08", timelist.get(7));
			paramMap.addValue("mevalue09", timelist.get(8));
			paramMap.addValue("mevalue10", timelist.get(9));
			paramMap.addValue("mevalue11", timelist.get(10));
			paramMap.addValue("mevalue12", timelist.get(11));
			paramMap.addValue("mevalue13", timelist.get(12));
			paramMap.addValue("mevalue14", timelist.get(13));
			paramMap.addValue("mevalue15", timelist.get(14));
			paramMap.addValue("mevalue16", timelist.get(15));
			paramMap.addValue("mevalue17", timelist.get(16));
			paramMap.addValue("mevalue18", timelist.get(17));
			paramMap.addValue("mevalue19", timelist.get(18));
			paramMap.addValue("mevalue20", timelist.get(19));
			paramMap.addValue("mevalue21", timelist.get(20));
			paramMap.addValue("mevalue22", timelist.get(21));
			paramMap.addValue("mevalue23", timelist.get(22));
			paramMap.addValue("mevalue24", timelist.get(23));
			paramMap.addValue("mevaluet", mevaluet);
			
			String sql =
					"INSERT INTO public.tb_rp320(" +
							"\"standdt\", \"powerid\", \"powernm\", \"chargedv\", \"mevalue01\", \"mevalue02\", \"mevalue03\", \"mevalue04\", \"mevalue05\", \"mevalue06\", \"mevalue07\", " +
							"\"mevalue08\", \"mevalue09\", \"mevalue10\", \"mevalue11\", \"mevalue12\", \"mevalue13\", \"mevalue14\", \"mevalue15\", \"mevalue16\", " +
							"\"mevalue17\", \"mevalue18\", \"mevalue19\", \"mevalue20\", \"mevalue21\", \"mevalue22\", \"mevalue23\", \"mevalue24\", \"mevaluet\" "+
							") VALUES (" +
							":standdt, :powerid, :powernm, :chargedv, :mevalue01, :mevalue02, :mevalue03, :mevalue04, :mevalue05, :mevalue06, :mevalue07, :mevalue08, :mevalue09, :mevalue10, :mevalue11, " +
							":mevalue12, :mevalue13, :mevalue14, :mevalue15, :mevalue16, :mevalue17, :mevalue18 , :mevalue19, :mevalue20, :mevalue21, :mevalue22, :mevalue23 , :mevalue24, :mevaluet" +
							")";
			
			String log_data =
					"index : " + i +
//							", number : " + number +
							", standdt : " + standdt +
							", powerid : " + powerid +
							", powernm : " + powernm +
							", chargedv : " + chargedv +
							", mevalue01 : " + timelist.get(0)  +
							", mevalue02 : " + timelist.get(1)  +
							", mevalue03 : " + timelist.get(2)  +
							", mevalue04 : " + timelist.get(3)  +
							", mevalue05 : " + timelist.get(4)  +
							", mevalue06 : " + timelist.get(5)  +
							", mevalue07 : " + timelist.get(6)  +
							", mevalue08 : " + timelist.get(7)  +
							", mevalue09 : " + timelist.get(8)  +
							", mevalue10 : " + timelist.get(9)  +
							", mevalue11 : " + timelist.get(10) +
							", mevalue12 : " + timelist.get(11) +
							", mevalue13 : " + timelist.get(12) +
							", mevalue14 : " + timelist.get(13) +
							", mevalue15 : " + timelist.get(14) +
							", mevalue16 : " + timelist.get(15) +
							", mevalue17 : " + timelist.get(16) +
							", mevalue18 : " + timelist.get(17) +
							", mevalue19 : " + timelist.get(18) +
							", mevalue20 : " + timelist.get(19) +
							", mevalue21 : " + timelist.get(20) +
							", mevalue22 : " + timelist.get(21) +
							", mevalue23 : " + timelist.get(22) +
							", mevalue24 : " + timelist.get(23) +
							", mevaluet : " + mevaluet;
			try {
				if(powerid != null && !powerid.isEmpty() && powernm != null && !powernm.isEmpty()){
					this.sqlRunner.execute(sql, paramMap);
					
				}
			} catch (Exception e){
				error_items.put("log",log_data);
				error_items.put("ex",e.getMessage());
			}
		}
		
		AjaxResult result = new AjaxResult();
		result.success = true;
		
		if (error_items.size() > 0)
			result.success = false;
		
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("error_items", error_items);
		
		result.data = item;
		return result;
	}
	
	
}
