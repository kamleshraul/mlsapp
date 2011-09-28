package org.mkcl.els.common.editors;


import java.beans.PropertyEditorSupport;

import org.mkcl.els.domain.Constituency;
import org.mkcl.els.service.IConstituencyService;

public class ConstituencyEditor extends PropertyEditorSupport{

private IConstituencyService service;
	
	public ConstituencyEditor(IConstituencyService service){
		this.service=service;
	}
	
	public void setAsText(String text) {
        if (!text.equals("")) {
        	Constituency type = (Constituency) this.service.findById(Long.parseLong(text));
            setValue(type);
        }
    }
}
