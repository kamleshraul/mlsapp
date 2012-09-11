package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionRepository extends BaseRepository<Question, Long>{

    public Integer findLastStarredUnstarredShortNoticeQuestionNo(final House house,final Session currentSession){
        String query="SELECT q.number FROM questions AS q JOIN sessions AS s JOIN houses AS h "+
        "JOIN questiontypes AS qt WHERE q.session_id=s.id AND s.house_id=h.id "+
        "AND h.id="+house.getId()+" AND s.id="+currentSession.getId()+" AND qt.id=q.question_type_id AND qt.type!='halfhourdiscussion' ORDER BY q.id DESC LIMIT 0,1";
        List result=this.em().createNativeQuery(query).getResultList();
        Integer lastNumber=0;
        if(!result.isEmpty()){
            Object i=result.get(0);
            lastNumber=Integer.parseInt(i.toString());
        }
        return lastNumber;
    }

    public Integer findLastHalfHourDiscussionQuestionNo(final House house,final Session currentSession){
        String query="SELECT q.number FROM questions AS q JOIN sessions AS s JOIN houses AS h "+
        "JOIN questiontypes AS qt WHERE q.session_id=s.id AND s.house_id=h.id "+
        "AND h.id="+house.getId()+" AND s.id="+currentSession.getId()+" AND qt.id=q.question_type_id AND qt.type=='halfhourdiscussion' ORDER BY q.id DESC LIMIT 0,1";
        List result=this.em().createNativeQuery(query).getResultList();
        Integer lastNumber=0;
        if(!result.isEmpty()){
            Object i=result.get(0);
            lastNumber=Integer.parseInt(i.toString());
        }
        return lastNumber;
    }

    public Integer assignQuestionNo(final HouseType houseType, final Session session,
            final DeviceType questionType) {
        //query to generate question no in assembly and council will differ a bit.Also query will
        //vary according to question type
        //for assembly
        String strHouseType=houseType.getType();
        String strQuestionType=questionType.getType();
        Long house=session.getHouse().getId();
        Long sessionId=session.getId();
        String query=null;
        if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
            if(strQuestionType.equals(ApplicationConstants.STARRED_QUESTION)||strQuestionType.equals(ApplicationConstants.UNSTARRED_QUESTION)||strQuestionType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
                query="SELECT q.number FROM questions AS q JOIN sessions AS s JOIN houses AS h "+
                "JOIN questiontypes AS qt WHERE q.session_id=s.id AND AND qt.id=q.question_type_id  AND s.house_id=h.id "+
                "AND h.id="+house+" AND s.id="+sessionId+" AND (qt.type=='shortnotice' OR qt.type='starred' OR qt.type='unstarred') ORDER BY q.id DESC LIMIT 0,1";
            }else if(strQuestionType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION)){
                query="SELECT q.number FROM questions AS q JOIN sessions AS s JOIN houses AS h "+
                "JOIN questiontypes AS qt WHERE q.session_id=s.id AND AND qt.id=q.question_type_id  AND s.house_id=h.id "+
                "AND h.id="+house+" AND s.id="+sessionId+" AND (qt.type=='halfhourdiscussion') ORDER BY q.id DESC LIMIT 0,1";
            }
        }else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
            if(strQuestionType.equals(ApplicationConstants.STARRED_QUESTION)||strQuestionType.equals(ApplicationConstants.UNSTARRED_QUESTION)||strQuestionType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){

            }else if(strQuestionType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION)){

            }
        }
        return null;
    }

}
