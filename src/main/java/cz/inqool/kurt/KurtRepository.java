package cz.inqool.kurt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cz.inqool.kurt.domain.Kurt;

@Repository
public interface KurtRepository extends JpaRepository<Kurt, Integer>  {

}
