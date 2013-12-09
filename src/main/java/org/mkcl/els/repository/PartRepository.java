package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.batik.svggen.font.table.Device;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.DeviceType;
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
	
	public List<Member> findProceedingMembersOfRoster(final Roster roster, final String locale){
		
		List<Member> members = new ArrayList<Member>();
		
		try{
			String query = "SELECT DISTINCT pp.primaryMember FROM Part pp"
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
			String query = "SELECT DISTINCT pp.primaryMember FROM Part pp"
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
	
	public List<Part> findAllPartsOfMemberOfRoster(final Roster roster,
			final Long memberId,
			final String locale){
		List<Part> parts = new ArrayList<Part>();
		try{
			String query = "SELECT pr FROM Part pr"
						+ " LEFT JOIN pr.proceeding p"
						+ " WHERE p.slot.roster.id=:rosterId"
						+ " AND pr.primaryMember.id=:memberId"
						+ " AND pr.locale=:locale";
			
			TypedQuery<Part> tQuery = this.em().createQuery(query, Part.class);
			tQuery.setParameter("rosterId", roster.getId());
			tQuery.setParameter("memberId", memberId);
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
	
	//TODO: have to change the query to include main heading instead of page heading	
	@SuppressWarnings("rawtypes")
	public List findVishaySuchiListWithoutMembers(final String catchWord, final Long rosterId, final String locale){
		String query = "SELECT" +  
						" 'catchWord' AS memberorcatchword," +
						" '"+ catchWord +"' AS catchword," +
						" '0' AS catchwordId," +
						" pr.primary_member AS memberId," +
						" '0' AS member," + 
						" (SELECT dt.name FROM devicetypes dt WHERE dt.id=pr.device_type) AS devicename," +
						" (SELECT dt.type FROM devicetypes dt WHERE dt.id=pr.device_type) AS devicetype," +
						" '0' AS devicecatchword," +
						" pr.page_heading AS originalpageheading," +
						" REPLACE(pr.page_heading,'" + catchWord + "','_____') AS replacedpageheading," +
						" pr.id AS partid" +
						" FROM parts pr" +
						" INNER JOIN proceedings proc ON(proc.id=pr.proceeding)" +
						" INNER JOIN slots sl ON(sl.id=proc.slot)" +
						" WHERE sl.roster=" + rosterId +
						" AND pr.primary_member IS NULL" +
						" AND pr.locale='" + locale + "'" +
						" AND POSITION('" + catchWord + "' IN pr.page_heading) > 0";
		
		Query pQuery = this.em().createNativeQuery(query);
		List data = pQuery.getResultList();
		
		return data;
	}
	
	//TODO: have to change the query to include main heading instead of page heading	
	@SuppressWarnings("rawtypes")
	public List findVishaySuchiListWithMembers(final String catchWord, 
			final Long rosterId,
			final Long memberId,
			final String locale){
		String query = "SELECT" +  
						" 'member' AS memberorcatchword," +
						" '" + catchWord + "' AS catchword," +
						" '0' AS catchwordId," +
						" m.id AS memberId," +
						" CONCAT(m.last_name,', ',t.name,' ', m.first_name) AS member," + 
						" (SELECT dt.name FROM devicetypes dt WHERE dt.id=pr.device_type) AS devicename," +
						" (SELECT dt.type FROM devicetypes dt WHERE dt.id=pr.device_type) AS devicetype," +
						" '0' AS devicecatchword," +
						" pr.page_heading AS originalpageheading," +
						" REPLACE(pr.page_heading,'" + catchWord + "','_____') AS replacedpageheading," +
						" pr.id AS partid" +
						" FROM parts pr" +
						" INNER JOIN proceedings proc ON(proc.id=pr.proceeding)" +
						" INNER JOIN slots sl ON(sl.id=proc.slot)" +
						" INNER JOIN members m ON(m.id=pr.primary_member)" +
						" INNER JOIN titles t ON(t.id=m.title_id)" +
						" WHERE sl.roster=" + rosterId +
						" AND pr.primary_member=" + memberId +
						" AND pr.locale='" + locale + "'" +
						" ORDER BY devicetype ASC";
		
		Query pQuery = this.em().createNativeQuery(query);
		List data = pQuery.getResultList();
		
		return data;
	}
	
	@SuppressWarnings({"rawtypes"})
	public List findPartsOfCatchwordInRoster(final Long rosterId, final String catchWord, final String locale){
		String query = "SELECT" +
						" pr.id AS partid" +
						" FROM parts pr" +
						" INNER JOIN proceedings proc ON(proc.id=pr.proceeding)" +
						" INNER JOIN slots sl ON(sl.id=proc.slot)" +
						" WHERE sl.roster=" + rosterId +
						" AND POSITION('" + catchWord + "' IN " + "pr.page_heading) > 0" +
						" AND pr.locale='" + locale +"'";
		Query pQuery = this.em().createNativeQuery(query);
								
		return pQuery.getResultList();
	}
	
	@SuppressWarnings({"rawtypes"})
	public List findPartsOfMemberInRoster(final Long rosterId, final Long memberId, final String locale){
		String query = "SELECT" +
						" pr.id AS partid" +
						" FROM parts pr" +
						" INNER JOIN proceedings proc ON(proc.id=pr.proceeding)" +
						" INNER JOIN slots sl ON(sl.id=proc.slot)" +
						" WHERE sl.roster=" + rosterId +
						" AND pr.primary_member=" + memberId +
						" AND pr.locale='" + locale +"'";
		Query pQuery = this.em().createNativeQuery(query);
								
		return pQuery.getResultList();
	}
	
	public List<MasterVO> findDevicesOfMemberInRoster(final Long rosterId, final Long memberId, final String locale){
		String query = "SELECT pr.deviceType FROM Part pr" + 
						" LEFT JOIN pr.proceeding pro" +
						" WHERE pro.slot.roster=:rosterId" +
						" AND pr.primaryMember.id=:memberId" +
						" AND pr.locale=:locale";
		
		TypedQuery<DeviceType> tQuery = this.em().createQuery(query, DeviceType.class);
		List<DeviceType> deviceTypes = tQuery.getResultList();
		List<MasterVO> devices = new ArrayList<MasterVO>();
		if(deviceTypes != null && !deviceTypes.isEmpty()){
			for(DeviceType dt : deviceTypes){
				MasterVO dtN = new MasterVO();
				dtN.setId(dt.getId());
				dtN.setValue(dt.getType());
				dtN.setName(dt.getName());
				devices.add(dtN);				
			}
		}
		
		return devices;
	}
}
