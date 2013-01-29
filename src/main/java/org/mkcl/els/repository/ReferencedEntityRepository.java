package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Repository;

@Repository
public class ReferencedEntityRepository extends BaseRepository<ReferencedEntity, Serializable>{
	
	public Boolean referencing(Long primaryId, Long referencingId, String locale) {
		try {
			Question primaryQuestion=Question.findById(Question.class,primaryId);
			Question referencedQuestion=Question.findById(Question.class,referencingId);
			List<ReferencedEntity> referencedEntities=new ArrayList<ReferencedEntity>();
			referencedEntities=primaryQuestion.getReferencedEntities();		
			boolean alreadyRefered=false;
			int position=0;
			for(ReferencedEntity i:referencedEntities){
				if(i.getQuestion().getId()==referencedQuestion.getId()){
					alreadyRefered=true;
				}
				position++;
			}
			if(!alreadyRefered){
				ReferencedEntity referencedEntity=new ReferencedEntity();
				referencedEntity.setLocale(referencedQuestion.getLocale());
				referencedEntity.setQuestion(referencedQuestion);
				referencedEntity.setPosition(position+1);
				referencedEntity.setDeviceType(referencedQuestion.getType());
				referencedEntity.persist();
				referencedEntities.add(referencedEntity);
				if(!referencedEntities.isEmpty()){
					primaryQuestion.setReferencedEntities(referencedEntities);
				}else{
					primaryQuestion.setReferencedEntities(null);
				}
				Status status=Status.findByFieldName(Status.class,"type","question_contains_references", locale);
				primaryQuestion.setInternalStatus(status);
				primaryQuestion.setRecommendationStatus(status);
				primaryQuestion.simpleMerge();
			}else{
				return false;
			}			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public Boolean deReferencing(Long primaryId, Long referencingId, String locale) {
		try {
			Question primaryQuestion=Question.findById(Question.class,primaryId);
			Question referencedQuestion=Question.findById(Question.class,referencingId);
			List<ReferencedEntity> referencedEntities=new ArrayList<ReferencedEntity>();
			List<ReferencedEntity> newReferencedEntities=new ArrayList<ReferencedEntity>();
			referencedEntities=primaryQuestion.getReferencedEntities();
			ReferencedEntity referencedEntityToRemove=null;
			for(ReferencedEntity i:referencedEntities){
				if(i.getQuestion().getId()==referencedQuestion.getId()){
					referencedEntityToRemove=i;				
				}else{
					newReferencedEntities.add(i);
				}
			}
			if(!newReferencedEntities.isEmpty()){
				primaryQuestion.setReferencedEntities(newReferencedEntities);
			}else{
				primaryQuestion.setReferencedEntities(null);
				Status status=Status.findByFieldName(Status.class,"type","question_before_workflow_tobeputup", locale);
				primaryQuestion.setInternalStatus(status);
				primaryQuestion.setRecommendationStatus(status);
			}		
			primaryQuestion.simpleMerge();
			referencedEntityToRemove.remove();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public List<QuestionSearchVO> fullTextSearchReferencing(String param,
			Question question, int start, int noOfRecords, String locale) {
		return null;
	}


}
