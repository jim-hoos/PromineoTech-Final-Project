package portfolio.tracker.service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import portfolio.tracker.dao.BrokerageDao;
import portfolio.tracker.dao.DailyPriceDao;
import portfolio.tracker.dao.HoldingDao;
import portfolio.tracker.dao.InvestmentDao;
import portfolio.tracker.dao.InvestmentTypeDao;
import portfolio.tracker.dao.PortfolioDao;
import portfolio.tracker.dao.ProjectDao;
import portfolio.tracker.dao.TransactionDao;
import portfolio.tracker.entity.Brokerages;
import portfolio.tracker.entity.DailyPrices;
import portfolio.tracker.entity.Holdings;
import portfolio.tracker.entity.Investments;
import portfolio.tracker.entity.InvestmentTypes;
import portfolio.tracker.entity.Portfolio;
import portfolio.tracker.entity.Transactions;
import portfolio.tracker.model.DailyPriceData;
import portfolio.tracker.model.PortfolioBrokerage;
import portfolio.tracker.model.PortfolioData;
import portfolio.tracker.model.PortfolioHoldings;
import portfolio.tracker.model.PortfolioInvestments;
import portfolio.tracker.model.RealTimePriceData;
import portfolio.tracker.model.PortfolioInvestmentTypeData;
import portfolio.tracker.model.TransactionData;

@Service
public class PortfolioService {
	/////////////////////////////////////////////
	// DAO (Data Access Object) List
	/////////////////////////////////////////////
	
	@Autowired
	private BrokerageDao brokerageDao;
	@Autowired
	private DailyPriceDao dailyPriceDao;
	@Autowired
	private HoldingDao holdingDao;
	@Autowired
	private InvestmentDao investmentDao;
	@Autowired
	private InvestmentTypeDao investmentTypeDao;
	@Autowired
	private PortfolioDao portfolioDao;
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private TransactionDao transactionDao;

	private static final String LOCAL_HOST_URL = "http://localhost:8080";
	private static final String TIINGO_URL = "https://api.tiingo.com/";
	private static final String TIINGO_DAILY_URL = "tiingo/daily/";
	private static final String TIINGO_IEX_URL = "iex/";
	
	// Default URI to localhost
	private final WebClient webClient = WebClient.create(LOCAL_HOST_URL);

	/////////////////////////////////////////////
	// Copy model to entity methods
	/////////////////////////////////////////////	
	private void copyDailyPriceFields(DailyPrices dailyPrice, DailyPriceData dailyPriceData) {
		dailyPrice.setDailyPriceId(dailyPriceData.getDailyPriceId());
		dailyPrice.setSymbol(dailyPriceData.getSymbol());
		dailyPrice.setClose(dailyPriceData.getClose());
		dailyPrice.setAdjClose(dailyPriceData.getAdjClose());
		dailyPrice.setHigh(dailyPriceData.getHigh());
		dailyPrice.setLow(dailyPriceData.getLow());
		dailyPrice.setOpen(dailyPriceData.getOpen());
		dailyPrice.setDate(dailyPriceData.getDate());
		dailyPrice.setVolume(dailyPriceData.getVolume());
	}
	
	private void copyHoldingFields(Holdings holding, PortfolioHoldings portfolioHolding) {
		holding.setHoldingId(portfolioHolding.getHoldingId());
		holding.setBrokerageId(portfolioHolding.getBrokerageId());
		holding.setInvestmentId(portfolioHolding.getInvestmentId());
	}

	private void copyInvestmentFields(Investments investment, PortfolioInvestments portfolioInvestment) {
		investment.setInvestmentId(portfolioInvestment.getInvestmentId());
		investment.setName(portfolioInvestment.getName());
		investment.setSymbol(portfolioInvestment.getSymbol());
	}
	
	private void copyPortfolioFields(Portfolio portfolio, PortfolioData portfolioData) {
		portfolio.setPortfolioId(portfolioData.getPortfolioId());
		portfolio.setHoldingId(portfolioData.getHoldingId());
		portfolio.setLatestPrice(portfolioData.getLatestPrice());
		portfolio.setLatestPriceDate(portfolioData.getLatestPriceDate());
		portfolio.setShares(portfolioData.getShares());
		
	}
	
	private void copyTransactionFields(Transactions transaction, TransactionData transactionData) {
		transaction.setTransactionId(transactionData.getTransactionId());
		transaction.setName(transactionData.getName());
		transaction.setPrice(transactionData.getPrice());
		transaction.setShares(transactionData.getShares());
		transaction.setSymbol(transactionData.getSymbol());
		transaction.setDatetime(transactionData.getDatetime());
		transaction.setType(transactionData.getType());
		transaction.setBrokerage(transactionData.getBrokerage());
	}
	
	// createHolding
	// This method calls the controller method insertHolding
	public PortfolioHoldings createHolding(PortfolioHoldings holding) {
		PortfolioHoldings holdingMono = webClient.post()
				.uri("/portfolio_tracker/holding")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(holding), PortfolioHoldings.class)
				.retrieve()
				.bodyToMono(PortfolioHoldings.class)
				.block();  // TODO: Investigate why block is needed
				
		return holdingMono;
		
	}

	// createInvestment
	// This method calls the controller method insertInvestment	
	public PortfolioInvestments createInvestment(PortfolioInvestments investment) {
		PortfolioInvestments investmentMono = webClient.post()
				.uri("/portfolio_tracker/investment/" + investment.getInvestmentTypeId())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(investment), PortfolioInvestments.class)
				.retrieve()
				.bodyToMono(PortfolioInvestments.class)
				.block();  // TODO: Investigate why block is needed
				
		return investmentMono;
		
	}

	// createPortfolio
	// This method calls the controller method insertPortfolio	
	public Mono<PortfolioData> createPortfolio(PortfolioData data) {
		Mono<PortfolioData> dataMono = webClient.post()
				.uri("/portfolio_tracker/portfolio")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(data), PortfolioData.class)
				.retrieve()
				.bodyToMono(PortfolioData.class);
		
		dataMono.subscribe(
				res -> System.out.println("Response: " + res),
				error -> System.err.println("Error: " + error),
				() -> System.out.println("Request Completed")
				);
				
		return dataMono;
		
	}

	// createTransaction
	// This method calls the controller method insertTransactions	
	public Mono<TransactionData> createTransaction(TransactionData data) {
		Mono<TransactionData> dataMono = webClient.post()
				.uri("/portfolio_tracker/transactions")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(data), TransactionData.class)
				.retrieve()
				.bodyToMono(TransactionData.class);
		
		dataMono.subscribe(
				res -> System.out.println("Response: " + res),
				error -> System.err.println("Error: " + error),
				() -> System.out.println("Request Completed")
				);
				
		return dataMono;
		
	}

	// deletePortfolio
	// This method calls the controller method deletePortfolioById	
	private Void deletePortfolio(PortfolioData data) {
		return webClient.delete()
				.uri("/portfolio_tracker/portfolio/" + data.getPortfolioId())
				.retrieve()
				.bodyToMono(Void.class)
				.block();  // TODO: Investigate why block is needed
		
	}

	// deletePortfolioById
	// This method is called by the controller method deletePortfolioById
	@Transactional(readOnly = false)
	public void deletePortfolioById(Long portfolioId) {
		Portfolio portfolio = findPortfolioById(portfolioId);
		portfolioDao.delete(portfolio);
	}

	/////////////////////////////////////////////
	// Find and findOrCreate methods
	/////////////////////////////////////////////	
	
	private DailyPrices findDailyPriceById(Long dailyPriceId) {
		return dailyPriceDao.findById(dailyPriceId).orElseThrow(() -> new NoSuchElementException("Daily price with ID = " + dailyPriceId + " was not found."));
	}
	
	private Holdings findHoldingById(long holdingId) {
		return holdingDao.findById(holdingId).orElseThrow(() -> new NoSuchElementException("Holding with ID = " + holdingId + " was not found."));
	}

	private Investments findInvestmenById(Long investmentTypeid, Long investmentId) {
		Investments investment = investmentDao.findById(investmentId).orElseThrow(() -> new NoSuchElementException("Investment with ID = " + investmentId + " was not found."));
		
		if (investment.getInvestmentTypes().getInvestmentTypeId() == investmentTypeid) {
			return investment;
		} else {
			throw new IllegalArgumentException("The investment with ID = " + investmentId + " is not included by the investment type with ID = " + investmentTypeid);
		}
	}
	
	private InvestmentTypes findInvestmentTypeById(Long investmentTypeId) {
		return investmentTypeDao.findById(investmentTypeId).orElseThrow(() -> new NoSuchElementException("Investment type with ID = " + investmentTypeId + " was not found."));
	}
	
	private DailyPrices findOrCreateDailyPrices(Long dailyPriceId) {
		if (Objects.isNull(dailyPriceId) || dailyPriceId == 0) {
			return new DailyPrices();
		} else {
			return findDailyPriceById(dailyPriceId);
		}
	}
	
	private Holdings findOrCreateHolding(long holdingId) {
		if (Objects.isNull(holdingId) || holdingId == 0) {
			return new Holdings();
		} else {
			return findHoldingById(holdingId);
		}
	}
	
	private Investments findOrCreateInvestment(Long investmentTypeid, Long investmentId) {
		if (Objects.isNull(investmentId) || investmentId == 0) {
			return new Investments();
		} else {
			return findInvestmenById(investmentTypeid, investmentId);
		}
	}
	
	private Portfolio findOrCreatePortfolio(Long portfolioId) {
		if (Objects.isNull(portfolioId) || portfolioId == 0) {
			return new Portfolio();
		} else {
			return findPortfolioById(portfolioId);
		}
	}
	
	private Transactions findOrCreateTransaction(Long transactionId) {
		if (Objects.isNull(transactionId) || transactionId == 0) {
			return new Transactions();
		} else {
			return findTransactionById(transactionId);
		}
	}
	
	private Portfolio findPortfolioById(Long portfolioId) {
		return portfolioDao.findById(portfolioId).orElseThrow(() -> new NoSuchElementException("Portfolio with ID = " + portfolioId + " was not found."));
	}

	private Transactions findTransactionById(Long transactionId) {
		return transactionDao.findById(transactionId).orElseThrow(() -> new NoSuchElementException("Transaction with ID = " + transactionId + " was not found."));
	}
	
	// getDailyTickerPriceData
	// This method calls Tiingo to get the daily ticker price for a ticker symbol
	private List<DailyPriceData> getDailyTickerPriceData(String symbol) throws URISyntaxException {
		URI uri = null;
		try {
			uri = new URI(TIINGO_URL + TIINGO_DAILY_URL + symbol + "/prices?token=eab90b238dd85d24da4d9e393b03274448ddb536");
		} catch (URISyntaxException e) {
			throw new URISyntaxException("uri: " + uri, "Bad uri provided.");
		}
		ParameterizedTypeReference<List<DailyPriceData>> typeRef = new ParameterizedTypeReference<List<DailyPriceData>>() {};
		return  webClient.get()
				.uri(uri)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(typeRef)
				.onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().value() == 404 ? Mono.empty() : Mono.error(ex))
				.block(); // TODO: Investigate why block is needed
	}

	// getRealTimeTickerPriceData
	// This method calls Tiingo to get the real-time price for a ticker symbol
	private List<RealTimePriceData> getRealTimeTickerPriceData(String symbol) throws URISyntaxException {
		URI uri = null;
		try {
			uri = new URI(TIINGO_URL + TIINGO_IEX_URL + symbol + "?token=eab90b238dd85d24da4d9e393b03274448ddb536");
		} catch (URISyntaxException e) {
			throw new URISyntaxException("uri: " + uri, "Bad uri provided.");
		}
		ParameterizedTypeReference<List<RealTimePriceData>> typeRef = new ParameterizedTypeReference<List<RealTimePriceData>>() {};
		return  webClient.get()
				.uri(uri)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(typeRef)
				.onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().value() == 404 ? Mono.empty() : Mono.error(ex))
				.block(); // TODO: Investigate why block is needed
	}	
	
	// getTickerPriceRangeData
	// This method calls Tiingo to get the daily ticker price for a ticker symbol and a date range
	private List<DailyPriceData> getTickerPriceRangeData(String symbol, String start_date, String end_date) throws Exception {
		String date_str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false);
		
		try {
			// for some reason, date is being parsed into a date one day earlier
			// Therefore, am adding a day to the start date to compensate
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(start_date));
			cal.add(Calendar.DATE, 1);
			start_date = sdf.format(cal.getTime());
//			sdf.parse(start_date);
			date_str = "startDate=" + start_date + "&";
			if (end_date != null && !end_date.isEmpty()) {
				sdf.parse(end_date);
				date_str += "endDate=" + end_date + "&";				
			}
		} catch (ParseException e) {
			throw new ParseException("Could not parse date", 0);
		}
		
		URI uri = null;
		try {
			uri = new URI(TIINGO_URL + TIINGO_DAILY_URL + symbol + "/prices?" + date_str + "token=eab90b238dd85d24da4d9e393b03274448ddb536");
		} catch (URISyntaxException e) {
			throw new URISyntaxException(date_str, date_str);
		}
		
		ParameterizedTypeReference<List<DailyPriceData>> typeRef = new ParameterizedTypeReference<List<DailyPriceData>>(){};
		
		return  webClient.get()
				.uri(uri)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(typeRef)
				.onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().value() == 404 ? Mono.empty() : Mono.error(ex))
				.block(); // TODO: Investigate why block is needed
	}

	// retrieveAllBrokerages
	// This method is called by the controller method getAllBrokerages to get all brokerages
	@Transactional(readOnly = true)
	public List<PortfolioBrokerage> retrieveAllBrokerages() {
		List<Brokerages> brokerages = brokerageDao.findAll();
		List<PortfolioBrokerage> portfolioBrokerages = new LinkedList<>();
		
		for (Brokerages brokerage : brokerages) {
			PortfolioBrokerage tempBrokerage = new PortfolioBrokerage(brokerage);
			portfolioBrokerages.add(tempBrokerage);
		}
		
		return portfolioBrokerages;
	}

	// retrieveAllInvestments
	// This method is called by the controller method getAllInvestments to get all investments
	@Transactional(readOnly = true)
	public List<PortfolioInvestments> retrieveAllInvestments() {
		List<Investments> investments = investmentDao.findAll();
		List<PortfolioInvestments> portfolioInvestments = new LinkedList<>();
		
		for (Investments investment : investments) {
			PortfolioInvestments tempInvestment = new PortfolioInvestments(investment);
			portfolioInvestments.add(tempInvestment);
		}
		
		return portfolioInvestments;
	}	

	// retrieveAllInvestmentTypes
	// This method is called by the controller method getAllInvestmentTypes to get all investment types
	@Transactional(readOnly = true)
	public List<PortfolioInvestmentTypeData> retrieveAllInvestmentTypes() {
		List<InvestmentTypes> investmentTypes = investmentTypeDao.findAll();
		List<PortfolioInvestmentTypeData> investmentTypeData = new LinkedList<>();
		
		for (InvestmentTypes investmentType : investmentTypes) {
			PortfolioInvestmentTypeData tempInvestmentTypeData = new PortfolioInvestmentTypeData(investmentType);
			investmentTypeData.add(tempInvestmentTypeData);
		}
		
		return investmentTypeData;
	}

	// retrieveAllTransactions
	@Transactional(readOnly = true)
	public List<TransactionData> retrieveAllTransactions() {
		List<Transactions> transactions = transactionDao.findAll();
		List<TransactionData> transactionData = new LinkedList<>();
		
		for (Transactions transaction : transactions) {
			TransactionData tempTransaction = new TransactionData(transaction);
			transactionData.add(tempTransaction);

		    // Print out Data to console
		    System.out.println();
		    System.out.println("**************************************************");
		    System.out.println("Name: " + tempTransaction.getName());
	    	System.out.println("\tType: " + tempTransaction.getType());
		    System.out.println("\tBrokerage: " + tempTransaction.getBrokerage());
		    System.out.println("\tShares: " + tempTransaction.getShares());
		    System.out.println("\tPrice per share: " + tempTransaction.getPrice());
		    System.out.println("\tDate: " + tempTransaction.getDatetime());
		    System.out.println("**************************************************");
		    System.out.println();
	    }
		
		return transactionData;
	}
	
	// retrieveDailyPriceData
	// This method is called by the controller method getCurrentPriceData to get current price data
	@Transactional(readOnly = true)
	public List<DailyPriceData> retrieveDailyPriceData(String symbol) throws Exception  {
		List<DailyPriceData> tickerPriceData = null;
		try {
			tickerPriceData = getDailyTickerPriceData(symbol);
			// manually add the symbol since wasn't mapped by external API call
			tickerPriceData.get(0).setSymbol(symbol);
		} catch (Exception e) {
			throw new Exception(e);
		}
		
		return tickerPriceData;

	}
	
	// retrieveHoldingById
	// This method is called by the controller method getHoldingById to get a holding by an Id
	@Transactional(readOnly = true)
	public PortfolioHoldings retrieveHoldingById(Long holdingId) {
		Optional<Holdings> holding = holdingDao.findById(holdingId);

		return new PortfolioHoldings(holding.orElseThrow(() -> new NoSuchElementException("Holding with ID = " + holdingId + " was not found.")));
	}
	
	// retrievePortfolio
	// This method is called by the controller method getPortfolio to get a complete portfolio
	@Transactional(readOnly = true)
	public List<PortfolioData> retrievePortfolio() throws Exception {
		List<Portfolio> portfolio = portfolioDao.findAll();
		List<PortfolioData> portfolioData = new LinkedList<>();
		double total = 0;
		
		for (Portfolio folio : portfolio) {
			PortfolioData tempData = new PortfolioData(folio);
			Map<String, String> hd = new HashMap<>();
			
			try {
				hd = projectDao.fetchHoldingDataById(folio.getHoldingId());
			} catch (Exception e) {
				throw new Exception(e);
			}
			
			tempData.setBrokerage(hd.get("brokerage_name"));
			tempData.setInvestment(hd.get("investment_name"));
			tempData.setSymbol(hd.get("symbol_name"));
			tempData.setInvestment_type(hd.get("type_name"));
			tempData.setInvestmentValue(tempData.getLatestPrice().multiply(tempData.getShares()));
			portfolioData.add(tempData);
			// Calculate running total
			total += tempData.getInvestmentValue().setScale(2).doubleValue();
			
			// Print out Data to console
			System.out.println("**************************************************");
			System.out.println("Investment: " + tempData.getInvestment() + (tempData.getSymbol() != null ? (" (" + tempData.getSymbol() + ")") : ""));
			System.out.println("\tType: " + tempData.getInvestment_type());
			System.out.println("\tBrokerage: " + tempData.getBrokerage());
			System.out.println("\tShares: " + tempData.getShares());
			System.out.println("\tPrice per share: " + tempData.getLatestPrice());
			System.out.println("\tValue: " + tempData.getInvestmentValue().setScale(2));
			System.out.println("**************************************************");
			System.out.println();
		}
		
		System.out.println("##################################################");
		System.out.println("Total portfolio value: " + total);
		System.out.println("##################################################");
		
		return portfolioData;
	}

	// retrievePortfolioByBrokerageIdAndSymbol
	// This method is called by the controller method getPortfolioByBrokerageIdAndSymbol to get
	//   a portfolio item by brokerage and symbol
	// TODO: missing @Transactional?
	public PortfolioData retrievePortfolioByBrokerageIdAndSymbol(Long brokerageId, String symbol) throws Exception {
		PortfolioData portfolio = new PortfolioData();
		try {
			portfolio = projectDao.fetchPortfolioByBrokerageAndSymbol(brokerageId, symbol);
		} catch (Exception e) {
			throw new Exception(e);
		}
		return portfolio;
	}
	
	// retrievePortfolioById
	// This method is called by the controller method getPortfolioById to get a portfolio item by Id
	@Transactional(readOnly = true)
	public PortfolioData retrievePortfolioById(Long portfolioId) throws Exception {
		Optional<Portfolio> portfolio = portfolioDao.findById(portfolioId);
		PortfolioData portfolioData = new PortfolioData(portfolio.orElseThrow(() -> new NoSuchElementException("Portfolio with ID = " + portfolioId + " was not found.")));

		Map<String, String> hd = new HashMap<>();
		
		try {
			hd = projectDao.fetchHoldingDataById(portfolioData.getHoldingId());
		} catch (Exception e) {
			throw new Exception(e);
		}
		
		portfolioData.setBrokerage(hd.get("brokerage_name"));
		portfolioData.setInvestment(hd.get("investment_name"));
		portfolioData.setSymbol(hd.get("symbol_name"));
		portfolioData.setInvestment_type(hd.get("type_name"));
		portfolioData.setInvestmentValue(portfolioData.getLatestPrice().multiply(portfolioData.getShares()));
		
		// Print out Data to console
		System.out.println();
		System.out.println("**************************************************");
		System.out.println("Investment: " + portfolioData.getInvestment() + (portfolioData.getSymbol() != null ? (" (" + portfolioData.getSymbol() + ")") : ""));
		System.out.println("\tType: " + portfolioData.getInvestment_type());
		System.out.println("\tBrokerage: " + portfolioData.getBrokerage());
		System.out.println("\tShares: " + portfolioData.getShares());
		System.out.println("\tPrice per share: " + portfolioData.getLatestPrice());
		System.out.println("\tValue: " + portfolioData.getInvestmentValue().setScale(2));
		System.out.println("**************************************************");
		System.out.println();
		
		return portfolioData; 
	}

	// retrievePriceRangeData
	// This method is called by the controller method getPriceRangeData to get price range data
	@Transactional(readOnly = true)
	public List<DailyPriceData> retrievePriceRangeData(String symbol, String start_date, String end_date) throws Exception {
		List<DailyPriceData> tickerPriceData = null;
		
		try {
			tickerPriceData = getTickerPriceRangeData(symbol, start_date, end_date);
			// Manually add symbol for each object in the list
			for (DailyPriceData dailyPriceData :  tickerPriceData) {
				dailyPriceData.setSymbol(symbol);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
		
		return tickerPriceData;

	}

	// retrieveRealTimePriceData
	@Transactional(readOnly = true)
	public List<RealTimePriceData> retrieveRealTimePriceData(String symbol) throws Exception {
		List<RealTimePriceData> tickerPriceData = null;
		try {
			tickerPriceData = getRealTimeTickerPriceData(symbol);
		} catch (Exception e) {
			throw new Exception(e);
		}
		
		return tickerPriceData;
	}
	
	// saveDailyPriceData
	// This method is called by the controller method insertDailyPrices to save the price data
	@Transactional(readOnly = false)
	public DailyPriceData saveDailyPriceData(DailyPriceData dailyPriceData) {
		DailyPrices dailyPrice = findOrCreateDailyPrices(dailyPriceData.getDailyPriceId());
		copyDailyPriceFields(dailyPrice, dailyPriceData);
		return new DailyPriceData(dailyPriceDao.save(dailyPrice));
	}

	// saveDailyPriceDataAdd
	// This method is called by the controller method insertDailyPricesAdd to get the current price data
	// and then save it to the daily_prices table
	// TODO: missing @Transactional?
	public DailyPriceData saveDailyPriceDataAdd(String symbol) throws Exception {
		List<DailyPriceData> dailyPriceData = retrieveDailyPriceData(symbol);
		DailyPriceData dailyPrices  = projectDao.insertDailyPrices(dailyPriceData.get(0));
		
		return dailyPrices;
	}
	
	// saveDailyPriceRangeData
	// TODO: missing @Transactional?
	public List<DailyPriceData> saveDailyPriceRangeData(List<DailyPriceData> dailyPriceRangeData) {
		List<DailyPriceData> listDailyPriceData = new LinkedList<>();
		for (DailyPriceData  dailyPriceData : dailyPriceRangeData) {
			DailyPrices dailyPrice = findOrCreateDailyPrices(dailyPriceData.getDailyPriceId());
			copyDailyPriceFields(dailyPrice, dailyPriceData);
			listDailyPriceData.add(new DailyPriceData(dailyPriceDao.save(dailyPrice)));
		}
		return listDailyPriceData;
	}

	// saveDailyPriceRangeDataAdd
	// This method is called by the controller method insertDailyPriceRangeAdd to get the current price data
	// for a range and then save it to the daily_prices table	
	// TODO: missing @Transactional?
	public List<DailyPriceData> saveDailyPriceRangeDataAdd(String symbol, String start_date, String end_date) throws Exception {
		List<DailyPriceData> dailyPriceData =  retrievePriceRangeData(symbol, start_date, end_date);
		List<DailyPriceData> dailyPriceDataSave = projectDao.insertDailyRangePrices(dailyPriceData);
		
		return dailyPriceDataSave;
	}
	
	// saveHolding
	@Transactional(readOnly = false)
	public PortfolioHoldings saveHolding(PortfolioHoldings portfolioHolding) {
		Holdings holding = findOrCreateHolding(portfolioHolding.getHoldingId());
		copyHoldingFields(holding, portfolioHolding);
		return new PortfolioHoldings(holdingDao.save(holding));
	}
	
	// saveInvestment
	@Transactional(readOnly = false)
	public PortfolioInvestments saveInvestment(Long investmentTypeId, PortfolioInvestments portfolioInvestment) {
		InvestmentTypes investmentType = findInvestmentTypeById(investmentTypeId);
		Investments investment = findOrCreateInvestment(investmentTypeId, portfolioInvestment.getInvestmentId());
		copyInvestmentFields(investment, portfolioInvestment);
		investment.setInvestmentTypes(investmentType);
		
		return new PortfolioInvestments(investmentDao.save(investment));
	}
	
	// savePortfolio
	@Transactional(readOnly = false)
	public PortfolioData savePortfolio(PortfolioData portfolioData) {
		Portfolio portfolio = findOrCreatePortfolio(portfolioData.getPortfolioId());
		copyPortfolioFields(portfolio, portfolioData);
		return new PortfolioData(portfolioDao.save(portfolio));
	}

	// saveTransactions
	@Transactional(readOnly = false)
	public TransactionData saveTransactions(TransactionData transactionData) {
		Transactions transaction = findOrCreateTransaction(transactionData.getTransactionId());
		copyTransactionFields(transaction, transactionData);
		return new TransactionData(transactionDao.save(transaction));
	}

	// updatePortfolio
	public Mono<PortfolioData> updatePortfolio(PortfolioData data) {
		Mono<PortfolioData> dataMono = webClient.put()
				.uri("/portfolio_tracker/portfolio/" + data.getPortfolioId())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(data), PortfolioData.class)
				.retrieve()
				.bodyToMono(PortfolioData.class);
		
		dataMono.subscribe(
				res -> System.out.println("Response: " + res),
				error -> System.err.println("Error: " + error),
				() -> System.out.println("Request Completed")
				);
				
		return dataMono;
		
	}

	// updatePortfolioPrices
	public List<PortfolioData> updatePortfolioPrices() throws Exception {
		List<PortfolioData> listPortfolioData = new LinkedList<>();
		Map<Long, String> ps = new HashMap<>();
		// Get all portfolio items that have a ticker symbol
		ps = projectDao.fetchPortfolioIdWithSymbol();
		
		// Loop through all portfolio Ids found
		for (Map.Entry<Long, String> entry : ps.entrySet()) {
			// Get the portfolio row by Id
			Optional<Portfolio> portfolio = portfolioDao.findById(entry.getKey());
			// Get the real-time price data for the associated ticket
			List<RealTimePriceData> rtpd = getRealTimeTickerPriceData(entry.getValue());
			if (rtpd != null && rtpd.size() > 0) {
			  Portfolio port = portfolio.get();
			  // Set the real-time price and current timestamp
			  port.setLatestPrice(rtpd.get(0).getTngoLast());
			  port.setLatestPriceDate(Calendar.getInstance().getTime());
			  // update the row in the db and add the model row to the list
			  listPortfolioData.add(new PortfolioData(portfolioDao.save(port)));
			}
		}
		
		return listPortfolioData;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// BUY AND SELL METHODS
	//////////////////////////////////////////////////////////////////////////
	
	// buyInvestmentWithPost
	@Transactional(readOnly = false)
	public PortfolioData buyInvestmentWithPost(Long brokerageId, Long typeId, String symbol, double shares) throws Exception  {
		PortfolioData data = new PortfolioData();
		// Get the current price for the symbol to be bought
		List<RealTimePriceData> realTimePriceData = retrieveRealTimePriceData(symbol);
		// Check if found the daily price - even single symbol returns a list
		if (realTimePriceData == null || realTimePriceData.size() < 1) {
			// Could not find so throw exception
			throw new NoSuchElementException("Could not retrieve daily price for symbol " + symbol); // NSEE
		}
		// Calculate the total cost for the purchase
		Double cost = shares * realTimePriceData.get(0).getTngoLast().doubleValue();
		
		// Get the money market balance
		PortfolioData moneyMarket = new PortfolioData();
		// Find the money market in the portfolio
		Map<String, String> moneyMarketData = projectDao.fetchMoneyMarketData(brokerageId);
		if (moneyMarketData.containsKey("shares") && ((Double.valueOf(moneyMarketData.get("shares")) * Double.valueOf(moneyMarketData.get("latest_price"))) > cost)) {
			moneyMarket.setShares(new BigDecimal(moneyMarketData.get("shares")));
			moneyMarket.setPortfolioId(Long.valueOf(moneyMarketData.get("portfolio_id")));
			moneyMarket.setHoldingId(Long.valueOf(moneyMarketData.get("holding_id")));
			moneyMarket.setLatestPrice(new BigDecimal(moneyMarketData.get("latest_price")));
		} else {
			// Could not find so throw exception
			throw new NoSuchElementException("Could not retrieve money market data or there is insufficient funds."); // NSEE
		}
		
		// Is there already a portfolio entry for this holding?
		// If there is, then this is not a POST call
		Long portfolioId = (long) 0;
		Map<String, String> portfolioFoundData = new HashMap<>();
		try {
			portfolioFoundData = projectDao.fetchInvestmentByBrokerageIdAndSymbol(brokerageId, symbol);
		} catch (Exception e) {
			throw new NoSuchElementException("Could not retrieve the investment for the given brokerage id and symbol."); // NSEE
		}
		// Make sure that result contains portfolio_id key
		if (portfolioFoundData.containsKey("portfolio_id")) {
			portfolioId = Long.valueOf(portfolioFoundData.get("portfolio_id"));
		}
		
		// if we found a portfolio entry, something went wrong and this is not a POST
		if (portfolioId > 0) {
			// throw exception
			throw new NoSuchElementException("Found portfolio entry when none expected. Use PUT call instead."); // NSEE
		}
		
		// Is there an existing investments row for this buy?
		Long investmentId;
		try {
			investmentId = projectDao.fetchInvestmentBySymbol(symbol);
		} catch (Exception e) {
			throw new NoSuchElementException("Found portfolio entry when none expected. Use PUT call instead."); // NSEE
		}
		
		String nameStr = "";
		// Check if found the investment_id for the given symbol
		if (investmentId == null) {
			// investment row does not exist, so create one
			PortfolioInvestments investment = new PortfolioInvestments();
			investment.setSymbol(symbol);
			investment.setInvestmentTypeId(typeId);
			if (typeId == 1) {
				nameStr = "Stock with ticker " + symbol;
			} else if (typeId == 2) {
				nameStr = "Mutual fund with ticker " + symbol;
			} else if (typeId == 4) {
				nameStr = "ETF with ticker " + symbol;
			} else {
				nameStr = symbol;
			}
			investment.setName(nameStr);
			// Create the new investments row
			PortfolioInvestments investmentMono = createInvestment(investment);
			investmentId = investmentMono.getInvestmentId();
		}

		// holding row does not exist, so create one
		PortfolioHoldings holding = new PortfolioHoldings();
		holding.setBrokerageId(brokerageId);
		holding.setInvestmentId(investmentId);
		// Create the new holdings row
		PortfolioHoldings holdingMono = createHolding(holding);

		// Set the portfolio data
		data.setHoldingId(holdingMono.getHoldingId());
		data.setLatestPrice(realTimePriceData.get(0).getTngoLast());
		data.setLatestPriceDate(Calendar.getInstance().getTime());
		data.setShares(BigDecimal.valueOf(shares));
		data.setSymbol(symbol);
		// Create the new portfolio row
		Mono<PortfolioData> dataMono = createPortfolio(data);
			
		// add transaction
		String brokerageName = projectDao.fetchBrokerageById(brokerageId);
		TransactionData tData = new TransactionData();
		tData.setName(nameStr);
		tData.setSymbol(symbol);
		tData.setShares(BigDecimal.valueOf(shares));
		tData.setType("Buy");
		tData.setDatetime(Calendar.getInstance().getTime());
		tData.setPrice(realTimePriceData.get(0).getTngoLast());
		tData.setBrokerage(brokerageName);
		// Create the new transactions row
		Mono<TransactionData> tDataMono = createTransaction(tData);
			
		// update money market balance
		Double balance = moneyMarket.getShares().doubleValue() - cost;
		moneyMarket.setShares(new BigDecimal(balance));
		moneyMarket.setLatestPriceDate(Calendar.getInstance().getTime());
		// Update the money market row
		Mono<PortfolioData> mmmono = updatePortfolio(moneyMarket);
			
		// add transaction
		TransactionData tDataMM = new TransactionData();
		tDataMM.setName(moneyMarketData.get("name"));
		tDataMM.setShares(new BigDecimal(cost));
		tDataMM.setType("Sell");
		tDataMM.setDatetime(Calendar.getInstance().getTime());
		tDataMM.setPrice(new BigDecimal(moneyMarketData.get("latest_price")));
		tDataMM.setBrokerage(moneyMarketData.get("brokerage_name"));
		// Create the new transactions row
		Mono<TransactionData> tDataMMMono = createTransaction(tDataMM);			

		return data;
	}
	
	// buyInvestmentWithPut
	@Transactional(readOnly = false)
	public PortfolioData buyInvestmentWithPut(Long brokerageId, String symbol, double shares) throws Exception {
		// Get the daily price data based upon the symbol
		PortfolioData data = new PortfolioData();
		List<RealTimePriceData> realTimePriceData = retrieveRealTimePriceData(symbol);
		if (realTimePriceData == null || realTimePriceData.size() < 1) {
			// throw exception
			throw new NoSuchElementException("Could not retrieve daily price for symbol " + symbol); // NSEE
		}
		// Calculate the total cost for the purchase
		Double cost = shares * realTimePriceData.get(0).getTngoLast().doubleValue();
		
		// Get the money market balance
		PortfolioData moneyMarket = new PortfolioData();
		Map<String, String> moneyMarketData = projectDao.fetchMoneyMarketData(brokerageId);
		if (moneyMarketData.containsKey("shares") && ((Double.valueOf(moneyMarketData.get("shares")) * Double.valueOf(moneyMarketData.get("latest_price"))) > cost)) {
			moneyMarket.setShares(new BigDecimal(moneyMarketData.get("shares")));
			moneyMarket.setPortfolioId(Long.valueOf(moneyMarketData.get("portfolio_id")));
			moneyMarket.setHoldingId(Long.valueOf(moneyMarketData.get("holding_id")));
			moneyMarket.setLatestPrice(new BigDecimal(moneyMarketData.get("latest_price")));
		} else {
			// throw exception
			throw new NoSuchElementException("Could not retrieve money market data or there is insufficient funds."); // NSEE
		}
		
		// Is there already a portfolio entry for this holding?
		Long portfolioId = (long) 0;
		Long holdingId = (long) 0;
		Double pShares = 0.0;
		Map<String, String> portfolioFoundData = projectDao.fetchInvestmentByBrokerageIdAndSymbol(brokerageId, symbol);
		if (portfolioFoundData.containsKey("portfolio_id")) {
			portfolioId = Long.valueOf(portfolioFoundData.get("portfolio_id"));
			holdingId = Long.valueOf(portfolioFoundData.get("holding_id"));
			pShares = Double.valueOf(portfolioFoundData.get("shares"));
		}
		
		// if we didn't find a portfolio entry, something went wrong and this is not a PUT
		if (portfolioId == 0) {
			// throw exception
			throw new NoSuchElementException("Did not find portfolio entry. Use POST call instead."); // NSEE
		}

		data.setHoldingId(holdingId);
		data.setLatestPrice(realTimePriceData.get(0).getTngoLast());
		data.setLatestPriceDate(Calendar.getInstance().getTime());
		data.setShares(BigDecimal.valueOf(shares).add(new BigDecimal(pShares)));
		data.setSymbol(symbol);
		data.setPortfolioId(portfolioId);
		// Update the portfolio
		Mono<PortfolioData> dataMono = updatePortfolio(data);
		
		// add transaction
		String brokerageName = projectDao.fetchBrokerageById(brokerageId);
		TransactionData tData = new TransactionData();
		tData.setName(portfolioFoundData.get("name"));
		tData.setSymbol(symbol);
		tData.setShares(BigDecimal.valueOf(shares));
		tData.setType("Buy");
		tData.setDatetime(Calendar.getInstance().getTime());
		tData.setPrice(realTimePriceData.get(0).getTngoLast());
		tData.setBrokerage(brokerageName);
		// Add the transaction
		Mono<TransactionData> tDataMono = createTransaction(tData);
			
		// update money market balance
		Double balance = moneyMarket.getShares().doubleValue() - cost;
		moneyMarket.setShares(new BigDecimal(balance));
		moneyMarket.setLatestPriceDate(Calendar.getInstance().getTime());
		// Update the portfolio
		Mono<PortfolioData> mmmono = updatePortfolio(moneyMarket);

		// add transaction
		TransactionData tDataMM = new TransactionData();
		tDataMM.setName(moneyMarketData.get("name"));
		tDataMM.setShares(new BigDecimal(cost));
		tDataMM.setType("Sell");
		tDataMM.setDatetime(Calendar.getInstance().getTime());
		tDataMM.setPrice(new BigDecimal(moneyMarketData.get("latest_price")));
		tDataMM.setBrokerage(moneyMarketData.get("brokerage_name"));
		// Add the transaction
		Mono<TransactionData> tDataMMMono = createTransaction(tDataMM);
		
		return data;

	}

	// sellPortfolioInvestmentDelete
	public Map<String, String> sellPortfolioInvestmentDelete(Long brokerageId, String symbol, double shares) throws Exception {
		// Get the daily price data based upon the symbol
		PortfolioData data = new PortfolioData();
		List<RealTimePriceData> realTimePriceData = retrieveRealTimePriceData(symbol);
		if (realTimePriceData == null || realTimePriceData.size() < 1) {
			// throw exception
			throw new NoSuchElementException("Could not retrieve daily price for symbol " + symbol); // NSEE
		}
		// Calculate the total cost for the sale
		Double sellValue = shares * realTimePriceData.get(0).getTngoLast().doubleValue();
		
		// Get the money market balance
		PortfolioData moneyMarket = new PortfolioData();
		Map<String, String> moneyMarketData = projectDao.fetchMoneyMarketData(brokerageId);
		if (moneyMarketData.containsKey("shares") ) {
			moneyMarket.setShares(new BigDecimal(moneyMarketData.get("shares")));
			moneyMarket.setPortfolioId(Long.valueOf(moneyMarketData.get("portfolio_id")));
			moneyMarket.setHoldingId(Long.valueOf(moneyMarketData.get("holding_id")));
			moneyMarket.setLatestPrice(new BigDecimal(moneyMarketData.get("latest_price")));
		} else {
			// throw exception
			throw new NoSuchElementException("Could not retrieve money market data."); // NSEE
		}
		
		// Is there already a portfolio entry for this holding?
		Long portfolioId = (long) 0;
		Long holdingId = (long) 0;
		Double pShares = 0.0;
		Map<String, String> portfolioFoundData = projectDao.fetchInvestmentByBrokerageIdAndSymbol(brokerageId, symbol);
		if (portfolioFoundData.containsKey("portfolio_id")) {
			portfolioId = Long.valueOf(portfolioFoundData.get("portfolio_id"));
			holdingId = Long.valueOf(portfolioFoundData.get("holding_id"));
			pShares = Double.valueOf(portfolioFoundData.get("shares"));
		}
		
		// if we didn't find a portfolio entry or we are not selling all the shares,
		// something went wrong or this is not a DELETE
		if (portfolioId == 0 || shares != pShares) {
			// throw exception
			throw new NoSuchElementException("Did not find portfolio entry or not selling exactly all shares. Use PUT call instead."); // NSEE
		}

		data.setHoldingId(holdingId);
		data.setLatestPrice(realTimePriceData.get(0).getTngoLast());
		data.setLatestPriceDate(Calendar.getInstance().getTime());
		data.setShares(BigDecimal.valueOf(0));
		data.setSymbol(symbol);
		data.setPortfolioId(portfolioId);
		// Delete the portfolio row
		deletePortfolio(data);

		// add transaction
		String brokerageName = projectDao.fetchBrokerageById(brokerageId);
		TransactionData tData = new TransactionData();
		tData.setName(portfolioFoundData.get("name"));
		tData.setSymbol(symbol);
		tData.setShares(BigDecimal.valueOf(shares));
		tData.setType("Sell");
		tData.setDatetime(Calendar.getInstance().getTime());
		tData.setPrice(realTimePriceData.get(0).getTngoLast());
		tData.setBrokerage(brokerageName);
		// Add the transaction
		Mono<TransactionData> tDataMono = createTransaction(tData);		
		
		// update money market balance
		Double balance = moneyMarket.getShares().doubleValue() + sellValue;
		moneyMarket.setShares(new BigDecimal(balance));
		moneyMarket.setLatestPriceDate(Calendar.getInstance().getTime());
		// Update the portfolio
		Mono<PortfolioData> mmmono = updatePortfolio(moneyMarket);
	
		// add transaction
		TransactionData tDataMM = new TransactionData();
		tDataMM.setName(moneyMarketData.get("name"));
		tDataMM.setShares(new BigDecimal(sellValue));
		tDataMM.setType("Buy");
		tDataMM.setDatetime(Calendar.getInstance().getTime());
		tDataMM.setPrice(new BigDecimal(moneyMarketData.get("latest_price")));
		tDataMM.setBrokerage(moneyMarketData.get("brokerage_name"));
		// Add the transaction
		Mono<TransactionData> tDataMMMono = createTransaction(tDataMM);		
		
		return Map.of("message", "Portfolio with ID = " + portfolioId + " deleted.");
	}

	// sellPortfolioInvestmentPut
	public PortfolioData sellPortfolioInvestmentPut(Long brokerageId, String symbol, double shares) throws Exception {
		// Get the daily price data based upon the symbol
		PortfolioData data = new PortfolioData();
		List<RealTimePriceData> realTimePriceData = retrieveRealTimePriceData(symbol);
		if (realTimePriceData == null || realTimePriceData.size() < 1) {
			// throw exception
			throw new NoSuchElementException("Could not retrieve daily price for symbol " + symbol); // NSEE
		}
		// Calculate the total cost for the sale
		Double sellValue = shares * realTimePriceData.get(0).getTngoLast().doubleValue();
		
		// Get the money market balance
		PortfolioData moneyMarket = new PortfolioData();
		Map<String, String> moneyMarketData = projectDao.fetchMoneyMarketData(brokerageId);
		if (moneyMarketData.containsKey("shares") ) {
			moneyMarket.setShares(new BigDecimal(moneyMarketData.get("shares")));
			moneyMarket.setPortfolioId(Long.valueOf(moneyMarketData.get("portfolio_id")));
			moneyMarket.setHoldingId(Long.valueOf(moneyMarketData.get("holding_id")));
			moneyMarket.setLatestPrice(new BigDecimal(moneyMarketData.get("latest_price")));
		} else {
			// throw exception
			throw new NoSuchElementException("Could not retrieve money market data."); // NSEE
		}
		
		// Is there already a portfolio entry for this holding?
		Long portfolioId = (long) 0;
		Long holdingId = (long) 0;
		Double pShares = 0.0;
		Map<String, String> portfolioFoundData = projectDao.fetchInvestmentByBrokerageIdAndSymbol(brokerageId, symbol);
		if (portfolioFoundData.containsKey("portfolio_id")) {
			portfolioId = Long.valueOf(portfolioFoundData.get("portfolio_id"));
			holdingId = Long.valueOf(portfolioFoundData.get("holding_id"));
			pShares = Double.valueOf(portfolioFoundData.get("shares"));
		}
		
		// if we didn't find a portfolio entry or we are selling all the shares,
		// something went wrong and this is not a DELETE
		if (portfolioId == 0 || pShares <= shares) {
			// throw exception
			throw new NoSuchElementException("Did not find portfolio entry. Use DELETE call instead."); // NSEE
		}

		data.setHoldingId(holdingId);
		data.setLatestPrice(realTimePriceData.get(0).getTngoLast());
		data.setLatestPriceDate(Calendar.getInstance().getTime());
		data.setShares(BigDecimal.valueOf(pShares).subtract(new BigDecimal(shares)));
		data.setSymbol(symbol);
		data.setPortfolioId(portfolioId);
		// Update the portfolio
		Mono<PortfolioData> dataMono = updatePortfolio(data);
	
		// add transaction
		String brokerageName = projectDao.fetchBrokerageById(brokerageId);
		TransactionData tData = new TransactionData();
		tData.setName(portfolioFoundData.get("name"));
		tData.setSymbol(symbol);
		tData.setShares(BigDecimal.valueOf(shares));
		tData.setType("Sell");
		tData.setDatetime(Calendar.getInstance().getTime());
		tData.setPrice(realTimePriceData.get(0).getTngoLast());
		tData.setBrokerage(brokerageName);
		// Add the transaction
		Mono<TransactionData> tDataMono = createTransaction(tData);		
		
		// update money market balance
		Double balance = moneyMarket.getShares().doubleValue() + sellValue;
		moneyMarket.setShares(new BigDecimal(balance));
		moneyMarket.setLatestPriceDate(Calendar.getInstance().getTime());
		// Update the portfolio
		Mono<PortfolioData> mmmono = updatePortfolio(moneyMarket);
	
		// add transaction
		TransactionData tDataMM = new TransactionData();
		tDataMM.setName(moneyMarketData.get("name"));
		tDataMM.setShares(new BigDecimal(sellValue));
		tDataMM.setType("Buy");
		tDataMM.setDatetime(Calendar.getInstance().getTime());
		tDataMM.setPrice(new BigDecimal(moneyMarketData.get("latest_price")));
		tDataMM.setBrokerage(moneyMarketData.get("brokerage_name"));
		// Add the transaction
		Mono<TransactionData> tDataMMMono = createTransaction(tDataMM);		
		
		return data;

	}
	
}
