package portfolio.tracker.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio.tracker.entity.Holdings;

@Data
@NoArgsConstructor
public class PortfolioHoldings {
	private long holdingId;
	private long investmentId;
	private long brokerageId;
	
	public PortfolioHoldings(Holdings holding) {
		holdingId = holding.getHoldingId();
		investmentId = holding.getInvestmentId();
		brokerageId = holding.getBrokerageId();
	}
}
