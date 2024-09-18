package cz.inqool.core;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

/**
 * Předek entit
 */
@MappedSuperclass //nejedná se o entitu
@Data
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "zmenacas")
	private LocalDateTime zmenaCas;
	
	@Column(name = "zmenauzivatel")
	private String zmenaUzivatel;
	
	@Column(name = "platnost")
	private Boolean platnost;
}
