package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class SessionRepository extends BaseRepository<Session, Long>{

    public Session findLatestSession(final HouseType houseType,final Integer sessionYear){
        //Inorder to find latest session we need two things.first housetype and then session year
        //This is because session are numbered from 1,2,3,4,.. each year.The session whose start date occurs
        //before is given a small number than the one that occurs after.e.g budget session which occurs
        //in the month of march is given number 1 and so on.
        //we will sort all the sessions of given house type and session year according to number in descending
        //order and the one at position 0(top) will be the latest session
        Search search=new Search();
        search.addFilterEqual("house.type",houseType);
        search.addFilterEqual("year",sessionYear);
        search.addSort("number",true);
        List<Session> sessions=this.search(search);
        if(!sessions.isEmpty()){
          return sessions.get(0);
        }else{
            return new Session();
        }
    }

    public List<Session> findSessionsByHouseAndYear(final House house,final Integer year){
        Search search=new Search();
        search.addFilterEqual("house", house);
        search.addFilterEqual("year", year);
        search.addSort("startDate",true);
        return this.search(search);
    }

	public Session findSessionByHouseSessionTypeYear(final House house,
			final SessionType sessionType, final Integer sessionYear) {
		Search search=new Search();
		search.addFilterEqual("house",house);
		search.addFilterEqual("type",sessionType);
		search.addFilterEqual("year",sessionYear);
		return this.searchUnique(search);

	}

    public Session findSessionByHouseTypeSessionTypeYear(final HouseType houseType,
            final SessionType sessionType, final Integer sessionYear) {
        Search search=new Search();
        search.addFilterEqual("house.type",houseType);
        search.addFilterEqual("type",sessionType);
        search.addFilterEqual("year",sessionYear);
        return this.searchUnique(search);
    }
    
    public List<Session> findSessionsByHouseTypeAndYear(final HouseType houseType,
             final Integer sessionYear) {
        Search search=new Search();
        search.addFilterEqual("house.type",houseType);
        search.addFilterEqual("year",sessionYear);
        return this.search(search);
    }


}
