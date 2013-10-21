package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.LapsedEntity;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LapsedEntityRepository  extends BaseRepository<LapsedEntity, Serializable>{
	
	@Transactional
	public Boolean referLapsed(final String device, final Long primaryId, final Long lapsedId, final String locale) {
		try {
			if(device.startsWith(ApplicationConstants.DEVICE_BILLS)){
				Bill primaryBill=Bill.findById(Bill.class,primaryId);
				Bill lapsedBill=Bill.findById(Bill.class,lapsedId);
				
				LapsedEntity lapsedEntity = new LapsedEntity();
				lapsedEntity.setDevice(lapsedBill);
				lapsedEntity.setLocale(lapsedBill.getLocale());
				lapsedEntity.setDevice(lapsedBill);
				lapsedEntity.setDeviceType(lapsedBill.getType());
				lapsedEntity.persist();
				
				primaryBill.setLapsedBill(lapsedEntity);				
				primaryBill.simpleMerge();				
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	@Transactional
	public Boolean deReferLapsed(final String device, final Long primaryId, final Long referencingId, final String locale) {
		try {
			if(device.startsWith(ApplicationConstants.DEVICE_BILLS)){			
				Bill primaryBill=Bill.findById(Bill.class,primaryId);					
				if(primaryBill.getLapsedBill() != null){
					//LapsedEntity lapsedEntityToBeRemoved = primaryBill.getLapsedBill();
					primaryBill.setLapsedBill(null);						
					primaryBill.simpleMerge();
					//refEntityToBeRemoved.remove();
				}			
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	public List<BillSearchVO> fullTextSearchReferLapsedBill(final String param, final Bill bill, 
			final String language, final int start,final int noOfRecords,final String locale) {
		
		List<BillSearchVO> billSearchVOs = new ArrayList<BillSearchVO>();
		
		String houseType = bill.getHouseType().getType();	
		
		Status statusRejected = Status.findByType(ApplicationConstants.BILL_FINAL_REJECTION,bill.getLocale());	
		
		StringBuffer sb = new StringBuffer();
		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
			List<Session> checkSessions = Session.findSessionsByHouseAndDateLimits(bill.getSession().getHouse(), bill.getSession().getHouse().getFirstDate(), bill.getSession().getStartDate());
			System.out.println(checkSessions.size());					
			int index = 1;
			for(Session ss: checkSessions){
				sb.append(ss.getId());
				if(index < checkSessions.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkSessions = null;
		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
			List<Member> checkMembers = HouseMemberRoleAssociation.findAllActiveMembersInHouse(bill.getSession().getHouse(), bill.getLocale());
			System.out.println(checkMembers.size());
			int index = 1;
			for(Member m: checkMembers){
				sb.append(m.getId());
				if(index < checkMembers.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkMembers = null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(bill.getSubmissionDate());
		
		String selectQuery = "SELECT b.id as billId, b.number as billNumber," +						
				" (CASE WHEN (lang.id=titleDraft.language_id) THEN titleDraft.text ELSE NULL END) AS billTitle," +
				" (CASE WHEN (lang.id=revisedTitleDraft.language_id) THEN revisedTitleDraft.text ELSE NULL END) AS billRevisedTitle," +
				" (CASE WHEN (lang.id=contentDraft.language_id) THEN contentDraft.text ELSE NULL END) AS billContent, " +
				" (CASE WHEN (lang.id=revisedContentDraft.language_id) THEN revisedContentDraft.text ELSE NULL END) AS billRevisedContent," +
				" lang.type AS languageType," +			
				" st.name as billStatus, dt.name as billDeviceType," +	
				" mi.name as billMinistry, sd.name as billSubDepartment, st.type as billStatusType, s.sessiontype_id as billSessionType,s.session_year as billSessionYear," +
					" CASE " +
					"		WHEN b.status_id="+statusRejected.getId().toString() + " THEN b.rejection_date " +
					"		ELSE b.admission_date " +
					" END as billDate " +
					" FROM bills as b " +	
					" LEFT JOIN housetypes as ht ON(b.housetype_id=ht.id)" +
					" LEFT JOIN sessions as s ON(b.session_id=s.id)" +
					" LEFT JOIN members as m ON(b.member_id=m.id)" +
					" LEFT JOIN status as st ON(b.recommendationstatus_id=st.id)" +
					" LEFT JOIN status as ist ON(b.internalstatus_id=ist.id)" +
					" LEFT JOIN devicetypes as dt ON(b.devicetype_id=dt.id)" +
					" LEFT JOIN ministries as mi ON(b.ministry_id=mi.id)" +				
					" LEFT JOIN subdepartments as sd ON(b.subdepartment_id=sd.id)" +
					" LEFT JOIN `bills_titles` AS bt ON (bt.bill_id = b.id)" +
					" LEFT JOIN `bills_revisedtitles` AS brt ON (brt.bill_id = b.id)" +
					" LEFT JOIN `bills_contentdrafts` AS bc ON (bc.bill_id = b.id)" +
					" LEFT JOIN `bills_revisedcontentdrafts` AS brc ON (brc.bill_id = b.id)" +
					" LEFT JOIN `text_drafts` AS titleDraft ON (titleDraft.id = bt.title_id)" +
					" LEFT JOIN `text_drafts` AS revisedTitleDraft ON (revisedTitleDraft.id = brt.revised_title_id)" +
					" LEFT JOIN `text_drafts` AS contentDraft ON (contentDraft.id = bc.content_draft_id)" +
					" LEFT JOIN `text_drafts` AS revisedContentDraft ON (revisedContentDraft.id = brc.revised_content_draft_id)" +
					" LEFT JOIN languages AS lang ON (lang.id = titleDraft.language_id OR lang.id = revisedTitleDraft.language_id" +
					" OR lang.id = contentDraft.language_id OR lang.id = revisedContentDraft.language_id)" + 
					" WHERE" +						
					" b.id <> " + bill.getId() + " AND b.parent is NULL" + 
					" AND b.housetype_id="+bill.getHouseType().getId().toString() + 
					" AND ist.type='"+ApplicationConstants.BILL_FINAL_LAPSED + "'" +
					" AND b.devicetype_id="+bill.getType().getId() + 
					" AND CASE " +
					" WHEN (dt.type='"+ApplicationConstants.NONOFFICIAL_BILL + 
					"' 	AND ht.type='"+ApplicationConstants.LOWER_HOUSE +
					"' ) THEN s.id IN (" + sb + ") " +					
					" WHEN (dt.type='"+ApplicationConstants.NONOFFICIAL_BILL + 
					"' 	AND ht.type='"+ApplicationConstants.UPPER_HOUSE +
					"' ) THEN m.id IN (" + sb + ") " +
					" WHEN dt.type='"+ApplicationConstants.GOVERNMENT_BILL + 					
					"' 	THEN year(b.submission_date)="+calendar.get(Calendar.YEAR) + 
					" END "; 
//					+
//					" AND lang.type = '" + language + "'";
		
		/**** fulltext query ****/
		String searchQuery = null;	
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND ((match(titleDraft.text) against('"+param+"' in natural language mode))" +
					" || (match(revisedTitleDraft.text) against('"+param+"' in natural language mode))" + 
					" || (match(contentDraft.text) against('"+param+"' in natural language mode))" + 
					" || (match(revisedContentDraft.text) against('"+param+"' in natural language mode))" + 
					" || titleDraft.text LIKE '"+param+"%' || revisedTitleDraft.text LIKE '"+param+"%'" +
					" || contentDraft.text LIKE '"+param+"%' || revisedContentDraft.text LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND ((match(titleDraft.text) against('"+buffer.toString()+"' in boolean mode))" +
					" || (match(revisedTitleDraft.text) against('"+buffer.toString()+"' in boolean mode))" + 
					" || (match(contentDraft.text) against('"+buffer.toString()+"' in boolean mode))" + 
					" || (match(revisedContentDraft.text) against('"+buffer.toString()+"' in boolean mode))";				
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND ((match(titleDraft.text) against('"+buffer.toString()+"' in boolean mode))" +
					" || (match(revisedTitleDraft.text) against('"+buffer.toString()+"' in boolean mode))" + 
					" || (match(contentDraft.text) against('"+buffer.toString()+"' in boolean mode))" + 
					" || (match(revisedContentDraft.text) against('"+buffer.toString()+"' in boolean mode))";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND ((match(titleDraft.text) against('"+param+"' in boolean mode))" +
					" || (match(revisedTitleDraft.text) against('"+param+"' in boolean mode))" + 
					" || (match(contentDraft.text) against('"+param+"' in boolean mode))" + 
					" || (match(revisedContentDraft.text) against('"+param+"' in boolean mode))";
		}
		
		/**** Order By Query ****/
		String orderByQuery=" ORDER BY b.submission_date "+ApplicationConstants.ASC + ", lang.id "+ApplicationConstants.ASC;
		
		/**** Final Query ****/
		String finalQuery="SELECT rs.billId,rs.billNumber,rs.billTitle,rs.billRevisedTitle,rs.billContent, "+
				" rs.billRevisedContent,rs.languageType,rs.billStatus,rs.billDeviceType," + 
				" rs.billMinistry,rs.billSubDepartment,rs.billStatusType, rs.billSessionType, rs.billSessionYear," + 
				" rs.billDate FROM ("+selectQuery+searchQuery+orderByQuery+") as rs LIMIT "+start+","+noOfRecords;
				
		List resultList=this.em().createNativeQuery(finalQuery).getResultList();
		String billId = "";
		BillSearchVO billSearchVO = new BillSearchVO();
		if(resultList != null){
			for(Object i : resultList){
				Object[] o = (Object[]) i;		
				if(!billId.equals(o[0].toString())) {
					if(!billSearchVOs.isEmpty()) {
						billSearchVO = new BillSearchVO();
					}
					billSearchVOs.add(billSearchVO);
					if(o[0] != null){
						billSearchVO.setId(Long.parseLong(o[0].toString()));
					}
					if(o[1] != null){
						billSearchVO.setNumber(o[1].toString());
					}
					if(o[7] != null){
						billSearchVO.setStatus(o[7].toString());
					}
					if(o[8] != null){
						billSearchVO.setDeviceType(o[8].toString());
					}					
					if(o[9] != null){
						billSearchVO.setMinistry(o[9].toString());
					}					
					if(o[10] != null){
						billSearchVO.setSubDepartment(o[10].toString());
					}					
					if(o[11] != null){
						billSearchVO.setStatusType(o[11].toString());
					}					
					if(o[12] != null){
						Long sessionTypeId = new Long(o[12].toString());
						SessionType sessionType = SessionType.findById(SessionType.class, sessionTypeId);
						if(sessionType != null){
							billSearchVO.setSessionType(sessionType.getSessionType());
						}
					}					
					if(o[13] != null){					
						billSearchVO.setSessionYear(o[13].toString());
					}					
					if(o[14] != null){	
						Date dateOfBill = FormaterUtil.formatStringToDate(o[14].toString(), "yyyy-MM-dd HH:mm:ss");
						String dateOfBillStr = FormaterUtil.formatDateToString(dateOfBill, "dd-MM-yyyy HH:mm:ss", bill.getLocale());
						billSearchVO.setDateOfBill(dateOfBillStr);
					}
//					if(o[15] != null){	
//						Date admissionDate = FormaterUtil.formatStringToDate(o[15].toString(), "yyyy-MM-dd HH:mm:ss");
//						String admissionDateStr = FormaterUtil.formatDateToString(admissionDate, "dd-MM-yyyy HH:mm:ss", bill.getLocale());
//						billSearchVO.setAdmissionDate(admissionDateStr);
//					}					
					if(o[2] != null){
						billSearchVO.setTitle(higlightText(o[2].toString(), param));
					}
					if(o[3] != null){
						billSearchVO.setRevisedTitle(higlightText(o[3].toString(), param));
					}
					if(o[4] != null){
						billSearchVO.setContent(higlightText(o[4].toString(), param));
					}
					if(o[5] != null){
						billSearchVO.setRevisedContent(higlightText(o[5].toString(), param));
					}					
					billId = o[0].toString();
				} else {
					if(o[2] != null){
						billSearchVO.setTitle(higlightText(o[2].toString(), param));
					}
					if(o[3] != null){
						billSearchVO.setRevisedTitle(higlightText(o[3].toString(), param));
					}
					if(o[4] != null){
						billSearchVO.setContent(higlightText(o[4].toString(), param));
					}
					if(o[5] != null){
						billSearchVO.setRevisedContent(higlightText(o[5].toString(), param));
					}
				}											
			}
		}
		
		return billSearchVOs;		
	}
	
	private String higlightText(final String textToHiglight,final String pattern) {

		String highlightedText=textToHiglight;
		String replaceMentText="<span class='highlightedSearchPattern'>";
		String replaceMentTextEnd="</span>";
		if((!pattern.contains("+"))&&(!pattern.contains("-"))){
			String[] temp=pattern.trim().split(" ");
			for(String j:temp){
				if(!j.isEmpty()){
					if(!highlightedText.contains(replaceMentText+j.trim()+replaceMentTextEnd)){
						highlightedText=highlightedText.replaceAll(j.trim(),replaceMentText+j.trim()+replaceMentTextEnd);
					}
				}
			}			
		}else if((pattern.contains("+"))&&(!pattern.contains("-"))){
			String[] temp=pattern.trim().split("\\+");
			for(String j:temp){
				if(!highlightedText.contains(replaceMentText+j.trim()+replaceMentTextEnd)){
					highlightedText=highlightedText.replaceAll(j.trim(),replaceMentText+j.trim()+replaceMentTextEnd);
				}
			}			
		}else if((!pattern.contains("+"))&&(pattern.contains("-"))){
			String[] temp=pattern.trim().split("\\-");
			String[] temp1=temp[0].trim().split(" ");
			for(String j:temp1){
				if(!highlightedText.contains(replaceMentText+j.trim()+replaceMentTextEnd)){
					highlightedText=highlightedText.replaceAll(j.trim(),replaceMentText+j.trim()+replaceMentTextEnd);
				}
			}		
		}else if(pattern.contains("+")&& pattern.contains("-")){
			String[] temp=pattern.trim().split("\\-");
			String[] temp1=temp[0].trim().split("\\+");
			for(String j:temp1){
				String[] temp2=j.trim().split(" ");
				for(String k:temp2){
					if(!highlightedText.contains(replaceMentText+k.trim()+replaceMentTextEnd)){
						highlightedText=highlightedText.replaceAll(k.trim(),replaceMentText+k.trim()+replaceMentTextEnd);
					}
				}
			}		
		}
		return highlightedText;
	}
	
}
