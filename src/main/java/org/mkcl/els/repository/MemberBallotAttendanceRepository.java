package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberBallotAttendanceRepository extends BaseRepository<MemberBallotAttendance, Serializable>{

    public List<MemberBallotAttendance> findAll(final Session session,
            final DeviceType questionType,final String attendance,
            final String sortBy,
            final String locale) {
        Search search=new Search();
        search.addFilterEqual("session.id", session.getId());
        search.addFilterEqual("deviceType.id", questionType.getId());
        search.addFilterEqual("locale", locale);
        if(attendance.equals("true")){
            search.addFilterEqual("attendance", true);
        }else if(attendance.equals("false")){
            search.addFilterEqual("attendance", false);
        }
        if(sortBy.equals("member")){
        search.addSort("member.lastName",false);
        }else{
        search.addSort("position",false);
        }
        return this.search(search);
    }

    @SuppressWarnings("unchecked")
    public List<Member> findMembersByAttendance(final Session session,
            final DeviceType deviceType, final Boolean attendanceType, final String locale) {
        String query="SELECT m FROM MemberBallotAttendance mba JOIN mba.member m WHERE "+
                     " mba.session.id="+session.getId()+" AND mba.deviceType.id="+deviceType.getId()+" "+
                     " AND mba.attendance="+attendanceType+" AND mba.locale='"+locale+"' ORDER BY mba.position";
        return this.em().createQuery(query).getResultList();
    }
}
