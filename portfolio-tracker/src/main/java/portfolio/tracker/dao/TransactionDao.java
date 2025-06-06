package portfolio.tracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.tracker.entity.Transactions;

public interface TransactionDao extends JpaRepository<Transactions, Long> {

}
