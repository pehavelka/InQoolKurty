package cz.inqool.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;


/**
 * Třída pro obsluhu chybových hlášení a varování
 * 
 */
@Data
public class ErrorCollector {

	private List<Map<String, Object>> errors = new ArrayList<>();

	private void add(final EUrovenHlaseni uroven, final String hlaseni) {
		Map<String, Object> error = new HashMap<>();
		error.put("uroven", uroven);
		error.put("popis", hlaseni);
		errors.add(error);
	}
	
	/**
	 * Přidat chybu
	 *  
	 * @param hlaseni
	 */
	public void AddChyba(final String hlaseni) {
		add(EUrovenHlaseni.ERROR, hlaseni);
	}
	
	/**
	 * Přidat varování
	 * 
	 * @param hlaseni
	 */
	public void AddVarovani(final String hlaseni) {
		add(EUrovenHlaseni.WARN, hlaseni);
	}
	
	/**
	 * Vyhodnotí, zda kontrola končí chybou 400
	 * 
	 * @param provest
	 * @return
	 */
	public boolean konec400(final boolean provest) {
		if (!errors.isEmpty()) {
			if (!provest || errors.stream()
			        .map(error -> (EUrovenHlaseni) error.get("uroven"))
			        .filter(f -> EUrovenHlaseni.ERROR.equals(f))
			        .count() > 0) {
				return true;
			}
		}
		
		return false;
	}
}
