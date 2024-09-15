package cz.inqool.zakaznik;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cz.inqool.zakaznik.domain.Zakaznik;

@Repository
public interface ZakaznikRepository extends JpaRepository<Zakaznik, Integer>  {
	
	boolean existsByTelefon(String telefon);

    boolean existsByTelefonAndIdNot(String telefon, Integer id);
    
    Optional<Zakaznik> findByTelefon(String telefon); 
    
}
