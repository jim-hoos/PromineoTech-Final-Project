package portfolio.tracker.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio.tracker.entity.InvestmentTypes;
import portfolio.tracker.entity.Investments;

@Data
@NoArgsConstructor
public class PortfolioInvestmentTypeData {
	private long investmentTypeId;
	private String name;
	
	private Set<PortfolioInvestments> investments = new HashSet<>(); //

	public PortfolioInvestmentTypeData(InvestmentTypes investmentTypes) {
		investmentTypeId = investmentTypes.getInvestmentTypeId();
		name = investmentTypes.getName();
		
		for (Investments investment : investmentTypes.getInvestments()) { //
			investments.add(new PortfolioInvestments(investment)); //
		} //
	}

}
