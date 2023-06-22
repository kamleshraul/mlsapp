package org.mkcl.els.repository;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ReferenceLinkVO;
import org.mkcl.els.common.vo.TemplateVO;
import org.mkcl.els.common.vo.TestVO;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.ReferenceLinks;
import org.springframework.stereotype.Repository;

@Repository
public class ReferenceLinkRepository extends BaseRepository<ReferenceLinks, Serializable>{

	public List<ReferenceLinkVO> findReferences(final Integer housetype  , final Integer devicetype, final Integer documenttype){
	
		/*List<ReferenceLinkVO> referenceLinkVOs=new ArrayList<ReferenceLinkVO>();*/
		String query = "";
		if(devicetype.equals(5)){
			
			Query q =  Query.findByFieldName(Query.class, "keyField", "REFERENCELINK_WS_QUERY_DEVICETYPE_5", "mr_IN");
			
			if(q != null)
			{
			 query= q.getKeyField();
			}
		}
		else {
			Query q =  Query.findByFieldName(Query.class, "keyField", "REFERENCELINK_WS_QUERY_DEVICETYPE_OTHER", "mr_IN");
			
			if(q != null)
			{
			 query= q.getQuery();
			}
		}
		
		if(!query.isEmpty() || query != "")
		{
			javax.persistence.Query q  =  this.em().createNativeQuery(query); 
			q.setParameter("deviceType", devicetype);
			q.setParameter("houseType", housetype);
			q.setParameter("documentType", documenttype);
			List records= q.getResultList();
		
			List<ReferenceLinkVO> referenceLinkVOs =new ArrayList<ReferenceLinkVO>();
			
			Date dbFormat = null;
			for(Object i:records){
				Object[] o=(Object[]) i;
				ReferenceLinkVO referenceInfo =new ReferenceLinkVO();
				referenceInfo.setName(o[0]!=null?o[0].toString().trim():"-");
				if(o[1].toString() != null){
					try {
						dbFormat = FormaterUtil.getDateFormatter("yyyy-MM-dd", "mr_IN").parse(o[1].toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String serverFormat=FormaterUtil.getDateFormatter("dd MMM yyyy","mr_IN").format(dbFormat);
					referenceInfo.setDate(serverFormat);
				}
				else{
					referenceInfo.setDate("-");
				}
				referenceInfo.setLink(o[2]!=null?o[2].toString().trim():"-");
				referenceInfo.setLocale(o[3]!=null?o[3].toString().trim():"-");
				referenceInfo.setLocalizedname(o[4]!=null?o[4].toString().trim():"-");
				referenceInfo.setEnglishFormatDate(o[5]!=null?o[5].toString().trim():"-");
				referenceLinkVOs.add(referenceInfo);
			}
					
			return referenceLinkVOs;
		}
		return null;
	}
	
	
	public List<TestVO> getSessionAndReferenceNumber(final Integer houseType,final String locale ) {
		Query q = Query.findByFieldName(Query.class, "keyField", "GET_SESSION_YAADI_NUMBER_REFERENCE_LINK", "mr_IN");
		if (q != null) {
			List<TestVO> yaadiSessionDetails = new ArrayList<TestVO>();
			String query = q.getQuery();
			javax.persistence.Query nativeq = this.em().createNativeQuery(query);
			nativeq.setParameter("houseType", houseType);
			nativeq.setParameter("locale", locale);
			List<Object> details = nativeq.getResultList();
			if (details != null) {
				for (Object i : details) {
					Object[] o = (Object[]) i;
					TestVO t = new TestVO();
					t.setId(Long.parseLong(o[1].toString()));
					t.setName(o[0].toString());
					if (o[2] != null && !o[2].toString().isEmpty()) {
						t.setValue(o[2].toString());
					}
					if (o[3] != null && !o[3].toString().isEmpty()) {
						t.setFormattedOrder(o[3].toString());
					}
					yaadiSessionDetails.add(t);
				}
				return yaadiSessionDetails;
			}
		}
		return null;
	}
	
	public List<TemplateVO> getYaadiLinkAndNumber(final Integer sessionId,final Integer documentTypeId){
		Query q = Query.findByFieldName(Query.class, "keyField", "GET_REFERENCE_LINK_AND_YAADI_NUMBER", "mr_IN");
		
		if(q != null) {
			List<TemplateVO> ydlinks = new ArrayList<TemplateVO>();
			String query = q.getQuery();
			javax.persistence.Query nativeq = this.em().createNativeQuery(query);
			nativeq.setParameter("sessionId", sessionId);
			nativeq.setParameter("documentTypeId", documentTypeId);
			List<Object> details = nativeq.getResultList();
			if(details != null) {
				for (Object i : details) {
					Object[] o = (Object[]) i;
					TemplateVO t = new TemplateVO();
					t.setTitle(o[0].toString());
					t.setContent(o[1].toString());
					ydlinks.add(t);
				}
				return ydlinks;
			}
		}
		
		return null;
	}
	
}