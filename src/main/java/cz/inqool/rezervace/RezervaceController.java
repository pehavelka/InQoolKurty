package cz.inqool.rezervace;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.inqool.core.ErrorCollector;
import cz.inqool.rezervace.domain.RezervaceDto;
import cz.inqool.rezervace.domain.RezervaceEditaceDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Správa rezervace kurtu
 */
@Slf4j
@RestController
@RequestMapping("api/rezervace")
public class RezervaceController {

	@Autowired
	private RezervaceService service;
	
	/**
	 * UC: Seznam všech rezervací
	 * 
	 * @return
	 */
	@GetMapping
	public ResponseEntity<Object> getSeznam() {
		log.debug("getSeznam");
		return ResponseEntity.ok(service.seznam());
	}
	
	/**
	 * UC: Seznam rezervací dle kurtu
	 * 
	 * @param kurtId
	 * @return
	 */
	@GetMapping(path = "seznam-kurt/{kurtId}")
	public ResponseEntity<Object> getSeznamDleKurtu(@PathVariable Integer kurtId) {
		log.debug("getSeznamDleKurtu");
		return ResponseEntity.ok(service.seznamDleKurtu(kurtId));
	}
	
	/**
	 * UC: Seznam rezervací dle telefonu
	 * 
	 * @param telefon
	 * @param budouci, nepovinný parametr 1 - zobrazí jen rezervace do budoucnosti
	 * @return
	 */
	@GetMapping(path = "seznam-telefon/{telefon}")
	public ResponseEntity<Object> getSeznamDleTelefonu(@PathVariable String telefon
			, @RequestParam(required = false) Boolean budouci) {
		log.debug("getSeznamDleTelefonu");
		return ResponseEntity.ok(service.seznamDleTelefonu(telefon, budouci));
	}
	
	/**
	 * UC: Správa rezervace kurtu - detail
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(path = "/{id}")
	public ResponseEntity<Object> getDetail(@PathVariable Integer id) {
		log.debug("getDetail");
		Optional<RezervaceDto> dto = service.detail(id);

		if (dto.isEmpty()) {
			return ResponseEntity.badRequest().body(String.format("Záznam s id %s nenalezen.", id));
		}

		return ResponseEntity.ok(dto);
	}
	
	/**
	 * UC: Správa rezervace kurtu - nový záznam
	 * 
	 * V případě zadání tel.čísla, které neexistuje, tak se vytvoří pro toto číslo nový zákazník.
	 * V případě zadání tel.čísla, které existuje, a je rozdílné jméno zákazníka, tak se jméno aktualizuje.
	 * V případě, že je zákazník neplatný, tak se nastaví jako platný.
	 * Není možné objednat rezervaci do minulosti.
	 * 
	 * @param data
	 * @param provest   false(první průchod): Pokud jsou chyby nebo varování, vrátí se chybový kód 400 a seznam hlášení
	 * 					true(potvrzení varování) : Pokud jsou chyby, vrátí se chybový kód 400, pokud jsou nebo nejsou varování, provede se akce a vrátí se kód OK 200
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Object> novy(@RequestBody RezervaceEditaceDto data, @RequestParam(defaultValue = "false") boolean provest) {
		log.debug("novy");
		ErrorCollector errs = new ErrorCollector();
		
		service.kontroly(data, errs, true);
		if (errs.konec400(provest)) {
			return ResponseEntity.badRequest().body(errs.getErrors());
		}
		
		RezervaceDto dto = service.ulozitNovy(data);

		return ResponseEntity.ok(dto);
	}

	/**
	 * UC: Správa rezervace kurtu - změna 
	 * 
	 * @param data
	 * @param provest   false(první průchod): Pokud jsou chyby nebo varování, vrátí se chybový kód 400 a seznam hlášení
	 * 					true(potvrzení varování) : Pokud jsou chyby, vrátí se chybový kód 400, pokud jsou nebo nejsou varování, provede se akce a vrátí se kód OK 200
	 * @return
	 */
	@PutMapping
	public ResponseEntity<Object> zmena(@RequestBody RezervaceEditaceDto data, @RequestParam(defaultValue = "false") boolean provest) {
		log.debug("zmena");
		ErrorCollector errs = new ErrorCollector();
		
		service.kontroly(data, errs, false);
		if (errs.konec400(provest)) {
			return ResponseEntity.badRequest().body(errs.getErrors());
		}
		
		RezervaceDto dto = service.ulozitZmena(data);

		return ResponseEntity.ok(dto);
	}
	
	/**
	 * UC: Správa rezervace kurtu - zneplatnit záznam
	 * 
	 * @param id
	 * @param provest   false(první průchod): Pokud jsou chyby nebo varování, vrátí se chybový kód 400 a seznam hlášení
	 * 					true(potvrzení varování) : Pokud jsou chyby, vrátí se chybový kód 400, pokud jsou nebo nejsou varování, provede se akce a vrátí se kód OK 200
	 * @return
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Object> zneplatnit(@PathVariable Integer id, @RequestParam(defaultValue = "false") boolean provest) {
		log.debug("zneplatnit");
		ErrorCollector errs = new ErrorCollector();
		
		RezervaceDto dto = RezervaceDto
							.builder()
							.id(id)
							.build();
		
		service.kontrolyZneplatnit(dto, errs);
		
		if (errs.konec400(provest)) {
			return ResponseEntity.badRequest().body(errs.getErrors());
		}
		
		dto = service.ulozitZneplatnit(id);

		return ResponseEntity.ok(dto);
	}
}