package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowConfigRepository extends BaseRepository<WorkflowConfig, Serializable>{
		
	public WorkflowConfig findLatest(final Long deviceTypeId,
			final String workflowType,final String locale) {
		/**** Latest Workflow Configurations ****/
		String query="SELECT wc FROM WorkflowConfig wc JOIN wc.workflow wf JOIN wc.deviceType d " +
				" WHERE d.id="+deviceTypeId+" AND wf.type='"+workflowType+"' " +
				" AND wc.isLocked=true AND wc.isLatest=true";				
		try{
			return (WorkflowConfig) this.em().createQuery(query).getSingleResult();
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowActor> findActors(final Long workflowConfgigId,
			final Integer level,
			final String sortOrder,
			final String locale) {
		String query="SELECT wa FROM WorkflowConfig wc JOIN wc.workflowactors wa "+
					 " WHERE wc.id="+workflowConfgigId+" AND wa.level>"+level+
					 " ORDER BY wa.level "+sortOrder;
		List<WorkflowActor> workflowActors=this.em().createQuery(query).getResultList();
		/**** end of workflow ****/
		if(workflowActors==null){
		return new ArrayList<WorkflowActor>();	
		}
		/**** Workflow continues with the top most entry of the workflowactors ****/
		return workflowActors;
	}
	
	public void removeActor(final Long workflowconfigId, final Long workflowactorId) {
		String query1="DELETE from wfconfig_wfactors where wfconfig_id="+workflowconfigId+" and wfactors_id="+workflowactorId;
		this.em().createNativeQuery(query1).executeUpdate();
		String query2="DELETE from workflowactors WHERE id="+workflowactorId;
		this.em().createNativeQuery(query2).executeUpdate();
	}
}
