package mes.app.system.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.util.StringUtils;
import mes.domain.GUIConfiguration;
import mes.domain.model.GUIData;
import mes.domain.services.SqlRunner;

@Service
public class MenuSetupService {

	@Autowired
	SqlRunner sqlRunner;

	// 메뉴폴더 리스트 조회
	public List<Map<String, Object>> getFolderTreeList() {
		
		String sql = """
				select id,
				                   "Parent_id",
				                   "FrontFolder_id",
				                   "FolderName",
				                   "IconCSS",
				                   "_order"
				            from menu_folder
				            order by "_order"
				""";
		
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, null);
        
        return items;
	}
		
	// 메뉴항목 리스트 조회
	public List<Map<String, Object>> getMenuList(Integer folder_id) {

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("folder_id", folder_id);
		
		String sql = """
				select mi."MenuCode"
		        , mi."MenuName"
		        , mi."Url" 
		        , mi."MenuFolder_id"
		        , mi._order
		        from menu_item mi 
		        inner join menu_folder mf on mf.id = mi."MenuFolder_id"
		        where mi."MenuFolder_id" = :folder_id 
		        order by mi."_order"
				""";
		
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, paramMap);
        
        return items;
	}

	// 소스메뉴 리스트 조회
	public List<Map<String, Object>> getGuiUseList(String unset, String keyword) {
		List<Map<String, Object>> guiUseList = new ArrayList<>();
		Map<String, GUIData> guiDataMap = GUIConfiguration.getGUIDataMap();

		String sql = """
                SELECT mi."MenuCode",
                       mi."MenuName",
                       mi."Url",
                       mi."MenuFolder_id",
                       mf."FolderName"
                FROM menu_item mi
                INNER JOIN menu_folder mf ON mf.id = mi."MenuFolder_id"
                ORDER BY mi."MenuName"
                """;

		List<Map<String, Object>> menuItems = sqlRunner.getRows(sql, null);

		for (Map.Entry<String, GUIData> entry : guiDataMap.entrySet()) {
			String menuCode = entry.getKey();
			GUIData guiData = entry.getValue();

			boolean exists = menuItems.stream().anyMatch(m -> m.get("MenuCode").equals(menuCode));
			if (keyword == null || keyword.isEmpty() || keyword.equals(menuCode) || keyword.equals(guiData.name)) {
				Map<String, Object> guiItem = new HashMap<>();
				guiItem.put("MenuCode", menuCode);
				guiItem.put("MenuName", guiData.name);
				guiItem.put("FolderName", exists ? menuItems.stream().filter(m -> m.get("MenuCode").equals(menuCode)).findFirst().get().get("FolderName") : "");
				guiItem.put("exists", exists);
				guiItem.put("template", guiData.templates.get("default"));
				guiUseList.add(guiItem);
			}
		}
		return guiUseList;
	}


	// 소스메뉴 리스트 조회
//	@SuppressWarnings("finally")
//	public List<Map<String, Object>> getGuiUseList(String unset, String keyword) {
//
//		List<Map<String, Object>> gui_use_list = new ArrayList<>();
//
//		Map<String, GUIData> dic_gui = GUIConfiguration.getGUIDataMap();
//
//		String sql = """
//				select mi."MenuCode"
//		        ,mi."MenuName"
//		        , mi."Url"
//		        , mi."MenuFolder_id"
//		        , mf."FolderName"
//		        from menu_item mi
//		        inner join menu_folder mf on mf.id = mi."MenuFolder_id"
//		        order by "MenuName"
//				""";
//
//        List<Map<String, Object>> menu_items = this.sqlRunner.getRows(sql, null);
//
//        try {
//
//    		for (Map.Entry<String, GUIData> elem : dic_gui.entrySet()) {
//
//        		Boolean matching = false;
//        		Boolean exists = false;
//
//    			GUIData gui = elem.getValue();
//    			String gui_name = gui.name;
//
//    			if (StringUtils.isEmpty(keyword)) {
//    				matching = true;
//    			} else if (gui_name.equals(keyword)) {
//    				matching = true;
//    			} else if (elem.getKey().equals(keyword)) {
//    				matching = true;
//    			}
//
//    			String menuName= "";
//    			String folderName = "";
//
//    			for (Map<String, Object> m : menu_items) {
//    				if (m.get("MenuCode").equals(elem.getKey())) {
//    					menuName = (String) m.get("MenuName");
//    					folderName = (String) m.get("FolderName");
//    					exists = true;
//    					break;
//    				}
//    			}
//
//				// 미설정메뉴만
//    			if (matching) {
//
//					Map<String, Object> guiList = null;
//
//    				if (StringUtils.isEmpty(unset) == false) {
//
//    					if (exists == false) {
//    						guiList = new HashMap<String, Object>();
//
//	    					guiList.put("MenuCode", elem.getKey());
//	    					guiList.put("MenuName", gui_name);
//	    					guiList.put("FolderName", "");
//	    					guiList.put("exists", exists);
//							guiList.put("template", gui.templates.get("default"));
//	    					gui_use_list.add(guiList);
//    					}
//    				} else {
//    					// 설정되었든 아니든 다 나오게
//    					if (exists == false) {
//    						guiList = new HashMap<String, Object>();
//
//        					guiList.put("MenuCode", elem.getKey());
//        					guiList.put("MenuName", gui_name);
//        					guiList.put("FolderName", "");
//        					guiList.put("exists", exists);
//							guiList.put("template", gui.templates.get("default"));
//        					gui_use_list.add(guiList);
//    					} else {
//    						// DB설정 메뉴명을 보여준다.
//    						guiList = new HashMap<String, Object>();
//
//        					guiList.put("MenuCode", elem.getKey());
//        					guiList.put("MenuName", menuName);
//        					guiList.put("FolderName", folderName);
//        					guiList.put("exists", exists);
//							guiList.put("template", gui.templates.get("default"));
//        					gui_use_list.add(guiList);
//    					}
//    				}
//    			}
//    		}
//        } catch (Exception ex) {
//
//        } finally {
//            return gui_use_list;
//		}
//	}
}
