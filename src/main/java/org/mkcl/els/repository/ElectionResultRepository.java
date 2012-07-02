package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Election;
import org.mkcl.els.domain.ElectionResult;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;


@Repository
public class ElectionResultRepository extends BaseRepository<ElectionResult,Long>{

    @SuppressWarnings("unchecked")
    public List<ElectionResult> findByHouseAndMember(final Long houseId,final Long memberId,final String locale){
        String query="SELECT m from ElectionResult m  WHERE m.locale='"+locale+"' AND m.member.id="+memberId+" AND m.election.house.id="+houseId;
        return this.em().createQuery(query).getResultList();
    }

	public ElectionResult findByMemberElectionConstituency(Member member,
			Election election, Constituency constituency,String locale) {
		Search search=new Search();
		search.addFilterEqual("member",member);
		search.addFilterEqual("constituency",constituency);
		search.addFilterEqual("election",election);
		search.addFilterEqual("locale",locale);
		return this.searchUnique(search);
	}
}
