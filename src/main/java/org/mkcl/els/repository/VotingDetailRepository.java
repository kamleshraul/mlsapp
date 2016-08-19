package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.VotingDetail;
import org.springframework.stereotype.Repository;

@Repository
public class VotingDetailRepository extends BaseRepository<VotingDetail, Long>{
	
	public List<VotingDetail> findByVotingForDeviceInGivenHouse(final Device device, 
			final DeviceType deviceType, final HouseType houseType, final String votingFor) {
		String strQuery = null;
		if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_BILLS)) {
			strQuery="SELECT vd FROM Bill m JOIN m.votingDetails vd"+
					" WHERE vd.houseType=:houseType"+
					" AND vd.votingFor=:votingFor"+
					" AND m.id=:device ORDER BY vd.houseRound "+ApplicationConstants.DESC;
		}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
			strQuery="SELECT vd FROM Resolution r JOIN r.votingDetail vd" +
					" WHERE vd.houseType=:houseType" +
					" AND vd.votingFor=:votingFor"+
					" AND r.id=:device";
		}
		if(strQuery!=null) {
			Query query=this.em().createQuery(strQuery);
			query.setParameter("houseType",houseType.getType());
			query.setParameter("votingFor",votingFor);
			query.setParameter("device",device.getId());			
			return query.getResultList();
		} else {
			return null;
		}
	}

}
