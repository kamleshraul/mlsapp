package org.mkcl.els.service;

import java.util.List;

import org.mkcl.els.domain.Title;

public interface ITitleService extends IGenericService<Title ,Long>{

	public Title findByName(String name);
	
	public List<Title> findAllSorted();

}
