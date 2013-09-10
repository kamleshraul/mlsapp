package org.mkcl.els.repository;

import java.util.Date;

import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseParty;
import org.mkcl.els.domain.PartyType;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class HousePartyRepository extends BaseRepository<HouseParty, Long> {

	public HouseParty find(final House house, 
			final PartyType partyType,
			final Date fromDate, 
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("house", house);
		search.addFilterEqual("partyType", partyType);
		search.addFilterEqual("fromDate", fromDate);
		search.addFilterEqual("locale", locale);
		HouseParty houseParty = this.searchUnique(search);
		return houseParty;
	}

	public HouseParty findInBetween(final House house, 
			final PartyType partyType,
			final Date date, 
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("house", house);
		search.addFilterEqual("partyType", partyType);
		search.addFilterLessOrEqual("fromDate", date);
		search.addFilterGreaterOrEqual("toDate", date);
		search.addFilterEqual("locale", locale);
		HouseParty houseParty = this.searchUnique(search);
		return houseParty;
	}
	
}