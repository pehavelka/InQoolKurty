package cz.inqool.povrch;

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
import cz.inqool.povrch.domain.PovrchDto;

/**
 * Správa povrchů
 */
@RestController
@RequestMapping("api/povrch")
public class PovrchController {

	@Autowired
	private PovrchService service;
	
	/**
	 * UC: Seznam povrchů
	 * 
	 * @return
	 */
	@GetMapping
	public ResponseEntity<Object> getSeznam() {
		return ResponseEntity.ok(service.seznam());
	}
	
	/**
	 * UC: Správa povrchů - detail
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(path = "/{id}")
	public ResponseEntity<Object> getDetail(@PathVariable Integer id) {
		Optional<PovrchDto> dto = service.detail(id);

		if (dto.isEmpty()) {
			return ResponseEntity.badRequest().body(String.format("Záznam s id %s nenalezen.", id));
		}

		return ResponseEntity.ok(dto);
	}
	
	/**
	 * UC: Správa povrchů - nový záznam
	 * 
	 * @param data
	 * @param provest   false(první průchod): Pokud jsou chyby nebo varování, vrátí se chybový kód 400 a seznam hlášení
	 * 					true(potvrzení varování) : Pokud jsou chyby, vrátí se chybový kód 400, pokud jsou nebo nejsou varování, provede se akce a vrátí se kód OK 200
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Object> novy(@RequestBody PovrchDto data, @RequestParam(defaultValue = "false") boolean provest) {
		ErrorCollector errs = new ErrorCollector();
		
		service.kontroly(data, errs, true);
		if (errs.konec400(provest)) {
			return ResponseEntity.badRequest().body(errs.getErrors());
		}

		PovrchDto dto = service.ulozitNovy(data);

		return ResponseEntity.ok(dto);
	}

	/**
	 * UC: Správa povrchů - změna 
	 * 
	 * @param data
	 * @param provest   false(první průchod): Pokud jsou chyby nebo varování, vrátí se chybový kód 400 a seznam hlášení
	 * 					true(potvrzení varování) : Pokud jsou chyby, vrátí se chybový kód 400, pokud jsou nebo nejsou varování, provede se akce a vrátí se kód OK 200
	 * @return
	 */
	@PutMapping
	public ResponseEntity<Object> zmena(@RequestBody PovrchDto data, @RequestParam(defaultValue = "false") boolean provest) {
		ErrorCollector errs = new ErrorCollector();
		
		service.kontroly(data, errs, false);
		if (errs.konec400(provest)) {
			return ResponseEntity.badRequest().body(errs.getErrors());
		}
		
		PovrchDto dto = service.ulozitZmena(data);

		return ResponseEntity.ok(dto);
	}
	
	/**
	 * UC: Správa povrchů - zneplatnit záznam
	 * 
	 * @param id
	 * @param provest   false(první průchod): Pokud jsou chyby nebo varování, vrátí se chybový kód 400 a seznam hlášení
	 * 					true(potvrzení varování) : Pokud jsou chyby, vrátí se chybový kód 400, pokud jsou nebo nejsou varování, provede se akce a vrátí se kód OK 200
	 * @return
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Object> zneplatnit(@PathVariable Integer id, @RequestParam(defaultValue = "false") boolean provest) {
		ErrorCollector errs = new ErrorCollector();
		
		PovrchDto dto = PovrchDto
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