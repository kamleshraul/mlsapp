package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallotCouncil;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberBallotCouncilRepository extends BaseRepository<MemberBallotCouncil, Serializable>{

	public Boolean createMemberBallot(final Session session,final DeviceType deviceType,final Integer round,final String locale){
		/*
		 * First we make a check of whether member ballot for given session and given
		 * round has already taken .
		 */
		Search search=new Search();
		search.addFilterEqual("session",session);
		search.addFilterEqual("round",round);
		search.addFilterEqual("deviceType",deviceType);
		int count=this.count(search);
		if(count==0){
		/*
		 * For council we need to arrange all the active members of council  between the session period
		 * in some random patterns spread across 5 rounds.
		 */
		/*
		 * For first round we take all those  members of councils who will be active for the duration of sessions
		 * sorted in asc order of their last names as input for shuffling
		 */
		List<Member> activeMembers=HouseMemberRoleAssociation.findAllActiveMembersInSession(session, locale);
		int order=1;
		Collections.shuffle(activeMembers);
		Date date=new Date();
		for(Member i:activeMembers){
			MemberBallotCouncil memberBallotCouncil=new MemberBallotCouncil();
			memberBallotCouncil.setBallotDate(date);
			memberBallotCouncil.setLocale(locale);
			memberBallotCouncil.setMember(i);
			memberBallotCouncil.setRound(round);
			memberBallotCouncil.setSession(session);
			memberBallotCouncil.setDeviceType(deviceType);
			memberBallotCouncil.setPosition(order);
			memberBallotCouncil.persist();
			order++;
		}
		return true;
		}else{
			return false;
		}
	}

	@SuppressWarnings("unchecked")
    public List<Member> viewMemberBallot(final Session session, final DeviceType deviceType,
			final int round, final String locale) {
	    String query="SELECT m FROM MemberBallotCouncil mbc JOIN mbc.member m WHERE mbc.session.id="+session.getId()+"  "+
	                 " AND mbc.deviceType.id="+deviceType.getId()+" AND mbc.round="+round+" AND mbc.locale='"+locale+"' ORDER BY mbc.position "+ApplicationConstants.ASC;
		List<Member> members=new ArrayList<Member>();
	    members=this.em().createQuery(query).getResultList();
		return members;
	}
}
