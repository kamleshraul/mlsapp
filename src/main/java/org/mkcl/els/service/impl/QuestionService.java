package org.mkcl.els.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.service.IQuestionService;
import org.springframework.stereotype.Service;

@Service
public class QuestionService implements IQuestionService{

    @Override
    public List<String> findSupportingMembers(final String strQuestionId) {
    	/*
    	 * Here we will create tasks for those supporting members for which task 
    	 * has not already been created i.e for those supporting members whose
    	 * decision status is assigned.Once task is created its status will change to pending. 
    	 */
        List<String> supportingMembersNames=new ArrayList<String>();
        Question question=Question.findById(Question.class,Long.parseLong(strQuestionId));
        List<SupportingMember> subSupportingMembers=question.getSupportingMembers();
        //we will send approval to members whose status is still request not send.this is ok incase a new member has been added
        for(SupportingMember i:subSupportingMembers){
        	if(i.getDecisionStatus().getType().trim().equals("supportingmember_assigned")){
            supportingMembersNames.add(User.find(i.getMember()).getCredential().getUsername());
        	}
        }
        return supportingMembersNames;
    }

    @Override
    public String findEmailByUsername(final String username) {
        Credential credential=Credential.findByFieldName(Credential.class, "username", username, "");
        return credential.getEmail();
    }

    @Override
    public String findByLocaleAndCode(final String locale, final String code) {
        MessageResource messageResource=new MessageResource();
        String message=null;
        try {
            message=new String(messageResource.findByLocaleAndCode(locale, code).getBytes("UTF-8"),"UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return message;
    }



}
