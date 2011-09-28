package org.mkcl.els.common.editors;

import java.beans.PropertyEditorSupport;

import org.mkcl.els.domain.Tehsil;
import org.mkcl.els.service.ITehsilService;

public class TehsilEditor extends PropertyEditorSupport{
	
	private ITehsilService service;
	
	public TehsilEditor(ITehsilService service){
		this.service=service;
	}
	
	public void setAsText(String text) {
        if (!text.equals("")) {
        	Tehsil type = (Tehsil) this.service.findById(Long.parseLong(text));
            setValue(type);
        }
    }
}
