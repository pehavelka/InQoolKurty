package cz.inqool.kurt.domain;

import java.io.Serializable;

import cz.inqool.core.BaseEntity;
import cz.inqool.povrch.domain.Povrch;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entita Kurt
 */
@Entity
@Table(name = "kurt")
@Data
@EqualsAndHashCode(callSuper=true)
public class Kurt extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -8148775703607632308L;

	@ManyToOne
	@JoinColumn(name = "refpovrch")
	private Povrch povrch;
	
	@Column(name = "nazev")
	private String nazev;
}
