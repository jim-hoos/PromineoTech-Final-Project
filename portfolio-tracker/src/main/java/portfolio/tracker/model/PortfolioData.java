package portfolio.tracker.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio.tracker.entity.Portfolio;

@Data
@NoArgsConstructor
public class PortfolioData {
	private Long portfolioId;
	private Long holdingId;
	private BigDecimal shares;
	private Date latestPriceDate;
	private BigDecimal latestPrice;
	
	private String brokerage;
	private String investment;
	private String symbol;
	private String investment_type;
	private BigDecimal investmentValue;
	
	public PortfolioData(Portfolio portfolio) {
		portfolioId = portfolio.getPortfolioId();
		holdingId = portfolio.getHoldingId();
		shares = portfolio.getShares();
		latestPriceDate = portfolio.getLatestPriceDate();
		latestPrice = portfolio.getLatestPrice();
	}
}
