package org.mkcl.els.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.MemberDepartment;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
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

}