package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.vo.ConstituencyVO;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Reference;
import org.mkcl.els.domain.State;
import org.springframework.stereotype.Repository;


@Repository
public class ConstituencyRepository extends BaseRepository<Constituency, Long>{
	
	public List<ConstituencyVO> findByDefaultStateAndHouseType(String defaultState,
			String houseType, String locale, String sortBy, String sortOrder) {
		/*	query1
		 *  SELECT c.display_name,c.id FROM masters_constituencies AS c WHERE c.id IN(
			SELECT constituency_id FROM associations_constituency_district AS a WHERE district_id IN(
			SELECT d.id FROM masters_districts AS d JOIN masters_divisions AS di JOIN masters_states AS s WHERE s.locale='en_US' AND s.name='Maharashtra'
			AND d.division_id=di.id AND di.state_id=s.id));
		 */
		/*
		 * 	SELECT c.display_name,c.id FROM masters_constituencies AS c JOIN masters_housetype AS h WHERE h.type='lowerhouse' AND c.id IN(
			SELECT constituency_id FROM associations_constituency_district AS a WHERE district_id IN(
			SELECT d.id FROM masters_districts AS d JOIN masters_divisions AS di JOIN masters_states AS s WHERE s.locale='en_US' AND s.name='Maharashtra'
			AND d.division_id=di.id AND di.state_id=s.id));
		 */
		String query="SELECT c.display_name,c.id FROM masters_constituencies AS c JOIN masters_housetype "+
					  "AS h WHERE h.type='"+houseType+"' AND c.id IN("+
					  "SELECT constituency_id FROM associations_constituency_district AS a WHERE district_id IN("+
						"SELECT d.id FROM masters_districts AS d JOIN masters_divisions AS di JOIN masters_states AS s "+
						"WHERE s.locale='"+locale+"' AND s.name='"+defaultState+"' AND d.division_id=di.id AND di.state_id=s.id))"+
						" ORDER BY c.name";
		List constituencies= this.em().createNativeQuery(query).getResultList();
		List<ConstituencyVO> constituencyVOs=new ArrayList<ConstituencyVO>();
		for(Object i:constituencies){
			Object[] o=(Object[]) i;
			ConstituencyVO constituencyVO=new ConstituencyVO(Long.parseLong(o[1].toString()),o[0].toString());
			constituencyVOs.add(constituencyVO);
		}
		return constituencyVOs;
	}

}
