/*
******************************************************************
File: org.mkcl.els.domain.Grid.java
Copyright (c) 2011, vishals, MKCL
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class Grid.
 *
 * @author vishals
 * @version v1.0.0
 */
@Entity
@Table(name="grids")
public class Grid implements Serializable {
	
	// Attributes --------------------------------------------------------------------------------------------------------------------

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** The name. */
	@Column(length=100)
	private String name;
	
	/** The title. */
	@Column(length=100)
	private String title;

	/** The col names. */
	@Column(length=4000)
	private String colNames;
	
	/** The col model. */
	@Column(length=4000)
	private String colModel;
	
	/** The page size. */
	private int pageSize;
	
	/** The sort field. */
	@Column(length=100)
	private String sortField;
	
	/** The sort order. */
	@Column(length=10)
	private String sortOrder;
	
	/** The sql. */
	@Column(length=4000)
	private String query;
	
	/** The sql. */
	@Column(length=4000)
	private String countQuery;
	
	/** The width. */
	private int width;
	
	/** The height. */
	private int height;
	
	/** The detail view. */
	@Column(length=200)
	private String detailView;

	// Constructors --------------------------------------------------------------------------------------------------------------------

	/**
	 * Instantiates a new grid.
	 */
	public Grid(){
	}
	
	/**
	 * Instantiates a new grid.
	 *
	 * @param name the name
	 * @param title the title
	 * @param colNames the col names
	 * @param colModel the col model
	 * @param pageSize the page size
	 * @param sortField the sort field
	 * @param sortOrder the sort order
	 * @param query the query
	 * @param countQuery the count query
	 * @param width the width
	 * @param height the height
	 * @param detailView the view to be shown on content panel
	 */
	public Grid(String name, String title, String colNames, String colModel,
			int pageSize, String sortField, String sortOrder,String query, String countQuery, int width, int height, String detailView) {
		super();
		this.name = name;
		this.title = title;
		this.colNames = colNames;
		this.colModel = colModel;
		this.pageSize = pageSize;
		this.sortField = sortField;
		this.sortOrder = sortOrder;
		this.query = query;
		this.countQuery = countQuery;
		this.height = height;
		this.width = width;
		this.detailView = detailView;
	}
	
	/**
	 * Instantiates a new grid.
	 *
	 * @param name the name
	 * @param title the title
	 * @param colNames the col names
	 * @param colModel the col model
	 * @param query the query
	 * @param countQuery the count query
	 * @param detailView the detail view
	 */
	public Grid(String name, String title, String colNames, String colModel, String query, String countQuery,String detailView){
		this(name,title,colNames,colModel,30,"id","asc",query,countQuery,100,100,detailView);
	}


	// Getters/Setter--------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

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
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the col names.
	 *
	 * @return the col names
	 */
	public String getColNames() {
		return colNames;
	}

	/**
	 * Sets the col names.
	 *
	 * @param colNames the new col names
	 */
	public void setColNames(String colNames) {
		this.colNames = colNames;
	}

	/**
	 * Gets the col model.
	 *
	 * @return the col model
	 */
	public String getColModel() {
		return colModel;
	}

	/**
	 * Sets the col model.
	 *
	 * @param colModel the new col model
	 */
	public void setColModel(String colModel) {
		this.colModel = colModel;
	}

	/**
	 * Gets the page size.
	 *
	 * @return the page size
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the page size.
	 *
	 * @param pageSize the new page size
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Gets the sort field.
	 *
	 * @return the sort field
	 */
	public String getSortField() {
		return sortField;
	}

	/**
	 * Sets the sort field.
	 *
	 * @param sortField the new sort field
	 */
	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	/**
	 * Gets the sort order.
	 *
	 * @return the sort order
	 */
	public String getSortOrder() {
		return sortOrder;
	}

	/**
	 * Sets the sort order.
	 *
	 * @param sortOrder the new sort order
	 */
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * Gets the sql.
	 *
	 * @return the sql
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Sets the sql.
	 *
	 * @param query the new query
	 */
	public void setQuery(String query) {
		this.query = query;
	}
	
	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width.
	 *
	 * @param width the new width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height.
	 *
	 * @param height the new height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Gets the count query.
	 *
	 * @return the count query
	 */
	public String getCountQuery() {
		return countQuery;
	}

	/**
	 * Sets the count query.
	 *
	 * @param countQuery the new count query
	 */
	public void setCountQuery(String countQuery) {
		this.countQuery = countQuery;
	}

	/**
	 * Gets the detail view.
	 *
	 * @return the detail view
	 */
	public String getDetailView() {
		return detailView;
	}

	/**
	 * Sets the detail view.
	 *
	 * @param detailView the new detail view
	 */
	public void setDetailView(String detailView) {
		this.detailView = detailView;
	}
	
	
}
