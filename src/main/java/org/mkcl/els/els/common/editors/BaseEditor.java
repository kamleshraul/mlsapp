/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.editors.BaseEditor.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.common.editors;

import java.beans.PropertyEditorSupport;

import org.mkcl.els.domain.BaseDomain;

/**
 * The Class BaseEditor.
 * 
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class BaseEditor extends PropertyEditorSupport {

    /** The clazz. */
    private final BaseDomain clazz;

    /**
     * Instantiates a new base editor.
     * 
     * @param clazz the clazz
     */
    public BaseEditor(final BaseDomain clazz) {
        this.clazz = clazz;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(final String text) {
        if (!text.equals("")) {
            this.setValue(BaseDomain.findById(clazz.getClass(),
                    Long.parseLong(text)));
        }
    }

}
