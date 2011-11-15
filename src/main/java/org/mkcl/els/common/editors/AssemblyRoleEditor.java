package org.mkcl.els.common.editors;

import java.beans.PropertyEditorSupport;

import org.mkcl.els.domain.AssemblyRole;
import org.mkcl.els.service.IAssemblyRoleService;

public class AssemblyRoleEditor extends PropertyEditorSupport{

	private IAssemblyRoleService service;
	
	public AssemblyRoleEditor(IAssemblyRoleService service) {
		super();
		this.service = service;
	}
	
	public void setAsText(String text) {
		if(! text.equals("")) {
			AssemblyRole assemblyRole = 
				(AssemblyRole) this.service.findById(Long.parseLong(text));			
			this.setValue(assemblyRole);
		}
	}
	
	
}
