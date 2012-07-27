package org.mkcl.els.repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class GroupRepository extends BaseRepository<Group, Long> {

	public List<Group> findByHouseTypeSessionTypeYear(final HouseType houseType,final SessionType sessionType,final Integer year){
		Search search=new Search();
		search.addFilterEqual("houseType",houseType);
		search.addFilterEqual("sessionType",sessionType);
		search.addFilterEqual("year",year);
		search.addSort("number",false);
		return this.search(search);
	}

	public List<String> findAnsweringDates(Long id) {
		String query="SELECT answering_date,locale FROM question_dates WHERE id="+id+" ORDER BY answering_date asc";
		List dates=this.em().createNativeQuery(query).getResultList();
		List<String> answeringDates=new ArrayList<String>();
		for(Object i:dates){
			Object[] o=(Object[]) i;
			if(o[0]!=null){
				try {
					Date dateDBFormat=FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATEFORMAT,o[1].toString()).parse(o[0].toString());
					String answeringDate=FormaterUtil.getDateFormatter(ApplicationConstants.SERVER_DATEFORMAT,o[1].toString()).format(dateDBFormat);
					answeringDates.add(answeringDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return answeringDates;
	}

	
}
