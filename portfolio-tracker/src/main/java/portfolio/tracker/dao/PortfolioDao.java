package portfolio.tracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import portfolio.tracker.entity.Portfolio;

public interface PortfolioDao extends JpaRepository<Portfolio, Long> {

}
