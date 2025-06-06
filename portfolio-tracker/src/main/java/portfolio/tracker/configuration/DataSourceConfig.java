package portfolio.tracker.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@EnableConfigurationProperties
public class DataSourceConfig {
	private String username;
	private String password;
	private String url;
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUsername(String username) {
		username = this.username;
	}
	
	public void setPassword(String password) {
		password = this.password;
	}
	
	public void setUrl(String url) {
		url = this.url;
	}
	
	public DataSourceConfig() {
		
	}
}
