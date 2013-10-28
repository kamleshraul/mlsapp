package org.mkcl.els.repository;

import java.io.Serializable;

import org.mkcl.els.domain.Proceeding;
import org.springframework.stereotype.Repository;

@Repository
public class ProceedingRepository extends BaseRepository<Proceeding, Serializable>{

	public Boolean removePart(Proceeding proceeding, Long partId) {
		try{
			String query2="DELETE from bookmarks where master_part="+partId+" OR slave_part="+partId;
			this.em().createNativeQuery(query2).executeUpdate();
			String query3="DELETE from parts WHERE id="+partId;
			this.em().createNativeQuery(query3).executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}


}
