package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Part;
import org.mkcl.els.domain.PartDraft;
import org.mkcl.els.domain.Roster;
import org.springframework.stereotype.Repository;

@Repository
public class PartRepository extends BaseRepository<Part, Serializable> {
		
	public List<PartDraft> findRevision(final Long partId, final Boolean includeWfCopy, final String locale){
		String strQuery = "SELECT pd"
							+ " FROM Part p" 
							+ " JOIN p.partDrafts pd"
							+ " WHERE p.id=:partId"
							+ " AND pd.isWorkflowCopy="+includeWfCopy
							+ " AND p.locale=:locale" 
							+ " ORDER BY pd.editedOn DESC";
		
		TypedQuery<PartDraft> jpQuery = this.em().createQuery(strQuery, PartDraft.class);
		jpQuery.setParameter("partId", partId);
		jpQuery.setParameter("locale", locale);
		
		List<PartDraft> drafts = jpQuery.getResultList();
		if(drafts != null){
			return drafts;
		}
		
		return (new ArrayList<PartDraft>());
	}

	public List<Part> findInterruptedProceedingInRoster(Roster roster,
			Locale locale) {
		String strQuery="SELECT p FROM Part p JOIN p.proceeding proc JOIN proc.slot s " +
				" JOIN s.roster r WHERE r.locale=:locale AND r.id=:rosterId AND p.isInterrupted=true  ";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("locale", locale.toString());
		query.setParameter("rosterId", roster.getId());
		List<Part> parts=query.getResultList();
		return parts;
	}	
	
	public List<Part> findAllPartOfProceedingOfRoster(final Roster roster, final Boolean usePrimaryMember, final String locale) throws ELSException{
		List<Part> parts = new ArrayList<Part>();
		String query = "SELECT pp FROM Part pp"
						+ " LEFT JOIN pp.proceeding p"
						+ " WHERE p.slot.roster.id=:rosterId"
						+ ((usePrimaryMember==true)? " AND pp.primaryMember!=" + null:"")
						+ " AND p.locale=:locale";
		try{
			TypedQuery<Part> tQuery = this.em().createQuery(query, Part.class);
			tQuery.setParameter("rosterId", roster.getId());
			tQuery.setParameter("locale", locale);
			parts = tQuery.getResultList();
		}catch (Exception e) {
			ELSException elsException = new ELSException();
			elsException.setParameter("PartRepository:findAllPartsOfProceedingOfRoster", e.getCause().getMessage());
			throw elsException;
		}
		return parts;
	}
	
	public List<Part> findAllPartRosterSearchTerm(final Roster roster, String searchTerm, final String locale) throws ELSException{
		List<Part> parts = new ArrayList<Part>();
		String query = "SELECT pp FROM Proceeding p"
						+ " LEFT JOIN p.parts pp"
						+ " WHERE p.slot.roater.id=:rosterId"
						+ " AND p.locale=:locale"
						+ " AND (p.editedContent LIKE :searchTerm OR p.reviseddContent LIKE :searchTerm)";
		try{
			TypedQuery<Part> tQuery = this.em().createQuery(query, Part.class);
			tQuery.setParameter("rosterId", roster.getId());
			tQuery.setParameter("locale", locale);
			tQuery.setParameter("searchTerm", "%" + searchTerm + "%");
			parts = tQuery.getResultList();
		}catch (Exception e) {
			ELSException elsException = new ELSException();
			elsException.setParameter("PartRepository:findAllPartRosterSearchTerm", e.getCause().getMessage());
			throw elsException;
		}
		return parts;
	}
	
	public List findAllEligibleForReplacement(final Roster roster, String searchTerm, String replaceTerm, String locale){
		List data = null;
		String query = "SELECT pp.id," 
							+" pp.revised_content," 
							+" pp.edited_content,"
							+" CASE WHEN pp.edited_content<>'' THEN pp.edited_content"
							+" ELSE pp.revised_content "
							+" END AS originalText,"
							+" CASE WHEN pp.edited_content<>'' THEN"
							+" REPLACE(pp.edited_content, '"+searchTerm+"','" + replaceTerm +"')"
							+" ELSE REPLACE(pp.revised_content, '"+searchTerm+"','" + replaceTerm +"')"
							+" END AS replacedText,"
							+" '0' AS undoData,"
							+" '0' AS redoData,"
							+" '0' AS useit"
							+" FROM rosters ro"
							+" INNER JOIN slots sl ON(sl.roster=ro.id)"
							+" INNER JOIN proceedings p ON(p.slot=sl.id)"
							+" INNER JOIN parts pp ON(pp.proceeding=p.id)"
							+" WHERE ro.id=" + roster.getId()
							+" AND MATCH(pp.revised_content,pp.edited_content) AGAINST('"+ searchTerm +"' IN NATURAL LANGUAGE MODE)";
		Query tQuery = this.em().createNativeQuery(query);
		data = tQuery.getResultList();
		return data;
	}
	
	public List<Member> findAllProceedingMembersOfRoster(final Roster roster, final String locale){
		
		List<Member> members = new ArrayList<Member>();
		
		try{
			String query = "SELECT pp.primaryMember FROM Part pp"
							+ " LEFT JOIN pp.proceeding p"
							+ " WHERE p.slot.roster.id=:rosterId"
							+ " AND pp.locale=:locale";
			TypedQuery<Member> tQuery = this.em().createQuery(query, Member.class);
			tQuery.setParameter("rosterId", roster.getId());
			tQuery.setParameter("locale", locale);
			members = tQuery.getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return members;
	}
	
	public List<Member> findAllProceedingMembersOfRosterHavingDevices(final Roster roster, final List<Long> devices, final String locale){
		
		List<Member> members = new ArrayList<Member>();
		
		try{
			String query = "SELECT pp.primaryMember FROM Part pp"
							+ " LEFT JOIN pp.proceeding p"
							+ " WHERE p.slot.roster.id=:rosterId"
							+ " AND pp.deviceType.id IN (:devices)"
							+ " AND pp.locale=:locale";
			TypedQuery<Member> tQuery = this.em().createQuery(query, Member.class);
			tQuery.setParameter("rosterId", roster.getId());
			tQuery.setParameter("devices", devices);
			tQuery.setParameter("locale", locale);
			members = tQuery.getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return members;
	}
	
	public List<Part> findAllPartsOfMemberOfRoster(final Roster roster, final String locale){
		List<Part> parts = new ArrayList<Part>();
		try{
			String query = "SELECT pr FROM Part pr"
						+ " LEFT JOIN pr.proceeding p"
						+ " WHERE p.slot.roster.id=:rosterId"
						+ " AND pr.locale=:locale";
			
			TypedQuery<Part> tQuery = this.em().createQuery(query, Part.class);
			tQuery.setParameter("rosterId", roster.getId());
			tQuery.setParameter("locale", locale);
			parts = tQuery.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return parts;			
	}
	
	public List<PartDraft> findAllNonWorkflowDraftsOfPart(final Part part, final String locale){
		String query = "SELECT pd FROM Part pr"
					   + " JOIN pr.partDrafts pd"
					   + " WHERE pd.isWorkflowCopy="+false
					   + " AND pr.id=:id"
					   + " AND pd.locale=:locale";
		
		TypedQuery<PartDraft> tQuery = this.em().createQuery(query, PartDraft.class);
		tQuery.setParameter("id", part.getId());
		tQuery.setParameter("locale", locale);
		List<PartDraft> pds = tQuery.getResultList();
		return pds;
	}
}
