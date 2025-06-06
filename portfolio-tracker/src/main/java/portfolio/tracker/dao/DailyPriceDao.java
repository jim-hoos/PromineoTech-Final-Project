package portfolio.tracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import portfolio.tracker.entity.DailyPrices;

public interface DailyPriceDao extends JpaRepository<DailyPrices, Long> {

}
