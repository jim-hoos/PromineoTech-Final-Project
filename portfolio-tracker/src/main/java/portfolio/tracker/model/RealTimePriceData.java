package portfolio.tracker.model;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RealTimePriceData {
	private String     ticker;
	private Date       timestamp;
	private BigDecimal open;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal tngoLast;
	private Long       volume;
	

}
