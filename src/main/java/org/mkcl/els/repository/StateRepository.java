package org.mkcl.els.repository;

import javax.persistence.TypedQuery;

import org.mkcl.els.domain.District;
import org.mkcl.els.domain.State;
import org.springframework.stereotype.Repository;

@Repository
public class StateRepository extends BaseRepository<State, Long> {

	public State find(final District district,
			final String locale) {
		StringBuffer query = new StringBuffer();
		query.append("SELECT s" +
			" FROM District ds JOIN ds.division dv JOIN dv.state s" +
			" WHERE ds.id = " + district.getId());
		
		TypedQuery<State> tQuery = 
			this.em().createQuery(query.toString(), State.class);
		State state = tQuery.getSingleResult();
		return state;
	}
	
}