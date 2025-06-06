package portfolio.tracker.entity;

import java.util.HashSet;
import java.util.Set;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class InvestmentTypes {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="investment_type_id") //
	private Long investmentTypeId;
	private String name;

	@OneToMany(mappedBy = "investmentTypes", cascade = CascadeType.ALL, orphanRemoval = true) //
	private Set<Investments> investments = new HashSet<>(); //
}
