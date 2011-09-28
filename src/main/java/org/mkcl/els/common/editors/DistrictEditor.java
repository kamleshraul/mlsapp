package org.mkcl.els.common.editors;

import java.beans.PropertyEditorSupport;

import org.mkcl.els.domain.District;
import org.mkcl.els.service.IDistrictService;

public class DistrictEditor extends PropertyEditorSupport{

	private IDistrictService service;
	
	public DistrictEditor(IDistrictService service){
		this.service=service;
	}
	
	public void setAsText(String text) {
        if (!text.equals("")) {
        	District type = (District) this.service.findById(Long.parseLong(text));
            setValue(type);
        }
    }
}
