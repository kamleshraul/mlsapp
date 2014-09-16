package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.domain.Proceeding;
import org.mkcl.els.domain.Slot;
import org.springframework.stereotype.Repository;

@Repository
public class ProceedingRepository extends BaseRepository<Proceeding, Serializable>{

	public Boolean removePart(Proceeding proceeding, Long partId) {
		try{
			String query2="DELETE from bookmarks where master_part="+partId+" OR slave_part="+partId;
			this.em().createNativeQuery(query2).executeUpdate();
			String query = "DELETE FROM parts_drafts_association WHERE part_id ="+partId;
			this.em().createNativeQuery(query).executeUpdate();
			String query3="DELETE from parts WHERE id="+partId;
			this.em().createNativeQuery(query3).executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public List<Proceeding> findAllFilledProceedingBySlot(Slot s) {
		String strQuery="SELECT DISTINCT proc FROM Proceeding proc JOIN proc.parts p WHERE proc.slot=:slot";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("slot", s);
		List<Proceeding> proceedings=query.getResultList();
		return proceedings;
	}


}
