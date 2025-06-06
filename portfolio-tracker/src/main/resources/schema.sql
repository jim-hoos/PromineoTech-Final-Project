CREATE TABLE IF NOT EXISTS investment_types (
	investment_type_id int NOT NULL AUTO_INCREMENT,
	name varchar(128) NOT NULL,
	PRIMARY KEY (investment_type_id)
);

CREATE TABLE IF NOT EXISTS investments (
	investment_id int NOT NULL AUTO_INCREMENT,
	name varchar(256) NOT NULL,
	symbol varchar(64),
	investment_type_id int NOT NULL,
	PRIMARY KEY (investment_id),
	FOREIGN KEY (investment_type_id) REFERENCES investment_types (investment_type_id)
);

CREATE TABLE IF NOT EXISTS brokerages (
	brokerage_id int NOT NULL AUTO_INCREMENT,
	name varchar(128),
	PRIMARY KEY (brokerage_id)
);

CREATE TABLE IF NOT EXISTS holdings (
	holding_id int NOT NULL AUTO_INCREMENT,
	investment_id int NOT NULL,
	brokerage_id int NOT NULL,
	PRIMARY KEY (holding_id),
	FOREIGN KEY (investment_id) REFERENCES investments (investment_id),
	FOREIGN KEY (brokerage_id) REFERENCES brokerages (brokerage_id)
);

CREATE TABLE IF NOT EXISTS portfolio (
	portfolio_id int NOT NULL AUTO_INCREMENT,
	holding_id int NOT NULL,
	shares decimal(10,2) NOT NULL,
	latest_price_date date NOT NULL,
	latest_price decimal(10,2) NOT NULL,
	PRIMARY KEY (portfolio_id),
	FOREIGN KEY (holding_id) REFERENCES holdings (holding_id)
);

CREATE TABLE IF NOT EXISTS daily_prices (
	daily_price_id int NOT NULL AUTO_INCREMENT,
	symbol varchar(64) NOT NULL,
	adjClose decimal(10,2) NOT NULL,
	`close` decimal(10,2) NOT NULL,
	`date` date NOT NULL DEFAULT (CURRENT_DATE()),
	high decimal(10,2) NOT NULL,
	low decimal(10,2) NOT NULL,
	`open` decimal(10,2) NOT NULL,
	volume bigint NOT NULL,
	PRIMARY KEY (daily_price_id),
	UNIQUE KEY `daily_prices_symbol_date` (`symbol`, `date`)
);

CREATE TABLE IF NOT EXISTS transactions (
	transaction_id int NOT NULL AUTO_INCREMENT,
	symbol varchar(64),
	name varchar(256) NOT NULL,
	type varchar(32) NOT NULL,
	brokerage varchar(128),
	shares decimal(10,2) NOT NULL,
	price decimal(10,2) NOT NULL,
	`datetime` datetime NOT NULL DEFAULT (CURRENT_TIMESTAMP()),
	PRIMARY KEY (transaction_id)
);