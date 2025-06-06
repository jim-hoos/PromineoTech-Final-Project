package portfolio.tracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import portfolio.tracker.entity.Holdings;

public interface HoldingDao extends JpaRepository<Holdings, Long> {

}
