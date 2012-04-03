package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "partial_updates")
public class PartialUpdate extends BaseDomain implements Serializable {

    private transient static final long serialVersionUID = 1L;

    @Column(length = 200)
    private String urlPattern;

    @Column(length = 5000)
    private String fieldsNotToBeOverwritten;

    public PartialUpdate() {
        super();
    }

    public PartialUpdate(String urlPattern, String fieldsNotToBeOverwritten) {
        super();
        this.urlPattern = urlPattern;
        this.fieldsNotToBeOverwritten = fieldsNotToBeOverwritten;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getFieldsNotToBeOverwritten() {
        return fieldsNotToBeOverwritten;
    }

    public void setFieldsNotToBeOverwritten(String fieldsNotToBeOverwritten) {
        this.fieldsNotToBeOverwritten = fieldsNotToBeOverwritten;
    }
}
