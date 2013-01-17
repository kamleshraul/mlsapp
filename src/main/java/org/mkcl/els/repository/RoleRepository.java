package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Role;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class RoleRepository extends BaseRepository<Role, Long>{
	
	public List<Role> findRolesByRoleType(
            final Class persistenceClass, String fieldName, String fieldValue,
            String sortBy, String sortOrder) {
        final Search search = new Search();
        if (sortOrder.toLowerCase().equals(ApplicationConstants.ASC)) {
            search.addSortAsc(sortBy);
        }
        else {
            search.addSortDesc(sortBy);
        }
        search.addFilterEqual(fieldName, fieldValue);
        final List<Role> records = this._search(persistenceClass, search);
        return records;
    }

	@SuppressWarnings("unchecked")
	public String findDelimitedQISRoles(String locale) {
		String query="SELECT m FROM Role m WHERE m.locale='"+locale+
					"' AND (m.type LIKE 'QIS_%' OR m.type='SUPER_ADMIN')  ORDER BY m.type";
		List<Role> roles=this.em().createQuery(query).getResultList();
		StringBuffer buffer=new StringBuffer();
		for(Role i:roles){
			buffer.append(i.getType()+",");
		}
		buffer.deleteCharAt(buffer.length()-1);
		return buffer.toString();
	}

}
