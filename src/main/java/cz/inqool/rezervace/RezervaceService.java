package cz.inqool.rezervace;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.inqool.core.ErrorCollector;
import cz.inqool.kurt.KurtRepository;
import cz.inqool.kurt.domain.Kurt;
import cz.inqool.rezervace.domain.Rezervace;
import cz.inqool.rezervace.domain.RezervaceDto;
import cz.inqool.rezervace.domain.RezervaceEditaceDto;
import cz.inqool.zakaznik.ZakaznikRepository;
import cz.inqool.zakaznik.ZakaznikService;
import cz.inqool.zakaznik.domain.Zakaznik;
import cz.inqool.zakaznik.domain.ZakaznikDto;

@Service
public class RezervaceService {
	
	/**
	 * Násobek 1.5 ceny za čtyřhru
	 */
	public static final BigDecimal CENA_ZA_CTYRHRU = BigDecimal.valueOf(1.5);
	
	@Autowired
	private RezervaceRepository repo;
	
	@Autowired
	private KurtRepository repoKurt;
	
	@Autowired
	private ZakaznikRepository repoZakaznik;
	
	@Autowired
	private ZakaznikService zakaznikService;
	
	/**
	 * Seznam
	 * 
	 * @return
	 */
	public List<RezervaceDto> seznam() {
		return repo.findAll().stream().map(RezervaceDto::of).collect(Collectors.toList());
	}

	/**
	 * Seznam rezervací dle id kurtu
	 * 
	 * @param id
	 * @return
	 */
	public List<RezervaceDto> seznamDleKurtu(Integer id) {
		return repo.findByKurt_IdOrderByDatumOdAsc(id).stream().map(RezervaceDto::of).collect(Collectors.toList());
	}
	
	/**
	 * Seznam rezervací dle telefonu
	 * 
	 * @param telefon
	 * @param budouci 1 - zobrazí jen rezervace do budoucnosti
	 * @return
	 */
	public List<RezervaceDto> seznamDleTelefonu(String telefon, Boolean budouci) {
		LocalDateTime datumOd = Boolean.TRUE.equals(budouci) ? LocalDateTime.now() : LocalDateTime.of(1970, 1, 1, 0, 0); // MIN nejde
		
		return repo.findByZakaznik_TelefonAndDatumOdGreaterThanEqualOrderByDatumOdAsc(telefon, datumOd).stream().map(RezervaceDto::of).collect(Collectors.toList());
	}
	
	/**
	 * Detail
	 * 
	 * @param id
	 * @return
	 */
	public Optional<RezervaceDto> detail(final Integer id) {
		return repo.findById(id).map(RezervaceDto::of);
	}
	
	/**
	 * Kontroly nový, změna
	 * 
	 * @param dto
	 * @param novy
	 * @return
	 */
	public void kontroly(final RezervaceEditaceDto dto, final ErrorCollector errs, final boolean novy) {
		Optional<Kurt> kurt = Optional.empty();
		if (!novy) {
			if (dto.getId() == null) {
				errs.AddChyba("Údaj ID není zadán.");
		    }
	
		    if (dto.getPlatnost() == null) {
		    	errs.AddChyba("Údaj Platnost není zadán.");
		    }
		}

	    if (dto.getKurtId() == null) {
	    	errs.AddChyba("Údaj Kurt není zadán.");
	    } else {
	    	kurt = repoKurt.findById(dto.getKurtId());
	    	if (kurt.isEmpty()) {
	    		errs.AddChyba("ID kurtu neexistuje.");
	    	} else {
	    		if (!kurt.get().getPlatnost()) {
	    			errs.AddVarovani(String.format("Kurt %s není platný.", kurt.get().getNazev()));
	    		}
	    	}
	    }

	    if (dto.getTelefon() == null) {
	    	errs.AddChyba("Údaj Telefon není zadán.");
	    }
	    
	    if (dto.getCeleJmeno() == null) {
	    	errs.AddChyba("Údaj Celé jméno není zadán.");
	    }
	    
	    if (dto.getDatumOd() == null) {
	    	errs.AddChyba("Údaj Datum od není zadán.");
	    }
	    
	    if (dto.getDatumOd().isBefore(LocalDateTime.now())) {
	    	errs.AddChyba("Údaj Datum od nesmí být do minulosti.");
	    }
	    
	    if (dto.getDatumDo() == null) {
	    	errs.AddChyba("Údaj Datum do není zadán.");
	    }
	    
	    if (dto.getDatumDo().isBefore(dto.getDatumOd())) {
	    	errs.AddChyba("Údaj Datum do je menší než Datum Od.");
	    }

	    // kontrola překrytí rezervací kurtů
	    if (kurt.isPresent()
	    	&& dto.getDatumOd() != null
	    	&& dto.getDatumDo() != null) {
	    	
	    	List<Rezervace> prekryteRezervace = null;
	    	
	    	if (novy) {
	    		prekryteRezervace = repo.findByKurtAndDatumDoGreaterThanEqualAndDatumOdLessThanEqual(kurt.get(), dto.getDatumOd(), dto.getDatumDo());
	    	} else {
	    		prekryteRezervace = repo.findByIdNotAndKurtAndDatumDoGreaterThanEqualAndDatumOdLessThanEqual(dto.getId(), kurt.get(), dto.getDatumOd(), dto.getDatumDo());
	    	}
	    	
	    	prekryteRezervace.forEach(r -> {
	    		errs.AddChyba(String.format("Rezervace kurtu s id %s se překrývá s rezervací id %s.", dto.getKurtId(), r.getId()));
	    	});
	    }
	    
	    if (dto.getId() != null && repo.findById(dto.getId()).isEmpty()) {
		    errs.AddChyba(String.format("Záznam s id %s nenalezen.", dto.getId()));
	    }
	}
	
	/**
	 * Uložit - nový
	 * 
	 * @param dto
	 * @return
	 */
	public RezervaceDto ulozitNovy(final RezervaceEditaceDto dto) {
		
		// vytvoření osoby dle telefonu, když neexistuje
		ulozeniZakaznika(dto);
		
		// výpočet pronájmu
		vypocetPronajmu(dto);

		// uložení rezervace
		Rezervace ent = new Rezervace();
		dtoToEntity(dto, ent);
		ent = repo.save(ent);
		
		return RezervaceDto.of(ent);
	}
	
	/**
	 * Uložit - změna
	 * 
	 * @param dto
	 * @return
	 */
	public RezervaceDto ulozitZmena(final RezervaceEditaceDto dto) {
		
		// vytvoření osoby dle telefonu, když neexistuje
		ulozeniZakaznika(dto);
				
		// výpočet pronájmu
		vypocetPronajmu(dto);
		
		Rezervace ent = repo.findById(dto.getId()).get();
		dtoToEntity(dto, ent);
		ent = repo.save(ent);
		
		return RezervaceDto.of(ent);
	}
	
	/**
	 * Uložení zákazníka, když neexistuje.
	 * Dohledává se dle telefonního čísla
	 * 
	 * @param dto
	 */
	private void ulozeniZakaznika(final RezervaceEditaceDto dto) {
		Optional<Zakaznik> z = repoZakaznik.findByTelefon(dto.getTelefon());
		if (z.isEmpty()) {
			ZakaznikDto zakaznik = ZakaznikDto.builder()
				.telefon(dto.getTelefon())
				.celeJmeno(dto.getCeleJmeno())
				.build();
			zakaznikService.ulozitNovy(zakaznik);
		} else {
			if (dto.getCeleJmeno() != null && !dto.getCeleJmeno().equals(z.get().getCeleJmeno())) {
				ZakaznikDto zakaznik = ZakaznikDto.builder()
						.telefon(dto.getTelefon())
						.celeJmeno(dto.getCeleJmeno())
						.id(z.get().getId())
						.platnost(true)
						.build();
					zakaznikService.ulozitZmena(zakaznik);
			}
		}
		
	}
	
	/**
	 * Výpočet ceny pronájmu za kurt.
	 * V případě čtyřhry je cena násobkem 1,5
	 * 
	 * @param dto
	 */
	private void vypocetPronajmu(final RezervaceEditaceDto dto) {
		// kurt je v tabulce not null
		Kurt kurt = repoKurt.findById(dto.getKurtId()).get();
		
		// Spočítáme rozdíl mezi daty v minutách
	    long dobaPronajmu = Duration.between(dto.getDatumOd(), dto.getDatumDo()).toMinutes();
      
	    BigDecimal cenaPronajem = kurt.getPovrch().getCena().multiply(BigDecimal.valueOf(dobaPronajmu));
	    
	    if (dto.getJeCtyrHra()) {
	    	cenaPronajem = cenaPronajem.multiply(CENA_ZA_CTYRHRU);
	    }
	    dto.setCenaPronajem(cenaPronajem);		
	}
	
	
	/**
	 * Uložit - zneplatnit
	 * 
	 * @param id
	 * @return
	 */
	public RezervaceDto ulozitZneplatnit(final Integer id) {
		Rezervace ent = repo.findById(id).get();
		ent.setPlatnost(false);
		ent = repo.save(ent);
		
		return RezervaceDto.of(ent);
	}
	
	/**
	 * Kontroly - zneplatnit 
	 * 
	 * @param dto
	 * @return
	 */
	public void kontrolyZneplatnit(final RezervaceDto dto, final ErrorCollector errs) {
		Optional<Rezervace> ent = repo.findById(dto.getId());
		if (ent.isEmpty()) {
			errs.AddChyba(String.format("Záznam s id %s nenalezen.", dto.getId()));
		} else {
			if (!ent.get().getPlatnost()) {
				errs.AddChyba(String.format("Záznam s id %s je neplatný.", dto.getId()));
			}
		}
	}

	/**
	 * Převede DTO na entitu
	 * 
	 * @param dto
	 * @param ent
	 */
	private void dtoToEntity(final RezervaceEditaceDto dto, final Rezervace ent) {
		ent.setZmenaCas(LocalDateTime.now());
		ent.setZmenaUzivatel("user"); //TODO tahat z JWT token
		ent.setPlatnost(dto.getPlatnost() == null? true : dto.getPlatnost()); // defualtní hodnota pro nový záznam true, při změně se kontroluje
		ent.setKurt(repoKurt.findById(dto.getKurtId()).get());
		ent.setZakaznik(repoZakaznik.findByTelefon(dto.getTelefon()).get());
		ent.setDatumOd(dto.getDatumOd());
		ent.setDatumDo(dto.getDatumDo());
		ent.setJeCtyrHra(dto.getJeCtyrHra());
		ent.setCenaPronajem(dto.getCenaPronajem());
	}
}
