package org.mkcl.els.repository;

import java.util.Date;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Citizen;
import org.mkcl.els.domain.CitizenQuestion;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Repository;

@Repository
public class CitizenQuestionRepository extends BaseRepository<CitizenQuestion, Long> {

	public CitizenQuestion AddCitizenQuestion(final String citizenID,final String districtID,final String constituencyID,final String departmentID,
			final String questionText,final String memberID,final String locale) throws ELSException{
			try{
		Citizen citizenObj=Citizen.findById(Citizen.class, Long.parseLong(citizenID));
		Member memberObj=Member.findById(Member.class, Long.parseLong(memberID));
		District districtObj=District.findById(District.class, Long.parseLong(districtID));
		Constituency constituencyObj=Constituency.findById(Constituency.class, Long.parseLong(constituencyID));
		Department departmentObj=Department.findById(Department.class, Long.parseLong(departmentID));
		CitizenQuestion cq=new CitizenQuestion();
		cq.setCitizen(citizenObj);
		cq.setMember(memberObj);
		cq.setDistrict(districtObj);
		cq.setConstituency(constituencyObj);
		cq.setDepartment(departmentObj);
		cq.setCreationDate(new Date());
		cq.setQuestionText(questionText);
		cq.setLocale(locale);
		CitizenQuestion c = (CitizenQuestion)  cq.persist();
		
		return c;
		} catch (Exception e) {
			logger.error("Entity Not Found",e);
			return null;
		}
	
	}
}