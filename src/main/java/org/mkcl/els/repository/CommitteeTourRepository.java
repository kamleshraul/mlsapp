package org.mkcl.els.repository;

import java.util.Date;
import java.util.List;

import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeTour;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class CommitteeTourRepository extends BaseRepository<CommitteeTour, Long> {

//	public CommitteeTour find(final Town town, 
//			final String venueName,
//			final Date fromDate, 
//			final Date toDate, 
//			final String subject, 
//			final String locale) {
//		Search search = new Search();
//		search.addFilterEqual("town", town);
//		search.addFilterEqual("venueName", venueName);
//		search.addFilterEqual("fromDate", fromDate);
//		search.addFilterEqual("toDate", toDate);
//		search.addFilterEqual("subject", subject);
//		search.addFilterEqual("locale", locale);
//		CommitteeTour tour = this.searchUnique(search);
//		return tour;
//	}
	
	public CommitteeTour find(final Committee committee, 
			final Date fromDate, 
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("committee", committee);
		search.addFilterEqual("fromDate", fromDate);
		search.addFilterEqual("locale", locale);
		CommitteeTour tour = this.searchUnique(search);
		return tour;
	}
	public List<CommitteeTour> findCommitteeTours(final Status status, 
			final Committee committee, 
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("status", status);
		search.addFilterEqual("locale", locale);
		search.addFilterEqual("committee", committee);
		List<CommitteeTour> committeetours = this.search(search);
		return committeetours;
	}
	
}