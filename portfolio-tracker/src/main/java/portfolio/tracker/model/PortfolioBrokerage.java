package portfolio.tracker.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio.tracker.entity.Brokerages;
import portfolio.tracker.entity.Investments;

@Data
@NoArgsConstructor
public class PortfolioBrokerage {
	private Long brokerageId;
	private String name;
	
	private Set<PortfolioInvestments> investments = new HashSet<>(); //
	
	public PortfolioBrokerage(Brokerages brokerages) {
		brokerageId = brokerages.getBrokerageId();
		name = brokerages.getName();
		
		for (Investments investment : brokerages.getInvestments()) { //
			investments.add(new PortfolioInvestments(investment));  //
		} //
	}
}
