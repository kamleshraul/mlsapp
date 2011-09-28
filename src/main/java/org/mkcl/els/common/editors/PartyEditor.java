package org.mkcl.els.common.editors;

import java.beans.PropertyEditorSupport;

import org.mkcl.els.domain.Party;
import org.mkcl.els.service.IPartyService;

public class PartyEditor extends PropertyEditorSupport{

	private IPartyService service;
	
	public PartyEditor(IPartyService service){
		this.service=service;
	}
	
	public void setAsText(String text) {
        if (!text.equals("")) {
        	Party type = (Party) this.service.findById(Long.parseLong(text));
            setValue(type);
        }
    }
}
