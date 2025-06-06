package portfolio.tracker.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "investments") //
@Data
public class Investments {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long investmentId;
	@EqualsAndHashCode.Exclude
	private String name;
	@EqualsAndHashCode.Exclude
	private String symbol;
	
	@EqualsAndHashCode.Exclude //
	@ToString.Exclude          //
	@ManyToOne                 //
	@JoinColumn(name = "investment_type_id", nullable = false) //
	private InvestmentTypes investmentTypes; //
	
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(
		name = "holdings",
		joinColumns = @JoinColumn(name = "investment_id"),
		inverseJoinColumns = @JoinColumn(name = "brokerage_id")
	)
	private Set<Brokerages> brokerages = new HashSet<>();
}
