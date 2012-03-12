package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.trg.dao.jpa.GenericDAOImpl;
import com.trg.search.Search;
import com.trg.search.jpa.JPASearchProcessor;

@Repository
public class BaseRepository<T, ID extends Serializable> extends
        GenericDAOImpl<T, ID> {
	
	private static final String ASC = "asc";
	private static final String DESC = "desc";
	private static final String ALL_LOCALE="all";
   
    @Override
    @PersistenceContext
    public  void setEntityManager(final EntityManager entityManager) {
        super.setEntityManager(entityManager);
        entityManager.setFlushMode(FlushModeType.AUTO);
    }
    
    @Override
    @Autowired
    public void setSearchProcessor(final JPASearchProcessor searchProcessor) {
        super.setSearchProcessor(searchProcessor);
    }   
    
    //======================= Towards Generalization ============================
    //===========================================================================    
    
    public <U extends T> U findById(final Class<U> persistenceClass, ID id) {
		return _find(persistenceClass, id);
	}
    
    @SuppressWarnings("unchecked")
	public <U extends T> U findByName(Class<U> persistenceClass,
			String fieldValue, String locale) {
    	 final Search search = new Search();
         search.addFilterEqual("name", fieldValue);
         if(locale == null) {
         	search.addFilterNull("locale");
     	} else if (locale.isEmpty()){
     		
     	} else {
         	search.addFilterEqual("locale", locale);
     	}
         return (U) this._searchUnique(persistenceClass, search);       
	} 
    
    @SuppressWarnings("unchecked")
    public <U extends T> U findByFieldName(final Class<U> persistenceClass,
    					final String fieldName,
    					final String fieldValue,
                        final String locale) {
        final Search search = new Search();
        search.addFilterEqual(fieldName, fieldValue);
        if(locale == null) {
        	search.addFilterNull("locale");
    	} else if (locale.isEmpty()){
    		
    	} else {
        	search.addFilterEqual("locale", locale);
    	}
        return  (U) this._searchUnique(persistenceClass, search);
    }
    
    @SuppressWarnings("unchecked")
    public <U extends T> U findByFieldNames(final Class<U> persistenceClass,
    					final Map<String, String> names,
                        final String locale) {
        final Search search = new Search();
        for(Entry<String, String> i : names.entrySet()){
        	search.addFilterEqual(i.getKey(), i.getValue());
        }
        if(locale == null) {
        	search.addFilterNull("locale");
    	} else if (locale.isEmpty()){
    		
    	} else {
        	search.addFilterEqual("locale", locale);
    	}
        return (U) this._searchUnique(persistenceClass, search);
    }
    
    @SuppressWarnings("unchecked")
	public <U extends T> List<U> findAll(final Class<U> persistenceClass,
    						final String sortBy,
    						final String sortOrder,
    						final String locale) {
    	final Search search = new Search();
    	if (sortOrder.toLowerCase().equals(ASC)) {
			search.addSortAsc(sortBy);
		}else {
			search.addSortDesc(sortBy);
		}
    	if(locale == null) {
        	search.addFilterNull("locale");
    	} else if (locale.isEmpty()){
    		
    	} else {
        	search.addFilterEqual("locale", locale);
    	}
    	final List<U> records = this._search(persistenceClass, search);
    	return records;
    } 
}
