package cz.inqool.kurt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.inqool.core.ErrorCollector;
import cz.inqool.kurt.domain.Kurt;
import cz.inqool.kurt.domain.KurtDto;
import cz.inqool.povrch.PovrchRepository;
import cz.inqool.povrch.domain.Povrch;

@Service
public class KurtService {
	
	@Autowired
	private KurtRepository repo;
	
	@Autowired
	private PovrchRepository povrchRepository;
	
	/**
	 * Seznam
	 * 
	 * @return
	 */
	public List<KurtDto> seznam() {
		return repo.findAll().stream().map(KurtDto::of).collect(Collectors.toList());
	}
	
	/**
	 * Detail
	 * 
	 * @param id
	 * @return
	 */
	public Optional<KurtDto> detail(final Integer id) {
		return repo.findById(id).map(KurtDto::of);
	}
	
	/**
	 * Kontroly nový, změna
	 * 
	 * @param dto
	 * @param novy
	 * @return
	 */
	public void kontroly(final KurtDto dto, final ErrorCollector errs, final boolean novy) {
		if (!novy) {
			if (dto.getId() == null) {
				errs.AddChyba("Údaj ID není zadán.");
		    }
	
		    if (dto.getPlatnost() == null) {
		    	errs.AddChyba("Údaj Platnost není zadán.");
		    }
		}

		if (dto.getPovrch() == null) {
	    	errs.AddChyba("Údaj Povrch není zadán.");
	    } else {
	    	if (dto.getPovrch().getId() != null) {
			    Optional<Povrch> ent = povrchRepository.findById(dto.getPovrch().getId());
			    if (ent.isEmpty()) {
			    	errs.AddChyba(String.format("Záznam s id %s nenalezen.", dto.getId()));
			    } else {
			    	if (!ent.get().getPlatnost()) {
			    		errs.AddVarovani(String.format("Povrch je neplatný.", ent.get().getNazev()));
			    	}
			    }
		    }
	    }
		
	    if (dto.getNazev() == null) {
	    	errs.AddChyba("Údaj Název není zadán.");
	    }

	    if (dto.getId() != null) {
		    Optional<Kurt> ent = repo.findById(dto.getId());
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
	public KurtDto ulozitNovy(final KurtDto dto) {
		Kurt ent = new Kurt();
		ent.setPovrch(new Povrch());
		dtoToEntity(dto, ent);
		ent = repo.save(ent);
	
		return KurtDto.of(ent);
	}
	
	/**
	 * Uložit - změna
	 * 
	 * @param dto
	 * @return
	 */
	public KurtDto ulozitZmena(final KurtDto dto) {
		Kurt ent = repo.findById(dto.getId()).get();
		dtoToEntity(dto, ent);
		ent = repo.save(ent);
		
		return KurtDto.of(ent);
	}
	
	/**
	 * Uložit - zneplatnit
	 * 
	 * @param id
	 * @return
	 */
	public KurtDto ulozitZneplatnit(final Integer id) {
		Kurt ent = repo.findById(id).get();
		ent.setPlatnost(false);
		ent = repo.save(ent);
		
		return KurtDto.of(ent);
	}
	
	/**
	 * Kontroly - zneplatnit 
	 * 
	 * @param dto
	 * @return
	 */
	public void kontrolyZneplatnit(final KurtDto dto, final ErrorCollector errs) {
		Optional<Kurt> ent = repo.findById(dto.getId());
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
	private void dtoToEntity(final KurtDto dto, final Kurt ent) {
		Povrch p = ent.getPovrch();
		p.setId(dto.getPovrch().getId());
		p.setZmenaUzivatel(dto.getPovrch().getZmenaUzivatel());
		p.setZmenaCas(dto.getPovrch().getZmenaCas());
		p.setPlatnost(dto.getPovrch().getPlatnost());
		p.setCena(dto.getPovrch().getCena());
		
		ent.setZmenaCas(LocalDateTime.now());
		ent.setZmenaUzivatel("user"); //TODO tahat z JWT token
		ent.setPlatnost(dto.getPlatnost() == null? true : dto.getPlatnost()); // defualtní hodnota pro nový záznam true, při změně se kontroluje
		ent.setNazev(dto.getNazev());
		ent.setPovrch(p);
	}
}

