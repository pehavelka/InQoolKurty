package cz.inqool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cz.inqool.core.ErrorCollector;
import cz.inqool.povrch.PovrchRepository;
import cz.inqool.povrch.PovrchService;
import cz.inqool.povrch.domain.Povrch;
import cz.inqool.povrch.domain.PovrchDto;


class PovrchTest {

	@InjectMocks
	private PovrchService PovrchService;
	
	@Mock
    private PovrchRepository PovrchRepository;
	
	@BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
	
	/**
	 * Test Povrch - seznam
	 */
	@Test
	void testPovrchSeznam() {
		Povrch povrch1 = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Povrch povrch2 = TestHelper.createPovrch(2, "Antuka", BigDecimal.valueOf(20), false);

	    when(PovrchRepository.findAll()).thenReturn(Arrays.asList(povrch1, povrch2));
        
		List<PovrchDto> seznam = PovrchService.seznam();
		assertThat(seznam).hasSize(2);
		assertThat(seznam.get(0).getId()).isEqualTo(1);
		assertThat(seznam.get(0).getNazev()).isEqualTo("Tráva");
		assertThat(seznam.get(0).getCena()).isEqualTo(BigDecimal.valueOf(10));
		assertThat(seznam.get(0).getPlatnost()).isEqualTo(true);
		assertThat(seznam.get(1).getId()).isEqualTo(2);
		assertThat(seznam.get(1).getNazev()).isEqualTo("Antuka");
		assertThat(seznam.get(1).getCena()).isEqualTo(BigDecimal.valueOf(20));
		assertThat(seznam.get(1).getPlatnost()).isEqualTo(false);
	}
	
	/**
	 * Test Povrch - detail
	 */
	@Test
	void testPovrchDetail() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
		
        when(PovrchRepository.findById(1)).thenReturn(Optional.of(povrch));
        
        Optional<PovrchDto> result = PovrchService.detail(1);
        assertThat(result.isPresent()).isEqualTo(true);
        assertThat(result.get().getId()).isEqualTo(1);
		assertThat(result.get().getNazev()).isEqualTo("Tráva");
		assertThat(result.get().getCena()).isEqualTo(BigDecimal.valueOf(10));
		assertThat(result.get().getPlatnost()).isEqualTo(true);
	}
	
	/**
	 * Test Povrch - nový
	 */
	@Test
	void testPovrchNovy() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
		PovrchDto PovrchDto = TestHelper.createPovrchDto(null, "Tráva", BigDecimal.valueOf(10), true);

        when(PovrchRepository.save(any(Povrch.class))).thenReturn(povrch);

        PovrchDto result = PovrchService.ulozitNovy(PovrchDto);
        assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getNazev()).isEqualTo("Tráva");
		assertThat(result.getCena()).isEqualTo(BigDecimal.valueOf(10));
		assertThat(result.getPlatnost()).isEqualTo(true);
	}

	/**
	 * Test Povrch - změna
	 */
	@Test
    void testPovrchZmena() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
		PovrchDto PovrchDto = TestHelper.createPovrchDto(1, "Tráva", BigDecimal.valueOf(10), true);

        when(PovrchRepository.findById(1)).thenReturn(Optional.of(povrch));
        when(PovrchRepository.save(any(Povrch.class))).thenReturn(povrch);

        PovrchDto result = PovrchService.ulozitZmena(PovrchDto);
        assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getNazev()).isEqualTo("Tráva");
		assertThat(result.getCena()).isEqualTo(BigDecimal.valueOf(10));
		assertThat(result.getPlatnost()).isEqualTo(true);
    }

	/**
	 * Test Povrch - kontroly
	 */
	@Test
    void testPovrchKontroly() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
        PovrchDto PovrchDto = new PovrchDto();
        ErrorCollector errs = new ErrorCollector();

        when(PovrchRepository.findById(1)).thenReturn(Optional.of(povrch));
        
        PovrchDto.setCena(BigDecimal.valueOf(0));
        PovrchService.kontroly(PovrchDto, errs, false);
        assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Údaj ID není zadán.");
        assertThat(errs.getErrors().get(1).get("popis")).isEqualTo("Údaj Platnost není zadán.");
        assertThat(errs.getErrors().get(2).get("popis")).isEqualTo("Údaj Název není zadán.");
	    assertThat(errs.getErrors().get(3).get("popis")).isEqualTo("Údaj Cena musí být větší než 0.");
	    
	    errs = new ErrorCollector();
	    PovrchDto = TestHelper.createPovrchDto(100, "Tráva", BigDecimal.valueOf(10), true);
	    
	    PovrchService.kontroly(PovrchDto, errs, false);
	    assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Záznam s id 100 nenalezen.");
    }
	
	/**
	 * Test Povrch - kontroly zneplatnit
	 */
	@Test
    void testPovrchKontrolyZneplatnit() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), false);
		PovrchDto PovrchDto = TestHelper.createPovrchDto(1, "Tráva", BigDecimal.valueOf(10), true);
		
        ErrorCollector errs = new ErrorCollector();
        PovrchDto.setId(1);
        
        when(PovrchRepository.findById(1)).thenReturn(Optional.of(povrch));
        
        PovrchService.kontrolyZneplatnit(PovrchDto, errs);
        assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Záznam s id 1 je neplatný.");
        
        errs = new ErrorCollector();
	    PovrchDto.setId(100);
	    
	    PovrchService.kontrolyZneplatnit(PovrchDto, errs);
	    assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Záznam s id 100 nenalezen.");
    }
	
	/**
	 * Test Povrch - zneplatnit
	 */
	@Test
    void testPovrchZneplatnit() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);

        when(PovrchRepository.findById(1)).thenReturn(Optional.of(povrch));
        when(PovrchRepository.save(any(Povrch.class))).thenReturn(povrch);

        PovrchDto result = PovrchService.ulozitZneplatnit(1);
        assertThat(result.getPlatnost()).isEqualTo(false);
    }
}
