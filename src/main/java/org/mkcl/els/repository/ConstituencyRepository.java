package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Reference;
import org.springframework.stereotype.Repository;
import com.trg.search.Search;

@Repository
public class ConstituencyRepository extends BaseRepository<Constituency, Long> {
  
    @SuppressWarnings("unchecked")
    public List<Constituency> findConstituenciesByDistrictName(final String name,final String locale) {
        District district = (District) District.findByName(District.class,name,locale);
        String constituencyQuery = "SELECT c FROM Constituency c "
                + "WHERE :district MEMBER OF c.districts";
        Query query = this.em().createQuery(constituencyQuery);
        query.setParameter("district", district);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Constituency> findConstituenciesByDistrictId(final Long districtId) {
        District district = (District) District.findById(District.class,districtId);
        String constituencyQuery = "SELECT c FROM Constituency c "
                + "WHERE :district MEMBER OF c.districts";
        Query query = this.em().createQuery(constituencyQuery);
        query.setParameter("district", district);
        return query.getResultList();
    }
    
    public List<Reference> findConstituenciesRefStartingWith(final String param,final String locale) {
        List<Reference> constituencies = new ArrayList<Reference>();
        Search search = new Search().addField("name").addField("name", "id")
                .addFilterILike("name", param + "%").addFilterEqual("locale",locale);
        search.setResultMode(Search.RESULT_MAP);
        constituencies = this.search(search);
        return constituencies;
    }	
  }
