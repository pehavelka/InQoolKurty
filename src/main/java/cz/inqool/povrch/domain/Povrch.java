package cz.inqool.povrch.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import cz.inqool.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entita Povrch 
 */
@Entity
@Table(name = "povrch")
@Data
@EqualsAndHashCode(callSuper=true)
public class Povrch extends BaseEntity implements Serializable  {

	private static final long serialVersionUID = 6098594904370161990L;

	@Column(name = "nazev")
	private String nazev;
	
	@Column(name = "cena")
	private BigDecimal cena;
}
