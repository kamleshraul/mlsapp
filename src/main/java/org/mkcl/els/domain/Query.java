package org.mkcl.els.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="queries")
public class Query extends BaseDomain{

    @Column(length=30000)
    private String query;

    @Column(length=100)
    private String keyField;

    public Query() {
        super();
    }

    public Query(final String keyField, final String query) {
        super();
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }


    public String getKeyField() {
        return keyField;
    }


    public void setKeyField(final String keyField) {
        this.keyField = keyField;
    }
}
