/*
******************************************************************
File: org.mkcl.els.common.vo.GridConfigVO.java
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
package org.mkcl.els.common.vo;

/**
 * The Class GridConfigVO.
 *
 * @author vishals
 * @version v1.0.0
 */
public class GridConfig {

	/** The title. */
	String title;
	
	/** The col names. */
	String colNames;
	
	/** The col model. */
	String colModel;
	
	/** The height. */
	int height;
	
	/** The width. */
	int width;

	/** The page size. */
	int pageSize;
	
	/** The sort order. */
	String sortOrder;
	
	/**
	 * Instantiates a new grid config vo.
	 */
	public GridConfig(){
	}

	/**
	 * Instantiates a new grid config vo.
	 *
	 * @param title the title
	 * @param colNames the col names
	 * @param colModel the col model
	 * @param height the height
	 * @param width the width
	 * @param pageSize the page size
	 * @param sortOrder the sort order
	 */
	public GridConfig(String title, String colNames, String colModel,
			int height, int width, int pageSize, String sortOrder) {
		super();
		this.title = title;
		this.colNames = colNames;
		this.colModel = colModel;
		this.height = height;
		this.width = width;
		this.pageSize = pageSize;
		this.sortOrder = sortOrder;
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
	
	
	
}
