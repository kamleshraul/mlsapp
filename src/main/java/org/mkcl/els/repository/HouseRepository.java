package org.mkcl.els.repository;

import java.util.Date;
import org.mkcl.els.domain.House;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class HouseRepository extends BaseRepository<House,Long>{

	public House findCurrentHouse(String locale){
		Search search=new Search();
		search.addFilterEqual("locale",locale);
		Date currentDate=new Date();
		search.addFilterGreaterOrEqual("lastDate", currentDate);
		search.addFilterLessOrEqual("firstDate",currentDate);
		return this.searchUnique(search);
	}
	
	public House findHouseByToFromDate(Date fromDate,Date toDate,String locale){
		Search search=new Search();
		search.addFilterEqual("locale",locale);
		search.addFilterGreaterOrEqual("lastDate", toDate);
		search.addFilterLessOrEqual("firstDate",fromDate);
		return this.searchUnique(search);
	}
}
