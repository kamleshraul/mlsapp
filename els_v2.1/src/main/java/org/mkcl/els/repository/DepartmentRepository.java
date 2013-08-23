package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Department;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class DepartmentRepository extends BaseRepository<Department, Serializable>{

	public List<Department> findAllSubDepartments(final String sortBy,
			final String sortOrder, final String locale) throws ELSException {
		String strquery="SELECT d FROM Department d" +
				" WHERE d.locale=:locale AND d.parentId IS NOT NULL ORDER BY d."+sortBy+" "+sortOrder;
		List<Department> departments = new ArrayList<Department>();
		try{
			TypedQuery<Department> query=this.em().createQuery(strquery,Department.class);
			query.setParameter("locale", locale);
			
			departments = query.getResultList();
			
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("DepartmentRepository_List<Department>_findAllSubDepartments", "No department found.");
			throw elsException;
		}
		return departments;
	}
}