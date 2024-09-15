package cz.inqool.povrch.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PovrchDto {
	
	private Integer id;
	private String zmenaUzivatel;
	private LocalDateTime zmenaCas;
	private Boolean platnost;

	private String nazev;
	private BigDecimal cena;
	
	public static PovrchDto of(final Povrch ent) {
		return PovrchDto
				.builder()
				.id(ent.getId())
				.zmenaUzivatel(ent.getZmenaUzivatel())
				.zmenaCas(ent.getZmenaCas())
				.platnost(ent.getPlatnost())
				.nazev(ent.getNazev())
				.cena(ent.getCena())
				.build();
	}
}
