package mes.domain.DTO;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@JacksonXmlRootElement(localName = "response")
@Data
public class SmpDto {
	@JacksonXmlProperty(localName = "header")
	private Header header;
	
	@JacksonXmlProperty(localName = "body")
	private Body body;
	
	@Data
	public static class Header {
		@JacksonXmlProperty(localName = "resultCode")
		private String resultCode;
		
		@JacksonXmlProperty(localName = "resultMsg")
		private String resultMsg;
		
		@JacksonXmlProperty(localName = "pageNo")
		private int pageNo;
		
		@JacksonXmlProperty(localName = "numOfRows")
		private int numOfRows;
		
		@JacksonXmlProperty(localName = "totalCount")
		private int totalCount;
		
		@JacksonXmlProperty(localName = "pageSize")
		private int pageSize;
		
		@JacksonXmlProperty(localName = "startPage")
		private int startPage;
	}
	
	@Data
	public static class Body {
		@JacksonXmlProperty(localName = "items")
		private Items items;
		
		@Data
		public static class Items {
			@JacksonXmlElementWrapper(useWrapping = false)
			@JacksonXmlProperty(localName = "item")
			private List<Item> items;
		}
		
		@Data
		public static class Item {
			@JacksonXmlProperty(localName = "tradeDay")
			private String tradeDay;
			
			@JacksonXmlProperty(localName = "tradeHour")
			private String tradeHour;
			
			@JacksonXmlProperty(localName = "areaCd")
			private String areaCd;
			
			@JacksonXmlProperty(localName = "smp")
			private double smp;
		}
	}
}