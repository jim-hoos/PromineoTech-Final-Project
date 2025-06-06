package portfolio.tracker.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio.tracker.entity.Transactions;

@Data
@NoArgsConstructor
public class TransactionData {
	private Long transactionId;
	private String symbol;
	private String name;
	private String type;
	private String brokerage;
	private BigDecimal shares;
	private BigDecimal price;
	private Date datetime;
	
	public TransactionData(Transactions transactions) {
		transactionId = transactions.getTransactionId();
		symbol = transactions.getSymbol();
		name = transactions.getName();
		type = transactions.getType();
		brokerage = transactions.getBrokerage();
		shares = transactions.getShares();
		price = transactions.getPrice();
		datetime = transactions.getDatetime();
	}
}
