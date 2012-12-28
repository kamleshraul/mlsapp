package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberBallotRepository extends BaseRepository<MemberBallot, Serializable>{

    public Boolean createMemberBallot(final Session session,final DeviceType deviceType,final Boolean attendanceType,final Integer round,final String locale){
        /*
         * First we make a check of whether member ballot for given session,device type,round and
         * attendance(present or absent) has already taken .
         */
        Search search=new Search();
        search.addFilterEqual("session",session);
        search.addFilterEqual("deviceType",deviceType);
        search.addFilterEqual("round",round);
        search.addFilterEqual("attendance",attendanceType);
        int count=this.count(search);
        if(count==0){
        List<Member> input=MemberBallotAttendance.findMembersByAttendance(session,deviceType,attendanceType,locale);
        int order=1;
        Collections.shuffle(input);
        Date date=new Date();
        for(Member i:input){
            MemberBallot memberBallot=new MemberBallot();
            memberBallot.setSession(session);
            memberBallot.setDeviceType(deviceType);
            memberBallot.setMember(i);
            memberBallot.setBallotDate(date);
            memberBallot.setRound(round);
            memberBallot.setPosition(order);
            memberBallot.setAttendance(attendanceType);
            memberBallot.setLocale(locale);
            memberBallot.persist();
            order++;
        }
        return true;
        }else{
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Member> viewMemberBallot(final Session session, final DeviceType deviceType,final Boolean attendanceType,
            final int round, final String locale) {
        String query="SELECT m FROM MemberBallot mb JOIN mb.member m WHERE mb.session.id="+session.getId()+"  "+
                     " AND mb.deviceType.id="+deviceType.getId()+" AND mb.round="+round+" AND mb.attendance="+attendanceType+" AND mbc.locale='"+locale+"' ORDER BY mbc.position "+ApplicationConstants.ASC;
        List<Member> members=new ArrayList<Member>();
        members=this.em().createQuery(query).getResultList();
        return members;
    }
}
