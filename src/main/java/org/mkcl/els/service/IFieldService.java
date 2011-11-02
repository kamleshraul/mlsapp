package org.mkcl.els.service;

import java.util.List;

import org.mkcl.els.domain.Field;

public interface IFieldService extends IGenericService<Field ,Long>{

	public Field findByName(String name);
	
	public List<Field> findByFormNameSorted(String formName);
}
