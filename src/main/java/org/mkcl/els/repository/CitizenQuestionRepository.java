package org.mkcl.els.repository;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Citizen;
import org.mkcl.els.domain.CitizenQuestion;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Repository;

@Repository
public class CitizenQuestionRepository extends BaseRepository<CitizenQuestion, Long> {

	public boolean AddCitizenQuestion(final String citizen,
			final String questionText,final String member,final String locale) throws ELSException{
			try{
		Citizen citizenObj=Citizen.findById(Citizen.class, Long.parseLong(citizen));
		Member memberObj=Citizen.findById(Member.class, Long.parseLong(member));
		CitizenQuestion cq=new CitizenQuestion();
		cq.setCitizen(citizenObj);
		cq.setLocale(locale);
		cq.setQuestionText(questionText);
		cq.setMember(memberObj);
		cq.merge();
		return true;
		} catch (Exception e) {
			logger.error("Entity Not Found",e);
			return false;
		}
	
	}
}