/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Book.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Book.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "books")
public class Book extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    private String name;

    /** The authors. */
    @ManyToMany
    @JoinTable(name = "associations_member_book", joinColumns = @JoinColumn(
            name = "book_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "member_id",
            referencedColumnName = "id"))
    private List<Member> authors;

    /** The isbn. */
    @Column(length = 100)
    private String isbn;

    /** The publication. */
    @Column(length = 600)
    private String publication;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new book.
     */
    public Book() {
        super();
    }

    /**
     * Instantiates a new book.
     *
     * @param name the name
     * @param authors the authors
     * @param isbn the isbn
     * @param publication the publication
     */
    public Book(final String name, final List<Member> authors, final String isbn,
            final String publication) {
        super();
        this.name = name;
        this.authors = authors;
        this.isbn = isbn;
        this.publication = publication;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    // ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the authors.
     *
     * @return the authors
     */
    public List<Member> getAuthors() {
        return authors;
    }

    /**
     * Sets the authors.
     *
     * @param authors the new authors
     */
    public void setAuthors(final List<Member> authors) {
        this.authors = authors;
    }

    /**
     * Gets the isbn.
     *
     * @return the isbn
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the isbn.
     *
     * @param isbn the new isbn
     */
    public void setIsbn(final String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the publication.
     *
     * @return the publication
     */
    public String getPublication() {
        return publication;
    }

    /**
     * Sets the publication.
     *
     * @param publication the new publication
     */
    public void setPublication(final String publication) {
        this.publication = publication;
    }

}
