/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Grid.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.GridConfig;
import org.mkcl.els.repository.GridRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class Grid.
 *
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "grids")
public class Grid extends BaseDomain implements Serializable {

    // Attributes -------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 100)
    private String name;

    /** The title. */
    @Column(length = 100)
    private String title;

    /** The col names. */
    @Column(length = 4000)
    private String colNames;

    /** The col model. */
    @Column(length = 4000)
    private String colModel;

    /** The page size. */
    private int pageSize;

    /** The sort field. */
    @Column(length = 100)
    private String sortField;

    /** The sort order. */
    @Column(length = 10)
    private String sortOrder;

    /** The query. */
    @Column(length = 4000)
    private String query;

    /** The count query. */
    @Column(length = 4000)
    private String countQuery;

    /** The width. */
    private int width;

    /** The height. */
    private int height;

    /** The detail view. */
    @Column(length = 200)
    private String detailView;

    /** The localized. */
    private Boolean localized;

    /** The multi select. */
    private boolean multiSelect = false;

    /** The native query. */
    private boolean nativeQuery = false;

    /*
     * for subgrids we need to add two additional parameters
     */
    private Long subGridId;

    private boolean subGrid=false;
    /*
     * for grouping
     */
    private boolean grouping=false;

    private String groupField;

    private boolean groupingCollapsed=false;
    
    /**** For Haorizontal Scroll In Grid ****/
    
    private boolean shrinkToFit;
    
    private boolean forceFit;

    /** The grid repository. */
    @Autowired
    private transient GridRepository gridRepository;

    // ---------------------------- Constructors
    /**
     * Instantiates a new grid.
     */
    public Grid() {
    }

    // -------------- constructor ------------------------------
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
     * @param detailView the detail view
     * @param localized the localized
     * @param multiSelect the multi select
     * @param nativeQuery the native query
     */
    public Grid(final String name,
            final String title,
            final String colNames,
            final String colModel,
            final int pageSize,
            final String sortField,
            final String sortOrder,
            final String query,
            final String countQuery,
            final int width,
            final int height,
            final String detailView,
            final Boolean localized,
            final boolean multiSelect,
            final boolean nativeQuery,
            final boolean shrinkToFit,
            final boolean forceFit) {
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
        this.multiSelect = multiSelect;
        this.nativeQuery = nativeQuery;
        this.shrinkToFit=shrinkToFit;
        this.forceFit=forceFit;
    }

    /**
     * Gets the grid repository.
     *
     * @return the grid repository
     */
    public static GridRepository getGridRepository() {
        GridRepository gridRepository = new Grid().gridRepository;
        if (gridRepository == null) {
            throw new IllegalStateException(
                    "AssemblyRepository has not been injected in Assembly Domain");
        }
        return gridRepository;
    }

    // -------------- Domain Methods ------------------------------
    /**
     * Find by detail view.
     *
     * @param urlPattern the url pattern
     * @param locale the locale
     * @return the grid
     * @author sandeeps
     * @throws ELSException 
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static Grid findByDetailView(final String urlPattern,
                                        final String locale) throws ELSException {
        return getGridRepository().findByDetailView(urlPattern, locale);
    }

    /**
     * Gets the config.
     *
     * @param gridId the grid id
     * @return the config
     */
    @Transactional
    public static GridConfig getConfig(final Long gridId) {
        Grid grid = Grid.findById(Grid.class, gridId);
        GridConfig gridConfig = new GridConfig(grid.getTitle(),
                grid.getColNames(), grid.getColModel(), grid.getHeight(),
                grid.getWidth(), grid.getPageSize(), grid.getSortOrder(),
                grid.getDetailView(), grid.isMultiSelect(),grid.getSortField(),grid.isSubGrid(),grid.getSubGridId()
                ,grid.isGrouping(),grid.getGroupField(),grid.isGroupingCollapsed(),
                grid.isShrinkToFit(),grid.isForceFit());
        return gridConfig;
    }

    // -------------- Getters & setters ------------------------------
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
    public void setTitle(final String title) {
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
    public void setColNames(final String colNames) {
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
    public void setColModel(final String colModel) {
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
    public void setPageSize(final int pageSize) {
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
    public void setSortField(final String sortField) {
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
    public void setSortOrder(final String sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Gets the query.
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query.
     *
     * @param query the new query
     */
    public void setQuery(final String query) {
        this.query = query;
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
    public void setCountQuery(final String countQuery) {
        this.countQuery = countQuery;
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
    public void setWidth(final int width) {
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
    public void setHeight(final int height) {
        this.height = height;
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
    public void setDetailView(final String detailView) {
        this.detailView = detailView;
    }

    /**
     * Gets the localized.
     *
     * @return the localized
     */
    public Boolean getLocalized() {
        return localized;
    }

    /**
     * Sets the localized.
     *
     * @param localized the new localized
     */
    public void setLocalized(final Boolean localized) {
        this.localized = localized;
    }

    /**
     * Checks if is multi select.
     *
     * @return true, if is multi select
     */
    public boolean isMultiSelect() {
        return multiSelect;
    }

    /**
     * Sets the multi select.
     *
     * @param multiSelect the new multi select
     */
    public void setMultiSelect(final boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    /**
     * Checks if is native query.
     *
     * @return true, if is native query
     */
    public boolean isNativeQuery() {
        return nativeQuery;
    }

    /**
     * Sets the native query.
     *
     * @param nativeQuery the new native query
     */
    public void setNativeQuery(final boolean nativeQuery) {
        this.nativeQuery = nativeQuery;
    }


    public Long getSubGridId() {
        return subGridId;
    }


    public void setSubGridId(final Long subGridId) {
        this.subGridId = subGridId;
    }


    public boolean isSubGrid() {
        return subGrid;
    }


    public void setSubGrid(final boolean subGrid) {
        this.subGrid = subGrid;
    }

    public boolean isGrouping() {
        return grouping;
    }


    public void setGrouping(final boolean grouping) {
        this.grouping = grouping;
    }

    public String getGroupField() {
        return groupField;
    }


    public void setGroupField(final String groupField) {
        this.groupField = groupField;
    }


    public boolean isGroupingCollapsed() {
        return groupingCollapsed;
    }


    public void setGroupingCollapsed(final boolean groupingCollapsed) {
        this.groupingCollapsed = groupingCollapsed;
    }

	public void setShrinkToFit(boolean shrinkToFit) {
		this.shrinkToFit = shrinkToFit;
	}

	public boolean isShrinkToFit() {
		return shrinkToFit;
	}

	public void setForceFit(boolean forceFit) {
		this.forceFit = forceFit;
	}

	public boolean isForceFit() {
		return forceFit;
	}

}
