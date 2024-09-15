package cz.inqool.zakaznik.domain;

import java.io.Serializable;

import cz.inqool.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entita Zákaznik 
 */
@Entity
@Table(name = "zakaznik")
@Data
@EqualsAndHashCode(callSuper=true)
public class Zakaznik extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1057647566608473124L;

	@Column(name = "telefon")
	private String telefon;
	
	@Column(name = "celejmeno")
	private String celeJmeno;
}
