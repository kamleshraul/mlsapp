package org.mkcl.els.domain.ballot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.Status;

public class ProprietyPointBallot {
	
	//=================================================
	//
	//=============== VIEW METHODS ====================
	//
	//=================================================
	public static List<BallotVO> findPreBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
		
		PreBallot preBallotPROISAssembly = PreBallot.find(session, deviceType, answeringDate, locale);
		List<BallotEntry> preBallotEntries = new ArrayList<BallotEntry>();
		
		if(preBallotPROISAssembly == null){
			
			PreBallot newPreBallot = new PreBallot(session, deviceType, answeringDate, new Date(), locale);				
			//TODO change to propriety points
			List<ProprietyPoint> proprietyPoints = 
					ProprietyPointBallot.computeProprietyPointsForBallot(session, deviceType, answeringDate, false, true, locale);
			
			List<ProprietyPoint> newProprietyPointList = new ArrayList<ProprietyPoint>();
			for(ProprietyPoint m : proprietyPoints){
				if(m.getPrimaryMember().isActiveMemberOn(new Date(), locale)){
					newProprietyPointList.add(m);
				}
			}
			for(ProprietyPoint m : newProprietyPointList) {
				
				{
					BallotEntry ballotEntry = new BallotEntry();
					ballotEntry.setMember(m.getPrimaryMember());
					ballotEntry.setLocale(m.getLocale());
					
					List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
					deviceSequence.add(new DeviceSequence(m, m.getLocale()));
					ballotEntry.setDeviceSequences(deviceSequence);
					preBallotEntries.add(ballotEntry);
					
					deviceSequence = null;
					ballotEntry = null;
				}
				
				BallotVO preBallotVO = new BallotVO();
				preBallotVO.setMemberName(m.getPrimaryMember().getFullname());
				preBallotVO.setQuestionNumber(m.getNumber());
				if(m.getRevisedSubject() != null && !m.getRevisedSubject().isEmpty()){
					preBallotVO.setQuestionSubject(m.getRevisedSubject());
				}else{
					preBallotVO.setQuestionSubject(m.getSubject());
				}
				
				preBallotVOs.add(preBallotVO);
			}
			
			Collections.sort(preBallotEntries, new Comparator<BallotEntry>() {
				@Override
				public int compare(BallotEntry b1, BallotEntry b2){
					if(!b1.getDeviceSequences().isEmpty() && !b2.getDeviceSequences().isEmpty()){
						return ((ProprietyPoint)b1.getDeviceSequences().get(0).getDevice()).getNumber().compareTo(((ProprietyPoint)b2.getDeviceSequences().get(0).getDevice()).getNumber());
					}
					return 0;
				}
			});
			
			Collections.sort(preBallotVOs, new Comparator<BallotVO>(){
				@Override
				public int compare(BallotVO b1, BallotVO b2){
					return b1.getQuestionNumber().compareTo(b2.getQuestionNumber());
				}
			});			
			
			//persist the preballot list
			if(preBallotEntries != null && !preBallotEntries.isEmpty()){
				newPreBallot.setBallotEntries(preBallotEntries);
				newPreBallot.persist();
			}
		}else{
			CustomParameter cspt = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase() + "_" + session.getHouse().getType().getType().toUpperCase() + "_PREBALLOT_RECREATE_IF_EXISTS", "");
			if(cspt == null || cspt.getValue().equals("YES")){
				Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
				if(ballot == null){
					/*StringBuffer deviceSequenceToBeDeleted = new StringBuffer();
					StringBuffer ballotEntriesToBeDeleted = new StringBuffer();
					
					for(BallotEntry be : preBallotPROISAssembly.getBallotEntries()){					
						for(DeviceSequence ds : be.getDeviceSequences()){
							deviceSequenceToBeDeleted.append(ds.getId()+",");
						}
						
						ballotEntriesToBeDeleted.append(be.getId()+",");
					}
					
					deviceSequenceToBeDeleted.replace(deviceSequenceToBeDeleted.toString().length(), deviceSequenceToBeDeleted.toString().length(), "");
					ballotEntriesToBeDeleted.replace(ballotEntriesToBeDeleted.toString().length(), ballotEntriesToBeDeleted.toString().length(), "");
					{
						Map<String, String[]> param = new HashMap<String, String[]>();
						param.put("locale", new String[]{locale});
						param.put("deviceS", new String[]{deviceSequenceToBeDeleted.toString()});
						param.put("ballotEntries", new String[]{ballotEntriesToBeDeleted.toString()});
						Query.findReportWithIn("DELETE_DEVICE_SEQUENCE", param);
						Query.findReportWithIn("DELETE_BALLOT_ENTRIES", param);
					}
					
					preBallotPROISAssembly.setBallotEntries(null);*/
					preBallotPROISAssembly.optimizedRemoveHDS();
					
					PreBallot newPreBallot = new PreBallot(session, deviceType, answeringDate, new Date(), locale);
					List<ProprietyPoint> proprietyPoints = 
							ProprietyPointBallot.computeProprietyPointsForBallot(session, deviceType, answeringDate, false, true, locale);
					
					List<ProprietyPoint> newProprietyPointList = new ArrayList<ProprietyPoint>();
					for(ProprietyPoint m : proprietyPoints){
						if(m.getPrimaryMember().isActiveMemberOn(new Date(), locale)){
							newProprietyPointList.add(m);
						}
					}
					for(ProprietyPoint m : newProprietyPointList) {
						
						{
							BallotEntry ballotEntry = new BallotEntry();
							ballotEntry.setMember(m.getPrimaryMember());
							ballotEntry.setLocale(m.getLocale());
							
							List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
							deviceSequence.add(new DeviceSequence(m, m.getLocale()));
							ballotEntry.setDeviceSequences(deviceSequence);
							preBallotEntries.add(ballotEntry);
							
							deviceSequence = null;
							ballotEntry = null;
						}
						
						BallotVO preBallotVO = new BallotVO();
						preBallotVO.setMemberName(m.getPrimaryMember().getFullname());
						preBallotVO.setQuestionNumber(m.getNumber());
						if(m.getRevisedSubject() != null && !m.getRevisedSubject().isEmpty()){
							preBallotVO.setQuestionSubject(m.getRevisedSubject());
						}else{
							preBallotVO.setQuestionSubject(m.getSubject());
						}
						
						preBallotVOs.add(preBallotVO);
					}
					
					Collections.sort(preBallotEntries, new Comparator<BallotEntry>() {
						@Override
						public int compare(BallotEntry b1, BallotEntry b2){
							if(!b1.getDeviceSequences().isEmpty() && !b2.getDeviceSequences().isEmpty()){
								return ((ProprietyPoint)b1.getDeviceSequences().get(0).getDevice()).getNumber().compareTo(((ProprietyPoint)b2.getDeviceSequences().get(0).getDevice()).getNumber());
							}
							return 0;
						}
					});
					
					Collections.sort(preBallotVOs, new Comparator<BallotVO>(){
						@Override
						public int compare(BallotVO b1, BallotVO b2){
							return b1.getQuestionNumber().compareTo(b2.getQuestionNumber());
						}
					});			
					
					//persist the preballot list
					newPreBallot.setBallotEntries(preBallotEntries);
					newPreBallot.persist();
					
				}else{
					if(!preBallotPROISAssembly.getBallotEntries().isEmpty()){
						/*for(BallotEntry be : preBallotPROISAssembly.getBallotEntries()){
							for(DeviceSequence ds : be.getDeviceSequences()){
								if(ds.getDevice() != null){
									if(ds.getDevice() instanceof ProprietyPoint) {
										ProprietyPoint q = (ProprietyPoint) ds.getDevice();
										BallotVO preBallotVO = new BallotVO();
										preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
										preBallotVO.setQuestionNumber(q.getNumber());
										if(q.getRevisedSubject() != null && !q.getRevisedSubject().isEmpty()){
											preBallotVO.setQuestionSubject(q.getRevisedSubject());
										}else{
											preBallotVO.setQuestionSubject(q.getSubject());
										}
										
										preBallotVOs.add(preBallotVO);
									}
								}
							}
						}*/
						
						Map<String, String[]> params = new HashMap<String, String[]>();
						params.put("locale", new String[]{locale});
						params.put("preBallotId", new String[]{preBallotPROISAssembly.getId().toString()});
						List devices = Query.findReport("PROPRIETYPOINT_PREBALLOT_DEVICES", params);
						
						if(devices != null && !devices.isEmpty()){
							for(Object o : devices){
								Object[] obj = (Object[])o;
								
								BallotVO preBallotVO = new BallotVO();
								preBallotVO.setMemberName(obj[2].toString());
								preBallotVO.setQuestionNumber(new Integer(obj[1].toString()));
								preBallotVO.setQuestionSubject(obj[3].toString());
								preBallotVOs.add(preBallotVO);
							}
						}
					}
				}
			}else{
				if(!preBallotPROISAssembly.getBallotEntries().isEmpty()){
					/*for(BallotEntry be : preBallotPROISAssembly.getBallotEntries()){
						for(DeviceSequence ds : be.getDeviceSequences()){
							if(ds.getDevice() != null){
								if(ds.getDevice() instanceof ProprietyPoint) {
									ProprietyPoint q = (ProprietyPoint) ds.getDevice();
									BallotVO preBallotVO = new BallotVO();
									preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
									preBallotVO.setQuestionNumber(q.getNumber());
									if(q.getRevisedSubject() != null && !q.getRevisedSubject().isEmpty()){
										preBallotVO.setQuestionSubject(q.getRevisedSubject());
									}else{
										preBallotVO.setQuestionSubject(q.getSubject());
									}
									
									preBallotVOs.add(preBallotVO);
								}
							}
						}
					}*/
					
					Map<String, String[]> params = new HashMap<String, String[]>();
					params.put("locale", new String[]{locale});
					params.put("preBallotId", new String[]{preBallotPROISAssembly.getId().toString()});
					List devices = Query.findReport("PROPRIETYPOINT_PREBALLOT_DEVICES", params);
					
					if(devices != null && !devices.isEmpty()){
						for(Object o : devices){
							Object[] obj = (Object[])o;
							
							BallotVO preBallotVO = new BallotVO();
							preBallotVO.setMemberName(obj[2].toString());
							preBallotVO.setQuestionNumber(new Integer(obj[1].toString()));
							preBallotVO.setQuestionSubject(obj[3].toString());
							preBallotVOs.add(preBallotVO);
						}
					}
				}
			}
		}
		
		return preBallotVOs;
	}
	
	//===============================================
	//
	//=============== INTERNAL METHODS ==============
	//
	//===============================================
	private static List<ProprietyPoint> computeProprietyPointsForBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Boolean isMandatoryUnique,
			final Boolean isPreBallot,
			final String locale) throws ELSException {
		//CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
	
		Date startTime = ProprietyPoint.findSubmissionStartTime(session, answeringDate); //for council 2nd argument should be 1 working day before answeringDate
		Date endTime = ProprietyPoint.findSubmissionEndTime(session, answeringDate); //for council 2nd argument should be 1 working day before answeringDate or session end date if current date is session date
		
		Status ADMITTED = Status.findByType(ApplicationConstants.PROPRIETYPOINT_FINAL_ADMISSION, locale);
		Status RECOMMENDADMITTED = Status.findByType(ApplicationConstants.PROPRIETYPOINT_RECOMMEND_ADMISSION, locale);
		Status[] internalStatuses = new Status[] { ADMITTED,RECOMMENDADMITTED };
		
		// TODO: internal Status will only refer to the lifecycle of a Question in the Workflow
		// i.e till ADMITTED. The further statuses of the Question viz Sent to department,
		// Balloted, Discussed will be captured in recommendationStatus. So, in compute Questions
		// the condition should be: For all the active members who have submitted "half hour 
		// discussion from question" between the specified time window (start time & end time) &
		// whose questions have been admitted (internal status = "ADMITTED") and not balloted
		// (recommendation status = "BALLOTED" or "DISCUSSED" or any further status) are to be
		// picked up for this Ballot.
		
		List<ProprietyPoint> proprietyPoints = ProprietyPoint.findByBallot(session, deviceType, answeringDate, internalStatuses, false, false, isMandatoryUnique, isPreBallot, startTime, endTime, ApplicationConstants.ASC, locale);
			
		return proprietyPoints;
	}

}