PORTFOLIO_TRACKER PROJECT
-------------------------------
JIm Hoos
June 4, 2025

Promineo Final Project Requirements:
- Database design which contains at least 3 entities and 3 tables
	7 entities and 7 tables
- Contains all CRUD operations (Create, Read, Update & Delete)
	All 4 CRUD ops are represented
- Each entity should have at least one CRUD operation
	All 7 tables/entities contain at least one CRUD op each
- AND:  one or more entities need to have all 4 CRUD operations (Create, Read, Update & Delete).
	Portfolio table/entity contains all 4 CRUD ops
- Contains at least 1 one-to-many relationship
	One to many relationship between InvestmentTypes to Investments
- Contains at least 1 many-to-many relationship with one or more CRUD operations on this relationship
	Many to many relationship between Investments to Brokerages
- Required:  REST Web API Server tested through Swagger, Postman or AdvancedRestClient (ARC) or a front-end client.
	Video includes AdvancedRestClient usage


Basic Functionality:
- View individual and overall portfolio holdings
- Buy/Sell securities (Stocks, ETFs, Mutual Funds) based upon real-time prices
- Recorded and viewable transaction history - not shown in video due to time constraints
- Ability to save and display historical security price data - not shown in video due to time constraints
- CRUD operations on all tables

Disclaimers:
- Orders are "market" only and based upon latest real-time prices
- Orders are "paper" transactions only - not with a traders license through an exchange
- Money market account is considered cash account and therefore can only have one per brokerage
- Trading is open 24 hours/day
- Limited investment types (no Options, futures, derivatives, etc.) and they are explicitly defined
- Limited brokerages - hardcoded to five
- No dividends or interest accumulated
- Project is endpoint driven so functionality resides inside endpoints whereas most of the code would
	normally be outside of endpoints (see buy & sell endpoints, for example).
- All functionality has been tested but time does not allow it all to be demonstrated in a five min. video.
- Added print to console for a few select endpoint calls to make the display output more user friendly
	since no front end is available.
- Not designed for performance - given more time would make changes.
- URIs could be more intuitive but chose brevity instead of verboseness.
	
ToDo & Future Enhancements:
- Add more comments
- Address "TODO" reference comments in the code
- Remove 'block' calls inside WebClient/react code
- Add a new table for research which could items like a 200 day moving average
- Add a front end using React which could include a portfolio table, graphs for historical performance,
	forms to enter buy/sell transactions.
