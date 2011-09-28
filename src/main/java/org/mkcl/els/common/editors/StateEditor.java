package org.mkcl.els.common.editors;

import java.beans.PropertyEditorSupport;

import org.mkcl.els.domain.State;
import org.mkcl.els.service.IStateService;

public class StateEditor extends PropertyEditorSupport{
	
	private IStateService service;
	
	public StateEditor(IStateService service){
		this.service=service;
	}
	
	public void setAsText(String text) {
        if (!text.equals("")) {
        	State type = (State) this.service.findById(Long.parseLong(text));
            setValue(type);
        }
    }
	

}
