INSERT INTO investment_types (name)
SELECT name
FROM (values
  ROW('Stock'),
  ROW('Mutual Fund'),
  ROW('Bond'),
  ROW('ETF'),
  ROW('Money Market')
 ) AS tvc (name)
WHERE NOT exists (
  SELECT 1
  FROM investment_types it
  WHERE it.name=tvc.name
);

INSERT INTO investments (name, symbol, investment_type_id)
SELECT name, symbol, investment_type_id
FROM (values
  ROW('Microsoft', 'MSFT', 1),
  ROW('Disney', 'DIS', 1),
  ROW('Amazon', 'AMZN', 1),
  ROW('Vanguard Total Stock Market Index Fund', 'VTSAX', 2),
  ROW('Fidelity 500 Index Fund', 'FXAIX', 2),
--  ROW('Boeing Bond', 'BA4983328', 3),
--  ROW('UnitedHealth Group Bond', 'UNH5856556', 3),
  ROW('Invesco QQQ Trust', 'QQQ', 4),
  ROW('SPDR S&P ETF Trust', 'SPY', 4),
  ROW('Charles Schwab Money Market', NULL, 5),
  ROW('Fidelity Money Market', NULL, 5)
 ) AS tvc (name, symbol, investment_type_id)
WHERE NOT exists (
  SELECT 1
  FROM investments i
  WHERE i.name=tvc.name
);

INSERT INTO brokerages (name)
SELECT name
FROM (values
  ROW('Charles Schwab'),
  ROW('Fidelity'),
  ROW('Vanguard'),
  ROW('BlackRock'),
  ROW('JP Morgan Chase')
 ) AS tvc (name)
WHERE NOT exists (
  SELECT 1
  FROM brokerages b
  WHERE b.name=tvc.name
);

INSERT INTO holdings (brokerage_id, investment_id)
SELECT brokerage_id, investment_id
FROM (values
  ROW(1, 1),
  ROW(1, 2),
  ROW(1, 6),
  ROW(1, 8),
  ROW(2, 1),
  ROW(2, 3),
  ROW(2, 5),
  ROW(2, 7),
  ROW(2, 9),
  ROW(3, 4),
  ROW(4, 4)
 ) AS tvc (brokerage_id, investment_id)
WHERE NOT exists (
  SELECT 1
  FROM holdings h
  WHERE h.brokerage_id=tvc.brokerage_id
);

INSERT INTO portfolio (holding_id, shares, latest_price_date, latest_price)
SELECT holding_id, shares, latest_price_date, latest_price
FROM (values
  ROW(1, 40.0, CURDATE(), 467.68),
  ROW(2, 50.0, CURDATE(), 112.53),
  ROW(3, 10.0, CURDATE(), 524.79),
  ROW(4, 5000.0, CURDATE(), 1.00),
  ROW(5, 50.0, CURDATE(), 467.68),
  ROW(6, 25.0, CURDATE(), 207.91),
  ROW(7, 30.0, CURDATE(), 207.75),
  ROW(8, 20.0, CURDATE(), 593.05),
  ROW(9, 10000.0, CURDATE(), 1.00),
  ROW(10, 65.0, CURDATE(), 142.80),
  ROW(11, 35.0, CURDATE(), 142.80)
 ) AS tvc (holding_id, shares, latest_price_date, latest_price)
WHERE NOT exists (
  SELECT 1
  FROM portfolio p
  WHERE p.holding_id=tvc.holding_id
);
