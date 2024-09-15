package cz.inqool.rezervace;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cz.inqool.kurt.domain.Kurt;
import cz.inqool.rezervace.domain.Rezervace;

@Repository
public interface RezervaceRepository extends JpaRepository<Rezervace, Integer>  {

	List<Rezervace> findByKurtAndDatumDoGreaterThanEqualAndDatumOdLessThanEqual(Kurt kurt, LocalDateTime datumOd, LocalDateTime datumDo);
	
	List<Rezervace> findByIdNotAndKurtAndDatumDoGreaterThanEqualAndDatumOdLessThanEqual(Integer id,Kurt kurt, LocalDateTime datumOd, LocalDateTime datumDo);
	
	List<Rezervace> findByKurt_IdOrderByDatumOdAsc(Integer kurtId);
	
	List<Rezervace> findByZakaznik_TelefonAndDatumOdGreaterThanEqualOrderByDatumOdAsc(String telefon, LocalDateTime datumOd);
}
