package portfolio.tracker.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio.tracker.entity.Brokerages;
import portfolio.tracker.entity.Investments;

@Data
@NoArgsConstructor
public class PortfolioInvestments {
	private Long investmentId;
	private String name;
	private String symbol;
	private Long investmentTypeId;
	
	private Set<PortfolioBrokerage> brokerages = new HashSet<>();
	
	public PortfolioInvestments(Investments investments) {
		investmentId = investments.getInvestmentId();
		name = investments.getName();
		symbol = investments.getSymbol();
		
		for (Brokerages brokerage : investments.getBrokerages()) {
			brokerages.add(new PortfolioBrokerage(brokerage));
		}
	}
}
