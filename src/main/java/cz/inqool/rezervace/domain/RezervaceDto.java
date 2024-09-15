package cz.inqool.rezervace.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cz.inqool.kurt.domain.KurtDto;
import cz.inqool.zakaznik.domain.ZakaznikDto;
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
public class RezervaceDto {

	private Integer id;
	private String zmenaUzivatel;
	private LocalDateTime zmenaCas;
	private Boolean platnost;

	private KurtDto kurt;
	private ZakaznikDto zakaznik;
	private LocalDateTime datumOd;
	private LocalDateTime datumDo;
	private Boolean jeCtyrHra;
	private BigDecimal cenaPronajem;
	
	public static RezervaceDto of(final Rezervace ent) {
		return RezervaceDto
				.builder()
				.id(ent.getId())
				.zmenaUzivatel(ent.getZmenaUzivatel())
				.zmenaCas(ent.getZmenaCas())
				.platnost(ent.getPlatnost())
				.kurt(KurtDto.of(ent.getKurt()))
				.zakaznik(ZakaznikDto.of(ent.getZakaznik()))
				.datumOd(ent.getDatumOd())
				.datumDo(ent.getDatumDo())
				.jeCtyrHra(ent.getJeCtyrHra())
				.cenaPronajem(ent.getCenaPronajem())
				.build();
	}
}
