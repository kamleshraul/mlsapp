package org.mkcl.els.common.editors;

import java.beans.PropertyEditorSupport;

import org.mkcl.els.domain.MenuItem;
import org.mkcl.els.service.IMenuItemService;

public class MenuItemEditor extends PropertyEditorSupport{
	
	private IMenuItemService service;
	
	 /**
     * Instantiates a new MenuItem type editor.
     *
     * @param repository the repository
     */
    public MenuItemEditor(IMenuItemService service) {
    	this.service = service;
    }
    

	/* (non-Javadoc)
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	public void setAsText(String text) {
        if (!text.equals("")) {
        	MenuItem type = (MenuItem) this.service.findById(Long.parseLong(text));
            setValue(type);
        }
    }

}
