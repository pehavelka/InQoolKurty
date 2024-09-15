package cz.inqool.zakaznik;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.inqool.core.ErrorCollector;
import cz.inqool.zakaznik.domain.Zakaznik;
import cz.inqool.zakaznik.domain.ZakaznikDto;

@Service
public class ZakaznikService {
	
	@Autowired
	private ZakaznikRepository repo;
	
	/**
	 * Seznam
	 * 
	 * @return
	 */
	public List<ZakaznikDto> seznam() {
		return repo.findAll().stream().map(ZakaznikDto::of).collect(Collectors.toList());
	}
	
	/**
	 * Detail
	 * 
	 * @param id
	 * @return
	 */
	public Optional<ZakaznikDto> detail(final Integer id) {
		return repo.findById(id).map(ZakaznikDto::of);
	}
	
	/**
	 * Kontroly nový, změna
	 * 
	 * @param dto
	 * @param novy
	 * @return
	 */
	public void kontroly(final ZakaznikDto dto, final ErrorCollector errs, final boolean novy) {
		if (!novy) {
			if (dto.getId() == null) {
				errs.AddChyba("Údaj ID není zadán.");
		    }
	
		    if (dto.getPlatnost() == null) {
		    	errs.AddChyba("Údaj Platnost není zadán.");
		    }
		    
		    if (dto.getTelefon() != null && repo.existsByTelefonAndIdNot(dto.getTelefon(), dto.getId())) {
		    	errs.AddChyba("Údaj Telefon již existuje.");
		    }
		} else {
			if (dto.getTelefon() != null && repo.existsByTelefon(dto.getTelefon())) {
		    	errs.AddChyba("Údaj Telefon již existuje.");
		    }
		}

	    if (dto.getTelefon() == null) {
	    	errs.AddChyba("Údaj Telefon není zadán.");
	    }

	    if (dto.getCeleJmeno() == null) {
	    	errs.AddChyba("Údaj Celé jméno není zadán.");
	    }
	    
	    if (dto.getId() != null) {
		    Optional<Zakaznik> ent = repo.findById(dto.getId());
		    if (ent.isEmpty()) {
		    	errs.AddChyba(String.format("Záznam s id %s nenalezen.", dto.getId()));
		    }
	    }
	}
	
	/**
	 * Uložit - nový
	 * 
	 * @param dto
	 * @return
	 */
	public ZakaznikDto ulozitNovy(final ZakaznikDto dto) {
		Zakaznik ent = new Zakaznik();
		dtoToEntity(dto, ent);
		ent = repo.save(ent);
		
		return ZakaznikDto.of(ent);
	}
	
	/**
	 * Uložit - změna
	 * 
	 * @param dto
	 * @return
	 */
	public ZakaznikDto ulozitZmena(final ZakaznikDto dto) {
		Zakaznik ent = repo.findById(dto.getId()).get();
		dtoToEntity(dto, ent);
		ent = repo.save(ent);
		
		return ZakaznikDto.of(ent);
	}
	
	/**
	 * Uložit - zneplatnit
	 * 
	 * @param id
	 * @return
	 */
	public ZakaznikDto ulozitZneplatnit(final Integer id) {
		Zakaznik ent = repo.findById(id).get();
		ent.setPlatnost(false);
		ent = repo.save(ent);
		
		return ZakaznikDto.of(ent);
	}
	
	/**
	 * Kontroly - zneplatnit 
	 * 
	 * @param dto
	 * @return
	 */
	public void kontrolyZneplatnit(final ZakaznikDto dto, final ErrorCollector errs) {
		Optional<Zakaznik> ent = repo.findById(dto.getId());
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
	private void dtoToEntity(final ZakaznikDto dto, final Zakaznik ent) {
		ent.setZmenaCas(LocalDateTime.now());
		ent.setZmenaUzivatel("user"); //TODO tahat z JWT token
		ent.setPlatnost(dto.getPlatnost() == null? true : dto.getPlatnost()); // defualtní hodnota pro nový záznam true, při změně se kontroluje
		ent.setTelefon(dto.getTelefon());
		ent.setCeleJmeno(dto.getCeleJmeno());
	}
}