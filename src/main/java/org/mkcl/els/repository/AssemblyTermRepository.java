package org.mkcl.els.repository;

import org.mkcl.els.domain.AssemblyTerm;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class AssemblyTermRepository extends BaseRepository<AssemblyTerm, Long>{
	public AssemblyTerm findByAssemblyTerm(Integer term){
		Search search = new Search();
		search.addFilterEqual("term",term);
		AssemblyTerm assemblyTerm = this.searchUnique(search);
		return assemblyTerm;
	}

}
