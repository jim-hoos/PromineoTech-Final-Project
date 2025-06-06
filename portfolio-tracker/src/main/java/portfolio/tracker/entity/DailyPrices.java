package portfolio.tracker.entity;

import java.math.BigDecimal;
import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class DailyPrices {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long dailyPriceId;
	private String symbol;
	private BigDecimal adjClose;
	private BigDecimal close;
	private Date date;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal open;
	private Long volume;
	
}
