package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class StatusRepository extends BaseRepository<Status, Serializable>{

    public List<Status> findStartingWith(final String pattern,final String sortBy,final String sortOrder,final String locale){
        Search search=new Search();
        search.addFilterLike("type",pattern+"%");
        if(sortOrder.equals(ApplicationConstants.ASC)){
            search.addSort(sortBy, false);
        }else if(sortOrder.equals(ApplicationConstants.DESC)){
            search.addSort(sortBy, true);
        }
        return this.search(search);
    }
}
