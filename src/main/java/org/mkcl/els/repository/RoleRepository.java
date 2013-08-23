package org.mkcl.els.repository;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Role;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class RoleRepository extends BaseRepository<Role, Long>{
	
	public List<Role> findRolesByRoleType(
            final Class persistenceClass,final String fieldName,final String fieldValue,
            final String sortBy,final String sortOrder) throws ELSException {
//        final Search search = new Search();
        StringBuffer strQuery = new StringBuffer(
        		"SELECT r FROM Role r" +
        		" WHERE r." + fieldName + "=:" + fieldName
        		);
        
        try{
        	if (sortOrder.toLowerCase().equals(ApplicationConstants.ASC) || sortOrder.toLowerCase().equals(ApplicationConstants.DESC)) {
        		strQuery.append(" ORDER BY r." + sortBy + " " + sortOrder);
            }

//        	search.addFilterEqual(fieldName, fieldValue);
//          final List<Role> records = this._search(persistenceClass, search);
        	TypedQuery<Role> jpQuery = this.em().createQuery(strQuery.toString(), Role.class);
        	jpQuery.setParameter(fieldName, fieldValue);
        	final List<Role> records = jpQuery.getResultList();
            
            return records;
            
        }catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("RoleRepository_List<Role>_findRolesByRoleType", "Roles Not found");
			throw elsException;
        }
        
    }

	public String findDelimitedQISRoles(final String locale) throws ELSException {
		String strquery="SELECT m FROM Role m" +
						" WHERE m.locale=:locale"+
						" AND (m.type LIKE :pattern" +
						" OR m.type='SUPER_ADMIN') ORDER BY m.type";
		try{
			TypedQuery<Role> query=this.em().createQuery(strquery, Role.class);
			query.setParameter("locale", locale);
			query.setParameter("pattern", "QIS_%");
			List<Role> roles=query.getResultList();
			StringBuffer buffer=new StringBuffer();
			for(Role i:roles){
				buffer.append(i.getType()+",");
			}
			buffer.deleteCharAt(buffer.length()-1);
			return buffer.toString();
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("RoleRepository_String_findDelimitedQISRoles", "Roles Not found");
			throw elsException;
        }
	
	}

}
