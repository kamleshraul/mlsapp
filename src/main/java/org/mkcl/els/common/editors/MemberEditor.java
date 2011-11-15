package org.mkcl.els.common.editors;

import java.beans.PropertyEditorSupport;

import org.mkcl.els.domain.MemberDetails;
import org.mkcl.els.service.IMemberDetailsService;

public class MemberEditor extends PropertyEditorSupport{
private IMemberDetailsService service;

public MemberEditor(IMemberDetailsService service){
	this.service=service;
}

public void setAsText(String text) {
    if (!text.equals("")) {
    	MemberDetails type = (MemberDetails) this.service.findById(Long.parseLong(text));
        setValue(type);
    }
}
}
