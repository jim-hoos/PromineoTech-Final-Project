package portfolio.tracker.model;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio.tracker.entity.DailyPrices;

@Data
@NoArgsConstructor
public class DailyPriceData {
	private Long       dailyPriceId;
	private String     symbol;
	private BigDecimal adjClose;
	private BigDecimal close;
	private Date       date;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal open;
	private Long       volume;
	
	public DailyPriceData(DailyPrices dailyPrices) {
		dailyPriceId = dailyPrices.getDailyPriceId();
		symbol = dailyPrices.getSymbol();
		adjClose = dailyPrices.getAdjClose();
		close = dailyPrices.getClose();
		date = dailyPrices.getDate();
		high = dailyPrices.getHigh();
		low = dailyPrices.getLow();
		open = dailyPrices.getOpen();
		volume = dailyPrices.getVolume();
	}
	
}
