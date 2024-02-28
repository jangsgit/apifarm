package mes.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="shipment")
@Setter
@Getter
@NoArgsConstructor
public class Shipment extends AbstractAuditModel{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	
	@Column(name = "\"ItemCode\"")
	String itemCode;
	
	@Column(name = "\"Qty\"")
	Float qty;
	
	@Column(name = "\"OrderQty\"")
	Float orderQty;
	
	@Column(name = "\"UnitPrice\"")
	Float unitPrice;
	
	@Column(name = "\"Price\"")
	Float price;
	
	@Column(name = "\"Vat\"")
	Float vat;
	
	@Column(name = "\"Description\"")
	String description;
	
	@Column(name = "\"SourceDataPk\"")
	Integer sourceDataPk;
	
	@Column(name = "\"SourceTableName\"")
	String sourceTableName;
	
	@Column(name = "\"Material_id\"")
	Integer materialId;
	
	@Column(name = "\"ShipmentHead_id\"")
	Integer shipmentHeadId;
	
	@Column(name = "\"Unit_id\"")
	Integer unitId;
}
