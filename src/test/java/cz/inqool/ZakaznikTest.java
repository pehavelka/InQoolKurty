package cz.inqool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cz.inqool.core.ErrorCollector;
import cz.inqool.zakaznik.ZakaznikRepository;
import cz.inqool.zakaznik.ZakaznikService;
import cz.inqool.zakaznik.domain.Zakaznik;
import cz.inqool.zakaznik.domain.ZakaznikDto;


class ZakaznikTest {

	@InjectMocks
	private ZakaznikService zakaznikService;
	
	@Mock
    private ZakaznikRepository zakaznikRepository;
	
	@BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
	
	/**
	 * Test Zakaznik - seznam
	 */
	@Test
	void testZakaznikSeznam() {
		Zakaznik zakaznik1 = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);
	    Zakaznik zakaznik2 = TestHelper.createZakaznik(2, "739000222", "Zakaznik 2", false);

	    when(zakaznikRepository.findAll()).thenReturn(Arrays.asList(zakaznik1, zakaznik2));
        
		List<ZakaznikDto> seznam = zakaznikService.seznam();
		assertThat(seznam).hasSize(2);
		assertThat(seznam.get(0).getId()).isEqualTo(1);
		assertThat(seznam.get(0).getTelefon()).isEqualTo("739000111");
		assertThat(seznam.get(0).getCeleJmeno()).isEqualTo("Zakaznik 1");
		assertThat(seznam.get(0).getPlatnost()).isEqualTo(true);
		assertThat(seznam.get(1).getId()).isEqualTo(2);
		assertThat(seznam.get(1).getTelefon()).isEqualTo("739000222");
		assertThat(seznam.get(1).getCeleJmeno()).isEqualTo("Zakaznik 2");
		assertThat(seznam.get(1).getPlatnost()).isEqualTo(false);
	}
	
	/**
	 * Test Zakaznik - detail
	 */
	@Test
	void testZakaznikDetail() {
		Zakaznik zakaznik = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);
		
        when(zakaznikRepository.findById(1)).thenReturn(Optional.of(zakaznik));
        
        Optional<ZakaznikDto> result = zakaznikService.detail(1);
        assertThat(result.isPresent()).isEqualTo(true);
        assertThat(result.get().getId()).isEqualTo(1);
		assertThat(result.get().getTelefon()).isEqualTo("739000111");
		assertThat(result.get().getCeleJmeno()).isEqualTo("Zakaznik 1");
		assertThat(result.get().getPlatnost()).isEqualTo(true);
	}
	
	/**
	 * Test Zakaznik - nový
	 */
	@Test
	void testZakaznikNovy() {
		Zakaznik zakaznik = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);
		ZakaznikDto zakaznikDto = TestHelper.createZakaznikDto(null, "739000111", "Zakaznik 1", true);

        when(zakaznikRepository.save(any(Zakaznik.class))).thenReturn(zakaznik);

        ZakaznikDto result = zakaznikService.ulozitNovy(zakaznikDto);
        assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getTelefon()).isEqualTo("739000111");
		assertThat(result.getCeleJmeno()).isEqualTo("Zakaznik 1");
		assertThat(result.getPlatnost()).isEqualTo(true);
	}

	/**
	 * Test Zakaznik - změna
	 */
	@Test
    void testZakaznikZmena() {
		Zakaznik zakaznik = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);
		ZakaznikDto zakaznikDto = TestHelper.createZakaznikDto(1, "739000111", "Zakaznik 1", true);

        when(zakaznikRepository.findById(1)).thenReturn(Optional.of(zakaznik));
        when(zakaznikRepository.save(any(Zakaznik.class))).thenReturn(zakaznik);

        ZakaznikDto result = zakaznikService.ulozitZmena(zakaznikDto);
        assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getTelefon()).isEqualTo("739000111");
		assertThat(result.getCeleJmeno()).isEqualTo("Zakaznik 1");
		assertThat(result.getPlatnost()).isEqualTo(true);
    }

	/**
	 * Test Zakaznik - kontroly
	 */
	@Test
    void testZakaznikKontroly() {
		Zakaznik zakaznik = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);
        ZakaznikDto zakaznikDto = new ZakaznikDto();
        ErrorCollector errs = new ErrorCollector();

        when(zakaznikRepository.findById(1)).thenReturn(Optional.of(zakaznik));
        
        zakaznikService.kontroly(zakaznikDto, errs, false);
        
        assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Údaj ID není zadán.");
        assertThat(errs.getErrors().get(1).get("popis")).isEqualTo("Údaj Platnost není zadán.");
        assertThat(errs.getErrors().get(2).get("popis")).isEqualTo("Údaj Telefon není zadán.");
	    assertThat(errs.getErrors().get(3).get("popis")).isEqualTo("Údaj Celé jméno není zadán.");
	    
	    errs = new ErrorCollector();
	    zakaznikDto = TestHelper.createZakaznikDto(100, "739000111", "Zakaznik 1", true);
	    
	    zakaznikService.kontroly(zakaznikDto, errs, false);
	    assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Záznam s id 100 nenalezen.");
    }
	
	/**
	 * Test Zakaznik - kontroly zneplatnit
	 */
	@Test
    void testZakaznikKontrolyZneplatnit() {
		Zakaznik zakaznik = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", false);
		ZakaznikDto zakaznikDto = TestHelper.createZakaznikDto(1, "739000111", "Zakaznik 1", true);
		
        ErrorCollector errs = new ErrorCollector();
        zakaznikDto.setId(1);
        
        when(zakaznikRepository.findById(1)).thenReturn(Optional.of(zakaznik));
        
        zakaznikService.kontrolyZneplatnit(zakaznikDto, errs);
        assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Záznam s id 1 je neplatný.");
        
        errs = new ErrorCollector();
	    zakaznikDto.setId(100);
	    
	    zakaznikService.kontrolyZneplatnit(zakaznikDto, errs);
	    assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Záznam s id 100 nenalezen.");
    }
	
	/**
	 * Test Zakaznik - zneplatnit
	 */
	@Test
    void testZakaznikZneplatnit() {
		Zakaznik zakaznik = TestHelper.createZakaznik(1, "739000111", "Zakaznik 1", true);

        when(zakaznikRepository.findById(1)).thenReturn(Optional.of(zakaznik));
        when(zakaznikRepository.save(any(Zakaznik.class))).thenReturn(zakaznik);

        ZakaznikDto result = zakaznikService.ulozitZneplatnit(1);
        assertThat(result.getPlatnost()).isEqualTo(false);
    }
}
