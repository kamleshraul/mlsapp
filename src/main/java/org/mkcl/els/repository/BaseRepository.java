/*
******************************************************************
File: org.mkcl.embassy.repository.BaseRepository.java
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

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.trg.dao.jpa.GenericDAOImpl;
import com.trg.search.jpa.JPASearchProcessor;

/**
 * The Class BaseRepository.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 * @author vishals
 * @version 1.0.0
 */
public  class BaseRepository<T, ID extends Serializable> extends GenericDAOImpl<T, ID>  {
	
        /* (non-Javadoc)
         * @see com.trg.dao.jpa.JPABaseDAO#setEntityManager(javax.persistence.EntityManager)
         */
        @Override
        @PersistenceContext
        public void setEntityManager(EntityManager entityManager) {
                super.setEntityManager(entityManager);
                entityManager.setFlushMode(FlushModeType.AUTO);
        }
        
        /* (non-Javadoc)
         * @see com.trg.dao.jpa.JPABaseDAO#setSearchProcessor(com.trg.search.jpa.JPASearchProcessor)
         */
        @Override
        @Autowired
        public void setSearchProcessor(JPASearchProcessor searchProcessor) {
                super.setSearchProcessor(searchProcessor);
        }
        
        
       /* *//**
         * Find all.
         *
         * @param page the page
         * @param maxRes the max res
         * @param column the column
         * @param desc the desc
         * @param isSearch the is search
         * @param baseFilters the base filters
         * @param filters the filters
         * @return the list
         * @throws ClassNotFoundException the class not found exception*//*
         
    	public List<T> findAll(int page, int maxRes, String column, boolean desc,
    			boolean isSearch, String baseFilters, org.mkcl.embassy.domain.Filter filters) throws ClassNotFoundException {
    		Search search = new Search();
    		search.setFirstResult(-1);
    		search.addSort(column, desc);
    		search.setMaxResults(maxRes);
    		search.setPage(page);
    		if (baseFilters != null) {
    			search = applyBaseFilters(search, baseFilters);
    		}
    		if (isSearch && filters != null) {
    			search = applyFilters(search, filters);
    		}
    		List<T> records = this.search(search);
    		return records;
    	}

    	*//**
	     * Count.
	     *
	     * @param isSearch the is search
	     * @param baseFilters the base filters
	     * @param filters the filters
	     * @return the int
	     * *//*
	     
    	public int count(boolean isSearch, String baseFilters, org.mkcl.embassy.domain.Filter filters) {
    		Search search = new Search();
    		if (baseFilters != null) {
    			search = applyBaseFilters(search, baseFilters);
    		}
    		if (isSearch && filters != null) {
    			search = applyFilters(search, filters);
    		}
    		return this.count(search);
    	}

    	*//**
    	 * Apply base filters.
    	 *
    	 * @param search the search
    	 * @param baseFilters the base filters
    	 * @return the search
    	 *//*
    	private Search applyBaseFilters(Search search, String baseFilters) {
    		String[] arrFilters = baseFilters.split(",");
    		for (String filter : arrFilters) {
    			String[] tokens = filter.split("=");
    			Filter f = Filter.equal(tokens[0], tokens[1]);
    			search.addFilter(f);
    		}
    		return search;
    	}

    	*//**
    	 * Apply filters.
    	 *
    	 * @param search the search
    	 * @param filters the filters
    	 * @return the search
    	 *//*
     	private Search applyFilters(Search search, org.mkcl.embassy.domain.Filter filters) {
    		Filter[] filterRules = new Filter[filters.getRules().length];
    		int count = 0;
    		for (Rule rule : filters.getRules()) {
    			String data = rule.getData();
    			if (rule.getOp().equalsIgnoreCase("bw")) {
    				data = data + "%";
    			} else if (rule.getOp().equalsIgnoreCase("ew")) {
    				data = "%" + data;
    			} else if (rule.getOp().equalsIgnoreCase("cn")) {
    				data = "%" + data + "%";
    			}
    			Filter filter = new Filter(rule.getField(), data, rule.getMappedOperator());
    			filterRules[count++] = filter;
    		}
    		if (filters.getGroupOp().equalsIgnoreCase("AND")) {
    			search.addFilterAnd(filterRules);
    		} else {
    			search.addFilterOr(filterRules);
    		}
    		return search;
    	}
        */
        
        
}

