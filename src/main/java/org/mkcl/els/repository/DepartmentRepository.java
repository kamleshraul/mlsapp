package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Department;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class DepartmentRepository extends BaseRepository<Department, Serializable>{

    public List<Department> findAllSubDepartments(final String sortBy,
            final String sortOrder, final String locale) {
        Search search=new Search();
        if(sortBy.trim().equals(ApplicationConstants.ASC)){
            search.addSort(sortBy,false);
        }else{
            search.addSort(sortBy,true);
        }
        search.addFilterEqual("locale",locale);
        search.addFilterNotNull("parentId");
        return this.search(search);
    }

}
