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
import cz.inqool.kurt.KurtRepository;
import cz.inqool.kurt.KurtService;
import cz.inqool.kurt.domain.Kurt;
import cz.inqool.kurt.domain.KurtDto;
import cz.inqool.povrch.PovrchRepository;
import cz.inqool.povrch.PovrchService;
import cz.inqool.povrch.domain.Povrch;
import cz.inqool.povrch.domain.PovrchDto;

class KurtTest {

	@Mock
	private KurtRepository kurtRepository;

	@InjectMocks
	private KurtService kurtService;
	
	@InjectMocks
	private PovrchService povrchService;
	
	@Mock
    private PovrchRepository povrchRepository;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	/**
	 * Test Kurt - seznam
	 */
	@Test
	public void testSeznam() {
		Povrch povrch1 = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt1 = TestHelper.createKurt(1, "Kurt č.1 - tráva", povrch1, true);
	    
	    Povrch povrch2 = TestHelper.createPovrch(2, "Antuka", BigDecimal.valueOf(20), true);
	    Kurt kurt2 = TestHelper.createKurt(2, "Kurt č.2 - antuka", povrch2, true);
		
		when(kurtRepository.findAll()).thenReturn(Arrays.asList(kurt1, kurt2));
		
		List<KurtDto> seznam = kurtService.seznam();
		assertThat(seznam).hasSize(2);
		assertThat(seznam.get(0).getNazev()).isEqualTo("Kurt č.1 - tráva");
		assertThat(seznam.get(0).getPovrch().getNazev()).isEqualTo("Tráva");
		assertThat(seznam.get(1).getNazev()).isEqualTo("Kurt č.2 - antuka");
		assertThat(seznam.get(1).getPovrch().getNazev()).isEqualTo("Antuka");
	}

	/**
	 * Test Kurt - detail 
	 */
	@Test
	public void testDetail() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt = TestHelper.createKurt(1, "Kurt č.1 - tráva", povrch, true);
	    
		when(kurtRepository.findById(1)).thenReturn(Optional.of(kurt));
	
		Optional<KurtDto> result = kurtService.detail(1);
		assertThat(result.isPresent()).isEqualTo(true);
		assertThat(result.get().getNazev()).isEqualTo("Kurt č.1 - tráva");
		assertThat(result.get().getPovrch().getNazev()).isEqualTo("Tráva");
	}

	
	/**
	 * Test Kurt - nový 
	 */
	@Test
	public void testKurtNovy() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt = TestHelper.createKurt(1, "Kurt č.1 - tráva", povrch, true);
	    
	    PovrchDto povrchDto = TestHelper.createPovrchDto(1, "Tráva", BigDecimal.valueOf(10), true);
	    KurtDto kurtDto = TestHelper.createKurtDto(1, "Kurt č.1 - tráva", povrchDto, true);
			
		when(kurtRepository.save(any(Kurt.class))).thenReturn(kurt);
		
		KurtDto result = kurtService.ulozitNovy(kurtDto);
		assertThat(result.getNazev()).isEqualTo("Kurt č.1 - tráva");
		assertThat(result.getPovrch().getNazev()).isEqualTo("Tráva");
	}

	/**
	 * Test Kurt - změna 
	 */
	@Test
	public void testKurtZmena() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt = TestHelper.createKurt(1, "Kurt č.1 - tráva - změna", povrch, true);
	    
	    PovrchDto povrchDto = TestHelper.createPovrchDto(1, "Tráva", BigDecimal.valueOf(10), true);
	    KurtDto kurtDto = TestHelper.createKurtDto(1, "Kurt č.1 - tráva", povrchDto, true);
		
		when(kurtRepository.findById(1)).thenReturn(Optional.of(kurt));
		when(kurtRepository.save(any(Kurt.class))).thenReturn(kurt);
		
		KurtDto result = kurtService.ulozitZmena(kurtDto);
		assertThat(result.getNazev()).isEqualTo("Kurt č.1 - tráva");
		assertThat(result.getPovrch().getNazev()).isEqualTo("Tráva");
	}
	
	/**
	 * Test Kurt - kontroly 
	 */
	@Test
	public void testKurtKontroly() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt = TestHelper.createKurt(1, "Kurt č.1 - tráva - změna", povrch, true);
	    
	    KurtDto kurtDto = TestHelper.createKurtDto(1, null, null, true);
	    
	    ErrorCollector errs = new ErrorCollector();

	    when(kurtRepository.findById(1)).thenReturn(Optional.of(kurt));
	    
	    kurtService.kontroly(kurtDto, errs, true);
	    assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Údaj Povrch není zadán.");
	    assertThat(errs.getErrors().get(1).get("popis")).isEqualTo("Údaj Název není zadán.");
	    
	    errs = new ErrorCollector();
	    PovrchDto povrchDto = TestHelper.createPovrchDto(100, "Tráva", BigDecimal.valueOf(10), true);
	    kurtDto = TestHelper.createKurtDto(1, "Kurt č.1 - tráva", povrchDto, true);
	    
	    povrchService.kontroly(povrchDto, errs, false);
	    assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Záznam s id 100 nenalezen.");
	}

	/**
	 * Test Kurt - kontroly zneplatnit
	 */
	@Test
	public void testKurtKontrolyZneplatnit() {
	    KurtDto kurtDto = TestHelper.createKurtDto(1, null, null, true);

	    Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
	    Kurt kurt = TestHelper.createKurt(1, "Kurt č.1 - tráva - změna", povrch, false);
	    
	    ErrorCollector errs = new ErrorCollector();
	    
	    when(kurtRepository.findById(1)).thenReturn(Optional.of(kurt));
	    
	    kurtService.kontrolyZneplatnit(kurtDto, errs);
	    assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Záznam s id 1 je neplatný.");
	    
	    errs = new ErrorCollector();
	    kurtDto.setId(100);
	    
	    kurtService.kontrolyZneplatnit(kurtDto, errs);
	    assertThat(errs.getErrors().get(0).get("popis")).isEqualTo("Záznam s id 100 nenalezen.");
	}
	
	/**
	 * Test Kurt - zneplatnit 
	 */
	@Test
	public void testKurtZneplatnit() {
		Povrch povrch = TestHelper.createPovrch(1, "Tráva", BigDecimal.valueOf(10), true);
		Kurt kurt = TestHelper.createKurt(1, "Kurt č.1 - tráva - změna", povrch, false);
		
		when(kurtRepository.findById(1)).thenReturn(Optional.of(kurt));
		when(kurtRepository.save(any(Kurt.class))).thenReturn(kurt);

		KurtDto result = kurtService.ulozitZneplatnit(1);
		assertThat(result.getPlatnost()).isEqualTo(false);
	}
}
