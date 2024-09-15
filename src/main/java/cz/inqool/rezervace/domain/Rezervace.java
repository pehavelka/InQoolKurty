package cz.inqool.rezervace.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import cz.inqool.core.BaseEntity;
import cz.inqool.kurt.domain.Kurt;
import cz.inqool.zakaznik.domain.Zakaznik;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entita Rezervace 
 */
@Entity
@Table(name = "rezervace")
@Data
@EqualsAndHashCode(callSuper=true)
public class Rezervace extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 4387687924255755216L;

	@ManyToOne
	@JoinColumn(name = "refkurt")
	private Kurt kurt;
	
	@ManyToOne
	@JoinColumn(name="refzakaznik")
	private Zakaznik zakaznik;
	
	@Column(name = "datumod")
	private LocalDateTime datumOd;
	
	@Column(name = "datumdo")
	private LocalDateTime datumDo;
	
	@Column(name = "ctyrhra")
	private Boolean jeCtyrHra;
	
	@Column(name = "cenapronajem")
	private BigDecimal cenaPronajem;
}
