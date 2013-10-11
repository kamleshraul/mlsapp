package org.mkcl.els.repository;

import java.util.Date;

import org.mkcl.els.domain.CommitteeTour;
import org.mkcl.els.domain.Town;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class CommitteeTourRepository extends BaseRepository<CommitteeTour, Long> {

	public CommitteeTour find(final Town town, 
			final String venueName,
			final Date fromDate, 
			final Date toDate, 
			final String subject, 
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("town", town);
		search.addFilterEqual("venueName", venueName);
		search.addFilterEqual("fromDate", fromDate);
		search.addFilterEqual("toDate", toDate);
		search.addFilterEqual("subject", subject);
		search.addFilterEqual("locale", locale);
		CommitteeTour tour = this.searchUnique(search);
		return tour;
	}
	
}