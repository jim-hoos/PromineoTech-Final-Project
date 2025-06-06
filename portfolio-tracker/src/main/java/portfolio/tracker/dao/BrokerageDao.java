package portfolio.tracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import portfolio.tracker.entity.Brokerages;

public interface BrokerageDao extends JpaRepository<Brokerages, Long> {

}
