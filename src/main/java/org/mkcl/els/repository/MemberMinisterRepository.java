package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberDepartment;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SubDepartment;
import org.springframework.stereotype.Repository;


@Repository
public class MemberMinisterRepository extends BaseRepository<MemberMinister, Long> {

	public MemberMinister findMemberMinister(final Ministry ministry, 
			final Date onDate,
			final String locale) {
		MemberMinister memberMinister = null;
		
		try {
			String strQuery = "SELECT mm" +
					" FROM MemberMinister mm" +
					" WHERE mm.ministry=:ministry AND mm.locale=:locale" +
					" AND mm.ministryFromDate<=:onDate" +
					" AND ( mm.ministryToDate IS NULL OR mm.ministryToDate>=:onDate)";
			Query query = this.em().createQuery(strQuery);
			query.setParameter("ministry", ministry);
			query.setParameter("locale", locale);
			query.setParameter("onDate", onDate);
			
			memberMinister = (MemberMinister) query.getSingleResult(); 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return memberMinister;
	}


	public List<Department> findAssignedDepartments(final Ministry ministry, 
			final Date onDate,
			final String locale){
		MemberMinister memberMinister = this.findMemberMinister(ministry,  onDate, locale);
		
		CustomParameter parameter =
			CustomParameter.findByFieldName(CustomParameter.class, "name", "DB_DATEFORMAT", "");
		Date currentDate = FormaterUtil.getCurrentDate(parameter.getValue());

		List<MemberDepartment> memberDepartments = new ArrayList<MemberDepartment>();
		if(memberMinister != null) {
		    memberDepartments = memberMinister.getMemberDepartments();
		}

		List<Department> departments = new ArrayList<Department>();
		for(MemberDepartment i : memberDepartments) {
			// A check wherein if i.toDate is a past date then discard the
			// department entry, else add the department to departments list
			if(i.getToDate() == null) {
				departments.add(i.getDepartment());
			} else if(! i.getToDate().before(onDate)) {
				departments.add(i.getDepartment());
			}
		}
		return departments;
	}


	public List<SubDepartment> findAssignedSubDepartments(final Ministry ministry, 
			final Department department,
			final Date onDate,
			final String locale) {
		String departmentName = department.getName();
		MemberMinister memberMinister = findMemberMinister(ministry, onDate, locale);

		CustomParameter parameter =
			CustomParameter.findByFieldName(CustomParameter.class, "name", "DB_DATEFORMAT", "");
		Date currentDate = FormaterUtil.getCurrentDate(parameter.getValue());

		List<MemberDepartment> memberDepartments = new ArrayList<MemberDepartment>();
        if(memberMinister != null) {
            memberDepartments = memberMinister.getMemberDepartments();
        }
		List<SubDepartment> subDepartments = new ArrayList<SubDepartment>();
		for(MemberDepartment i : memberDepartments){
			if(i.getToDate() == null){
				if(i.getDepartment().getName().equals(departmentName)){
					subDepartments.addAll(i.getSubDepartments());
				}
			} else if(! i.getToDate().before(currentDate)){
				if(i.getDepartment().getName().equals(departmentName)){
					subDepartments.addAll(i.getSubDepartments());
				}
			}
		}
		return subDepartments;
	}
	
	/**** Used in MotionController ****/
	public List<SubDepartment> findAssignedSubDepartments(final Ministry ministry,
			final Date onDate,
			final String locale) {
		MemberMinister memberMinister = this.findMemberMinister(ministry, onDate, locale);
		CustomParameter parameter =
			CustomParameter.findByFieldName(CustomParameter.class, "name", "DB_DATEFORMAT", "");
		Date currentDate = FormaterUtil.getCurrentDate(parameter.getValue());

		List<MemberDepartment> memberDepartments = new ArrayList<MemberDepartment>();
        if(memberMinister != null) {
            memberDepartments = memberMinister.getMemberDepartments();
        }
		List<SubDepartment> subDepartments = new ArrayList<SubDepartment>();
		for(MemberDepartment i : memberDepartments){
			if(i.getToDate() == null){
					subDepartments.addAll(i.getSubDepartments());
			} else if(! i.getToDate().before(onDate)){
					subDepartments.addAll(i.getSubDepartments());
			}
		}
		return subDepartments;
	}
	
    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedDepartmentsVO(final Group group, final String locale) {
    	
    	org.mkcl.els.domain.Query qQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.MEMBERMINISTER_FIND_ASSIGNED_DEPARTMENTSVO_WITH_GROUP, "");
		String strquery = qQuery.getQuery();
		List<MasterVO> references=new ArrayList<MasterVO>();
		
		try{
			Query query=this.em().createNativeQuery(strquery);
			query.setParameter("groupNumber", group.getNumber());
			query.setParameter("houseTypeId",group.getHouseType().getId());
			query.setParameter("sessionTypeId", group.getSessionType().getId());
			query.setParameter("year", group.getYear());
			query.setParameter("locale", locale);
	        List results=query.getResultList();
	        
	        for(Object i:results){
	            Object[] o=(Object[]) i;
	            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
	            references.add(masterVO);
	        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedDepartmentsVO(final Integer groupNumber,final HouseType houseType,final SessionType sessionType,final Integer year, final String locale) {
    	
    	List<MasterVO> references=new ArrayList<MasterVO>();
    	try{
	    	org.mkcl.els.domain.Query qQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.MEMBERMINISTER_FIND_ASSIGNED_DEPARTMENTSVO, "");
			String strquery = qQuery.getQuery();
			
			Query query=this.em().createNativeQuery(strquery);
			query.setParameter("groupNumber", groupNumber);
			query.setParameter("houseTypeId", houseType.getId());
			query.setParameter("sessionTypeId", sessionType.getId());
			query.setParameter("year", year);
			query.setParameter("locale", locale);
	        List results=query.getResultList();
	        for(Object i:results){
	            Object[] o=(Object[]) i;
	            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
	            references.add(masterVO);
	        }
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedDepartmentsVO(final Group[] group, final String locale) {
    	List<MasterVO> references=new ArrayList<MasterVO>();
    	try{
	    	String query="SELECT DISTINCT(d.name),d.id FROM groups AS g JOIN groups_ministries AS gm  JOIN ministries AS m JOIN members_ministries AS mm JOIN "+
	        " members_departments AS md  JOIN departments AS d "+
	        " WHERE gm.group_id=g.id AND m.id=gm.ministry_id AND mm.ministry_id=m.id  AND md.member_ministry_id=mm.id  AND d.id=md.department "+
	        " AND g.housetype_id="+group[0].getHouseType().getId()+" AND g.sessiontype_id="+group[0].getSessionType().getId()+" AND g.group_year="+group[0].getYear()+" AND g.locale='"+locale+"'";
	
	        StringBuffer buffer=new StringBuffer();
	        for(int i=0;i<group.length;i++){
	            if(i==0){
	                buffer.append(" AND(");
	                buffer.append(" g.number="+group[i].getNumber()+" ");
	            }else if(i==group.length-1){
	                buffer.append(" OR g.number="+group[i].getNumber()+" )");
	            }else{
	                buffer.append(" OR g.number="+group[i].getNumber()+" ");
	            }
	            if(1==group.length){
	                buffer.append(") ");
	            }
	        }
	        List results=this.em().createNativeQuery(query+buffer.toString()).getResultList();
	        
	        for(Object i:results){
	            Object[] o=(Object[]) i;
	            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
	            references.add(masterVO);
	        }
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			
		}
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedDepartmentsVO(final Integer[] groupNumbers,final HouseType houseType,final SessionType sessionType,final Integer year, final String locale) {
        
    	List<MasterVO> references=new ArrayList<MasterVO>();
    	
    	try{
	    	String query="SELECT DISTINCT(d.name),d.id FROM groups AS g JOIN groups_ministries AS gm  JOIN ministries AS m JOIN members_ministries AS mm JOIN "+
	        " members_departments AS md  JOIN departments AS d "+
	        " WHERE gm.group_id=g.id AND m.id=gm.ministry_id AND mm.ministry_id=m.id  AND md.member_ministry_id=mm.id  AND d.id=md.department "+
	        "AND   g.housetype_id="+houseType.getId()+" AND g.sessiontype_id="+sessionType.getId()+" AND g.group_year="+year+" AND g.locale='"+locale+"'";
	
	        StringBuffer buffer=new StringBuffer();
	        for(int i=0;i<groupNumbers.length;i++){
	            if(i==0){
	                buffer.append(" AND(");
	                buffer.append(" g.number="+groupNumbers[i]+" ");
	            }else if(i==groupNumbers.length-1){
	                buffer.append(" OR g.number="+groupNumbers[i]+" )");
	            }else{
	                buffer.append(" OR g.number="+groupNumbers[i]+" ");
	            }
	            if(1==groupNumbers.length){
	                buffer.append(") ");
	            }
	        }
	        List results=this.em().createNativeQuery(query+buffer.toString()).getResultList();
	        
	        for(Object i:results){
	            Object[] o=(Object[]) i;
	            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
	            references.add(masterVO);
	        }
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedSubDepartmentsVO(final Group group, final String locale) {
    	List<MasterVO> references=new ArrayList<MasterVO>();
    	try{
	    	org.mkcl.els.domain.Query qQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.MEMBERMINISTER_FIND_ASSIGNED_SUBDEPARTMENTSVO_WITH_GROUP, "");
			String strquery = qQuery.getQuery();
			Query query=this.em().createNativeQuery(strquery);
			query.setParameter("groupNumber", group.getNumber());
			query.setParameter("houseTypeId",group.getHouseType().getId());
			query.setParameter("sessionTypeId", group.getSessionType().getId());
			query.setParameter("year", group.getYear());
			query.setParameter("locale", locale);
	        List results=query.getResultList();
	        
	        for(Object i:results){
	            Object[] o=(Object[]) i;
	            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
	            references.add(masterVO);
	        }
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedSubDepartmentsVO(final Integer groupNumber,final HouseType houseType,final SessionType sessionType,final Integer year, final String locale) {
    	List<MasterVO> references=new ArrayList<MasterVO>();
    	try{
	    	org.mkcl.els.domain.Query qQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.MEMBERMINISTER_FIND_ASSIGNED_SUBDEPARTMENTSVO, "");
			String strquery = qQuery.getQuery();
			Query query=this.em().createNativeQuery(strquery);
			query.setParameter("groupNumber", groupNumber);
			query.setParameter("houseTypeId", houseType.getId());
			query.setParameter("sessionTypeId", sessionType.getId());
			query.setParameter("year", year);
			query.setParameter("locale", locale);
			List results=query.getResultList();
	        
	        for(Object i:results){
	            Object[] o=(Object[]) i;
	            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
	            references.add(masterVO);
	        }
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedSubDepartmentsVO(final Group[] group, final String locale) {
    	List<MasterVO> references=new ArrayList<MasterVO>();
    	
    	try{
	    	String query="SELECT DISTINCT(s.name),s.id FROM groups AS g JOIN groups_ministries AS gm  JOIN ministries AS m JOIN members_ministries AS mm JOIN "+
	        " members_departments AS md  JOIN departments AS d "+
	        " JOIN subdepartments AS s "+
	        " WHERE gm.group_id=g.id AND m.id=gm.ministry_id AND mm.ministry_id=m.id  AND md.member_ministry_id=mm.id  AND d.id=md.department "+
	        " AND s.department_id=d.id "+
	        "AND  g.housetype_id="+group[0].getHouseType().getId()+" AND g.sessiontype_id="+group[0].getSessionType().getId()+" AND g.group_year="+group[0].getYear()+" AND g.locale='"+locale+"'";
	        StringBuffer buffer=new StringBuffer();
	        for(int i=0;i<group.length;i++){
	            if(i==0){
	                buffer.append(" AND(");
	                buffer.append(" g.number="+group[i].getNumber()+" ");
	            }else if(i==group.length-1){
	                buffer.append(" OR g.number="+group[i].getNumber()+" )");
	            }else{
	                buffer.append(" OR g.number="+group[i].getNumber()+" ");
	            }
	            if(1==group.length){
	                buffer.append(") ");
	            }
	        }
	
	        List results=this.em().createNativeQuery(query+buffer.toString()).getResultList();
	        
	        for(Object i:results){
	            Object[] o=(Object[]) i;
	            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
	            references.add(masterVO);
	        }
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedSubDepartmentsVO(final Integer[] groupNumbers,final HouseType houseType,final SessionType sessionType,final Integer year, final String locale) {
        
    	List<MasterVO> references=new ArrayList<MasterVO>();
    	
    	try{
	    	String query="SELECT DISTINCT(s.name),s.id FROM groups AS g JOIN groups_ministries AS gm  JOIN ministries AS m JOIN members_ministries AS mm JOIN "+
	        " members_departments AS md  JOIN departments AS d "+
	        " JOIN subdepartments AS s "+
	        " WHERE gm.group_id=g.id AND m.id=gm.ministry_id AND mm.ministry_id=m.id  AND md.member_ministry_id=mm.id  AND d.id=md.department "+
	        " AND s.department_id=d.id "+
	        "AND  g.housetype_id="+houseType.getId()+" AND g.sessiontype_id="+sessionType.getId()+" AND g.group_year="+year+" AND g.locale='"+locale+"'";
	        StringBuffer buffer=new StringBuffer();
	        for(int i=0;i<groupNumbers.length;i++){
	            if(i==0){
	                buffer.append(" AND(");
	                buffer.append(" g.number="+groupNumbers[i]+" ");
	            }else if(i==groupNumbers.length-1){
	                buffer.append(" OR g.number="+groupNumbers[i]+" )");
	            }else{
	                buffer.append(" OR g.number="+groupNumbers[i]+" ");
	            }
	            if(1==groupNumbers.length){
	                buffer.append(") ");
	            }
	        }
	
	        List results=this.em().createNativeQuery(query+buffer.toString()).getResultList();
	        
	        for(Object i:results){
	            Object[] o=(Object[]) i;
	            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
	            references.add(masterVO);
	        }
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return references;
    }


	@SuppressWarnings("rawtypes")
	public List<MasterVO> findAssignedSubDepartmentsVO(
			final Integer[] groupNumbers, final String[] departmentNames,
			final HouseType houseType, final SessionType sessionType,
			final Integer year, final String locale) {

		List<MasterVO> references = new ArrayList<MasterVO>();

		try {
			String query = "SELECT DISTINCT(s.name),s.id FROM groups AS g JOIN groups_ministries AS gm  JOIN ministries AS m JOIN members_ministries AS mm JOIN "
					+ " members_departments AS md  JOIN departments AS d "
					+ " JOIN subdepartments AS s "
					+ " WHERE gm.group_id=g.id AND m.id=gm.ministry_id AND mm.ministry_id=m.id  AND md.member_ministry_id=mm.id  AND d.id=md.department "
					+ " AND s.department_id=d.id "
					+ "AND  g.housetype_id="
					+ houseType.getId()
					+ " AND g.sessiontype_id="
					+ sessionType.getId()
					+ " AND g.group_year="
					+ year
					+ " AND g.locale='" + locale + "'";
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < groupNumbers.length; i++) {
				if (i == 0) {
					buffer.append(" AND(");
					buffer.append(" g.number=" + groupNumbers[i] + " ");
				} else if (i == groupNumbers.length - 1) {
					buffer.append(" OR g.number=" + groupNumbers[i] + " )");
				} else {
					buffer.append(" OR g.number=" + groupNumbers[i] + " ");
				}
				if (1 == groupNumbers.length) {
					buffer.append(") ");
				}
			}
			StringBuffer buffer1 = new StringBuffer();
			for (int i = 0; i < departmentNames.length; i++) {
				if (i == 0) {
					buffer1.append(" AND(");
					buffer1.append(" d.name='" + departmentNames[i] + "' ");
				} else if (i == departmentNames.length - 1) {
					buffer1.append(" OR d.name='" + departmentNames[i] + "' )");
				} else {
					buffer1.append(" OR d.name='" + departmentNames[i] + "' ");
				}
				if (1 == departmentNames.length) {
					buffer1.append(") ");
				}
			}

			List results = this.em().createNativeQuery(query + buffer.toString() + buffer1.toString()).getResultList();

			for (Object i : results) {
				Object[] o = (Object[]) i;
				MasterVO masterVO = new MasterVO(
						Long.parseLong(o[1].toString()), o[0].toString());
				references.add(masterVO);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return references;
	}


	@SuppressWarnings("unchecked")
	public List<Department> findAssignedDepartments(
			final String[] ministriesArray, final String locale) {
		List<Department> depts = new ArrayList<Department>();
		try {
			CustomParameter parameter = CustomParameter.findByFieldName(CustomParameter.class, "name", "DB_DATEFORMAT", "");
			String strCurrentDate = FormaterUtil.getDateFormatter(parameter.getValue(), "en_US").format(new Date());
			String initialQuery = "SELECT DISTINCT(d) FROM MemberMinister mm JOIN mm.ministry mi "
					+ " JOIN mm.memberDepartments md JOIN md.department d WHERE "
					+ " (mm.ministryToDate >='"
					+ strCurrentDate
					+ "' OR mm.ministryToDate=null )"
					+ " AND mm.locale='"
					+ locale + "' AND mi.name IN ( ";
			StringBuffer buffer = new StringBuffer();
			for (String i : ministriesArray) {
				buffer.append("'" + i + "',");
			}
			buffer.deleteCharAt(buffer.length() - 1);
			String query = initialQuery + buffer.toString()
					+ ") ORDER BY d.name";
			depts = this.em().createQuery(query).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return depts;
	}

	//Unused Method... As Department is been removed from major devices this method is not used
	@SuppressWarnings("unchecked")
	public List<SubDepartment> findAssignedSubDepartments(final String[] ministries,
			final String[] departmentsNames,final String locale) {
		
		List<SubDepartment> subDepts = new ArrayList<SubDepartment>();
		try{
			CustomParameter parameter =
				CustomParameter.findByFieldName(CustomParameter.class, "name", "DB_DATEFORMAT", "");
			String strCurrentDate=FormaterUtil.getDateFormatter(parameter.getValue(),"en_US").format(new Date());
			String initialQuery="SELECT DISTINCT(sd) FROM MemberMinister mm JOIN mm.ministry mi "+
						 " JOIN mm.memberDepartments md JOIN md.department d JOIN md.subDepartments sd WHERE "+
						 " (mm.ministryToDate >='"+strCurrentDate+"' OR mm.ministryToDate=null) "+
						 " AND mm.locale='"+locale+"' AND d.name IN ( ";
			StringBuffer buffer=new StringBuffer();
			for(String i:departmentsNames){
			buffer.append("'"+i+"',");	
			}
			buffer.deleteCharAt(buffer.length()-1);
			String query=initialQuery+buffer.toString()+")";
			StringBuffer buffer2=new StringBuffer();
			buffer2.append(" AND mi.name IN(");
			for(String j:ministries){
				buffer2.append("'"+j+"',");
			}
			buffer2.deleteCharAt(buffer2.length()-1);
			String finalQuery=query+buffer2.toString()+") ORDER BY sd.name";
			
			subDepts = this.em().createQuery(finalQuery).getResultList();; 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return subDepts;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubDepartment> findAssignedSubDepartments(final String[] ministries,
			final String locale) {
		List<SubDepartment> subDepts = new ArrayList<SubDepartment>();
		try{
			Date toDateLimit = new Date();
			CustomParameter csptNewHouseFormationInProcess = CustomParameter.findByName(CustomParameter.class, "NEW_HOUSE_FORMATION_IN_PROCESS", "");
			if(csptNewHouseFormationInProcess!=null 
					&& csptNewHouseFormationInProcess.getValue()!=null
					&& csptNewHouseFormationInProcess.getValue().equals("YES")) {
				HouseType houseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
				Session latestSession = Session.findLatestSession(houseType);
				if(latestSession!=null) {
					toDateLimit = latestSession.getEndDate();
				}
			}
			CustomParameter parameter =
				CustomParameter.findByFieldName(CustomParameter.class, "name", "DB_DATEFORMAT", "");
			String strCurrentDate = FormaterUtil.
					getDateFormatter(parameter.getValue(),"en_US").format(toDateLimit);
			String initialQuery = "SELECT DISTINCT(sd) FROM MemberMinister mm JOIN mm.ministry mi "+
						 " JOIN mm.memberDepartments md JOIN md.department d JOIN md.subDepartments sd WHERE "+
						 " (mm.ministryToDate >='"+strCurrentDate+"' OR mm.ministryToDate=null) "+
						 " AND md.toDate >='" + strCurrentDate +"'"+
						 " AND mm.locale='"+locale+"' ";
			StringBuffer buffer = new StringBuffer();
			buffer.append(" AND mi.name IN(");
			for(String j : ministries){
				buffer.append("'" + j + "',");
			}
			buffer.deleteCharAt(buffer.length()-1);
			String finalQuery = initialQuery + buffer.toString() + ") ORDER BY sd.name";
			
			subDepts = this.em().createQuery(finalQuery).getResultList();; 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return subDepts;
	}

	
	
	public List<MemberMinister> findAssignedMemberMinisterOfMemberInSession(final Member member, final Session session,final String locale) {
		
		List<MemberMinister> memMinisters = new ArrayList<MemberMinister>();
		try{
		
			CustomParameter parameter =CustomParameter.findByFieldName(CustomParameter.class, "name", "DB_DATEFORMAT", "");
			Date currentDate = FormaterUtil.getCurrentDate(parameter.getValue());	
			String queryString = "SELECT mm FROM MemberMinister mm JOIN mm.ministry mi JOIN mm.member m " +
					"WHERE mi.id IN " +
					"(SELECT gm.id FROM Group g join g.ministries gm " +
					"WHERE g.houseType.id=:houseTypeId AND g.sessionType.id=:sessionTypeId"+
					" AND g.year=:sessionYear AND g.locale=:locale) " +
					" AND m.id=:memberId AND " +
					"(mm.ministryToDate >:strCurrDate  OR mm.ministryToDate IS NULL) AND " +
					"mm.locale=:locale";
			
			TypedQuery<MemberMinister> query = this.em().createQuery(queryString, MemberMinister.class);
			query.setParameter("houseTypeId", session.getHouse().getType().getId());
			query.setParameter("sessionTypeId", session.getType().getId());
			query.setParameter("sessionYear", session.getYear());
			query.setParameter("locale", locale);
			query.setParameter("memberId", member.getId());
			query.setParameter("strCurrDate", currentDate);
			memMinisters = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return memMinisters;
	}

	@SuppressWarnings("unchecked")
	public MemberMinister findMemberMinisterByAssignedMinistryInSession(Session session, Ministry ministry) {
		/** parameter for date on which to check to-date of member minister **/
		Date currentDate = new Date();
		Date activeOnCheckDate = null;
		if(currentDate.compareTo(session.getStartDate())<=0) {
			activeOnCheckDate = session.getStartDate();
		} else if(currentDate.compareTo(session.getStartDate())>0
				&& currentDate.compareTo(session.getEndDate())<0) {
			activeOnCheckDate = new Date();
		} else {
			activeOnCheckDate = session.getEndDate();
		}
		String queryString = "SELECT DISTINCT mm FROM MemberMinister mm JOIN mm.ministry mi JOIN mm.member m " +
				"WHERE mi.id=:ministryId AND " +
				"mm.ministryFromDate <=:activeOnCheckDate AND " +
				"(mm.ministryToDate >=:activeOnCheckDate OR mm.ministryToDate is NULL) AND " +
				"mm.locale = '" + session.getLocale() + "' ORDER BY mm.ministryFromDate DESC";
		TypedQuery<MemberMinister> query = this.em().createQuery(queryString, MemberMinister.class);
		query.setParameter("ministryId", ministry.getId());
		query.setParameter("activeOnCheckDate", activeOnCheckDate);
		List<MemberMinister> memberMinisters = query.getResultList();
		if(memberMinisters!=null&& !memberMinisters.isEmpty()){
			return memberMinisters.get(0);			
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public MemberMinister findMemberMinisterByAssignedMinistryInSessionWithMinistryFromGroups(Session session, Ministry ministry) {
		/** parameter for date on which to check to-date of member minister **/
		Date currentDate = new Date();
		Date activeOnCheckDate = null;
		if(currentDate.compareTo(session.getStartDate())<=0) {
			activeOnCheckDate = session.getStartDate();
		} else if(currentDate.compareTo(session.getStartDate())>0
				&& currentDate.compareTo(session.getEndDate())<0) {
			activeOnCheckDate = new Date();
		} else {
			activeOnCheckDate = session.getEndDate();
		}
		String queryString = "SELECT DISTINCT mm FROM MemberMinister mm JOIN mm.ministry mi JOIN mm.member m " +
				"WHERE mi.id IN " +
				"(SELECT gm.id FROM Group g join g.ministries gm " +
				"WHERE g.houseType.id=:houseTypeId AND g.sessionType.id=:sessionTypeId"+
				" AND g.year=:sessionYear AND g.locale=:locale) " +
				" AND mi.id=:ministryId AND " +
				"mm.ministryFromDate <=:activeOnCheckDate AND " +
				"(mm.ministryToDate >=:activeOnCheckDate OR mm.ministryToDate is NULL) AND " +
				"mm.locale = '" + session.getLocale() + "' ORDER BY mm.ministryFromDate DESC";
		TypedQuery<MemberMinister> query = this.em().createQuery(queryString, MemberMinister.class);
		query.setParameter("houseTypeId", session.getHouse().getType().getId());
		query.setParameter("sessionTypeId", session.getType().getId());
		query.setParameter("sessionYear", session.getYear());
		query.setParameter("locale", session.getLocale());
		query.setParameter("ministryId", ministry.getId());
		query.setParameter("activeOnCheckDate", activeOnCheckDate);
		List<MemberMinister> memberMinisters = query.getResultList();
		if(memberMinisters!=null&& !memberMinisters.isEmpty()){
			return memberMinisters.get(0);			
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Member findMemberByAssignedMinistryInSession(Session session, Ministry ministry) {
		MemberMinister memberMinister = this.findMemberMinisterByAssignedMinistryInSession(session, ministry);
		if(memberMinister!=null) {
			return memberMinister.getMember();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Member findMemberByAssignedMinistryInSessionWithMinistryFromGroups(Session session, Ministry ministry) {
		MemberMinister memberMinister = this.findMemberMinisterByAssignedMinistryInSessionWithMinistryFromGroups(session, ministry);
		if(memberMinister!=null) {
			return memberMinister.getMember();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MasterVO> findMinistersInSecondHouse(House house, String param,
			String locale) {
		Date currentDate = new Date();
		String strquery="SELECT DISTINCT m FROM Member m JOIN m.memberMinisters mm JOIN m.title t " +
				" WHERE m.id IN (SELECT DISTINCT hm.id FROM HouseMemberRoleAssociation hmra JOIN hmra.member hm WHERE hmra.house.id=:houseId AND hmra.toDate>=:currentDate) " +
				" AND mm.ministryToDate>=:currentDate" + 
				" AND m.locale=:locale and  (m.firstName LIKE :param  OR m.middleName LIKE :param " +
						" OR m.lastName LIKE :param  OR concat(m.lastName,' ',m.firstName) LIKE :param  " +
						" OR concat(m.firstName,' ',m.lastName) LIKE :param OR concat(m.lastName,' ',m.firstName,' ',m.middleName) LIKE :param " +
						" OR concat(m.lastName,', ',t.name,' ',m.firstName,' ',m.middleName) LIKE :param OR concat(m.firstName,' ',m.middleName,' ',m.lastName) LIKE :param) ORDER BY m.firstName asc";
		Query query=this.em().createQuery(strquery);
		query.setParameter("houseId", house.getId());
		query.setParameter("currentDate", currentDate);
		query.setParameter("locale", locale);
		query.setParameter("param", "%"+param+"%");
		List<Member> members=query.getResultList();
		List<MasterVO> masterVos=new ArrayList<MasterVO>();
		for(Member m:members){
			MasterVO masterVO=new MasterVO();
			masterVO.setId(m.getId());
			masterVO.setName(m.getFullname());
			masterVos.add(masterVO);
		}
		return masterVos;
	}

	@SuppressWarnings("unchecked")
	public List<MasterVO> findMinistersInGivenHouse(House house, String param,
			String locale) {
		Date currentDate = new Date();
		String strquery="SELECT DISTINCT m FROM Member m JOIN m.memberMinisters mm JOIN m.title t " +
				" WHERE m.id IN (SELECT DISTINCT hm.id FROM HouseMemberRoleAssociation hmra JOIN hmra.member hm WHERE hmra.house.id=:houseId AND hmra.toDate>=:currentDate) " +
				" AND mm.ministryToDate>=:currentDate" + 
				" AND m.locale=:locale and  (m.firstName LIKE :param  OR m.middleName LIKE :param " +
						" OR m.lastName LIKE :param  OR concat(m.lastName,' ',m.firstName) LIKE :param  " +
						" OR concat(m.firstName,' ',m.lastName) LIKE :param OR concat(m.lastName,' ',m.firstName,' ',m.middleName) LIKE :param " +
						" OR concat(m.lastName,', ',t.name,' ',m.firstName,' ',m.middleName) LIKE :param OR concat(m.firstName,' ',m.middleName,' ',m.lastName) LIKE :param) ORDER BY m.firstName asc";
		Query query=this.em().createQuery(strquery);
		query.setParameter("houseId", house.getId());
		query.setParameter("currentDate", currentDate);
		query.setParameter("locale", locale);
		query.setParameter("param", "%"+param+"%");
		List<Member> members=query.getResultList();
		List<MasterVO> masterVos=new ArrayList<MasterVO>();
		for(Member m:members){
			MasterVO masterVO=new MasterVO();
			masterVO.setId(m.getId());
			masterVO.setName(m.getFullname());
			masterVos.add(masterVO);
		}
		return masterVos;
	}
	
	public Member find(Ministry ministry, Locale locale) {
		String strQuery = "SELECT m FROM MemberMinister mm " +
				" JOIN mm.member m" +
				" JOIN mm.ministry mi" +
				" WHERE (mm.ministryToDate IS NULL OR mm.ministryToDate>=:currentDate)" +
				" AND (mm.ministryFromDate IS NULL OR mm.ministryFromDate<=:currentDate)" +
				" AND mm.ministry.id=:ministryId"+
				" AND m.locale=:locale";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("currentDate", new Date());
		query.setParameter("ministryId", ministry.getId());
		query.setParameter("locale", locale.toString());
		try{
			Member member=(Member) query.getSingleResult();
			return member;
		}catch(Exception e){
			return null;
		}
		
	}


	public List<SubDepartment> findAssignedSubDepartments(
			final String[] ministriesList, final Date activeFrom, final Date activeTo,
			final String locale) {
		List<SubDepartment> subDepts = new ArrayList<SubDepartment>();
		try{
			String initialQuery = "SELECT DISTINCT(sd) FROM MemberMinister mm JOIN mm.ministry mi "+
						 " JOIN mm.memberDepartments md JOIN md.department d JOIN md.subDepartments sd WHERE " +
						 " mm.ministryFromDate <= '" + activeTo +"'"+
						 " AND (mm.ministryToDate >='" + activeFrom + "' OR mm.ministryToDate=null) "+
						 " AND md.toDate >='" + activeFrom +"'"+
						 " AND mm.locale='"+locale+"' ";
			StringBuffer buffer = new StringBuffer();
			buffer.append(" AND mi.name IN(");
			for(String j : ministriesList){
				buffer.append("'" + j + "',");
			}
			buffer.deleteCharAt(buffer.length()-1);
			String finalQuery = initialQuery + buffer.toString() + ") ORDER BY sd.name";
			
			subDepts = this.em().createQuery(finalQuery).getResultList();; 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return subDepts;
	}


	public List<Ministry> findAssignedMinistries(Date activeFrom,
			Date activeTo, String locale) {
		CustomParameter csptNewHouseFormationInProcess = CustomParameter.findByName(CustomParameter.class, "NEW_HOUSE_FORMATION_IN_PROCESS", "");
		if(csptNewHouseFormationInProcess!=null 
				&& csptNewHouseFormationInProcess.getValue()!=null
				&& csptNewHouseFormationInProcess.getValue().equals("YES")) {
			HouseType houseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
			Session latestSession = null;
			try {
				latestSession = Session.findLatestSession(houseType);
				if(latestSession!=null) {
					activeTo = latestSession.getEndDate();
				}
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		List<Ministry> ministries = new ArrayList<Ministry>();
		try{
			String query = "SELECT DISTINCT(mi) FROM MemberMinister mm JOIN mm.ministry mi "+
						 " WHERE mm.ministryFromDate <= '" + activeTo +"'"+
						 " AND (mm.ministryToDate >='" + activeFrom + "' OR mm.ministryToDate=null) "+
						 " AND mm.locale='"+locale+"' " +
						 " ORDER BY mi.name";
			ministries = this.em().createQuery(query).getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return ministries;
	}	
	
}