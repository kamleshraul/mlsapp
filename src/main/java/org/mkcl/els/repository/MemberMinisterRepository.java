package org.mkcl.els.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberDepartment;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SubDepartment;
import org.springframework.stereotype.Repository;

import com.trg.search.Filter;
import com.trg.search.Search;

@Repository
public class MemberMinisterRepository extends BaseRepository<MemberMinister, Long> {

	public MemberMinister findMemberMinister(final Ministry ministry, final String locale) {
		Date currentDate = this.getCurrentDate();

		Search search = new Search();
		search.addFilterEqual("ministry", ministry);
		search.addFilterEqual("locale", locale);
		Filter[] filters = new Filter[]{
					Filter.isNull("ministryToDate"),
					Filter.greaterOrEqual("ministryToDate", currentDate)
				};
		search.addFilterOr(filters);
		return this.searchUnique(search);
	}


	public List<Department> findAssignedDepartments(final Ministry ministry, final String locale){
		MemberMinister memberMinister = this.findMemberMinister(ministry,  locale);
		Date currentDate = this.getCurrentDate();

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
			} else if(! i.getToDate().before(currentDate)) {
				departments.add(i.getDepartment());
			}
		}
		return departments;
	}


	public List<SubDepartment> findAssignedSubDepartments(final Ministry ministry, final Department department,
			 final String locale) {
		String departmentName = department.getName();
		MemberMinister memberMinister = this.findMemberMinister(ministry, locale);

		Date currentDate = this.getCurrentDate();

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


	//====================== Internal Methods ==============================
	private Date getCurrentDate() {
		CustomParameter parameter =
			CustomParameter.findByFieldName(CustomParameter.class, "name", "DB_DATEFORMAT", "");
		String dbDateFormat = parameter.getValue();
		return this.getCurrentDate(dbDateFormat);
	}


	private Date getCurrentDate(final String dateFormat) {
		Date currentDate = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String strCurrentDate = sdf.format(currentDate);
		Date date = currentDate;
		try {
			date = new SimpleDateFormat(dateFormat).parse(strCurrentDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}


    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedDepartmentsVO(final Group group, final String locale) {
        String query="SELECT DISTINCT(d.name),d.id FROM groups AS g JOIN groups_ministries AS gm  JOIN ministries AS m JOIN members_ministries AS mm JOIN "+
                     " members_departments AS md  JOIN departments AS d "+
                     " WHERE gm.group_id=g.id AND m.id=gm.ministry_id AND mm.ministry_id=m.id  AND md.member_ministry_id=mm.id  AND d.id=md.department "+
                     "AND  g.number="+group.getNumber()+" AND g.housetype_id="+group.getHouseType().getId()+" AND g.sessiontype_id="+group.getSessionType().getId()+" AND g.group_year="+group.getYear()+" AND g.locale='"+locale+"'";
        List results=this.em().createNativeQuery(query).getResultList();
        List<MasterVO> references=new ArrayList<MasterVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
            references.add(masterVO);
        }
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedDepartmentsVO(final Integer groupNumber,final HouseType houseType,final SessionType sessionType,final Integer year, final String locale) {
        String query="SELECT DISTINCT(d.name),d.id FROM groups AS g JOIN groups_ministries AS gm  JOIN ministries AS m JOIN members_ministries AS mm JOIN "+
                     " members_departments AS md  JOIN departments AS d "+
                     " WHERE gm.group_id=g.id AND m.id=gm.ministry_id AND mm.ministry_id=m.id  AND md.member_ministry_id=mm.id  AND d.id=md.department "+
                     "AND  g.number="+groupNumber+" AND g.housetype_id="+houseType.getId()+" AND g.sessiontype_id="+sessionType.getId()+" AND g.group_year="+year+" AND g.locale='"+locale+"'";
        List results=this.em().createNativeQuery(query).getResultList();
        List<MasterVO> references=new ArrayList<MasterVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
            references.add(masterVO);
        }
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedDepartmentsVO(final Group[] group, final String locale) {
        String query="SELECT DISTINCT(d.name),d.id FROM groups AS g JOIN groups_ministries AS gm  JOIN ministries AS m JOIN members_ministries AS mm JOIN "+
                     " members_departments AS md  JOIN departments AS d "+
                     " WHERE gm.group_id=g.id AND m.id=gm.ministry_id AND mm.ministry_id=m.id  AND md.member_ministry_id=mm.id  AND d.id=md.department "+
                     "AND   g.housetype_id="+group[0].getHouseType().getId()+" AND g.sessiontype_id="+group[0].getSessionType().getId()+" AND g.group_year="+group[0].getYear()+" AND g.locale='"+locale+"'";

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
        List<MasterVO> references=new ArrayList<MasterVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
            references.add(masterVO);
        }
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedDepartmentsVO(final Integer[] groupNumbers,final HouseType houseType,final SessionType sessionType,final Integer year, final String locale) {
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
        List<MasterVO> references=new ArrayList<MasterVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
            references.add(masterVO);
        }
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedSubDepartmentsVO(final Group group, final String locale) {
        String query="SELECT DISTINCT(s.name),s.id FROM groups AS g JOIN groups_ministries AS gm  JOIN ministries AS m JOIN members_ministries AS mm JOIN "+
                     " members_departments AS md  JOIN departments AS d "+
                     " JOIN subdepartments AS s "+
                     " WHERE gm.group_id=g.id AND m.id=gm.ministry_id AND mm.ministry_id=m.id  AND md.member_ministry_id=mm.id  AND d.id=md.department "+
                     " AND s.department_id=d.id "+
                     "AND  g.number="+group.getNumber()+" AND g.housetype_id="+group.getHouseType().getId()+" AND g.sessiontype_id="+group.getSessionType().getId()+" AND g.group_year="+group.getYear()+" AND g.locale='"+locale+"'";
        List results=this.em().createNativeQuery(query).getResultList();
        List<MasterVO> references=new ArrayList<MasterVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
            references.add(masterVO);
        }
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedSubDepartmentsVO(final Integer groupNumber,final HouseType houseType,final SessionType sessionType,final Integer year, final String locale) {
        String query="SELECT DISTINCT(s.name),s.id FROM groups AS g JOIN groups_ministries AS gm  JOIN ministries AS m JOIN members_ministries AS mm JOIN "+
                     " members_departments AS md  JOIN departments AS d "+
                     " JOIN subdepartments AS s "+
                     " WHERE gm.group_id=g.id AND m.id=gm.ministry_id AND mm.ministry_id=m.id  AND md.member_ministry_id=mm.id  AND d.id=md.department "+
                     " AND s.department_id=d.id "+
                     "AND  g.number="+groupNumber+" AND g.housetype_id="+houseType.getId()+" AND g.sessiontype_id="+sessionType.getId()+" AND g.group_year="+year+" AND g.locale='"+locale+"'";
        List results=this.em().createNativeQuery(query).getResultList();
        List<MasterVO> references=new ArrayList<MasterVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
            references.add(masterVO);
        }
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedSubDepartmentsVO(final Group[] group, final String locale) {
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
        List<MasterVO> references=new ArrayList<MasterVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
            references.add(masterVO);
        }
        return references;
    }

    @SuppressWarnings("rawtypes")
    public List<MasterVO> findAssignedSubDepartmentsVO(final Integer[] groupNumbers,final HouseType houseType,final SessionType sessionType,final Integer year, final String locale) {
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
        List<MasterVO> references=new ArrayList<MasterVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MasterVO masterVO=new MasterVO(Long.parseLong(o[1].toString()),o[0].toString());
            references.add(masterVO);
        }
        return references;
    }

}