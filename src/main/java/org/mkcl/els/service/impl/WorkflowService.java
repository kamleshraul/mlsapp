package org.mkcl.els.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.BillAmendmentMotion;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.EventMotion;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.service.IWorkflowService;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService implements IWorkflowService{

	@Override
	public List<String> findSupportingMembers(final String strDeviceId,
			final String strDeviceType) {
		/*
    	 * Here we will create tasks for those supporting members for which task
    	 * has not already been created i.e for those supporting members whose
    	 * decision status is assigned.Once task is created its status will change to pending.
    	 */
        List<String> supportingMembersNames=new ArrayList<String>();
        List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
        if(strDeviceType.startsWith("questions")){
	        Question question=Question.findById(Question.class,Long.parseLong(strDeviceId));
	        supportingMembers=question.getSupportingMembers();
        }else if(strDeviceType.startsWith("motions")){
        	
        	if(strDeviceType.startsWith("motions_cutmotion")){
        		CutMotion motion = CutMotion.findById(CutMotion.class,Long.parseLong(strDeviceId));
 		        supportingMembers = motion.getSupportingMembers();
        	}else if(strDeviceType.startsWith("motions_eventmotion")){
        		EventMotion motion = EventMotion.findById(EventMotion.class,Long.parseLong(strDeviceId));
 		        supportingMembers = motion.getSupportingMembers();
        	}else if(strDeviceType.startsWith("motions_discussionmotion")){
        		
        	}else{        	
		        Motion motion=Motion.findById(Motion.class,Long.parseLong(strDeviceId));
		        supportingMembers=motion.getSupportingMembers();
        	}
        }else if(strDeviceType.startsWith("bills")){
	        Bill bill=Bill.findById(Bill.class,Long.parseLong(strDeviceId));
	        supportingMembers=bill.getSupportingMembers();
        }else if(strDeviceType.startsWith("billamendmotions")){
            BillAmendmentMotion billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class,Long.parseLong(strDeviceId));
            supportingMembers=billAmendmentMotion.getSupportingMembers();
        }
        //we will send approval to members whose status is still request not send.this is ok incase a new member has been added
        for(SupportingMember i:supportingMembers){
        	if(i.getDecisionStatus().getType().trim().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
            try {
				supportingMembersNames.add(User.find(i.getMember()).getCredential().getUsername());
			} catch (ELSException e) {
				e.printStackTrace();
			}
        	}
        }
        return supportingMembersNames;
	}

}
