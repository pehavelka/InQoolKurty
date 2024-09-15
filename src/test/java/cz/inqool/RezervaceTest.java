package cz.inqool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cz.inqool.core.ErrorCollector;
import cz.inqool.kurt.KurtRepository;
import cz.inqool.kurt.KurtService;
import cz.inqool.kurt.domain.Kurt;
import cz.inqool.povrch.PovrchRepository;
import cz.inqool.povrch.PovrchService;
import cz.inqool.povrch.domain.Povrch;
import cz.inqool.rezervace.RezervaceRepository;
import cz.inqool.rezervace.RezervaceService;
import cz.inqool.rezervace.domain.Rezervace;
import cz.inqool.rezervace.domain.RezervaceDto;
import cz.inqool.rezervace.domain.RezervaceEditaceDto;
import cz.inqool.zakaznik.ZakaznikRepository;
import cz.inqool.zakaznik.ZakaznikService;
import cz.inqool.zakaznik.domain.Zakaznik;

class RezervaceTest {

	@Mock
	private KurtRepository kurtRepository;

	@InjectMocks
	private KurtService kurtService;
	
	@InjectMocks
	private PovrchService povrchService;
	
	@Mock
    private PovrchRepository povrchRepository;
	
	@InjectMocks
	private RezervaceService rezervaceService;
	
	@Mock
    private RezervaceRepository rezervaceRepository;
	
	@Mock
    private ZakaznikRepository zakaznikRepository;
	
	@InjectMocks
	private ZakaznikService zakaznikService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	/**
	 * Test Rezervace - seznam
	 */
	@Test
	public void testSeznam() {
		Povrch povrch1 = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt1 = TestHelper.createKurt(1, "Kurt č.1 - tráva", povrch1, true);
	    Zakaznik zakaznik1 = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);
	    Rezervace rezervace1 = TestHelper.createRezervace(1, kurt1, zakaznik1, LocalDateTime.of(2020, 1, 1, 16, 0), LocalDateTime.of(2020, 1, 1, 16, 30), true, BigDecimal.valueOf(450), true);
	    
	    Povrch povrch2 = TestHelper.createPovrch(2, "Antuka", BigDecimal.valueOf(20), true);
	    Kurt kurt2 = TestHelper.createKurt(2, "Kurt č.2 - antuka", povrch2, true);
	    Zakaznik zakaznik2 = TestHelper.createZakaznik(2, "739000222", "Zakaznik 2", false);
	    Rezervace rezervace2 = TestHelper.createRezervace(1, kurt2, zakaznik2, LocalDateTime.of(2020, 1, 1, 17, 15), LocalDateTime.of(2020, 1, 1, 18, 15), false, BigDecimal.valueOf(600), false);
		
		when(rezervaceRepository.findAll()).thenReturn(Arrays.asList(rezervace1, rezervace2));
		
		List<RezervaceDto> seznam = rezervaceService.seznam();
		assertThat(seznam).hasSize(2);
		assertThat(seznam.get(0).getKurt().getNazev()).isEqualTo("Kurt č.1 - tráva");
		assertThat(seznam.get(0).getKurt().getPovrch().getNazev()).isEqualTo("Tráva");
		assertThat(seznam.get(0).getDatumOd()).isEqualTo(LocalDateTime.of(2020, 1, 1, 16, 0));
		assertThat(seznam.get(0).getDatumDo()).isEqualTo(LocalDateTime.of(2020, 1, 1, 16, 30));
		assertThat(seznam.get(0).getJeCtyrHra()).isEqualTo(true);
		assertThat(seznam.get(0).getPlatnost()).isEqualTo(true);
		assertThat(seznam.get(0).getCenaPronajem()).isEqualTo(BigDecimal.valueOf(450));
		assertThat(seznam.get(0).getZakaznik().getCeleJmeno()).isEqualTo("Zakaznik 1");
		assertThat(seznam.get(0).getZakaznik().getTelefon()).isEqualTo("739000111");
		assertThat(seznam.get(1).getKurt().getNazev()).isEqualTo("Kurt č.2 - antuka");
		assertThat(seznam.get(1).getKurt().getPovrch().getNazev()).isEqualTo("Antuka");
		assertThat(seznam.get(1).getDatumOd()).isEqualTo(LocalDateTime.of(2020, 1, 1, 17, 15));
		assertThat(seznam.get(1).getDatumDo()).isEqualTo(LocalDateTime.of(2020, 1, 1, 18, 15));
		assertThat(seznam.get(1).getJeCtyrHra()).isEqualTo(false);
		assertThat(seznam.get(1).getPlatnost()).isEqualTo(false);
		assertThat(seznam.get(1).getCenaPronajem()).isEqualTo(BigDecimal.valueOf(600));
		assertThat(seznam.get(1).getZakaznik().getCeleJmeno()).isEqualTo("Zakaznik 2");
		assertThat(seznam.get(1).getZakaznik().getTelefon()).isEqualTo("739000222");
	}

	/**
	 * Test Rezervace - seznam dle kurtu
	 */
	@Test
	public void testSeznamDleKurtu() {
		Povrch povrch1 = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt1 = TestHelper.createKurt(1, "Kurt č.1 - tráva", povrch1, true);
	    Zakaznik zakaznik1 = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);
	    Rezervace rezervace1 = TestHelper.createRezervace(1, kurt1, zakaznik1, LocalDateTime.of(2020, 1, 1, 16, 0), LocalDateTime.of(2020, 1, 1, 16, 30), true, BigDecimal.valueOf(450), true);
	    		
	    Zakaznik zakaznik2 = TestHelper.createZakaznik(2, "739000222", "Zakaznik 2", false);
	    Rezervace rezervace2 = TestHelper.createRezervace(1, kurt1, zakaznik2, LocalDateTime.of(2020, 1, 1, 17, 15), LocalDateTime.of(2020, 1, 1, 18, 15), false, BigDecimal.valueOf(600), false);

		when(rezervaceRepository.findByKurt_IdOrderByDatumOdAsc(1)).thenReturn(Arrays.asList(rezervace1, rezervace2));
		
		List<RezervaceDto> seznam = rezervaceService.seznamDleKurtu(1);
		assertThat(seznam).hasSize(2);
		assertThat(seznam.get(0).getKurt().getNazev()).isEqualTo("Kurt č.1 - tráva");
		assertThat(seznam.get(0).getKurt().getPovrch().getNazev()).isEqualTo("Tráva");
		assertThat(seznam.get(0).getDatumOd()).isEqualTo(LocalDateTime.of(2020, 1, 1, 16, 0));
		assertThat(seznam.get(0).getDatumDo()).isEqualTo(LocalDateTime.of(2020, 1, 1, 16, 30));
		assertThat(seznam.get(0).getJeCtyrHra()).isEqualTo(true);
		assertThat(seznam.get(0).getPlatnost()).isEqualTo(true);
		assertThat(seznam.get(0).getCenaPronajem()).isEqualTo(BigDecimal.valueOf(450));
		assertThat(seznam.get(0).getZakaznik().getCeleJmeno()).isEqualTo("Zakaznik 1");
		assertThat(seznam.get(0).getZakaznik().getTelefon()).isEqualTo("739000111");
		assertThat(seznam.get(1).getKurt().getNazev()).isEqualTo("Kurt č.1 - tráva");
		assertThat(seznam.get(1).getKurt().getPovrch().getNazev()).isEqualTo("Tráva");
		assertThat(seznam.get(1).getDatumOd()).isEqualTo(LocalDateTime.of(2020, 1, 1, 17, 15));
		assertThat(seznam.get(1).getDatumDo()).isEqualTo(LocalDateTime.of(2020, 1, 1, 18, 15));
		assertThat(seznam.get(1).getJeCtyrHra()).isEqualTo(false);
		assertThat(seznam.get(1).getPlatnost()).isEqualTo(false);
		assertThat(seznam.get(1).getCenaPronajem()).isEqualTo(BigDecimal.valueOf(600));
		assertThat(seznam.get(1).getZakaznik().getCeleJmeno()).isEqualTo("Zakaznik 2");
		assertThat(seznam.get(1).getZakaznik().getTelefon()).isEqualTo("739000222");
	}
	
	/**
	 * Test Rezervace - seznam dle kurtu
	 */
	@Test
	public void testSeznamDleTelefonu() {
		Povrch povrch1 = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt1 = TestHelper.createKurt(1, "Kurt č.1 - tráva", povrch1, true);
	    Zakaznik zakaznik1 = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);
	    Rezervace rezervace1 = TestHelper.createRezervace(1, kurt1, zakaznik1, LocalDateTime.of(2025, 1, 1, 16, 0), LocalDateTime.of(2025, 1, 1, 16, 30), true, BigDecimal.valueOf(450), true);
	    		
		when(rezervaceRepository.findByZakaznik_TelefonAndDatumOdGreaterThanEqualOrderByDatumOdAsc("739000111", LocalDateTime.of(1970, 1, 1, 0, 0))).thenReturn(Arrays.asList(rezervace1));
		
		List<RezervaceDto> seznam = rezervaceService.seznamDleTelefonu("739000111", false);
		assertThat(seznam).hasSize(1);
		assertThat(seznam.get(0).getKurt().getNazev()).isEqualTo("Kurt č.1 - tráva");
		assertThat(seznam.get(0).getKurt().getPovrch().getNazev()).isEqualTo("Tráva");
		assertThat(seznam.get(0).getDatumOd()).isEqualTo(LocalDateTime.of(2025, 1, 1, 16, 0));
		assertThat(seznam.get(0).getDatumDo()).isEqualTo(LocalDateTime.of(2025, 1, 1, 16, 30));
		assertThat(seznam.get(0).getJeCtyrHra()).isEqualTo(true);
		assertThat(seznam.get(0).getPlatnost()).isEqualTo(true);
		assertThat(seznam.get(0).getCenaPronajem()).isEqualTo(BigDecimal.valueOf(450));
		assertThat(seznam.get(0).getZakaznik().getCeleJmeno()).isEqualTo("Zakaznik 1");
		assertThat(seznam.get(0).getZakaznik().getTelefon()).isEqualTo("739000111");
	}
	
	/**
	 * Test Rezervace - detail 
	 */
	@Test
	public void testDetail() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt = TestHelper.createKurt(1, "Kurt č.1 - tráva", povrch, true);
	    Zakaznik zakaznik = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);
	    Rezervace rezervace = TestHelper.createRezervace(1, kurt, zakaznik, LocalDateTime.of(2020, 1, 1, 16, 0), LocalDateTime.of(2020, 1, 1, 16, 30), true, BigDecimal.valueOf(450), true);
	    
		when(rezervaceRepository.findById(1)).thenReturn(Optional.of(rezervace));
	
		Optional<RezervaceDto> result = rezervaceService.detail(1);
		assertThat(result.isPresent()).isEqualTo(true);
		assertThat(result.get().getKurt().getNazev()).isEqualTo("Kurt č.1 - tráva");
		assertThat(result.get().getKurt().getPovrch().getNazev()).isEqualTo("Tráva");
		assertThat(result.get().getDatumOd()).isEqualTo(LocalDateTime.of(2020, 1, 1, 16, 0));
		assertThat(result.get().getDatumDo()).isEqualTo(LocalDateTime.of(2020, 1, 1, 16, 30));
		assertThat(result.get().getJeCtyrHra()).isEqualTo(true);
		assertThat(result.get().getPlatnost()).isEqualTo(true);
		assertThat(result.get().getCenaPronajem()).isEqualTo(BigDecimal.valueOf(450));
		assertThat(result.get().getZakaznik().getCeleJmeno()).isEqualTo("Zakaznik 1");
		assertThat(result.get().getZakaznik().getTelefon()).isEqualTo("739000111");
	}

	
	/**
	 * Test Rezervace - nový 
	 */
	@Test
	public void testKurtNovy() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt = TestHelper.createKurt(1, "Kurt č.1 - tráva", povrch, true);
	    Zakaznik zakaznik = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);
	    Rezervace rezervace = TestHelper.createRezervace(1, kurt, zakaznik, LocalDateTime.of(2020, 1, 1, 16, 0), LocalDateTime.of(2020, 1, 1, 16, 30), true, BigDecimal.valueOf(450), true);
			
		when(rezervaceRepository.save(any(Rezervace.class))).thenReturn(rezervace);
		when(zakaznikRepository.save(any(Zakaznik.class))).thenReturn(zakaznik);
		when(zakaznikRepository.findById(1)).thenReturn(Optional.of(zakaznik));
		when(zakaznikRepository.findByTelefon("739000111")).thenReturn(Optional.of(zakaznik));
		when(kurtRepository.findById(1)).thenReturn(Optional.of(kurt));
		
		RezervaceEditaceDto rezervaceDto = TestHelper.createRezervaceEditaceDto(1, true, 1, "739000111", "Zakaznik 1", LocalDateTime.of(2020, 1, 1, 16, 0), LocalDateTime.of(2020, 1, 1, 16, 30), true);
	
		/**
		 * spočítaná hodnota pronájmu:  30 minut * 10 Kč/minutu * 1,5 za čtyřhru = 450
		 */
		RezervaceDto result = rezervaceService.ulozitNovy(rezervaceDto);
		assertThat(result.getKurt().getNazev()).isEqualTo("Kurt č.1 - tráva");
		assertThat(result.getKurt().getPovrch().getNazev()).isEqualTo("Tráva");
		assertThat(result.getDatumOd()).isEqualTo(LocalDateTime.of(2020, 1, 1, 16, 0));
		assertThat(result.getDatumDo()).isEqualTo(LocalDateTime.of(2020, 1, 1, 16, 30));
		assertThat(result.getJeCtyrHra()).isEqualTo(true);
		assertThat(result.getPlatnost()).isEqualTo(true);
		assertThat(result.getCenaPronajem()).isEqualTo(BigDecimal.valueOf(450));
		assertThat(result.getZakaznik().getCeleJmeno()).isEqualTo("Zakaznik 1");
		assertThat(result.getZakaznik().getTelefon()).isEqualTo("739000111");
	}

	/**
	 * Test Rezervace - změna 
	 */
	@Test
	public void testKurtZmena() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt = TestHelper.createKurt(1, "Kurt č.1 - tráva", povrch, true);
	    Zakaznik zakaznik = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);
	    Rezervace rezervace = TestHelper.createRezervace(1, kurt, zakaznik, LocalDateTime.of(2020, 1, 1, 16, 0), LocalDateTime.of(2020, 1, 1, 16, 30), true, BigDecimal.valueOf(450), true);
	    
	    when(rezervaceRepository.save(any(Rezervace.class))).thenReturn(rezervace);
	    when(rezervaceRepository.findById(1)).thenReturn(Optional.of(rezervace));
		when(zakaznikRepository.save(any(Zakaznik.class))).thenReturn(zakaznik);
		when(zakaznikRepository.findById(1)).thenReturn(Optional.of(zakaznik));
		when(zakaznikRepository.findByTelefon("739000111")).thenReturn(Optional.of(zakaznik));
		when(kurtRepository.findById(1)).thenReturn(Optional.of(kurt));
	    
		RezervaceEditaceDto rezervaceDto = TestHelper.createRezervaceEditaceDto(1, true, 1, "739000111", "Zakaznik 1", LocalDateTime.of(2020, 1, 1, 16, 0), LocalDateTime.of(2020, 1, 1, 16, 30), true);
		
	    RezervaceDto result = rezervaceService.ulozitZmena(rezervaceDto);
	    assertThat(result.getKurt().getNazev()).isEqualTo("Kurt č.1 - tráva");
		assertThat(result.getKurt().getPovrch().getNazev()).isEqualTo("Tráva");
		assertThat(result.getDatumOd()).isEqualTo(LocalDateTime.of(2020, 1, 1, 16, 0));
		assertThat(result.getDatumDo()).isEqualTo(LocalDateTime.of(2020, 1, 1, 16, 30));
		assertThat(result.getJeCtyrHra()).isEqualTo(true);
		assertThat(result.getPlatnost()).isEqualTo(true);
		assertThat(result.getCenaPronajem()).isEqualTo(BigDecimal.valueOf(450.0));
		assertThat(result.getZakaznik().getCeleJmeno()).isEqualTo("Zakaznik 1");
		assertThat(result.getZakaznik().getTelefon()).isEqualTo("739000111");
	}
	
	/**
	 * Test Rezervace - kontroly 
	 */
	@Test
	public void testKurtKontroly() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), false);
	    Kurt kurt = TestHelper.createKurt(1, "Kurt č.1 - tráva", povrch, true);
		RezervaceEditaceDto rezervaceDto = TestHelper.createRezervaceEditaceDto(null, true, 1, "739000111", "Zakaznik 1", LocalDateTime.of(2025, 1, 1, 16, 0), LocalDateTime.of(2025, 1, 1, 16, 30), true);
	    ErrorCollector errs = new ErrorCollector();
	    
	    when(kurtRepository.findById(1)).thenReturn(Optional.of(kurt));

	    // záznam bez chyb
	    rezervaceService.kontroly(rezervaceDto, errs, true);
	    assertThat(errs.getErrors().size()).isEqualTo(0);
	    
	    // záznam s rezervací do minulosti
	    rezervaceDto.setDatumOd(LocalDateTime.of(2020, 1, 1, 16, 0));
	    rezervaceService.kontroly(rezervaceDto, errs, true);
	    
	    assertThat(errs.getErrors().size()).isEqualTo(1);
	    assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Údaj Datum od nesmí být do minulosti.");
	    
	    // datum od je vetší než datum do
	    errs = new ErrorCollector();
	    rezervaceDto.setDatumOd(LocalDateTime.of(2025, 1, 1, 16, 0));
	    rezervaceDto.setDatumDo(LocalDateTime.of(2025, 1, 1, 15, 0));
	    rezervaceService.kontroly(rezervaceDto, errs, true);
	    
	    assertThat(errs.getErrors().size()).isEqualTo(1);
	    assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Údaj Datum do je menší než Datum Od.");
	}
}
