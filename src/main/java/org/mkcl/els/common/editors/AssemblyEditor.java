package org.mkcl.els.common.editors;

import java.beans.PropertyEditorSupport;

import org.mkcl.els.domain.Assembly;
import org.mkcl.els.service.IAssemblyService;

public class AssemblyEditor extends PropertyEditorSupport{
	
	private IAssemblyService service;

	public AssemblyEditor(IAssemblyService service) {
		super();
		this.service = service;
	}
	
	public void setAsText(String text) {
		if(! text.equals("")) {
			Assembly assembly = 
				(Assembly) this.service.findById(Long.parseLong(text));			
			this.setValue(assembly);
		}
	}
}
