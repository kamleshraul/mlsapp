/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MemberHouseRoleRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class MemberHouseRoleRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class MemberHouseRoleRepository extends
BaseRepository<HouseMemberRoleAssociation, Serializable> {

	/**
	 * Find by member id and id.
	 *
	 * @param memberId the member id
	 * @param recordIndex the record index
	 * @return the house member role association
	 */
	public HouseMemberRoleAssociation findByMemberIdAndId(final Long memberId,
			final int recordIndex) {
		String query = "SELECT m FROM HouseMemberRoleAssociation m WHERE m.member.id="
			+ memberId + " AND m.recordIndex=" + recordIndex;

		try {
			return (HouseMemberRoleAssociation) this.em().createQuery(query)
			.getSingleResult();
		}
		catch (NoResultException e) {
			e.printStackTrace();
			return new HouseMemberRoleAssociation();
		}
	}

	/**
	 * Find highest record index.
	 *
	 * @param member the member
	 * @return the int
	 */
	@SuppressWarnings("unchecked")
	public int findHighestRecordIndex(final Long member) {
		String query = "SELECT m FROM HouseMemberRoleAssociation m WHERE m.member.id="
			+ member + " ORDER BY m.recordIndex desc LIMIT 1";
		List<HouseMemberRoleAssociation> associations = this.em()
		.createQuery(query).getResultList();
		if(associations.isEmpty()){
			return 0;
		}else{
			return associations.get(0).getRecordIndex();
		}
	}

	/**
	 * Find by pk.
	 *
	 * @param association the association
	 * @return the house member role association
	 */
	public HouseMemberRoleAssociation findByPK(
			final HouseMemberRoleAssociation association) {
		Search search = new Search();
		search.addFilterEqual("member", association.getMember());
		search.addFilterEqual("role", association.getRole());
		search.addFilterEqual("house", association.getHouse());
		search.addFilterEqual("recordIndex", association.getRecordIndex());
		if(association.getFromDate()==null){
			search.addFilterNull("fromDate");
		}else{
			search.addFilterEqual("fromDate", association.getFromDate());
		}
		if(association.getToDate()==null){
			search.addFilterNull("toDate");
		}else{
			search.addFilterEqual("toDate", association.getToDate());
		}
		return (HouseMemberRoleAssociation) this.searchUnique(search);
	}

	@SuppressWarnings("unchecked")
	public List<HouseMemberRoleAssociation> findByMemberIdRolePriorityHouseId(
			final Long member, final int rolepriority, final Long house, final String locale) {
		String query="SELECT hmra FROM HouseMemberRoleAssociation hmra JOIN hmra.role r JOIN hmra.member m JOIN hmra.house h" +
		" WHERE hmra.locale='"+locale+"' AND m.id="+member+" AND r.priority="+rolepriority+" AND h.id="+house;
		List<HouseMemberRoleAssociation> houseMemberRoleAssociations=this.em().createQuery(query).getResultList();
		return houseMemberRoleAssociations;
	}

	@SuppressWarnings("rawtypes")
	public List<MasterVO> findAllActiveMemberVOSInSession(final House house,
			final Session session, final String locale) {
		List<MasterVO> memberVOS=new ArrayList<MasterVO>();
		try {
			Date sessionStartDate=session.getStartDate();
			Date sessionEndDate=session.getEndDate();
			String query=null;
			if(sessionStartDate!=null && sessionEndDate!=null){
				SimpleDateFormat format=new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT);
				String strSessionStartDate=format.format(sessionStartDate);
				String strSessionEndDate=format.format(sessionEndDate);
				query="SELECT m.id,t.name,m.first_name,m.middle_name,m.last_name FROM members_houses_roles as mhr JOIN members as m JOIN memberroles as mr "+
				" JOIN titles as t WHERE t.id=m.title_id and mr.id=mhr.role and mhr.member=m.id and m.locale='"+locale+"' "+
				" and mhr.to_date>='"+strSessionStartDate+"' and mhr.to_date>='"+strSessionEndDate+"' and mr.priority=0 and mhr.house_id="+house.getId()+" ORDER BY m.last_name asc";
				List members=this.em().createNativeQuery(query).getResultList();
				for(Object i:members){
					Object[] o=(Object[]) i;
					MasterVO masterVO=new MasterVO();
					masterVO.setId(Long.parseLong(o[0].toString()));
					if(o[3]!=null){
						masterVO.setName(o[4].toString()+", "+o[1].toString()+" "+o[2].toString()+" "+o[3].toString());
					}else{
						masterVO.setName(o[4].toString()+", "+o[1].toString()+" "+o[2].toString());
					}
					memberVOS.add(masterVO);
				}
			}
			return memberVOS;
		} catch (Exception e) {
			e.printStackTrace();
			return memberVOS;
		}
		}

	@SuppressWarnings("rawtypes")
    public List<MasterVO> findAllActiveMemberVOSInSession(final House house,
            final Session session, final String locale,final String param) {
        List<MasterVO> memberVOS=new ArrayList<MasterVO>();
        try {
            Date sessionStartDate=session.getStartDate();
            Date sessionEndDate=session.getEndDate();
            String query=null;
            if(sessionStartDate!=null && sessionEndDate!=null){
                SimpleDateFormat format=new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT);
                String strSessionStartDate=format.format(sessionStartDate);
                String strSessionEndDate=format.format(sessionEndDate);
                query="SELECT m.id,t.name,m.first_name,m.middle_name,m.last_name FROM members_houses_roles as mhr JOIN members as m JOIN memberroles as mr "+
                " JOIN titles as t WHERE t.id=m.title_id and mr.id=mhr.role and mhr.member=m.id and m.locale='"+locale+"' "+
                " and mhr.to_date>='"+strSessionStartDate+"' and mhr.to_date>='"+strSessionEndDate+"' and mr.priority=0 and mhr.house_id="+house.getId()+" and (m.first_name LIKE '%"+param+"%' OR m.middle_name LIKE '%"+param+"%' OR m.last_name LIKE '%"+param+"%' OR concat(m.last_name,' ',m.first_name) LIKE '%"+param+"%' OR concat(m.first_name,' ',m.last_name) LIKE '%"+param+"%' OR concat(m.last_name,' ',m.first_name,' ',m.middle_name) LIKE '%"+param+"%' OR concat(m.last_name,', ',t.name,' ',m.first_name,' ',m.middle_name) LIKE '%"+param+"%' OR concat(m.first_name,' ',m.middle_name,' ',m.last_name) LIKE '%"+param+"%')  ORDER BY m.last_name asc";
                List members=this.em().createNativeQuery(query).getResultList();
                for(Object i:members){
                    Object[] o=(Object[]) i;
                    MasterVO masterVO=new MasterVO();
                    masterVO.setId(Long.parseLong(o[0].toString()));
                    if(o[3]!=null){
                        masterVO.setName(o[4].toString()+", "+o[1].toString()+" "+o[2].toString()+" "+o[3].toString());
                    }else{
                        masterVO.setName(o[4].toString()+", "+o[1].toString()+" "+o[2].toString());
                    }
                    memberVOS.add(masterVO);
                }
            }
            return memberVOS;
        } catch (Exception e) {
            e.printStackTrace();
            return memberVOS;
        }
        }
}
