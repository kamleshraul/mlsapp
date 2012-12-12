package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.WorkflowConfig;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowConfigRepository extends BaseRepository<WorkflowConfig, Serializable>{

	@SuppressWarnings({ "rawtypes" })
	public List<Reference> findActors(final Long sessionId,
			final Long deviceId,
			final String workflowType,
			final Integer groupNumber,
			final Long workflowConfigId,
			final Integer level,
			final String sortorder){
	    String query1="SELECT ugt.type,ugt.name FROM workflowactors as wa JOIN wf_config as wc JOIN wfconfig_wfactors as wcwa JOIN usergroups_types as ugt JOIN workflows as wf "+
		" WHERE wa.user_group_type=ugt.id and wcwa.wfactors_id=wa.id and wc.id=wcwa.wfconfig_id and wc.workflow_id=wf.id and wc.session_id="+sessionId+" and wc.devicetype_id="+deviceId+" and wf.type LIKE '%"+workflowType+"%' ";
		String query2=null;
		if(sortorder.equals(ApplicationConstants.ASC)){
			query2=" and (wa.level>"+level+" or (wa.level="+level+" and wa.group_name LIKE '%"+groupNumber+"%') or (wa.level="+level+" and wa.group_name ='') or (wa.level="+level+" and wa.group_name is null)) and wc.id="+workflowConfigId+" order by wa.level asc ";
		}else{
            query2=" and wa.level<"+(level-1)+" and (wa.group_name LIKE '%"+groupNumber+"%' or wa.group_name ='' or wa.group_name is null ) and wc.id="+workflowConfigId+" order by wa.level desc ";
		}
		List results=this.em().createNativeQuery(query1+query2).getResultList();
		List<Reference> references=new ArrayList<Reference>();
		for(Object i:results){
			Object[] o=(Object[]) i;
			Reference reference=new Reference();
			if(o[0]!=null){
				reference.setId(o[0].toString());
			}
			if(o[1]!=null){
				reference.setName(o[1].toString());
			}
			references.add(reference);
		}
		return references;
	}

	public WorkflowConfig findLatest(final Long sessionId,
			final Long deviceTypeId, final String workflowType) {
		String query="SELECT w FROM WorkflowConfig w JOIN w.session s JOIN w.workflow wf JOIN w.deviceType d WHERE s.id="+sessionId+" AND "+
					 "d.id="+deviceTypeId+" AND wf.type LIKE '%"+workflowType+"%' ORDER BY w.createdOn desc LIMIT 0,1";
		try{
			return (WorkflowConfig) this.em().createQuery(query).getSingleResult();
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}
	}

	public void removeActor(final Long workflowconfigId, final Long workflowactorId) {
		String query1="DELETE from wfconfig_wfactors where wfconfig_id="+workflowconfigId+" and wfactors_id="+workflowactorId;
		this.em().createNativeQuery(query1).executeUpdate();
		String query2="DELETE from workflowactors WHERE id="+workflowactorId;
		this.em().createNativeQuery(query2).executeUpdate();
	}

	public Integer getLevel(final Long workflowconfigId,final String actor) {
			String query="SELECT wfa.level FROM workflowactors as wfa JOIN wfconfig_wfactors as wcwf "+
			"JOIN usergroups_types as ugt WHERE wcwf.wfactors_id=wfa.id and wfa.user_group_type=ugt.id and "+
			" ugt.type='"+actor+"' ORDER  BY wfa.level desc limit 0,1";
			Object result=this.em().createNativeQuery(query).getSingleResult();
			if(result!=null){
				return Integer.parseInt(result.toString());
			}
			return 0;
	}
}
