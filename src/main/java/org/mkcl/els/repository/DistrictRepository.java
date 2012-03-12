
package org.mkcl.els.repository;
import java.util.List;

import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.District;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class DistrictRepository extends BaseRepository<District, Long> {

    /** The Constant ASC. */
    private static final String ASC = "asc";

    /** The Constant DESC. */
    private static final String DESC = "desc";       

   
    public List<District> findDistrictsByStateId(final Long stateId,
                                                 final String orderBy,
                                                 final String sortOrder) {
        Search search = new Search();
        search.addFilterEqual("state.id", stateId);
        if(sortOrder.toLowerCase().equals(ASC)){
        search.addSort(orderBy, false);
        }else{
        search.addSort(orderBy, true);
        }
        List<District> districts = this.search(search);
        return districts;
    } 

  
    public List<District> findDistrictsByConstituencyId(final Long constituencyId,
                                                        final String orderBy,
                                                        final String sortOrder) {       
        String select = "SELECT c FROM Constituency c JOIN FETCH c.districts d "
                + "WHERE c.id = "
                + constituencyId
                + " ORDER BY d."
                + orderBy + " " + sortOrder;
        Constituency constituency;
        constituency = (Constituency) this.em().createQuery(select)
                .getSingleResult();
        return constituency.getDistricts();
    }
    
	public List<District> findDistrictsByStateName(String stateName,
			String orderBy, String sortOrder) {
		Search search=new Search();
		search.addFilterEqual("state.name", stateName);	
		if(sortOrder.toLowerCase().equals(ASC)){
			search.addSortAsc(orderBy);
		}else{
			search.addSortDesc(orderBy);
		}
		return this.search(search);
	}

}
