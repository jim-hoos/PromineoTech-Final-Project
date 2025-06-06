package portfolio.tracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import portfolio.tracker.entity.Investments;

public interface InvestmentDao extends JpaRepository<Investments, Long> {

}
