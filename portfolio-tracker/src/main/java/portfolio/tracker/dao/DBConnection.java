package portfolio.tracker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import portfolio.tracker.configuration.DataSourceConfig;

@Component
//@ConfigurationProperties(prefix = "spring.datasource")
public class DBConnection {
//	@Value("${spring.datasource.url}")
//	private static  String url;
//	
//	@Value("${spring.datasource.username}")
//	private static String username;
//	
//	@Value("${spring.datasource.password}")
//	private static String password;
	
//	private static DataSourceConfig dataSourceConfig;
	
	public static Connection getConnection() throws Exception {
		try {
//			dataSourceConfig = new DataSourceConfig();
//			String url = String.format("%s?user=%s&password=%s&useSSL=false", dataSourceConfig.getUrl(),
//					dataSourceConfig.getUsername(), dataSourceConfig.getPassword());
			String url = String.format("%s?user=%s&password=%s&useSSL=false", "jdbc:mysql://localhost:3306/portfolio_tracker", "portfolio_tracker", "portfolio_tracker");
			Connection conn = DriverManager.getConnection(url);
			return conn;
		} catch (SQLException e) {
			System.out.println("Error getting connection.");
			throw new Exception(e);
		}
	}
}
