package cz.inqool.povrch;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.inqool.core.ErrorCollector;
import cz.inqool.povrch.domain.Povrch;
import cz.inqool.povrch.domain.PovrchDto;

@Service
public class PovrchService {
	
	@Autowired
	private PovrchRepository repo;
	
	/**
	 * Seznam
	 * 
	 * @return
	 */
	public List<PovrchDto> seznam() {
		return repo.findAll().stream().map(PovrchDto::of).collect(Collectors.toList());
	}
	
	/**
	 * Detail
	 * 
	 * @param id
	 * @return
	 */
	public Optional<PovrchDto> detail(final Integer id) {
		return repo.findById(id).map(PovrchDto::of);
	}
	
	/**
	 * Kontroly nový, změna
	 * 
	 * @param dto
	 * @param novy
	 * @return
	 */
	public void kontroly(final PovrchDto dto, final ErrorCollector errs, final boolean novy) {
		if (!novy) {
			if (dto.getId() == null) {
				errs.AddChyba("Údaj ID není zadán.");
		    }
	
		    if (dto.getPlatnost() == null) {
		    	errs.AddChyba("Údaj Platnost není zadán.");
		    }
		}

	    if (dto.getNazev() == null) {
	    	errs.AddChyba("Údaj Název není zadán.");
	    }

	    if (dto.getCena() == null) {
	    	errs.AddChyba("Údaj Cena není zadán.");
	    }
	    
	    if (dto.getCena() != null && dto.getCena().compareTo(BigDecimal.valueOf(0.01)) < 0) {
	    	errs.AddChyba("Údaj Cena musí být větší než 0.");
	    }

	    if (dto.getId() != null) {
		    Optional<Povrch> ent = repo.findById(dto.getId());
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
	public PovrchDto ulozitNovy(final PovrchDto dto) {
		Povrch ent = new Povrch();
		dtoToEntity(dto, ent);
		ent = repo.save(ent);
		
		return PovrchDto.of(ent);
	}
	
	/**
	 * Uložit - změna
	 * 
	 * @param dto
	 * @return
	 */
	public PovrchDto ulozitZmena(final PovrchDto dto) {
		Povrch ent = repo.findById(dto.getId()).get();
		dtoToEntity(dto, ent);
		ent = repo.save(ent);
		
		return PovrchDto.of(ent);
	}
	
	/**
	 * Uložit - zneplatnit
	 * 
	 * @param id
	 * @return
	 */
	public PovrchDto ulozitZneplatnit(final Integer id) {
		Povrch ent = repo.findById(id).get();
		ent.setPlatnost(false);
		ent = repo.save(ent);
		
		return PovrchDto.of(ent);
	}
	
	/**
	 * Kontroly - zneplatnit 
	 * 
	 * @param dto
	 * @return
	 */
	public void kontrolyZneplatnit(final PovrchDto dto, final ErrorCollector errs) {
		Optional<Povrch> ent = repo.findById(dto.getId());
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
	private void dtoToEntity(final PovrchDto dto, final Povrch ent) {
		ent.setZmenaCas(LocalDateTime.now());
		ent.setZmenaUzivatel("user"); //TODO tahat z JWT token
		ent.setPlatnost(dto.getPlatnost() == null? true : dto.getPlatnost()); // defualtní hodnota pro nový záznam true, při změně se kontroluje
		ent.setNazev(dto.getNazev());
		ent.setCena(dto.getCena());
	}
}
