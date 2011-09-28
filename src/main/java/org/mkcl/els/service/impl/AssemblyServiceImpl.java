package org.mkcl.els.service.impl;

import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.AssemblyNumber;
import org.mkcl.els.repository.AssemblyRepository;
import org.mkcl.els.service.IAssemblyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssemblyServiceImpl extends GenericServiceImpl<Assembly,Long>
implements IAssemblyService{
	
	private AssemblyRepository assemblyRepository;
	
	@Autowired
	public void setAssemblyRepository(AssemblyRepository assemblyRepository) {
		this.dao = assemblyRepository;
		this.assemblyRepository = assemblyRepository;
	}

	@Override
	public Assembly findByAssemblyNumber(AssemblyNumber assemblyNumber) {
		return assemblyRepository.findByAssemblyNumber(assemblyNumber);
	}

}
