package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.QuestionDates;
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
        search.addFilterEqual("session.id",session.getId());
        search.addFilterEqual("deviceType.id",deviceType.getId());
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

    public List<MemberBallot> findByMember(final Session session,
            final DeviceType deviceType, final Member member, final String locale) {
        Search search=new Search();
        search.addFilterEqual("session.id",session.getId());
        search.addFilterEqual("deviceType.id",deviceType.getId());
        search.addFilterEqual("member.id",member.getId());
        search.addFilterEqual("locale",locale);
        return this.search(search);
    }

    public MemberBallot findByMemberRound(final Session session,
            final DeviceType questionType, final Member member, final int round, final String locale) {
        Search search=new Search();
        search.addFilterEqual("session.id",session.getId());
        search.addFilterEqual("deviceType.id",questionType.getId());
        search.addFilterEqual("member.id",member.getId());
        search.addFilterEqual("round",round);
        search.addFilterEqual("locale",locale);
        return this.searchUnique(search);
    }

    @SuppressWarnings("unchecked")
    public List<MemberBallot> viewMemberBallot(final Session session, final DeviceType deviceType,final Boolean attendanceType,
            final int round, final String locale) {
        String query="SELECT mb FROM MemberBallot mb JOIN mb.member m WHERE mb.session.id="+session.getId()+"  "+
        " AND mb.deviceType.id="+deviceType.getId()+" AND mb.round="+round+" AND mb.attendance="+attendanceType+" AND mb.locale='"+locale+"' ORDER BY mb.position "+ApplicationConstants.ASC;
        List<MemberBallot> memberBallots=new ArrayList<MemberBallot>();
        memberBallots=this.em().createQuery(query).getResultList();
        return memberBallots;
    }

    @SuppressWarnings("unchecked")
    public List<MemberBallot> viewMemberBallot(final Session session,
            final DeviceType deviceType, final boolean attendance, final int round, final Group group,
            final String locale) {
        String query="SELECT mb FROM MemberBallot mb JOIN mb.member m JOIN mb.questionChoices qc JOIN qc.question q JOIN q.group g WHERE mb.session.id="+session.getId()+"  "+
        " AND mb.deviceType.id="+deviceType.getId()+" AND mb.round="+round+" AND mb.attendance="+attendance+" AND g.id="+group.getId()+" AND mb.locale='"+locale+"' ORDER BY mb.position "+ApplicationConstants.ASC;
        List<MemberBallot> memberBallots=new ArrayList<MemberBallot>();
        memberBallots=this.em().createQuery(query).getResultList();
        return memberBallots;
    }

    @SuppressWarnings("unchecked")
    public List<MemberBallot> viewMemberBallot(final Session session,
            final DeviceType deviceType, final boolean attendance, final int round,
            final QuestionDates answeringDate, final String locale) {
        String query="SELECT mb FROM MemberBallot mb JOIN mb.member m JOIN mb.questionChoices qc WHERE mb.session.id="+session.getId()+"  "+
        " AND mb.deviceType.id="+deviceType.getId()+" AND mb.round="+round+" AND mb.attendance="+attendance+" AND qc.newAnsweringDate.id="+answeringDate.getId()+" AND mb.locale='"+locale+"' ORDER BY mb.position "+ApplicationConstants.ASC;
        List<MemberBallot> memberBallots=new ArrayList<MemberBallot>();
        memberBallots=this.em().createQuery(query).getResultList();
        return memberBallots;
    }

    public Integer findPrimaryCount(final Session session, final DeviceType deviceType,
            final String locale) {
        String query="SELECT q FROM MemberBallot mb JOIN mb.questionChoices qc JOIN qc.question q"+
        " WHERE mb.session.id="+session.getId()+" AND mb.deviceType.id="+deviceType.getId()+" "+
        " AND mb.locale='"+locale+"' AND q.parent=null";
        Integer count=this.em().createQuery(query).getResultList().size();
        return count;
    }

    public Boolean updateClubbing(final Session session, final DeviceType deviceType,
            final int start, final int size, final String locale) {
        try {
            this.em().createNativeQuery("call memberballot_updateclubbing_procedure(?,?,?,?,?)").setParameter(1,session.getId()).setParameter(2,deviceType.getId()).setParameter(3,start).setParameter(4,size).setParameter(5,locale).executeUpdate();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
