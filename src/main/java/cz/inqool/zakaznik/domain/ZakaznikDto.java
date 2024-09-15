package cz.inqool.zakaznik.domain;

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
public class ZakaznikDto {

	private Integer id;
	private String zmenaUzivatel;
	private LocalDateTime zmenaCas;
	private Boolean platnost;
	
	private String telefon;
	private String celeJmeno;
	
	public static ZakaznikDto of(final Zakaznik ent) {
		return ZakaznikDto
				.builder()
				.id(ent.getId())
				.zmenaUzivatel(ent.getZmenaUzivatel())
				.zmenaCas(ent.getZmenaCas())
				.platnost(ent.getPlatnost())
				.telefon(ent.getTelefon())
				.celeJmeno(ent.getCeleJmeno())
				.build();
	}
}
