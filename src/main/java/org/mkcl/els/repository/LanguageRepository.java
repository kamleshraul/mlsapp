package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Language;
import org.springframework.stereotype.Repository;

@Repository
public class LanguageRepository extends BaseRepository<Language, Long> {

	@SuppressWarnings("unchecked")
	public List<Language> findAllSortedByPriorityAndName(final String locale) throws ELSException{
		String query="SELECT m FROM Language m" +
				" WHERE m.locale=:locale ORDER BY m.priority,m.name";
		List<Language> langs = new ArrayList<Language>();
		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("locale", locale);
			
			langs =  jpQuery.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("LanguageRepository_List<Language>_findAllSortedByPriorityAndName", "No language found.");
			throw elsException;
		}
		
		return langs;
	}

	@SuppressWarnings("unchecked")
	public List<Language> findAllLanguagesByModule(final String module,final String locale) throws ELSException {
		List<Language> languages=new ArrayList<Language>();
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,module+"_LANGUAGES", locale);
		if(customParameter!=null){
			String[] allowedlanguages=customParameter.getValue().split(",");
			String query="SELECT m FROM Language m WHERE m.locale=:locale";
			StringBuffer buffer=new StringBuffer();
			if(allowedlanguages.length>0){
				buffer.append(" AND (");
				for(String i:allowedlanguages){
					buffer.append("m.name='"+i+"' OR ");
				}
				buffer.delete(buffer.length()-3,buffer.length()-1);
				buffer.append(" )");
				String finalQuery=query+buffer.toString()+" ORDER BY m.priority,m.name";
				try{
					Query jpQuery = this.em().createQuery(finalQuery);
					jpQuery.setParameter("locale", locale);
					languages = jpQuery.getResultList();
				}catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
					ELSException elsException = new ELSException();
					elsException.setParameter("LanguageRepository_List<Language>_findAllLanguagesByModule", "No language found.");
					throw elsException;
				}
			}			
		}
		return languages;
	}
}
