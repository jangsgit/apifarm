package mes.app.sale.service;

import mes.domain.DTO.TB_RP510Dto;
import mes.domain.entity.TB_RP510;
import mes.domain.repository.TB_RP510Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class SaleUploadService {

//	SecurityContext sc = SecurityContextHolder.getContext();
//	Authentication auth = sc.getAuthentication();
//	User user = (User) auth.getPrincipal();
	
	@Autowired
	private TB_RP510Repository TB_RP510Repository;
	
	@Autowired
	SqlRunner sqlRunner;
	
	// 매출 정보 조회
	public List<TB_RP510> fetchAllSales() {
		return TB_RP510Repository.findAll(Sort.by(Sort.Direction.ASC, "salesym"));
	}
	
	// 특정 날짜 범위에 따른 매출 데이터 조회
	public List<TB_RP510> fetchAllSalesByDateRange(String startDate, String endDate) {
		Sort sort = Sort.by(Sort.Direction.ASC, "salesym");
		return TB_RP510Repository.findBySalesymBetween(startDate, endDate, sort);
	}
	
	// 매출 정보 저장
	@Transactional
	public void saveSalesInfo(TB_RP510Dto dto) throws Exception {
		
		if (TB_RP510Repository.existsBySalesym(dto.getSalesym())) {
			throw new Exception("해당 매출년월이 이미 존재합니다.");
		}
		
		TB_RP510 rp510 = new TB_RP510();
		rp510.setSalesym(dto.getSalesym());
		rp510.setSpworkcd(dto.getSpworkcd());
		rp510.setSpcompcd(dto.getSpcompcd());
		rp510.setSpplancd(dto.getSpplancd());
		rp510.setSpworknm(dto.getSpworknm());
		rp510.setSpcompnm(dto.getSpcompnm());
		rp510.setSpplannm(dto.getSpplannm());
		rp510.setEnergyQty(dto.getEnergyQty());
		rp510.setSmpCost(dto.getSmpCost());
		rp510.setSmpAmt(dto.getSmpAmt());
		rp510.setRecCost(dto.getRecCost());
		rp510.setRecQty(dto.getRecQty());
		rp510.setRecAmt(dto.getRecAmt());
		rp510.setStotAmt(dto.getStotAmt());
		rp510.setInDate(dto.getInDate());
		rp510.setInUserId(dto.getInUserId());
		rp510.setInUserNm(dto.getInUserNm());
		TB_RP510Repository.save(rp510);
	}
	
	// 수정
	@Transactional
	public void updateSalesInfo(String salesym, TB_RP510Dto dto) {
		TB_RP510 existingSale = TB_RP510Repository.findById(salesym).orElseThrow(() -> new RuntimeException("매출 정보를 찾을 수 없습니다."));
		existingSale.setSpworkcd(dto.getSpworkcd());
		existingSale.setSpcompcd(dto.getSpcompcd());
		existingSale.setSpplancd(dto.getSpplancd());
		existingSale.setSpworknm(dto.getSpworknm());
		existingSale.setSpcompnm(dto.getSpcompnm());
		existingSale.setSpplannm(dto.getSpplannm());
		existingSale.setEnergyQty(dto.getEnergyQty());
		existingSale.setSmpCost(dto.getSmpCost());
		existingSale.setSmpAmt(dto.getSmpAmt());
		existingSale.setRecCost(dto.getRecCost());
		existingSale.setRecQty(dto.getRecQty());
		existingSale.setRecAmt(dto.getRecAmt());
		existingSale.setStotAmt(dto.getStotAmt());
		existingSale.setInDate(dto.getInDate());
		existingSale.setInUserId(dto.getInUserId());
		existingSale.setInUserNm(dto.getInUserNm());
	}
	
	// 삭제
	@Transactional
	public void deleteSalesInfo(String salesym) {
		TB_RP510Repository.deleteById(salesym);
	}
	
}
