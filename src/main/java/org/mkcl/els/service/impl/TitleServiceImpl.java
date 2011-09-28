package org.mkcl.els.service.impl;

import java.util.List;

import org.mkcl.els.domain.Title;
import org.mkcl.els.repository.AssemblyRepository;
import org.mkcl.els.repository.TitleRepository;
import org.mkcl.els.service.ITitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TitleServiceImpl extends GenericServiceImpl<Title,Long>
implements ITitleService{

	private TitleRepository titleRepository;
	
	@Autowired
	public void setTitleRepository(TitleRepository titleRepository) {
		this.dao = titleRepository;
		this.titleRepository = titleRepository;
	}
	@Override
	public Title findByName(String name) {
		return titleRepository.findByName(name);
	}
	@Override
	public List<Title> findAllSorted() {
		return titleRepository.findAllSorted();
	}

}
