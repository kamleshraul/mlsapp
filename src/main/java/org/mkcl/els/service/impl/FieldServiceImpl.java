package org.mkcl.els.service.impl;

import java.util.List;

import org.mkcl.els.domain.Field;
import org.mkcl.els.repository.AssemblyRoleRepository;
import org.mkcl.els.repository.FieldRepository;
import org.mkcl.els.service.IFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FieldServiceImpl extends GenericServiceImpl<Field,Long>
implements IFieldService{

	private FieldRepository fieldRepository;

	@Autowired
	public void setFieldRepository(
			FieldRepository fieldRepository) {
		this.dao = fieldRepository;
		this.fieldRepository = fieldRepository;
	}	
	
	@Override
	public Field findByName(String name) {
		return fieldRepository.findByName(name);
	}

	@Override
	public List<Field> findByFormNameSorted(String formName) {
		return fieldRepository.findByFormNameSorted(formName);
	}

}
