package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.vo.ElectionVO;
import org.mkcl.els.domain.Election;
import org.springframework.stereotype.Repository;

@Repository
public class ElectionRepository extends BaseRepository<Election,Long> {

	@SuppressWarnings("unchecked")
	public List<ElectionVO> findByHouseType(String houseType,String locale) {
		/*
		 * SELECT * FROM masters_elections  AS e JOIN  masters_electiontypes AS et JOIN masters_housetype AS h WHERE e.electiontype_id=et.id
			AND et.housetype_id=h.id AND h.type='lowerhouse' AND e.locale='en_US'
		 */
		String query="SELECT e.id,e.name FROM masters_elections AS e JOIN  masters_electiontypes AS et JOIN masters_housetype AS h "+
		"WHERE e.electiontype_id=et.id AND et.housetype_id=h.id AND h.type='"+houseType+"' AND e.locale='"+locale+"'";
		List elections=this.em().createNativeQuery(query).getResultList();
		List<ElectionVO> electionVOs=new ArrayList<ElectionVO>();
		for(Object i:elections){
			Object[] o=(Object[]) i;
			ElectionVO electionVO=new ElectionVO(Long.parseLong(o[0].toString()),o[1].toString());
			electionVOs.add(electionVO);
		}
		return electionVOs;
	}

}
