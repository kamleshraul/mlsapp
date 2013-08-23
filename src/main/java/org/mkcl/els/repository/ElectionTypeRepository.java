package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Election;
import org.mkcl.els.domain.ElectionType;
import org.springframework.stereotype.Repository;

@Repository
public class ElectionTypeRepository extends BaseRepository<ElectionType, Long> {

	/**
	 * @param houseType
	 * @param locale
	 * @return
	 * @throws ELSException
	 */
	public List<ElectionType> findByHouseType(final String houseType, final String locale) throws ELSException {
		String query = null;
		Boolean flag = false;
		List<ElectionType> electionTypes = new ArrayList<ElectionType>();
		
		if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
			query = "SELECT e FROM ElectionType e" +
					" WHERE e.locale=:locale";
			flag = true;
		} else {
			query = "SELECT e FROM ElectionType e" +
					" WHERE e.locale=:locale" +
					" AND e.houseType.type=:houseType";
		} 
		
		try{
			/**** Used TypedQuery so as to remove the warning of raw type conversion(Unchecked conversion) ****/ 
			TypedQuery<ElectionType> jpQuery = this.em().createQuery(query, ElectionType.class);
			jpQuery.setParameter("locale", locale);		
			if(!flag){
				jpQuery.setParameter("houseType", houseType);
			}
			/**** To assure empty result set is returned ****/
			electionTypes = jpQuery.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ElectionTypeRepository_List<ElectionType>_findByHouseType", "Election type is unavailable.");
			throw elsException;
		}
		
		return electionTypes;
	}
}
