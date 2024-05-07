package mes.app.shipment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mes.app.shipment.service.ShipmentListService;
import mes.app.shipment.service.ShipmentStmtService;
import mes.app.shipment.service.TradeStmtService;
import mes.domain.entity.Shipment;
import mes.domain.entity.ShipmentHead;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.repository.ShipmentHeadRepository;
import mes.domain.repository.ShipmentRepository;
import mes.domain.services.CommonUtil;
import mes.domain.services.DateUtil;

@RestController
@RequestMapping("/api/shipment/shipment_stmt")
public class ShipmentStmtController {

	@Autowired
	private ShipmentListService shipmentListService;
	
	@Autowired
	private ShipmentStmtService shipmentStmtService;

	@Autowired
	ShipmentHeadRepository shipmentHeadRepository;

	@Autowired
	ShipmentRepository shipmentRepository;

	@Autowired
	TradeStmtService tradeStmtService;
	
	// 출하지시 목록 조회
	@GetMapping("/order_list")
	public AjaxResult getOrderList(
			@RequestParam(value="srchStartDt", required=false) String date_from,
			@RequestParam(value="srchEndDt", required=false) String date_to,
			@RequestParam(value="cboCompany", required=false) String comp_pk,
			@RequestParam(value="cboMatGroup", required=false) String mat_grp_pk,
			@RequestParam(value="cboMaterial", required=false) String mat_pk) {
		
		List<Map<String, Object>> items = this.shipmentListService.getShipmentHeadList(date_from, date_to, comp_pk, mat_grp_pk, mat_pk, null, "");
		
		AjaxResult result = new AjaxResult();
		result.data = items;
		
		return result;
	}
	
	// 출하 품목
	@GetMapping("/shipment_item_list")
	public AjaxResult getShipmentItemList(
			@RequestParam(value="head_id", required=false) String head_id,
			@RequestParam(value="calc_money", required=false) String calc_money) {
		
		List<Map<String, Object>> items = null; 

		if ("Y".equals(calc_money)) {
			items = this.shipmentStmtService.getShipmentItemForStmtList(Integer.parseInt(head_id));
		} else {
			items = this.shipmentListService.getShipmentItemList(head_id);	
		}		
		
		AjaxResult result = new AjaxResult();
		result.data = items;
		
		return result;
	}
	
	// 단가 변경
	@PostMapping("/update_unit_price")
	@Transactional
	public AjaxResult saveUnitPrice(
			@RequestParam(value="head_id", required=false) Integer head_id,
			@RequestBody MultiValueMap<String,Object> dataList,
			HttpServletRequest request,
			Authentication auth) {
		
		User user = (User)auth.getPrincipal();
		
		AjaxResult result = new AjaxResult();
		
		List<Map<String, Object>> item = CommonUtil.loadJsonListMap(dataList.getFirst("Q").toString());

		if (item.size() == 0) {
			result.success = false;
			return result;
		}
		
		ShipmentHead head = this.shipmentHeadRepository.getShipmentHeadById(head_id);
		
		if (head == null) {			
			result.success = false;
			return result;			
		} else {
			
			if ("Y".equals(head.getStatementIssuedYN())) {
				result.success = false;
				return result;				
			} else {				
				Float qty_sum = null;
				Float price_sum = null;
				Float vat_sum = null;
				
				for (int i = 0; i < item.size(); i++) {
					
					Integer ship_pk = (Integer) item.get(i).get("ship_pk");
					Shipment shipment = this.shipmentRepository.getShipmentById(ship_pk);
					
					if (shipment != null) {
						
						String vat_exempt_yn = (String) item.get(i).get("vat_exempt_yn");
						
						if (vat_exempt_yn == null || vat_exempt_yn == "") {
							vat_exempt_yn = "N";
						}
			            
						Float unit_price = CommonUtil.tryFloatNull(item.get(i).get("unit_price"));
						Float order_qty = shipment.getOrderQty();
						
			            if (qty_sum == null) {
			            	qty_sum = (float) 0;
			            }
			            
						qty_sum += order_qty;
	
		                Float vat = (float) 0;
		                Float price = (float) 0;
		                
			            if (unit_price != null) {
			            	
			            	if (price_sum == null) {
			            		price_sum = (float) 0;
				            }
			            	
			            	price = unit_price * order_qty;
			                price_sum += price;
	                    
			                if ("Y".equals(vat_exempt_yn)) {
	                        	vat = (float) 0;
	                        } else {
	                        	vat = (float) (price * 0.1);
	                        }

			            	if (vat_sum == null) {
			            		vat_sum = (float) 0;
				            }			        
			            	
	                        vat_sum += vat;
			            } else {			            	
			            	unit_price = null;
		                    price = null;
		                    vat  = null;
			            }
			            
	                    shipment.setUnitPrice(unit_price);
	                    shipment.setPrice(price); 
	                    shipment.setVat(vat);
	                    shipment.set_audit(user);
	                    
	                    this.shipmentRepository.save(shipment);
					}
				}

                head.setTotalQty(qty_sum);
                head.setTotalPrice(price_sum);
                head.setTotalVat(vat_sum);
                head.set_audit(user);
                
                this.shipmentHeadRepository.save(head);
			}
		}
		
		return result;	
	}
		
	// 명세서 발행처리	
	@PostMapping("/update_stmt_issue")
	public AjaxResult issueStatement(
			@RequestParam("head_id") Integer head_id,
			HttpServletRequest request,
			Authentication auth) {
		User user = (User)auth.getPrincipal();
		AjaxResult result = new AjaxResult();		
		ShipmentHead head = this.shipmentHeadRepository.getShipmentHeadById(head_id);
		if (head == null) {
			result.success = false;
			return result;
		} else {
			if ("Y".equals(head.getStatementIssuedYN())) {
				result.success = false;
				return result;
			} else {

                head.setStatementIssuedYN("Y");
                head.setIssueDate(DateUtil.getNowTimeStamp());	//DateUtil.getTodayString()
                head.setStatementNumber("");
                head.set_audit(user);
                head = this.shipmentHeadRepository.save(head);
                result.data = head;
			}
		}
		return result;
	}

	// 거래명세서 출력
	@PostMapping("/print_trade_stmt")
	public AjaxResult printTradingStatement(
			@RequestParam(value="head_id", required=false) Integer head_id) {
		Map<String, Object> header = this.tradeStmtService.getTradeStmtHeaderInfo(head_id);
		List<Map<String, Object>> items = this.tradeStmtService.getTradeStmtItemList(head_id);
		
        Map<String, Object> rtnData = new HashMap<String, Object>();
        rtnData.putAll(header);
        rtnData.put("item_list", items);

		AjaxResult result = new AjaxResult();
		result.data = rtnData;
		return result;
	}
}
