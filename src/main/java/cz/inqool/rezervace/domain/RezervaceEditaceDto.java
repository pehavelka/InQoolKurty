package cz.inqool.rezervace.domain;

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
public class RezervaceEditaceDto {

	private Integer id;
	private String zmenaUzivatel;
	private LocalDateTime zmenaCas;
	private Boolean platnost;

	private Integer kurtId;
	private String telefon;
	private String celeJmeno;
	private LocalDateTime datumOd;
	private LocalDateTime datumDo;
	private Boolean jeCtyrHra;
	private BigDecimal cenaPronajem;
	
	public static RezervaceEditaceDto of(final Rezervace ent) {
		return RezervaceEditaceDto
				.builder()
				.id(ent.getId())
				.zmenaUzivatel(ent.getZmenaUzivatel())
				.zmenaCas(ent.getZmenaCas())
				.platnost(ent.getPlatnost())
				.kurtId(ent.getKurt().getId())
				.telefon(ent.getZakaznik().getTelefon())
				.celeJmeno(ent.getZakaznik().getCeleJmeno())
				.datumOd(ent.getDatumOd())
				.datumDo(ent.getDatumDo())
				.jeCtyrHra(ent.getJeCtyrHra())
				.cenaPronajem(ent.getCenaPronajem())
				.build();
	}
}
