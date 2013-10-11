package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Town;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class DistrictRepository.
 *
 * @author Dhananjay
 * @since v1.0.0
 */
@Repository
public class DistrictRepository extends BaseRepository<District, Long> {

	/** The Constant ASC. */
	private static final String ASC = "asc";

	/**
	 * Find districts by division id.
	 *
	 * @param divisionId
	 *            the division id
	 * @param orderBy
	 *            the order by
	 * @param sortOrder
	 *            the sort order
	 * @return the list
	 * @author Dhananjay
	 * @throws ELSException 
	 * @since v1.0.0
	 */
	public List<District> findDistrictsByDivisionId(final Long divisionId,
			final String orderBy, final String sortOrder, final String locale) throws ELSException {
		
		String strquery="SELECT d FROM District d WHERE d.division.id=:divisionId"+
				" AND d.locale=:locale ORDER BY d."+orderBy+" "+sortOrder;
		List<District> districts = new ArrayList<District>();
		try{
			TypedQuery<District> query = this.em().createQuery(strquery,District.class);
			query.setParameter("divisionId", divisionId);
			query.setParameter("locale", locale);
			List<District> dX = query.getResultList();
			
			if(dX != null){
				districts = dX;
			}
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ConstituencyRepository_List<District>_findDistrictsByDivisionId", "No district found.");
			throw elsException;
		}
		return districts;
	}

	/**
	 * Find districts by constituency id.
	 *
	 * @param constituencyId
	 *            the constituency id
	 * @param orderBy
	 *            the order by
	 * @param sortOrder
	 *            the sort order
	 * @return the list
	 * @author Dhananjay
	 * @throws ELSException 
	 * @since v1.0.0
	 */
	public List<District> findDistrictsByConstituencyId(
			final Long constituencyId, final String orderBy,
			final String sortOrder) throws ELSException {
		String select = "SELECT c FROM Constituency c JOIN FETCH c.districts d "
				+ "WHERE c.id =:constituencyId ORDER BY d."+ orderBy+ " " + sortOrder;
		Constituency constituency;
		try{
			Query query = this.em().createQuery(select);
			query.setParameter("constituencyId", constituencyId);
			constituency = (Constituency) query.getSingleResult();
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("DistrictRepository_List<District>_findDistrictsByDivisionId", "No district found.");
			throw elsException;
		}
		return constituency.getDistricts();
	}

	/**
	 * Find districts by division name.
	 *
	 * @param divisionName
	 *            the division name
	 * @param orderBy
	 *            the order by
	 * @param sortOrder
	 *            the sort order
	 * @return the list
	 * @author Dhananjay
	 * @throws ELSException 
	 * @since v1.0.0
	 */
	public List<District> findDistrictsByDivisionName(final String divisionName,
			final String orderBy, final String sortOrder, final String locale) throws ELSException {
		String strQuery="SELECT d FROM District d WHERE d.division.name=:divisionName"+
				" AND d.locale=:locale ORDER BY d."+ orderBy+ " "+ sortOrder;
		List<District> districts = new ArrayList<District>();
		
		try{
			TypedQuery<District> query=this.em().createQuery(strQuery, District.class);
			query.setParameter("divisionName", divisionName);
			query.setParameter("locale", locale);
			List<District> dX = query.getResultList();
			if(dX != null){
				districts = dX;
			}
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ConstituencyRepository_List<District>_findDistrictsByDivisionId", "No district found.");
			throw elsException;
		}
		
		return districts;
		
	}

	/**
	 * Find districts by state id.
	 *
	 * @param StateId
	 *            the state id
	 * @param orderBy
	 *            the order by
	 * @param sortOrder
	 *            the sort order
	 * @param locale
	 *            the locale
	 * @return the list
	 * @author Dhananjay
	 * @throws ELSException 
	 * @since v1.0.0
	 */
	@SuppressWarnings("unchecked")
	public List<District> findDistrictsByStateId(final Long stateId,
			final String orderBy, final String sortOrder, final String locale) throws ELSException {
		String strquery = "SELECT d FROM District d Join d.division div Join div.state s" +
				" WHERE s.id=:stateId AND d.locale=:locale ORDER BY d."+ orderBy + " " + sortOrder;
		List<District> districts = new ArrayList<District>();
		
		try{
			Query query=this.em().createQuery(strquery);
			query.setParameter("stateId", stateId);
			query.setParameter("locale", locale);
			
			List<District> dX = query.getResultList();
			if(dX != null){
				districts = dX;
			}
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("DistrictRepository_List<District>_findDistrictsByStateId", "No district found.");
			throw elsException;
		}
		return districts;
	}

	/**
	 * Find districts by state name.
	 *
	 * @param StateName
	 *            the state name
	 * @param orderBy
	 *            the order by
	 * @param sortOrder
	 *            the sort order
	 * @return the list
	 * @author Dhananjay
	 * @throws ELSException 
	 * @since v1.0.0
	 */
	public List<District> findDistrictsByStateName(final String stateName,
			final String orderBy, final String sortOrder) throws ELSException {
		String strquery="SELECT d FROM District d WHERE d.division.state.name=:stateName ORDER BY d."+orderBy+" "+ sortOrder;
		List<District> districts = new ArrayList<District>();
		
		try{
			TypedQuery<District> jpQuery=this.em().createQuery(strquery,District.class);
			jpQuery.setParameter("stateName", stateName);

			List<District> dX = jpQuery.getResultList();
			if(dX != null){
				districts = dX;
			}
			
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("DistrictRepository_List<District>_findDistrictsByStateName", "No district found.");
			throw elsException;
		}
		return districts;
	}

	public List<Reference> findDistrictsRefByStateId(final Long stateId,
			final String sortBy, final String sortOrder, final String locale) throws ELSException {
		String strquery = "SELECT d.id,d.name FROM District d" +
					" Join d.division div" +
					" Join div.state s" +
					" WHERE s.id=:stateId" +
					" AND d.locale=:locale" +
					" ORDER BY d."+ sortBy + " " + sortOrder;
		
		List<Reference> districtsRef=new ArrayList<Reference>();
		
		try{
			Query query = this.em().createQuery(strquery);
			query.setParameter("stateId", stateId);
			query.setParameter("locale", locale);
			List districts= query.getResultList();
			
			for(Object i:districts){
				Object[] o=(Object[]) i;
				Reference reference=new Reference(o[0].toString(),o[1].toString());
				districtsRef.add(reference);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("DistrictRepository_List<Reference>_findDistrictsRefByStateId", "No district found.");
			throw elsException;
		}
		
		return districtsRef;
	}

	public District find(final Town town, 
			final String locale) {
		StringBuffer query = new StringBuffer();
		query.append("SELECT d" +
			" FROM Town t JOIN District d" +
			" WHERE t.id = " + town.getId());
		
		TypedQuery<District> tQuery = 
			this.em().createQuery(query.toString(), District.class);
		District district = tQuery.getSingleResult();
		return district;
	}

}