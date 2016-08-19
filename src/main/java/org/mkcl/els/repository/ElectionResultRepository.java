package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Election;
import org.mkcl.els.domain.ElectionResult;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Repository;


@Repository
public class ElectionResultRepository extends BaseRepository<ElectionResult,Long>{

    public List<ElectionResult> findByHouseAndMember(final Long houseId,final Long memberId,final String locale) throws ELSException{
        String strquery="SELECT m from ElectionResult m" +
        		" WHERE m.locale=:locale" +
        		" AND m.member.id=:memberId" +
        		" AND m.election.house.id=:houseId";
        List<ElectionResult> electionResults = new ArrayList<ElectionResult>();
        
        try{
	        TypedQuery<ElectionResult> query=this.em().createQuery(strquery, ElectionResult.class);
	        query.setParameter("locale", locale);
	        query.setParameter("memberId", memberId);
	        query.setParameter("houseId", houseId);
	        
	        electionResults = query.getResultList();
	        
        }catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ElectionResultRepository_List<ElectionResult>_findByHouseAndMember", "Election result is unavailable.");
			throw elsException;
		}
        
        return electionResults;
    }

	public ElectionResult findByMemberElectionConstituency(final Member member,
			final Election election,final Constituency constituency,final String locale) throws ELSException {
		String strquery="SELECT DISTINCT er FROM ElectionResult er WHERE er.member=:member"+
				" AND er.constituency=:constituency"+
				" AND er.election=:election AND er.locale=:locale";
		ElectionResult elecResult = null;
		try{
			Query query=this.em().createQuery(strquery);
			query.setParameter("member", member);
			query.setParameter("constituency", constituency);
			query.setParameter("election", election);
			query.setParameter("locale", locale);
			
			elecResult = (ElectionResult)query.getSingleResult();
			
		}catch (Exception e) {
			return null;
		}
		
		return elecResult;
	}
}
