package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.domain.Reference;
import org.mkcl.els.domain.Tehsil;
import org.springframework.stereotype.Repository;

@Repository
public class TehsilRepository extends BaseRepository<Tehsil,Long> {

	public List<Reference> findTehsilsRefByDistrictId(Long districtId,
			String sortBy, String sortOrder, String locale) {
		String query="SELECT t.id,t.name FROM Tehsil t WHERE t.district.id="+districtId+" AND t.locale='"+locale+"' ORDER BY t."+sortBy+" "+sortOrder;
		List tehsils=this.em().createQuery(query).getResultList();
		List<Reference> tehsilsRef=new ArrayList<Reference>();
		for(Object i:tehsils){
			Object[] o=(Object[]) i;
			Reference reference=new Reference(o[0].toString(),o[1].toString());
			tehsilsRef.add(reference);			
		}
		return tehsilsRef;
	}

}
