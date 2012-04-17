package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Reference;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

// TODO: Auto-generated Javadoc
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

	/** The Constant DESC. */
	private static final String DESC = "desc";

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
	 * @since v1.0.0
	 */
	public List<District> findDistrictsByDivisionId(final Long divisionId,
			final String orderBy, final String sortOrder, final String locale) {
		Search search = new Search();
		search.addFilterEqual("division.id", divisionId);
		search.addFilterEqual("locale", locale);
		if (sortOrder.toLowerCase().equals(ASC)) {
			search.addSort(orderBy, false);
		} else {
			search.addSort(orderBy, true);
		}
		List<District> districts = this.search(search);
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
	 * @since v1.0.0
	 */
	public List<District> findDistrictsByConstituencyId(
			final Long constituencyId, final String orderBy,
			final String sortOrder) {
		String select = "SELECT c FROM Constituency c JOIN FETCH c.districts d "
				+ "WHERE c.id = "
				+ constituencyId
				+ " ORDER BY d."
				+ orderBy
				+ " " + sortOrder;
		Constituency constituency;
		constituency = (Constituency) this.em().createQuery(select)
				.getSingleResult();
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
	 * @since v1.0.0
	 */
	public List<District> findDistrictsByDivisionName(String divisionName,
			String orderBy, String sortOrder, String locale) {
		Search search = new Search();
		search.addFilterEqual("division.name", divisionName);
		search.addFilterEqual("locale", locale);
		if (sortOrder.toLowerCase().equals(ASC)) {
			search.addSortAsc(orderBy);
		} else {
			search.addSortDesc(orderBy);
		}
		return this.search(search);
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
	 * @since v1.0.0
	 */
	@SuppressWarnings("unchecked")
	public List<District> findDistrictsByStateId(final Long stateId,
			final String orderBy, final String sortOrder, String locale) {
		String query = "SELECT d FROM District d Join d.division div Join div.state s WHERE s.id="
				+ stateId
				+ " AND d.locale='"
				+ locale
				+ "' ORDER BY d."
				+ orderBy + " " + sortOrder;
		return this.em().createQuery(query).getResultList();
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
	 * @since v1.0.0
	 */
	public List<District> findDistrictsByStateName(String StateName,
			String orderBy, String sortOrder) {
		Search search = new Search();
		search.addFilterEqual("State.name", StateName);
		if (sortOrder.toLowerCase().equals(ASC)) {
			search.addSortAsc(orderBy);
		} else {
			search.addSortDesc(orderBy);
		}
		return this.search(search);
	}

	public List<Reference> findDistrictsRefByStateId(Long stateId,
			String sortBy, String sortOrder, String locale) {
		String query = "SELECT d.id,d.name FROM District d Join d.division div Join div.state s WHERE s.id="
			+ stateId
			+ " AND d.locale='"
			+ locale
			+ "' ORDER BY d."
			+ sortBy + " " + sortOrder;
	List districts= this.em().createQuery(query).getResultList();
	List<Reference> districtsRef=new ArrayList<Reference>();
	for(Object i:districts){
		Object[] o=(Object[]) i;
		Reference reference=new Reference(o[0].toString(),o[1].toString());
		districtsRef.add(reference);	
	}
	return districtsRef;
	}
}
