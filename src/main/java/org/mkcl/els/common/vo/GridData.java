/*
******************************************************************
File: org.mkcl.els.common.vo.GridVO.java
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

import java.util.List;
import java.util.Map;

/**
 * The Class GridVO.
 *
 * @author vishals
 * @version v1.0.0
 */
public class GridData {

	/** The page. */
	private int page;
	
	/** The total. */
	private int total;
	
	/** The records. */
	private long records;
	
	/** The rows. */
	private List<Map<String, Object>> rows;

	public GridData(){
	}
	
	/**
	 * Instantiates a new grid vo.
	 *
	 * @param page the page
	 * @param total the total
	 * @param records the records
	 * @param rows the rows
	 */
	public GridData(int page, int total, long records,
			List<Map<String, Object>> rows) {
		super();
		this.page = page;
		this.total = total;
		this.records = records;
		this.rows = rows;
	}

	/**
	 * Gets the page.
	 *
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Sets the page.
	 *
	 * @param page the new page
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * Gets the total.
	 *
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * Sets the total.
	 *
	 * @param total the new total
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * Gets the records.
	 *
	 * @return the records
	 */
	public long getRecords() {
		return records;
	}

	/**
	 * Sets the records.
	 *
	 * @param records the new records
	 */
	public void setRecords(long records) {
		this.records = records;
	}

	/**
	 * Gets the rows.
	 *
	 * @return the rows
	 */
	public List<Map<String, Object>> getRows() {
		return rows;
	}

	/**
	 * Sets the rows.
	 *
	 * @param rows the rows
	 */
	public void setRows(List<Map<String, Object>> rows) {
		this.rows = rows;
	}
	
	
}
