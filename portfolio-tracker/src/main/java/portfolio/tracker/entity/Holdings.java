package portfolio.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Holdings {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long holdingId;
	@Column(name ="investment_id")
	private long investmentId;
	@Column(name ="brokerage_id")
	private long brokerageId;
}
