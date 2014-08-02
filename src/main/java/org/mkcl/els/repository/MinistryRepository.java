package org.mkcl.els.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MinistryVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SubDepartment;
import org.springframework.stereotype.Repository;

@Repository
public class MinistryRepository extends BaseRepository<Ministry, Long> {

	@SuppressWarnings("unchecked")
	public List<Ministry> findUnassignedMinistries(final String locale) {
		
		List<Ministry> ministries = new ArrayList<Ministry>();
		try{
			CustomParameter dbDateFormat =
				CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			Date currDate = new Date();
			/**
			 * I am trying to mimic mm.ministryToDate > CURDATE(), but since
			 * CURDATE() is MySQL specific i am using DB_DATEFORMAT from
			 * custom_parameters.
			 */
			String strQuery = "SELECT m " +
			"FROM Ministry m WHERE m.locale =:locale AND m.id NOT IN " +
			"(SELECT m.id FROM MemberMinister mm JOIN mm.ministry m " +
			"WHERE mm.ministryToDate IS NULL OR mm.ministryToDate > :currentDate) " +
			"ORDER BY m.name";
			javax.persistence.Query query=this.em().createQuery(strQuery);
			query.setParameter("locale",locale);
			query.setParameter("currentDate", currDate);
			ministries = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return ministries;
	}
	
	@SuppressWarnings("unchecked")
	public List<Ministry> findAssignedMinistries(final String locale) {
		
		List<Ministry> ministries = new ArrayList<Ministry>();
		
		try{
			CustomParameter dbDateFormat =
				CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			Date currDate = new Date();
			/**
			 * I am trying to mimic mm.ministryToDate > CURDATE(), but since
			 * CURDATE() is MySQL specific i am using DB_DATEFORMAT from
			 * custom_parameters.
			 */
			String strQuery = "SELECT m FROM Ministry m " +
			"WHERE m.locale =:locale AND " +
			"m.id IN " +
				"(SELECT m.id FROM MemberMinister mm JOIN mm.ministry m " +
				"WHERE mm.ministryToDate IS NULL OR mm.ministryToDate>=:currentDate) " +
			"ORDER BY m.name";
			javax.persistence.Query query=this.em().createQuery(strQuery);
			query.setParameter("locale",locale);
			query.setParameter("currentDate",currDate);
			ministries = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return ministries;
	}

    @SuppressWarnings("rawtypes")
    public List<Ministry> findMinistriesAssignedToGroups(final HouseType houseType,
            final Integer sessionYear, final SessionType sessionType,final String locale) throws ELSException {
    	List<Ministry> ministries = new ArrayList<Ministry>();
		try {
			Query nativeQuery=Query.findByFieldName(Query.class, "keyField", ApplicationConstants.MINISTRY_FIND_MINISTRIES_ASSIGNED_TO_GROUPS_QUERY, "");
			String strQuery=nativeQuery.getQuery();
			javax.persistence.Query query=this.em().createNativeQuery(strQuery);
			query.setParameter("locale",locale);
			query.setParameter("houseTypeId", houseType.getId());
			query.setParameter("sessionTypeId", sessionType.getId());
			query.setParameter("sessionYear", sessionYear);
			List results=query.getResultList();
			ministries = new ArrayList<Ministry>();
			for(Object i:results){
			    Object[] o=(Object[]) i;
			    Ministry ministry=new Ministry();
			    ministry.setId(Long.parseLong(o[0].toString()));
			    ministry.setLocale(o[1].toString());
			    ministry.setVersion(Long.parseLong(o[2].toString()));
			    ministry.setIsExpired(Boolean.parseBoolean(o[3].toString()));
			    ministry.setName(o[4].toString());
			    ministry.setRemarks(o[5].toString());
			    ministries.add(ministry);
			}
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MinistryRepository_List<Ministry>_findMinistriesAssignedToGroups", "No ministry found.");
			throw elsException;
		}
        return ministries;
    }

	public List<MasterVO> findMinistriesAssignedToGroupsByTerm(HouseType houseType,
			Integer sessionYear, SessionType sessionType, String param,
			String locale) {
		String strQuery="SELECT m.id,m.name" +
				" FROM ministries AS m " +
				"JOIN groups_ministries AS gm " +
				"JOIN groups AS g " +
				"WHERE m.id=gm.ministry_id " +
				"AND gm.group_id=g.id " +
				"AND m.locale=:locale " +
				"AND g.housetype_id=:houseTypeId " +
				"AND g.sessiontype_id=:sessionTypeId " +
				"AND g.group_year=:sessionYear " +
				"AND m.name like :term " +
				"ORDER BY m.name ASC";
		javax.persistence.Query query=this.em().createNativeQuery(strQuery);
		query.setParameter("locale", locale);
		query.setParameter("houseTypeId", houseType.getId());
		query.setParameter("sessionTypeId", sessionType.getId());
		query.setParameter("sessionYear", sessionYear);
		query.setParameter("term","%"+param+"%");
		List ministries=query.getResultList();
		List<MasterVO> ministryVos=new ArrayList<MasterVO>();
		for(Object i:ministries){
			Object[] o=(Object[]) i;
			MasterVO masterVO=new MasterVO();
			masterVO.setId(Long.parseLong(o[0].toString()));
			masterVO.setName( o[1].toString());
			ministryVos.add(masterVO);
		}
		return ministryVos;
	}

	public Ministry find(SubDepartment subDepartment,Locale locale) {
		String strQuery= "SELECT m FROM MemberMinister mm"+ 
						" JOIN mm.ministry m"+
						" JOIN mm.memberDepartments md"+ 
						" JOIN md.subDepartments msd"+ 
						" WHERE msd.id=:subDepartmentId"+
						" AND (mm.ministryToDate IS NULL OR mm.ministryToDate>=:currentDate)"+
						" AND (mm.ministryFromDate IS NULL OR mm.ministryFromDate<=:currentDate)";
		
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("subDepartmentId", subDepartment.getId());
		query.setParameter("currentDate", new Date());
		try{
			Ministry ministry=(Ministry) query.getSingleResult();
			return ministry;
		}catch(Exception e){
			return null;
		}
		
		
						
	}

	
}
