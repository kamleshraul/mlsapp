package org.mkcl.els.repository;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ReferenceLinkVO;
import org.mkcl.els.domain.ReferenceLinks;
import org.springframework.stereotype.Repository;

@Repository
public class ReferenceLinkRepository extends BaseRepository<ReferenceLinks, Serializable>{

	public List<ReferenceLinkVO> findReferences(final Integer devicetype, final Integer housetype, final Integer documenttype){
	
		/*List<ReferenceLinkVO> referenceLinkVOs=new ArrayList<ReferenceLinkVO>();*/
		String query = "";
		if(devicetype == 5){
			query = "SELECT CONCAT('अतारांकित प्रश्नोत्तरांची ',rt.display_name,' (', st.session_type,') अधिवेशन ', formater(ss.session_year,'mr_IN')) AS NAME, rk.date, rk.link "
					+" FROM reference_links rk" 
					+" JOIN sessions ss ON (rk.session_id = ss.id)"
					+" JOIN sessiontypes st ON (ss.sessiontype_id = st.id)"
					+" JOIN referencetypes rt ON (rk.documenttype_id = rt.id)"
					+" WHERE rk.devicetype_id = "+devicetype+" AND rk.housetype_id = "+housetype+" AND rk.documenttype_id = "+documenttype+" ORDER BY rk.id ASC";
		}
		else {
			query = "SELECT rk.name,rk.date,rk.link,rk.locale,rt.display_name,rk.date,ss.session_year FROM reference_links rk "
					+"JOIN sessions ss ON (rk.session_id = ss.id)"
					+" JOIN sessiontypes st ON (ss.sessiontype_id = st.id)"
					+" JOIN referencetypes rt ON (rk.documenttype_id = rt.id)"
					+"where rk.devicetype_id ='"+devicetype+"' and rk.housetype_id = '"+housetype+"' and rk.documenttype_id = '"+documenttype+"' ORDER BY rk.id ASC";	
		}
		
		List records= this.em().createNativeQuery(query).getResultList();
	
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
}