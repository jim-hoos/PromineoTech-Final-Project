package portfolio.tracker.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Component;

import portfolio.tracker.dao.DBConnection;
import portfolio.tracker.model.DailyPriceData;
import portfolio.tracker.model.PortfolioData;

@Component
public class ProjectDao {
	private static final String HOLDINGS_TABLE = "holdings";
	private static final String INVESTMENTS_TABLE = "investments";
	private static final String INVESTMENT_TYPES_TABLE = "investment_types";
	private static final String BROKERAGES_TABLE = "brokerages";
	private static final String PORTFOLIO_TABLE = "portfolio";
	private static final String DAILY_PRICES_TABLE = "daily_prices";
	private static final Long   MONEY_MARKET_ID = (long) 5;
	
	public Map<String, String> fetchHoldingDataById(Long holdingId) throws Exception {
		String sql = "SELECT it.name as type_name, i.name as investment_name, i.symbol as symbol_name, b.name as brokerage_name FROM " + HOLDINGS_TABLE + " h "
				+ "JOIN " + INVESTMENTS_TABLE + " i ON h.investment_id = i.investment_id "
				+ "JOIN " + INVESTMENT_TYPES_TABLE + " it ON i.investment_type_id = it.investment_type_id "
				+ "JOIN " + BROKERAGES_TABLE + " b on h.brokerage_id = b.brokerage_id "
				+ "WHERE h.holding_id = ?";
		
		try (Connection conn = DBConnection.getConnection()) {
			
			try {
				Map<String, String> hd = new HashMap<>();
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					stmt.setLong(1, holdingId);
					
					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							hd.put("type_name", rs.getString(1));
							hd.put("investment_name", rs.getString(2));
							hd.put("symbol_name", rs.getString(3));
							hd.put("brokerage_name", rs.getString(4));
						}
					}
				}
				
				return hd;
			} catch (Exception e) {
				throw new Exception(e);
			}
		} catch (SQLException e) {
			throw new Exception(e);
		}

	}
	
	public Map<String, String> fetchMoneyMarketData(Long brokerageId) throws Exception {
		String sql = "SELECT p.*, i.name AS investment_name, b.name AS brokerage_name FROM " + BROKERAGES_TABLE + " b "
				+ "JOIN " + HOLDINGS_TABLE + " h ON b.brokerage_id = h.brokerage_id "
				+ "JOIN " + INVESTMENTS_TABLE + " i ON h.investment_id = i.investment_id "
				+ "JOIN " + PORTFOLIO_TABLE + " p ON h.holding_id = p.holding_id "
				+ "WHERE h.brokerage_id = ? AND i.investment_type_id = ?";
		
		try (Connection conn = DBConnection.getConnection()) {
			try {
				Map<String, String> mmd = new HashMap<>();
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					stmt.setLong(1, brokerageId);
					stmt.setLong(2, MONEY_MARKET_ID);
					
					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							mmd.put("portfolio_id", String.valueOf(rs.getLong(1)));
							mmd.put("holding_id", String.valueOf(rs.getLong(2)));
							mmd.put("shares", String.valueOf(rs.getDouble(3)));
							mmd.put("latest_price_date", String.valueOf(rs.getDate(4)));
							mmd.put("latest_price", String.valueOf(rs.getDouble(5)));
							mmd.put("name", rs.getString(6));
							mmd.put("brokerage_name", rs.getString(7));
						}
					}
				}
				
				return mmd;
			} catch (Exception e) {
				throw new NoSuchElementException("Could not retrieve money market data."); // NSEE
			}
		} catch (SQLException e) {
			throw new SQLException(e); // NSEE
		} catch (Exception e1) {
			throw new NoSuchElementException("Could not retrieve money market data."); // NSEE
		}

	}

	public Map<String, String> fetchInvestmentByBrokerageIdAndSymbol(Long brokerageId, String symbol) throws Exception {
		String sql = "SELECT p.portfolio_id, p.shares, p.latest_price, h.holding_id, i.name FROM " + HOLDINGS_TABLE + " h "
				+ "JOIN " + INVESTMENTS_TABLE + " i ON h.investment_id = i.investment_id "
				+ "JOIN " + PORTFOLIO_TABLE + " p ON h.holding_id = p.holding_id "
				+ "WHERE h.brokerage_id = ? AND i.symbol = ?";
		
		try (Connection conn = DBConnection.getConnection()) {
			try {
				Map<String, String> id = new HashMap<>();
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					stmt.setLong(1, brokerageId);
					stmt.setString(2, symbol);
					
					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							id.put("portfolio_id", String.valueOf(rs.getLong(1)));
							id.put("shares", String.valueOf(rs.getDouble(2)));
							id.put("latest_price", String.valueOf(rs.getDouble(3)));
							id.put("holding_id", String.valueOf(rs.getLong(4)));
							id.put("name", rs.getString(5));
						}
					}
				}
				
				return id;
			} catch (Exception e) {
				throw new Exception(e);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}

	}

	public Long fetchInvestmentBySymbol(String symbol) throws Exception {
		String sql = "SELECT investment_id FROM " + INVESTMENTS_TABLE
				+ " WHERE symbol = ?";
		
		try (Connection conn = DBConnection.getConnection()) {
			try {
				Long investmentId = null;
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					stmt.setString(1, symbol);
					
					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							investmentId = rs.getLong(1);
						}
					}
				}
				
				return investmentId;
			} catch (Exception e) {
				throw new Exception(e);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}

	}
	
	public PortfolioData fetchPortfolioByBrokerageAndSymbol(Long brokerageId, String symbol) throws Exception {
		String sql = "SELECT p.portfolio_id, p.holding_id, p.shares, p.latest_price_date, p.latest_price FROM " + HOLDINGS_TABLE + " h "
				+ "JOIN " + INVESTMENTS_TABLE + " i ON h.investment_id = i.investment_id "
				+ "JOIN " + PORTFOLIO_TABLE + " p ON h.holding_id = p.holding_id "
				+ "WHERE h.brokerage_id = ? AND i.symbol = ?";
		
		try (Connection conn = DBConnection.getConnection()) {
			try {
				PortfolioData portfolio = new PortfolioData();
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					stmt.setLong(1, brokerageId);
					stmt.setString(2, symbol);
					
					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							portfolio.setPortfolioId(rs.getLong(1));
							portfolio.setHoldingId(rs.getLong(2));
							portfolio.setShares(rs.getBigDecimal(3));
							portfolio.setLatestPriceDate(rs.getDate(4));
							portfolio.setLatestPrice(rs.getBigDecimal(5));
						}
					}
				}
				
				if (portfolio.getPortfolioId() != null) {
					return portfolio;
				} else {
					return null;
				}
			} catch (Exception e) {
				throw new Exception(e);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}

	}
	
	public Map<String, Long> fetchHoldingId(Long brokerageId, String symbol) throws Exception {
		String sql = "SELECT h.holding_id FROM " + INVESTMENTS_TABLE + " i "
				+ "JOIN " + HOLDINGS_TABLE + " h ON i.investment_id = h.investment_id "
				+ "WHERE h.brokerage_id = ? AND i.symbol = ?";
		
		try (Connection conn = DBConnection.getConnection()) {
			try {
				Map<String, Long> hd = new HashMap<>();
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					stmt.setLong(1, brokerageId);
					stmt.setString(2, symbol);
					
					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							hd.put("holding_id", rs.getLong(1));
						}
					}
				}
				
				return hd;
			} catch (Exception e) {
				throw new Exception(e);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}

	}
	
	public DailyPriceData insertDailyPrices(DailyPriceData dailyPrices) throws Exception {
		String sql = "INSERT IGNORE INTO " + DAILY_PRICES_TABLE
				+ " (symbol, adjClose, close, date, high, low, open, volume)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DBConnection.getConnection()) {
			conn.setAutoCommit(false);
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, dailyPrices.getSymbol());
				stmt.setBigDecimal(2, (BigDecimal)dailyPrices.getAdjClose());
				stmt.setBigDecimal(3, dailyPrices.getClose());
				stmt.setDate(4, (Date) dailyPrices.getDate());
				stmt.setBigDecimal(5,  dailyPrices.getHigh());
				stmt.setBigDecimal(6, dailyPrices.getLow());
				stmt.setBigDecimal(7, dailyPrices.getOpen());
				stmt.setLong(8, dailyPrices.getVolume());
				
				stmt.executeUpdate();
				
				Integer dpId = getLastInsertId(conn, DAILY_PRICES_TABLE);
				conn.commit();
				dailyPrices.setDailyPriceId((long) dpId);
				
				return dailyPrices;
			} catch (Exception e) {
				conn.rollback();
				throw new Exception(e);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} catch (Exception e1) {
			throw new Exception(e1);
		}

	}

	public List<DailyPriceData> insertDailyRangePrices(List<DailyPriceData> dailyRangePrices) throws Exception {
		String sql = "INSERT IGNORE INTO " + DAILY_PRICES_TABLE
				+ " (symbol, adjClose, close, date, high, low, open, volume)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DBConnection.getConnection()) {
			conn.setAutoCommit(false);
			
			List<DailyPriceData> listDailyPrices = new LinkedList<>();
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				for (DailyPriceData dailyPrices : dailyRangePrices) {
				  stmt.setString(1, dailyPrices.getSymbol());
				  stmt.setBigDecimal(2, (BigDecimal)dailyPrices.getAdjClose());
				  stmt.setBigDecimal(3, dailyPrices.getClose());
				  stmt.setDate(4, (Date) dailyPrices.getDate());
				  stmt.setBigDecimal(5,  dailyPrices.getHigh());
				  stmt.setBigDecimal(6, dailyPrices.getLow());
				  stmt.setBigDecimal(7, dailyPrices.getOpen());
				  stmt.setLong(8, dailyPrices.getVolume());
				
				  stmt.executeUpdate();
				
				  Integer dpId = getLastInsertId(conn, DAILY_PRICES_TABLE);
				
				  dailyPrices.setDailyPriceId((long) dpId);
				  listDailyPrices.add(dailyPrices);
				}
				conn.commit();
				
				return listDailyPrices;
			} catch (Exception e) {
				conn.rollback();
				throw new Exception(e);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} catch (Exception e1) {
			throw new Exception(e1);
		}

	}
	
	public String fetchBrokerageById(Long id) throws Exception {
		String sql = "SELECT name FROM " + BROKERAGES_TABLE
				+ " WHERE brokerage_id = ?";
		
		try (Connection conn = DBConnection.getConnection()) {
			try {
				String name = "";
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					stmt.setLong(1, id);
					
					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							name = rs.getString(1);
						}
					}
				}
				
				return name;
			} catch (Exception e) {
				throw new Exception(e);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}

	}

	public  Map<Long, String> fetchPortfolioIdWithSymbol() throws Exception {
		String sql = "SELECT p.portfolio_id, i.symbol FROM " + PORTFOLIO_TABLE + " p "
				+ "JOIN " + HOLDINGS_TABLE + " h ON p.holding_id = h.holding_id "
				+ "JOIN " + INVESTMENTS_TABLE + " i ON h.investment_id = i.investment_id "
				+ "WHERE i.symbol IS NOT NULL";
		
		try (Connection conn = DBConnection.getConnection()) {
			try {
				Map<Long, String> pm = new HashMap<>();
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					
					try (ResultSet rs = stmt.executeQuery()) {
						while (rs.next()) {
							pm.put(rs.getLong(1), rs.getString(2));
						}
					}
				}
				
				return pm;
			} catch (Exception e) {
				throw new Exception(e);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}

	}
	
	protected Integer getLastInsertId(Connection conn, String table) throws SQLException {
	    String sql = String.format("SELECT LAST_INSERT_ID() FROM %s", table);

		try(Statement stmt = conn.createStatement()) {
	      try(ResultSet rs = stmt.executeQuery(sql)) {
		    if(rs.next()) {
		      return rs.getInt(1);
		    }

	       throw new SQLException("Unable to retrieve the primary key value. No result set!");
		  }
	   }
	}
}
