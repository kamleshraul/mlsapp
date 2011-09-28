package org.mkcl.els.service.impl;

import org.mkcl.els.domain.AssemblyTerm;
import org.mkcl.els.repository.AssemblyTermRepository;
import org.mkcl.els.service.IAssemblyTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssemblyTermServiceImpl extends GenericServiceImpl<AssemblyTerm,Long>
implements IAssemblyTermService{

	@Autowired
	AssemblyTermRepository assemblyTermRepository;
	
	@Autowired
	public void setAssemblyTermRepository(AssemblyTermRepository assemblyTermRepository) {
		this.dao = assemblyTermRepository;
		this.assemblyTermRepository = assemblyTermRepository;
	}
	
	@Override
	public AssemblyTerm findByAssemblyTerm(Integer term) {
		return assemblyTermRepository.findByAssemblyTerm(term);
	}

}
