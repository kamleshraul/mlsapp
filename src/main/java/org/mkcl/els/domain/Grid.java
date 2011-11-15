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
import javax.persistence.Version;

// TODO: Auto-generated Javadoc
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

	/** The Localized. */
	private Boolean localized;
	
	/** The version. */
    @Version
    private Long version;
    
    private boolean multiSelect=false;
    
    private boolean nativeQuery=false;
	
	// Constructors --------------------------------------------------------------------------------------------------------------------

	/**
	 * Instantiates a new grid.
	 */
	public Grid(){
	}

	public Grid(String name, String title, String colNames, String colModel,
			int pageSize, String sortField, String sortOrder, String query,
			String countQuery, int width, int height, String detailView,
			Boolean localized, Long version, boolean multiSelect,
			boolean nativeQuery) {
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
		this.width = width;
		this.height = height;
		this.detailView = detailView;
		this.localized = localized;
		this.version = version;
		this.multiSelect = multiSelect;
		this.nativeQuery = nativeQuery;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getColNames() {
		return colNames;
	}

	public void setColNames(String colNames) {
		this.colNames = colNames;
	}

	public String getColModel() {
		return colModel;
	}

	public void setColModel(String colModel) {
		this.colModel = colModel;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getCountQuery() {
		return countQuery;
	}

	public void setCountQuery(String countQuery) {
		this.countQuery = countQuery;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getDetailView() {
		return detailView;
	}

	public void setDetailView(String detailView) {
		this.detailView = detailView;
	}

	public Boolean getLocalized() {
		return localized;
	}

	public void setLocalized(Boolean localized) {
		this.localized = localized;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public boolean isMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	public boolean isNativeQuery() {
		return nativeQuery;
	}

	public void setNativeQuery(boolean nativeQuery) {
		this.nativeQuery = nativeQuery;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
