/*
 ******************************************************************
File: org.mkcl.els.repository.GridRepository.java
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
package org.mkcl.els.repository;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Query;

import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.domain.Grid;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class GridRepository.
 *
 * @author vishals
 * @version v1.0.0
 */
@Repository
public class GridRepository extends BaseRepository<Grid,Long>{

	private static final String DEFAULT_LOCALE="en";
	/**
	 * Gets the data.
	 *
	 * @param gridId the grid id
	 * @param rows the rows
	 * @param page the page
	 * @param sidx the sidx
	 * @param order the order
	 * @return the data
	 */
	@SuppressWarnings("unchecked")
	public GridData getData(Long gridId, Integer limit, Integer page, String sidx, String order){
		Grid grid = this.find(gridId);

		String count_select = grid.getCountQuery() + " ORDER BY " + sidx + " " + order;
		Query countQuery = this.em().createQuery(count_select);
		Long count = (Long)countQuery.getSingleResult();

		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/limit); 
		} 

		if (page > total_pages){
			page = total_pages;
		}

		int start = (int) (limit * page - limit);
		if(start<0){
			start=0;
		}

		String select = grid.getQuery() + " ORDER BY " + sidx + " " + order;
		Query query = this.em().createQuery(select);
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Map<String,Object>> records = query.getResultList();


		GridData gridVO = new GridData(page,limit,count,records);
		return gridVO;
	}

	@SuppressWarnings("unchecked")
	public GridData getData(Long gridId, Integer limit, Integer page, String sidx, String order, Locale locale){

		Grid grid = this.find(gridId);
		String count_select=null;
		if(!sidx.contains(".")){
			count_select = grid.getCountQuery() + " ORDER BY m." + sidx + " " + order;
		}
		else{
			count_select = grid.getCountQuery() + " ORDER BY " + sidx + " " + order;
		}
		Query countQuery = this.em().createQuery(count_select);
		if(count_select.contains("=:locale")){			
			if(grid.getLocalized()){
				countQuery.setParameter("locale", locale.toString());
			}
			else{
				countQuery.setParameter("locale",DEFAULT_LOCALE);
			}
		}
		Long count =  (Long) countQuery.getSingleResult();

		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/limit); 
		} 

		if (page > total_pages){
			page = total_pages;
		}

		int start = (int) (limit * page - limit);
		if(start<0){
			start=0;
		}

		String select=null; 
		if(!sidx.contains(".")){
			select= grid.getQuery() + " ORDER BY m." + sidx + " " + order;
		}
		else{
			select= grid.getQuery() + " ORDER BY " + sidx + " " + order;
		}			
		Query query = this.em().createQuery(select);
		if(select.contains("=:locale")){			
			if(grid.getLocalized()){
				query.setParameter("locale", locale.toString());
			}
			else{
				query.setParameter("locale",DEFAULT_LOCALE);
			}
		}

		query.setFirstResult(start);
		query.setMaxResults((int)(count>limit?count:limit));
		List<Map<String,Object>> records = query.getResultList();

		GridData gridVO = new GridData(page,total_pages,count,records);
		return gridVO;
	}

	@SuppressWarnings("unchecked")
	public GridData getData(Long gridId, Integer limit, Integer page, String sidx, String order, String filterSql, Locale locale){
		Grid grid = this.find(gridId);

		String count_select =null;
		if(!sidx.contains(".")){
			count_select= grid.getCountQuery() +  filterSql + " ORDER BY m." + sidx + " " + order;
		}
		else{
			count_select= grid.getCountQuery() +  filterSql + " ORDER BY " + sidx + " " + order;
		}
		Query countQuery = this.em().createQuery(count_select);
		if(grid.getLocalized()){
			countQuery.setParameter("locale", locale.toString());
		}
		Long count = (Long)countQuery.getSingleResult();


		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/limit); 
		} 

		if (page > total_pages){
			page = total_pages;
		}

		int start = (int) (limit * page - limit);
		if(start<0){
			start=0;
		}

		String select =null;
		if(!sidx.contains(".")){
			select= grid.getQuery() + filterSql + " ORDER BY m." + sidx + " " + order;
		}
		else{
			select= grid.getQuery() + filterSql + " ORDER BY " + sidx + " " + order;
		}
		Query query = this.em().createQuery(select);
		if(grid.getLocalized()){
			query.setParameter("locale", locale.toString());
		}
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Map<String,Object>> records = query.getResultList();
		GridData gridVO = new GridData(page,limit,count,records);
		return gridVO;
	}

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the grid
	 */
	public Grid findByName(String name){
		Search search = new Search();
		search.addFilterEqual("name", name);
		Grid grid = this.searchUnique(search);
		return grid;
	}

}
