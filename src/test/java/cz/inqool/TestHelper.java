package cz.inqool;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cz.inqool.kurt.domain.Kurt;
import cz.inqool.kurt.domain.KurtDto;
import cz.inqool.povrch.domain.Povrch;
import cz.inqool.povrch.domain.PovrchDto;
import cz.inqool.rezervace.domain.Rezervace;
import cz.inqool.rezervace.domain.RezervaceDto;
import cz.inqool.rezervace.domain.RezervaceEditaceDto;
import cz.inqool.zakaznik.domain.Zakaznik;
import cz.inqool.zakaznik.domain.ZakaznikDto;

class TestHelper {

    public static Povrch createPovrch(Integer id, String nazev, BigDecimal cena, Boolean platnost) {
        Povrch povrch = new Povrch();
        povrch.setId(id);
        povrch.setNazev(nazev);
        povrch.setCena(cena);
        povrch.setPlatnost(platnost);
        return povrch;
    }

    public static PovrchDto createPovrchDto(Integer id, String nazev, BigDecimal cena, Boolean platnost) {
        return PovrchDto.builder()
                .id(id)
                .nazev(nazev)
                .cena(cena)
                .platnost(platnost)
                .build();
    }

    public static Kurt createKurt(Integer id, String nazev, Povrch povrch, Boolean platnost) {
        Kurt kurt = new Kurt();
        kurt.setId(id);
        kurt.setNazev(nazev);
        kurt.setPovrch(povrch);
        kurt.setPlatnost(platnost);
        return kurt;
    }

    public static KurtDto createKurtDto(Integer id, String nazev, PovrchDto povrch, Boolean platnost) {
        return KurtDto.builder()
                .id(id)
                .nazev(nazev)
                .povrch(povrch)
                .platnost(platnost)
                .build();
    }
    
    public static Zakaznik createZakaznik(Integer id, String telefon, String celeJmeno, Boolean platnost) {
	    Zakaznik zakaznik = new Zakaznik();
	    zakaznik.setId(id);
	    zakaznik.setTelefon(telefon);
	    zakaznik.setCeleJmeno(celeJmeno);
	    zakaznik.setPlatnost(platnost);
	    return zakaznik;
	}
	
    public static ZakaznikDto createZakaznikDto(Integer id, String telefon, String celeJmeno, Boolean platnost) {
	    return ZakaznikDto.builder()
	            .id(id)
	            .telefon(telefon)
	            .celeJmeno(celeJmeno)
	            .platnost(platnost)
	            .build();
	}
    
    public static Rezervace createRezervace(Integer id, Kurt kurt, Zakaznik zakaznik, LocalDateTime datumOd, LocalDateTime datumDo, Boolean jeCtyrHra, BigDecimal cenaPronajem, Boolean platnost) {
        Rezervace rezervace = new Rezervace();
        rezervace.setId(id);
        rezervace.setKurt(kurt);
        rezervace.setZakaznik(zakaznik);
        rezervace.setDatumOd(datumOd);
        rezervace.setDatumDo(datumDo);
        rezervace.setJeCtyrHra(jeCtyrHra);
        rezervace.setCenaPronajem(cenaPronajem);
        rezervace.setPlatnost(platnost);
        return rezervace;
    }
    
    public static RezervaceDto createRezervaceDto(Integer id, Boolean platnost, KurtDto kurt, ZakaznikDto zakaznik, LocalDateTime datumOd, LocalDateTime datumDo, Boolean jeCtyrHra, BigDecimal cenaPronajem) {
        return RezervaceDto.builder()
                .id(id)
                .kurt(kurt)
                .zakaznik(zakaznik)
                .datumOd(datumOd)
                .datumDo(datumDo)
                .jeCtyrHra(jeCtyrHra)
                .cenaPronajem(cenaPronajem)
                .platnost(platnost)
                .build();
    }
    
    public static RezervaceEditaceDto createRezervaceEditaceDto(Integer id, Boolean platnost, Integer kurtId, String telefon, String celeJmeno, LocalDateTime datumOd, LocalDateTime datumDo, Boolean jeCtyrHra) {
        return RezervaceEditaceDto.builder()
                .id(id)
                .platnost(platnost)
                .kurtId(kurtId)
                .telefon(telefon)
                .celeJmeno(celeJmeno)
                .datumOd(datumOd)
                .datumDo(datumDo)
                .jeCtyrHra(jeCtyrHra)
                .build();
    }
}
