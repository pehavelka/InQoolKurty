package cz.inqool.zakaznik;

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
import cz.inqool.zakaznik.domain.ZakaznikDto;

/**
 * Správa zákazníků
 */
@RestController
@RequestMapping("api/zakaznik")
public class ZakaznikController {

	@Autowired
	private ZakaznikService service;
	
	/**
	 * Seznam
	 * 
	 * @return
	 */
	@GetMapping
	public ResponseEntity<Object> getSeznam() {
		return ResponseEntity.ok(service.seznam());
	}
	
	/**
	 * Detail
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(path = "/{id}")
	public ResponseEntity<Object> getDetail(@PathVariable Integer id) {
		Optional<ZakaznikDto> dto = service.detail(id);

		if (dto.isEmpty()) {
			return ResponseEntity.badRequest().body(String.format("Záznam s id %s nenalezen.", id));
		}

		return ResponseEntity.ok(dto);
	}
	
	/**
	 * Nový záznam
	 * 
	 * @param data
	 * @param provest   false(první průchod): Pokud jsou chyby nebo varování, vrátí se chybový kód 400 a seznam hlášení
	 * 					true(potvrzení varování) : Pokud jsou chyby, vrátí se chybový kód 400, pokud jsou nebo nejsou varování, provede se akce a vrátí se kód OK 200
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Object> novy(@RequestBody ZakaznikDto data, @RequestParam(defaultValue = "false") boolean provest) {
		ErrorCollector errs = new ErrorCollector();
		
		service.kontroly(data, errs, true);
		if (errs.konec400(provest)) {
			return ResponseEntity.badRequest().body(errs.getErrors());
		}

		ZakaznikDto dto = service.ulozitNovy(data);

		return ResponseEntity.ok(dto);
	}

	/**
	 * Uložení změn 
	 * 
	 * @param data
	 * @param provest   false(první průchod): Pokud jsou chyby nebo varování, vrátí se chybový kód 400 a seznam hlášení
	 * 					true(potvrzení varování) : Pokud jsou chyby, vrátí se chybový kód 400, pokud jsou nebo nejsou varování, provede se akce a vrátí se kód OK 200
	 * @return
	 */
	@PutMapping
	public ResponseEntity<Object> zmena(@RequestBody ZakaznikDto data, @RequestParam(defaultValue = "false") boolean provest) {
		ErrorCollector errs = new ErrorCollector();
		
		service.kontroly(data, errs, false);
		if (errs.konec400(provest)) {
			return ResponseEntity.badRequest().body(errs.getErrors());
		}
		
		ZakaznikDto dto = service.ulozitZmena(data);

		return ResponseEntity.ok(dto);
	}
	
	/**
	 * Zneplatnit záznam
	 * 
	 * @param id
	 * @param provest   false(první průchod): Pokud jsou chyby nebo varování, vrátí se chybový kód 400 a seznam hlášení
	 * 					true(potvrzení varování) : Pokud jsou chyby, vrátí se chybový kód 400, pokud jsou nebo nejsou varování, provede se akce a vrátí se kód OK 200
	 * @return
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Object> zneplatnit(@PathVariable Integer id, @RequestParam(defaultValue = "false") boolean provest) {
		ErrorCollector errs = new ErrorCollector();
		
		ZakaznikDto dto = ZakaznikDto
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