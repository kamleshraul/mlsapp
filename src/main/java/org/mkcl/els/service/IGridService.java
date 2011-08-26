/*
******************************************************************
File: org.mkcl.els.service.IGridService.java
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
package org.mkcl.els.service;

import java.util.Locale;

import org.mkcl.els.common.vo.GridConfig;
import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.domain.Grid;

/**
 * The Interface IGridService.
 *
 * @author vishals
 * @version v1.0.0
 */
public interface IGridService extends IGenericService<Grid,Long>{
	
	
	/**
	 * Gets the data irrespective of locale.
	 *
	 * @param gridId the grid id
	 * @param limit the max no. of rows to fetch
	 * @param page the page
	 * @param sidx the sidx
	 * @param order the order
	 * @return the data
	 */
	public GridData getData(Long gridId, Integer limit, Integer page, String sidx, String order);
	
	
	/**
	 * Gets locale specific data.
	 *
	 * @param gridId the grid id
	 * @param limit the max no. of rows to fetch
	 * @param page the page
	 * @param sidx the sidx
	 * @param order the order
	 * @param locale the locale
	 * @return the data
	 */
	public GridData getData(Long gridId, Integer limit, Integer page, String sidx, String order, Locale locale);
	
	/**
	 * Searches the data.
	 *
	 * @param gridId the grid id
	 * @param limit the max no. of rows to fetch
	 * @param page the page
	 * @param sidx the sidx
	 * @param order the order
	 * @param filterSql the filter sql
	 * @param locale the locale
	 * @return the data
	 */
	public GridData getData(Long gridId, Integer limit, Integer page, String sidx, String order, String filterSql, Locale locale);
	
	/**
	 * Gets the config.
	 *
	 * @param gridId the grid id
	 * @return the config
	 */
	public GridConfig getConfig(Long gridId);
	
	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the grid
	 */
	public Grid findByName(String name);
		

}
