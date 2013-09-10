package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.HouseType;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class CommitteeNameRepository extends BaseRepository<CommitteeName, Long> {

	public CommitteeName find(final String name,
			final CommitteeType committeeType,
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("name", name);
		search.addFilterEqual("committeeType", committeeType);
		search.addFilterEqual("locale", locale);
		search.addFilterEqual("isExpired", false);
		CommitteeName committeeName = this.searchUnique(search);
		return committeeName;
	}
	
	public List<CommitteeName> find(final CommitteeType committeeType,
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("committeeType", committeeType);
		search.addFilterEqual("locale", locale);
		search.addFilterEqual("isExpired", false);
		List<CommitteeName> committeeNames = this.search(search);
		return committeeNames;
	}
	
	public List<CommitteeName> find(final HouseType houseType,
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("committeeType.houseType", houseType);
		search.addFilterEqual("locale", locale);
		search.addFilterEqual("isExpired", false);
		List<CommitteeName> committeeNames = this.search(search);
		return committeeNames;
	}
	
	public List<CommitteeName> findAll(final String locale) {
		Search search = new Search();
		search.addFilterEqual("locale", locale);
		search.addFilterEqual("isExpired", false);
		List<CommitteeName> committeeNames = this.search(search);
		return committeeNames;
	}
}