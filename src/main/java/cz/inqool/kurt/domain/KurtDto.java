package cz.inqool.kurt.domain;

import java.time.LocalDateTime;

import cz.inqool.povrch.domain.PovrchDto;
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
public class KurtDto {

	private Integer id;
	private String zmenaUzivatel;
	private LocalDateTime zmenaCas;
	private Boolean platnost;

	private PovrchDto povrch;
	private String nazev;
	
	public static KurtDto of(final Kurt ent) {
		return KurtDto
				.builder()
				.id(ent.getId())
				.zmenaUzivatel(ent.getZmenaUzivatel())
				.zmenaCas(ent.getZmenaCas())
				.platnost(ent.getPlatnost())
				.povrch(PovrchDto.of(ent.getPovrch()))
				.nazev(ent.getNazev())
				.build();
	}
}
