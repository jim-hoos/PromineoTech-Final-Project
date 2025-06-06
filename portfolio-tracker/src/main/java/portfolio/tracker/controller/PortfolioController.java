package portfolio.tracker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import lombok.extern.slf4j.Slf4j;
import portfolio.tracker.model.DailyPriceData;
import portfolio.tracker.model.PortfolioBrokerage;
import portfolio.tracker.model.PortfolioData;
import portfolio.tracker.model.PortfolioHoldings;
import portfolio.tracker.model.PortfolioInvestmentTypeData;
import portfolio.tracker.model.PortfolioInvestments;
import portfolio.tracker.model.RealTimePriceData;
import portfolio.tracker.model.TransactionData;
import portfolio.tracker.service.PortfolioService;

@RestController
@RequestMapping("/portfolio_tracker")
@Slf4j
public class PortfolioController {
	@Autowired
	private PortfolioService portfolioService;
	
	@GetMapping("/investmentTypes")
	public List<PortfolioInvestmentTypeData> getAllInvestmentTypes() {
		log.info("Retrieving all investment types");
		return portfolioService.retrieveAllInvestmentTypes();
	}
	
	@GetMapping("/investments")
	public List<PortfolioInvestments> getAllInvestments() {
		log.info("Retrieving all investments");
		return portfolioService.retrieveAllInvestments();
	}
	
	@GetMapping("/brokerages")
	public List<PortfolioBrokerage> getAllBrokerages() {
		log.info("Retrieving all brokerages");
		return portfolioService.retrieveAllBrokerages();
	}
	
	@GetMapping("/holdings/{holdingId}")
	public PortfolioHoldings getHoldingById(@PathVariable Long holdingId) {
		log.info("Retrieving holding data with ID = ", holdingId);
		return portfolioService.retrieveHoldingById(holdingId);	
	}
	
	@GetMapping("/portfolio")
	public List<PortfolioData> getPortfolio() throws Exception {
		log.info("Retrieving the portfolio");
		return portfolioService.retrievePortfolio();
	}

	@GetMapping("/portfolio/{portfolioId}")
	public PortfolioData getPortfolioById(@PathVariable Long portfolioId) throws Exception {
		log.info("Retrieving the portfolio with ID = {}", portfolioId);
		return portfolioService.retrievePortfolioById(portfolioId);
	}

	@GetMapping("/portfolio/{brokerageId}/{symbol}")
	public PortfolioData getPortfolioByBrokerageIdAndSymbol(@PathVariable Long brokerageId, @PathVariable String symbol) throws Exception {
		log.info("Retrieving the portfolio with brokerage ID = {} and symbol = {}", brokerageId, symbol);
		return portfolioService.retrievePortfolioByBrokerageIdAndSymbol(brokerageId, symbol);
	}
	
	@PutMapping("/portfolio/{portfolioId}")
	public PortfolioData updatePortfolio(@PathVariable Long portfolioId, @RequestBody PortfolioData portfolioData) {
		portfolioData.setPortfolioId(portfolioId);
		log.info("Updating portfolio {}", portfolioData);
		return portfolioService.savePortfolio(portfolioData);
	}
	
	@PostMapping("/portfolio")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PortfolioData insertPortfolio(@RequestBody PortfolioData portfolioData) {
		log.info("Inserting portfolio {}", portfolioData);
		return portfolioService.savePortfolio(portfolioData);
	}
	
	@PostMapping("/holding")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PortfolioHoldings insertHolding(@RequestBody PortfolioHoldings portfolioHolding) {
		log.info("Inserting holding {}", portfolioHolding);
		return portfolioService.saveHolding(portfolioHolding);
	}

	@PostMapping("/investment/{investmentTypeId}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PortfolioInvestments insertInvestment(@PathVariable Long investmentTypeId, @RequestBody PortfolioInvestments portfolioInvestment) {
		log.info("Inserting investment {}", portfolioInvestment);
		return portfolioService.saveInvestment(investmentTypeId, portfolioInvestment);
	}	
	
	@DeleteMapping("/portfolio/{portfolioId}")
	public Map<String, String> deletePortfolioById(@PathVariable Long portfolioId) {
		log.info("Deleting portfolio with ID = " + portfolioId);
		portfolioService.deletePortfolioById(portfolioId);
		
		return Map.of("message", "Portfolio with ID = " + portfolioId + " deleted.");
	}

	@PostMapping("/portfolio/buy/{brokerageId}/{typeId}/{symbol}/{shares}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PortfolioData buyPortfolioInvestmentPost(@PathVariable Long brokerageId, @PathVariable Long typeId,
			@PathVariable String symbol, @PathVariable double shares, @RequestBody PortfolioData PortfolioData) throws Exception {
		log.info("Buying {} shares of {} of type ID = {} with brokerage ID = {}", shares, symbol, typeId, brokerageId);

		return portfolioService.buyInvestmentWithPost(brokerageId, typeId, symbol, shares);
	}
	
	@PutMapping("/portfolio/buy/{brokerageId}/{symbol}/{shares}")
	public PortfolioData buyPortfolioInvestmentPut(@PathVariable Long brokerageId, @PathVariable String symbol,
			@PathVariable double shares, @RequestBody PortfolioData PortfolioData) throws Exception {
		log.info("Buying {} shares of {} with brokerage ID = {}", shares, symbol, brokerageId);

		return portfolioService.buyInvestmentWithPut(brokerageId, symbol, shares);
	}
	
	@DeleteMapping("/portfolio/sell/{brokerageId}/{symbol}/{shares}")
	public Map<String, String> sellPortfolioInvestmentDelete(@PathVariable Long brokerageId, @PathVariable String symbol,
			@PathVariable double shares) throws Exception {
		log.info("Deleting portfolio with brokerage ID = " + brokerageId + " and symbol = " + symbol);

		return portfolioService.sellPortfolioInvestmentDelete(brokerageId, symbol, shares);
	}
	
	@PutMapping("/portfolio/sell/{brokerageId}/{symbol}/{shares}")
	public PortfolioData sellPortfolioInvestmentPut(@PathVariable Long brokerageId, @PathVariable String symbol,
			@PathVariable double shares, @RequestBody PortfolioData PortfolioData) throws Exception {
		log.info("Selling {} shares of {} with brokerage ID = {}", shares, symbol, brokerageId);

		return portfolioService.sellPortfolioInvestmentPut(brokerageId, symbol, shares);
	}
	
	@GetMapping("/daily_prices/{symbol}")
	public List<DailyPriceData> getCurrentPriceData(@PathVariable String symbol) throws Exception {
		log.info("Retrieving price data for ticker " + symbol);
		return portfolioService.retrieveDailyPriceData(symbol);
	}
	
	@GetMapping({"/daily_prices/range/{symbol}/{start_date}", "/daily_prices/range/{symbol}/{start_date}/{end_date}"})
	public List<DailyPriceData> getPriceRangeData(@PathVariable Map<String, String> pathVars) throws Exception {
		String symbol = pathVars.get("symbol");
		String start_date = pathVars.get("start_date");
		String end_date = pathVars.get("end_date");
		log.info("Retrieving price range data for ticker " + symbol + " from " + start_date + ((end_date != null) ? (" to " + end_date) : ""));
		return portfolioService.retrievePriceRangeData(symbol, start_date, end_date);
	}
	
	@PostMapping("/transactions")
	@ResponseStatus(code = HttpStatus.CREATED)
	public TransactionData insertTransactions(@RequestBody TransactionData transactionData) {
		log.info("Inserting transactions {}", transactionData);
		return portfolioService.saveTransactions(transactionData);
	}
	
	@PostMapping("/daily_prices")
	@ResponseStatus(code = HttpStatus.CREATED)
	public DailyPriceData insertDailyPrices(@RequestBody DailyPriceData dailyPriceData) {
		log.info("Inserting daily price data {}", dailyPriceData);
		return portfolioService.saveDailyPriceData(dailyPriceData);
	}
	
	@PostMapping("/daily_prices/range")
	@ResponseStatus(code = HttpStatus.CREATED)
	public List<DailyPriceData> insertDailyPriceRange(@RequestBody List<DailyPriceData> dailyPriceRangeData) {
		log.info("Inserting daily price data {}", dailyPriceRangeData);
		return portfolioService.saveDailyPriceRangeData(dailyPriceRangeData);
	}
	
	@PostMapping("/daily_prices/add/{symbol}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public DailyPriceData insertDailyPricesAdd(@PathVariable String symbol) throws Exception {
		log.info("Inserting daily price data for symbol " + symbol);
		return portfolioService.saveDailyPriceDataAdd(symbol);
	}
	
	@PostMapping({"/daily_prices/range/{symbol}/{start_date}", "/daily_prices/range/{symbol}/{start_date}/{end_date}"})
	@ResponseStatus(code = HttpStatus.CREATED)
	public List<DailyPriceData> insertDailyPriceRangeAdd(@PathVariable Map<String, String> pathVars) throws Exception {
		String symbol = pathVars.get("symbol");
		String start_date = pathVars.get("start_date");
		String end_date = pathVars.get("end_date");
		log.info("Retrieving price range data for ticker " + symbol + " from " + start_date + ((end_date != null) ? (" to " + end_date) : ""));
		return portfolioService.saveDailyPriceRangeDataAdd(symbol, start_date, end_date);
	}
	
	@GetMapping("/transactions")
	public List<TransactionData> getAllTransactions() {
		log.info("Retrieving all transactions");
		return portfolioService.retrieveAllTransactions();
	}
	
	@GetMapping("/realtime_price/{symbol}")
	public List<RealTimePriceData> getRealTimePriceData(@PathVariable String symbol) throws Exception {
		log.info("Retrieving realtime price data for ticker " + symbol);
		return portfolioService.retrieveRealTimePriceData(symbol);
	}
	
	@PutMapping("/portfolio/prices")
	public List<PortfolioData> updatePortfolio() throws Exception {
		log.info("Updating portfolio prices");
		return portfolioService.updatePortfolioPrices();
	}
}

