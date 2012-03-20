package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.Division;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

// TODO: Auto-generated Javadoc
/**
 * The Class DivisionRepository.
 * 
 * @author Dhananjay
 * @since v1.0.0
 */
@Repository
public class DivisionRepository extends BaseRepository<Division, Long> {

	/** The Constant ASC. */
	private static final String ASC = "asc";

	/** The Constant DESC. */
	private static final String DESC = "desc";

	/**
	 * Find divisions by state id.
	 * 
	 * @param stateId
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
	public List<Division> findDivisionsByStateId(final Long stateId,
			final String orderBy, final String sortOrder, final String locale) {
		Search search = new Search();
		search.addFilterEqual("state.id", stateId);
		search.addFilterEqual("locale", locale);
		if (sortOrder.toLowerCase().equals(ASC)) {
			search.addSort(orderBy, false);
		} else {
			search.addSort(orderBy, true);
		}
		List<Division> divisions = this.search(search);
		return divisions;
	}

	/*
	 * public List<Division> findDivisionsByConstituencyId( final Long
	 * constituencyId, final String orderBy, final String sortOrder) { String
	 * select = "SELECT c FROM Constituency c JOIN FETCH c.divisions d " +
	 * "WHERE c.id = " + constituencyId + " ORDER BY d." + orderBy + " " +
	 * sortOrder; Constituency constituency; constituency = (Constituency)
	 * this.em().createQuery(select) .getSingleResult(); return
	 * constituency.getDivisions(); }
	 */

	/**
	 * Find divisions by state name.
	 * 
	 * @param stateName
	 *            the state name
	 * @param orderBy
	 *            the order by
	 * @param sortOrder
	 *            the sort order
	 * @return the list
	 * @author Dhananjay
	 * @since v1.0.0
	 */
	public List<Division> findDivisionsByStateName(String stateName,
			String orderBy, String sortOrder) {
		Search search = new Search();
		search.addFilterEqual("state.name", stateName);
		if (sortOrder.toLowerCase().equals(ASC)) {
			search.addSortAsc(orderBy);
		} else {
			search.addSortDesc(orderBy);
		}
		return this.search(search);
	}

}
