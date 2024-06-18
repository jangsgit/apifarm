package mes.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.databind.ObjectMapper;

import mes.domain.model.GUIData;

public class GUIConfiguration {	

	private static final GUIConfiguration configuration = new GUIConfiguration();
	
	private final Map<String, GUIData> guiDataMap = new HashMap<String, GUIData>();
	
	private GUIConfiguration(){
	    this.loadJsonConfig();
	}
	
	public static GUIData getGUIInfo(String gui) {

		GUIData value = configuration.guiDataMap.get(gui);
		System.out.println(value);
		return configuration.guiDataMap.get(gui);
	}
	
	public static Map<String, GUIData> getGUIDataMap(){
		return configuration.guiDataMap;
	}
	
	@SuppressWarnings("unchecked")
	private void loadJsonConfig() {
		
		ObjectMapper objectMapper = new ObjectMapper();  //Jackson 라이브러리, JSON 데이터를 Java 객체로 변환하거나 그 반대로 변환
		
		ClassPathResource resource = new ClassPathResource("gui.json"); //클래스패스에서 리소스를 읽기 위해 사용됩니다. 여기서는 gui.json 파일을 읽기 위해 사용
		InputStreamReader inputStreamReader;
		try {
			inputStreamReader = new InputStreamReader(resource.getInputStream(), "UTF-8");  //바이트 스트림을 문자 스트림으로 변환하는 데 사용되며, UTF-8 인코딩을 지정
			
			Stream<String> streamOfString= new BufferedReader(inputStreamReader).lines();     //inputStreamReader를 BufferedReader로 감싸고, lines() 메서드를 사용하여
			String json = streamOfString.collect(Collectors.joining());						// 스트림을 줄 단위로 읽습니다. 이 결과는 Stream<String> 객체입니다.
	        
	        
			Map<String, Object> map = objectMapper.readValue(json, Map.class);		// JSON 문자열을 읽어 Java Map<String, Object>로 변환합니다.
	        
			for( Map.Entry<String, Object> elem : map.entrySet() ){     //맵의 각 엔트리를 반복합니다. 각 엔트리는 키와 값의 쌍으로 구성되어 있습니다.
				String guiCode = (String)elem.getKey();                 // 엔트리의 키를 guiCode 변수에 저장합니다. 이 키는 String 타입입니다.
				Map<String, Object> dicInfo = (Map<String, Object>)elem.getValue();
				String menuName = (String)dicInfo.get("name");
				
				Map<String, String> template = (Map<String, String>)dicInfo.get("templates");
				
				GUIData guiData = new GUIData(menuName, template);    //menuName과 template을 사용하여 GUIData 객체를 생성합니다.
				
				this.guiDataMap.put(guiCode, guiData);    //guiCode를 키로, guiData 객체를 값으로 하여 guiDataMap에 추가합니다.
				
	        }	        
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
