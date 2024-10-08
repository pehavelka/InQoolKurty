package cz.inqool.kurt;

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
import cz.inqool.kurt.domain.KurtDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Správa kurtů
 */
@Slf4j
@RestController
@RequestMapping("api/kurt")
public class KurtController {

	@Autowired
	private KurtService service;
	
	/**
	 * UC: Seznam kurtů
	 * 
	 * @return
	 */
	@GetMapping
	public ResponseEntity<Object> getSeznam() {
		log.debug("getSeznam");
		return ResponseEntity.ok(service.seznam());
	}
	
	/**
	 * UC: Správa kurtů - detail
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(path = "/{id}")
	public ResponseEntity<Object> getDetail(@PathVariable Integer id) {
		log.debug("getDetail");
		Optional<KurtDto> dto = service.detail(id);

		if (dto.isEmpty()) {
			return ResponseEntity.badRequest().body(String.format("Záznam s id %s nenalezen.", id));
		}

		return ResponseEntity.ok(dto);
	}
	
	/**
	 * UC: Správa kurtů - nový záznam
	 * 
	 * @param data
	 * @param provest   false(první průchod): Pokud jsou chyby nebo varování, vrátí se chybový kód 400 a seznam hlášení
	 * 					true(potvrzení varování) : Pokud jsou chyby, vrátí se chybový kód 400, pokud jsou nebo nejsou varování, provede se akce a vrátí se kód OK 200
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Object> novy(@RequestBody KurtDto data, @RequestParam(defaultValue = "false") boolean provest) {
		log.debug("novy");
		ErrorCollector errs = new ErrorCollector();
		
		service.kontroly(data, errs, true);
		if (errs.konec400(provest)) {
			return ResponseEntity.badRequest().body(errs.getErrors());
		}
		
		KurtDto dto = service.ulozitNovy(data);

		return ResponseEntity.ok(dto);
	}

	/**
	 * UC: Správa kurtů - změna 
	 * 
	 * @param data
	 * @param provest   false(první průchod): Pokud jsou chyby nebo varování, vrátí se chybový kód 400 a seznam hlášení
	 * 					true(potvrzení varování) : Pokud jsou chyby, vrátí se chybový kód 400, pokud jsou nebo nejsou varování, provede se akce a vrátí se kód OK 200
	 * @return
	 */
	@PutMapping
	public ResponseEntity<Object> zmena(@RequestBody KurtDto data, @RequestParam(defaultValue = "false") boolean provest) {
		log.debug("zmena");
		ErrorCollector errs = new ErrorCollector();
		
		service.kontroly(data, errs, false);
		if (errs.konec400(provest)) {
			return ResponseEntity.badRequest().body(errs.getErrors());
		}
		
		KurtDto dto = service.ulozitZmena(data);

		return ResponseEntity.ok(dto);
	}
	
	/**
	 * UC: Správa kurtů - zneplatnit záznam
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
		
		KurtDto dto = KurtDto
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