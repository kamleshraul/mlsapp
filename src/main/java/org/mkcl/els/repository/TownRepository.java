package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.Town;
import org.mkcl.els.domain.Zillaparishad;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class TownRepository extends BaseRepository<Town, Long> {

	public Town find(final String name, 
			final District district, 
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("name", name);
		search.addFilterEqual("district", district);
		search.addFilterEqual("locale", locale);
		Town town = this.searchUnique(search);
		return town;
	}
	
	public List<Town> find(final District district, 
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("district", district);
		search.addFilterEqual("locale", locale);
		List<Town> towns = this.search(search);
		return towns;
	}
	
	@SuppressWarnings("unchecked")
	public List<Town> findTownsbyDistricts(
			final String[] districtsArray, final String locale) {
		List<Town> towns = new ArrayList<Town>();
		try {
			String initialQuery = "SELECT DISTINCT(t) FROM Town t "
								+ " where t.locale='"
					+ locale + "' AND t.district IN ( ";
			StringBuffer buffer = new StringBuffer();
			for (String i : districtsArray) {
				buffer.append("'" + i + "',");
			}
			buffer.deleteCharAt(buffer.length() - 1);
			String query = initialQuery + buffer.toString()
					+ ") ORDER BY t.name";
			towns = this.em().createQuery(query).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return towns;
	}
	
	@SuppressWarnings("unchecked")
	public List<Town> findTownsByDistrictId(final Long districtId,
			final String orderBy, final String sortOrder, final String locale) throws ELSException {
		String strQuery="SELECT t FROM Town t" +
				" WHERE t.district.id=:districtId" +
				" AND t.locale=:locale" +
				" ORDER BY t."+orderBy+" "+sortOrder;
		List<Town> towns = new ArrayList<Town>();
		
		try{
			Query query=this.em().createQuery(strQuery);
			query.setParameter("districtId", districtId);
			query.setParameter("locale", locale);
			
			List<Town> dX = query.getResultList();
			if(dX != null){
				towns = dX;
			}
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("DistrictRepository_List<District>_findDistrictsByStateId", "No district found.");
			throw elsException;
		}
		return towns;
	}
	
	
}