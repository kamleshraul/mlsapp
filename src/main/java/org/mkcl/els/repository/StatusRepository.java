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

	@SuppressWarnings("unchecked")
	public List<Status> findAssistantQuestionStatus(final String sortBY,
			final String sortOrder,final String locale) {
		String query="SELECT m FROM Status m WHERE m.locale='"+locale+"' AND (m.type LIKE 'question_%' OR m.type='questions_submit')"+
		"AND m.type<>'questions_complete' and m.type<>'questions_incomplete'"+
		" ORDER BY m.priority "+ApplicationConstants.ASC;
		return this.em().createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Status> findStatusContainedIn(String commadelimitedStatusTypes,
			String locale) {
 		String initialQuery="SELECT s FROM Status s WHERE s.locale='"+locale+"' ";
		StringBuffer buffer=new StringBuffer();
		String[] statusAllowed=commadelimitedStatusTypes.split(",");
		for(String i:statusAllowed){
			if(!i.isEmpty()){
				/**** trim i since there can be extra white spaces present ****/
				buffer.append(" (s.type='"+i.trim()+"') OR");
			}
		}
		buffer.deleteCharAt(buffer.length()-1);
		buffer.deleteCharAt(buffer.length()-1);
		String query=initialQuery+" AND ("+buffer.toString()+") ORDER BY s.priority "+ApplicationConstants.DESC
		+",s.name "+ApplicationConstants.ASC;
		return this.em().createQuery(query).getResultList();
	}
}
