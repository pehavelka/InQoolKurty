package cz.inqool.povrch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cz.inqool.povrch.domain.Povrch;

@Repository
public interface PovrchRepository extends JpaRepository<Povrch, Integer>  {

}
