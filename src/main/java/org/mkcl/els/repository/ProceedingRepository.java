package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.Proceeding;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

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

	public List<RevisionHistoryVO> getRevisions(Long partId, String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.PART_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("partId",partId);
		query.setParameter("locale",locale);
		List results = query.getResultList();
		List<RevisionHistoryVO> partRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO partRevisionVO = new RevisionHistoryVO();
			if(o[0]!=null) {
				partRevisionVO.setEditedBY(o[0].toString());
			} else {
				partRevisionVO.setEditedBY("");
			}
			if(o[1]!=null) {
				partRevisionVO.setEditedOn(o[1].toString());
			} else {
				partRevisionVO.setEditedOn("");
			}
			if(o[2]!=null && !o[2].toString().isEmpty() && o1 != null && o1[2]!=null && !o1[2].toString().isEmpty()){
				LinkedList<Diff> diff=d.diff_main(o1[2].toString(), o[2].toString());
				String partContent = d.diff_prettyHtml(diff);
				if(partContent.contains("&lt;")){
					partContent = partContent.replaceAll("&lt;", "<");
				}
				if(partContent.contains("&gt;")){
					partContent = partContent.replaceAll("&gt;", ">");
				}
				if(partContent.contains("&amp;nbsp;")){
					partContent = partContent.replaceAll("&amp;nbsp;"," ");
				}
				partRevisionVO.setDetails(partContent);
			}else{
				if(o[2]!=null) {
					partRevisionVO.setDetails(o[2].toString());
				} else {
					partRevisionVO.setDetails("");
				}					
			}
			partRevisionVOs.add(partRevisionVO);
		}
		return partRevisionVOs;
	}


}
