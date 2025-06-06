package portfolio.tracker.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Portfolio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long portfolioId;
	private Long holdingId;
	private BigDecimal shares;
	private Date latestPriceDate;
	private BigDecimal latestPrice;
}
