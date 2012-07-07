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

    public Session findLatestSession(final House house){
        Search search=new Search();
        search.addFilterEqual("house", house);
        search.addSort("startDate",true);
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
}
