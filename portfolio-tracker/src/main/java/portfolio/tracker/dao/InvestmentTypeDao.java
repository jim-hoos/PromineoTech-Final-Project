package portfolio.tracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import portfolio.tracker.entity.InvestmentTypes;

public interface InvestmentTypeDao extends JpaRepository<InvestmentTypes, Long>{

}
