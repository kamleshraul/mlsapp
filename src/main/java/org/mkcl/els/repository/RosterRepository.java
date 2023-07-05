package org.mkcl.els.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.Query;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.Adjournment;
import org.mkcl.els.domain.CommitteeMeeting;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Part;
import org.mkcl.els.domain.Proceeding;
import org.mkcl.els.domain.Reporter;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RosterRepository extends BaseRepository<Roster, Serializable>{


	@SuppressWarnings("unchecked")
	public Roster findLastCreated(final Session session,final Language language,final String locale) {
		String strQuery="SELECT m FROM Roster m WHERE m.session.id=:session " +
		"AND m.locale=:locale AND m.language.id=:languageId ORDER BY m.id "+ApplicationConstants.DESC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("session",session.getId());
		query.setParameter("locale",locale);
		query.setParameter("languageId", language.getId());
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<Roster> rosters=query.getResultList();
		if(rosters!=null&&!rosters.isEmpty()){
			return rosters.get(0);
		}else{
			return null;
		}		
	}

	public Boolean generateSlot(final Roster roster,final String reporterAction) {
		/**** if false is returned then slot generation has failed.If true then successful action
		 * has taken place ****/
		/**** New Roster being created along with slot generation ****/
		Boolean slotsAlreadyCreated= slotsAlreadyCreated(roster);
		if(slotsAlreadyCreated==false){
			if(roster.getAction().equals("create_slots")){
				return generateNewSlots(roster,roster.getStartTime(),roster.getEndTime(),"BEGINING");
			}
		}
		/**** Created roster being edited along slot generation ****/
		else{			
			Roster savedRoster=Roster.findById(Roster.class,roster.getId());	
			List<Reporter> savedRosterReporters=Roster.findReportersByActiveStatus(savedRoster, true);
			/**** Start Time/End Time/Slot Duration Changed ****/
			if(!(roster.getStartTime().equals(savedRoster.getStartTime()))
					&&!(roster.getEndTime().equals(savedRoster.getEndTime()))
					&&!(roster.getSlotDuration().equals(savedRoster.getSlotDuration()))){
				if(roster.getAction().equals("recreate_slots")){
					Boolean deleteStatus=deleteExistingSlots(roster.getId(),savedRoster.getStartTime(),savedRoster.getEndTime());
					if(deleteStatus){
						generateNewSlots(roster,roster.getStartTime(),roster.getEndTime(),"BEGINING");
					}else{
						return false;
					}
				}
			}
			/**** Start Time/Slot Duration Changed ****/
			else if(!(roster.getStartTime().equals(savedRoster.getStartTime()))
					&&(roster.getEndTime().equals(savedRoster.getEndTime()))
					&&!(roster.getSlotDuration().equals(savedRoster.getSlotDuration()))){
				if(roster.getAction().equals("recreate_slots")){
					Boolean deleteStatus=deleteExistingSlots(roster.getId(),savedRoster.getStartTime(),savedRoster.getEndTime());
					if(deleteStatus){
						generateNewSlots(roster,roster.getStartTime(),roster.getEndTime(),"BEGINING");
					}else{
						return false;
					}
				}
			}
			/**** End Time Preponded/Slot Duration Changed ****/
			else if((roster.getStartTime().equals(savedRoster.getStartTime()))
					&&(roster.getEndTime().before(savedRoster.getEndTime()))
					&&!(roster.getSlotDuration().equals(savedRoster.getSlotDuration()))){
				if(roster.getAction().equals("recreate_slots")){
					Boolean deleteStatus=deleteExistingSlots(roster.getId(),roster.getSlotDurationChangedFrom(),savedRoster.getEndTime());
					if(deleteStatus){
						generateNewSlots(roster,roster.getSlotDurationChangedFrom(),roster.getEndTime(),"LAST_ASSIGNED_USER");
					}else{
						return false;
					}
				}
			}
			/**** End Time Postponded/Slot Duration Changed ****/
			else if((roster.getStartTime().equals(savedRoster.getStartTime()))
					&&(roster.getEndTime().after(savedRoster.getEndTime()))
					&&!(roster.getSlotDuration().equals(savedRoster.getSlotDuration()))){
				if(roster.getAction().equals("recreate_slots")){
					Boolean deleteStatus=deleteExistingSlots(roster.getId(),roster.getSlotDurationChangedFrom(),savedRoster.getEndTime());
					if(deleteStatus){
						generateNewSlots(roster,roster.getSlotDurationChangedFrom(),roster.getEndTime(),"LAST_ASSIGNED_USER");
					}else{
						return false;
					}
				}
			}
			/**** Start Time/End Time Changed ****/
			else if(!(roster.getStartTime().equals(savedRoster.getStartTime()))
					&&!(roster.getEndTime().equals(savedRoster.getEndTime()))
					&&(roster.getSlotDuration().equals(savedRoster.getSlotDuration()))){
				if(roster.getAction().equals("recreate_slots")){
					Boolean deleteStatus=deleteExistingSlots(roster.getId(),savedRoster.getStartTime(),savedRoster.getEndTime());
					if(deleteStatus){
						generateNewSlots(roster,roster.getStartTime(),roster.getEndTime(),"BEGINING");
					}else{
						return false;
					}
				}
			}
			/**** Start Time is preponded/postponded (only thing possible is recreate slots)****/
			else if((roster.getStartTime().before(savedRoster.getStartTime())
					||roster.getStartTime().after(savedRoster.getStartTime()))
			){
				if(roster.getAction().equals("recreate_slots")){
					Boolean deleteStatus=deleteExistingSlots(roster.getId(),savedRoster.getStartTime(),savedRoster.getEndTime());
					if(deleteStatus){
						generateNewSlots(roster,roster.getStartTime(),roster.getEndTime(),"BEGINING");
					}else{
						return false;
					}
				}
			}					
			/**** End Time is preponded ****/
			else if(roster.getEndTime().before(savedRoster.getEndTime())){
				if(roster.getAction().equals("delete_slots")){
					return deleteExistingSlots(roster.getId(),roster.getEndTime(),savedRoster.getEndTime());				
				}
			}
			/**** End Time is postponded ****/
			else if(roster.getEndTime().after(savedRoster.getEndTime())){
				if(roster.getAction().equals("create_new_slots")){
					//check adjournment time
					List<Adjournment> adjourments=findAdjournmentForRosterAfterTime(roster,savedRoster.getEndTime());
					Slot lastGeneratedSlot=Slot.lastGeneratedSlot(roster);
					List<List<Date>> slotStartEndTimeList=calculateNewStartEndTimeForSlotsAccordingToAdjournments(adjourments,savedRoster,roster,lastGeneratedSlot);
					if(adjourments!=null && adjourments.size()>0) {
						boolean isSlotGenerated=false;
						for(List<Date> lstSlotBlock: slotStartEndTimeList) {
							
							if(lstSlotBlock!=null && lstSlotBlock.size()>1) {
								List<List<Date>> adjustedSlotTimmings=adjustSlotTimingsForSlotDuration(lstSlotBlock,roster.getSlotDuration(),roster.getEndTime());
								for( List<Date> adjustedSlotTimming:adjustedSlotTimmings)
									isSlotGenerated= generateNewSlots(roster,adjustedSlotTimming.get(0),adjustedSlotTimming.get(1),"LAST_ASSIGNED_USER");
							}
							
						}
						return isSlotGenerated;
					}
					else {
						return generateNewSlots(roster,savedRoster.getEndTime(),roster.getEndTime(),"LAST_ASSIGNED_USER");
					}
				}
			}
			/**** Slot Duration is changed ****/
			else if(!roster.getSlotDuration().equals(savedRoster.getSlotDuration())){
				if(roster.getAction().equals("recreate_slots_from_slot_duration_changed_time")){
					Boolean deleteStatus=deleteExistingSlots(roster.getId(),roster.getSlotDurationChangedFrom(),savedRoster.getEndTime());
					if(deleteStatus){
						return generateNewSlots(roster,roster.getSlotDurationChangedFrom(),roster.getEndTime(),"LAST_ASSIGNED_USER");
					}else{
						return false;
					}
				}				
			}	
			/**** if reporters are being added/removed(This recreation of slots by not changing
			 * any other parameter and just addding/removing reporters is allowed
			 * till start time is a furture time) ****/
			//Here capture reporter change time
			//Remove the future date validation
			//savedRoster.getStartTime() has to be replaced with reporterChangedTime
			//roster.getStartTime() has to be replaced with reporterChangedTime
			//hidden field add/remove reporter
			//In case of remove LAST_ASSIGNED_USER
			//In case of add get the appropriate logic 
//			else if(roster.getAction().equals("recreate_slots")&&roster.getStartTime().after(new Date())){
//				Boolean deleteStatus=deleteExistingSlots(roster.getId(),savedRoster.getStartTime(),savedRoster.getEndTime());
//				if(deleteStatus){
//					return generateNewSlots(roster,roster.getStartTime(),roster.getEndTime(),"BEGINING");
//				}else{
//					return false;
//				}
//			}
			else {
				if(roster.getAction().equals("recreate_slots_from_reporter_changed_time")){
					Boolean deleteStatus=deleteExistingSlots(roster.getId(),roster.getReporterChangedFrom(),savedRoster.getEndTime());
					if(deleteStatus){
//						if(reporterAction.equals("add") ){
//							return generateNewSlots(roster,roster.getReporterChangedFrom(),roster.getEndTime(),"ORIGINAL_ASSIGNED_USER");
//						}else if(reporterAction.equals("remove")){
//							return generateNewSlots(roster,roster.getReporterChangedFrom(),roster.getEndTime(),"LAST_ASSIGNED_USER");
//						}
						return generateNewSlots(roster,roster.getReporterChangedFrom(),roster.getEndTime(),"ORIGINAL_ASSIGNED_USER");
					}else{
						return false;
					}
				}
			}
			
		}
		return false;
	}	

	public Boolean generateSlot(final Adjournment adjournment) {
		/**** In case of adjournment,roster's start time,end time,slot duration and reporters
		 * are going to be fixed.
		 */
		Roster roster=adjournment.getRoster();
		if(roster!=null){
			if(adjournment.getAction().equals("turnoff")){
				return toggleSlotsAdjournment(roster.getId(),adjournment.getStartTime(),adjournment.getEndTime(),true);
			}else if(adjournment.getAction().equals("turnoffandshift")){
				Boolean deleteStatus=deleteExistingSlotsAdjournment(roster.getId(),adjournment.getStartTime(),roster.getEndTime());
				Boolean toggleStatus= toggleSlotsAdjournment(roster.getId(),adjournment.getStartTime(),adjournment.getEndTime(),true);
				flush();
				//Boolean generateStatus=generateNewSlotsAdjournment(roster,adjournment.getEndTime(),roster.getEndTime(), "BEGINING");
				Boolean generateStatus=generateNewSlotsAdjournment(roster,adjournment, "LAST_ASSIGNED_USER");
				if(toggleStatus&&generateStatus&&deleteStatus){
					return true;
				}
			}	
		}			
		return false;
	}

	/**** Private Methods ****/
	public Boolean slotsAlreadyCreated(final Roster roster) {
		String strQuery="SELECT COUNT(s.id) FROM Slot s WHERE s.roster.id=:roster AND s.blnDeleted=false";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("roster",roster.getId());
		Long count=(Long) query.getSingleResult();
		if(count==0){
			return false;
		}else{
			return true;
		}		
	}

	public Boolean generateNewSlots(final Roster roster,final Date startTime,final Date endTime,
			final String reportersToBeTakenFrom) {
		try {
			if(startTime!=null&&endTime!=null&&roster.getSlotDuration()!=null
					&&roster.getReporters()!=null&&!roster.getReporters().isEmpty()){
				List<Reporter> reporters=new ArrayList<Reporter>();
				/**** getting all active reporters(This has to be done from the 
				 * reporters in memory rather than from database as the reporters 
				 * at this point have not been committed to memory) ****/
				int reporterPositionIndex=0;
				for(Reporter i:roster.getReporters()){
					if(i.getIsActive()){
						if(i!=null)
							i.setPosition(++reporterPositionIndex);
						
						reporters.add(i);
					}
				}
				/**** lastReporterPosition specifies the index of the reporter from where to start in reporters ****/
				int startingIndex=0;
				int lastSlotReporterPosition=0;
				char ch='A';
				int repeat=1;
				if(reportersToBeTakenFrom.equals("BEGINING")){
				}else if(reportersToBeTakenFrom.equals("LAST_ASSIGNED_USER")){
					Slot lastGeneratedSlot=Slot.lastGeneratedSlot(roster);
					if(lastGeneratedSlot!=null&&lastGeneratedSlot.getReporter()!=null){
						lastSlotReporterPosition=lastGeneratedSlot.getReporter().getPosition();			
						if(lastGeneratedSlot.getName()!=null&&!lastGeneratedSlot.getName().isEmpty()){
							ch=lastGeneratedSlot.getName().charAt(0);
							repeat=lastGeneratedSlot.getName().length();
							if(ch=='Z'){								
								ch='A';
								repeat++;
							}else{
								ch++;
							}
						}
						/**** if last generated slot end time is > start time then it must be updated ****/
						if(lastGeneratedSlot.getEndTime()!=null&&lastGeneratedSlot.getEndTime().after(startTime)){
							lastGeneratedSlot.setEndTime(startTime);
							lastGeneratedSlot.merge();
						}
					}
				}else if(reportersToBeTakenFrom.equals("ORIGINAL_ASSIGNED_USER")){
					//Slot originalGeneratedSlot=Slot.lastOriginalSlot(roster);
					// Finding Slot which is previous to Reporter change time
					Slot slotPreviousToReporterChangeTime = Slot.slotPreviousToReporterChangeTime(roster);
					Reporter nextReporter = null;
					StringBuffer nextSlotName = new StringBuffer();
					if(slotPreviousToReporterChangeTime!=null){
						repeat = slotPreviousToReporterChangeTime.getName().length();
						int reporterPreviousToReporterChangedSlotPosition = slotPreviousToReporterChangeTime.getReporter().getPosition();
						if(!slotPreviousToReporterChangeTime.getReporter().getIsActive())
							reporterPreviousToReporterChangedSlotPosition=0;
						//Getting the Reporters Position of the Slot next to Reporter Change time
						for(Reporter i:reporters){
							int reporterPosition=i.getPosition();
							if(reporterPosition > reporterPreviousToReporterChangedSlotPosition){
								nextReporter = i;
								break;
							}
						}
						if(nextReporter == null){
							nextReporter = reporters.get(0);
						}
						if(slotPreviousToReporterChangeTime.getName()!=null&&!slotPreviousToReporterChangeTime.getName().isEmpty()){
							//Getting the Slot Name of Slot next to Reporter Change time
							ch = slotPreviousToReporterChangeTime.getName().charAt(0);
							if(ch<'Z'){
								ch++;
							}else{
								ch='A';
								repeat++;
							}
							
							for(int i=1;i<=repeat;i++){
								nextSlotName.append(ch);
							}
							
							lastSlotReporterPosition = slotPreviousToReporterChangeTime.getReporter().getPosition();
							if(!slotPreviousToReporterChangeTime.getReporter().getIsActive())
								lastSlotReporterPosition=0;
							
							ch = nextSlotName.charAt(0);
							repeat = nextSlotName.length();
						}
					}
				}
				
				Date slotStartTime=startTime;
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(startTime);
				calendar.add(Calendar.MINUTE,roster.getSlotDuration());
				Date slotEndTime=calendar.getTime();
				int count=startingIndex;	
				/**** last slot end time will be used to generate the last slot which falls short
				 * of end time by some margin
				 */
				Date lastSlotEndTime=null;
				while(slotEndTime.before(endTime)||slotEndTime.equals(endTime)){
					Slot slot=new Slot();
					slot.setEndTime(slotEndTime);
					slot.setLocale(roster.getLocale());
					slot.setRoster(roster);
					slot.setStartTime(slotStartTime);
					slot.setTurnedoff(false);
					if(lastSlotReporterPosition==0){
						slot.setReporter(reporters.get(count));
					}else{
						/**** Here we are trying to find out the position where to start adding reporters ****/
						/**** We find the first reporter whose position is greater thean the position
						 * of last generated slot reporter.If it is found then addition of reporter will
						 * carry from it else addition of reporter will start from begining.****/
						int innerIndex=0;
						for(Reporter i:reporters){
							int reporterPosition=i.getPosition();
							if(reporterPosition>lastSlotReporterPosition){
								count=innerIndex;
								lastSlotReporterPosition=0;
								slot.setReporter(reporters.get(count));
								break;
							}
							innerIndex++;
						}
						if(slot.getReporter()==null){
							lastSlotReporterPosition=0;
							slot.setReporter(reporters.get(count));
						}
					}
					StringBuffer buffer=new StringBuffer();
					for(int i=1;i<=repeat;i++){
						buffer.append(ch);
					}
					slot.setName(buffer.toString());									
					slot.persist();
					Proceeding proceeding=new Proceeding();
					proceeding.setLocale(slot.getLocale());
					proceeding.setSlot(slot);
					proceeding.persist();
					if(ch<'Z'){
						ch++;
					}else{
						ch='A';
						repeat++;
					}
					lastSlotEndTime=slotEndTime;
					slotStartTime=slotEndTime;
					calendar.add(Calendar.MINUTE, roster.getSlotDuration());
					slotEndTime=calendar.getTime();
					if(count==reporters.size()-1){
						count=0;
					}else{
						count++;
					}
				}
				/**** This is for the last slot which falls sort of end time for some margin****/
				if(lastSlotEndTime.before(endTime)){
					Slot slot=new Slot();
					slot.setEndTime(endTime);
					slot.setLocale(roster.getLocale());
					slot.setRoster(roster);
					slot.setStartTime(lastSlotEndTime);
					slot.setTurnedoff(false);
					slot.setReporter(reporters.get(count));
					StringBuffer buffer=new StringBuffer();
					for(int i=1;i<=repeat;i++){
						buffer.append(ch);
					}
					slot.setName(buffer.toString());									
					slot.persist();		
					Proceeding proceeding=new Proceeding();
					proceeding.setLocale(slot.getLocale());
					proceeding.setSlot(slot);
					proceeding.persist();
				}
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			logger.error("SLOT GENERATION FAILED",e);
			return false;
		}	
	}	
	
//	public Boolean generateNewSlotsAdjournment(final Roster roster,final Date startTime,final Date endTime,
//			final String reportersToBeTakenFrom) {
//		
//		try {
//			if(startTime!=null&&endTime!=null&&roster.getSlotDuration()!=null
//					&&roster.getReporters()!=null&&!roster.getReporters().isEmpty()){
//				List<Reporter> reporters=new ArrayList<Reporter>();
//				/**** getting all active reporters(This has to be done from the 
//				 * reporters in memory rather than from database as the reporters 
//				 * at this point have not been committed to memory) ****/
//				for(Reporter i:roster.getReporters()){
//					if(i.getIsActive()){
//						reporters.add(i);
//					}
//				}
//				/**** lastReporterPosition specifies the index of the reporter from where to start in reporters ****/
//				int startingIndex=0;
//				int lastSlotReporterPosition=0;
//				char ch='A';
//				int repeat=1;
//				if(reportersToBeTakenFrom.equals("BEGINING")){
//				}else if(reportersToBeTakenFrom.equals("LAST_ASSIGNED_USER")){
//					Slot lastGeneratedSlot=Slot.lastAdjournedSlot(roster);
//					if(lastGeneratedSlot!=null&&lastGeneratedSlot.getReporter()!=null){
//						lastSlotReporterPosition=lastGeneratedSlot.getReporter().getPosition();			
//						if(lastGeneratedSlot.getName()!=null&&!lastGeneratedSlot.getName().isEmpty()){
//							ch=lastGeneratedSlot.getName().charAt(0);
//							repeat=lastGeneratedSlot.getName().length();
//							if(ch=='Z'){								
//								ch='A';
//								repeat++;
//							}else{
//								ch++;
//							}
//						}
//						/**** if last generated slot end time is > start time then it must be updated ****/
////						if(lastGeneratedSlot.getEndTime()!=null&&lastGeneratedSlot.getEndTime().after(startTime)){
////							lastGeneratedSlot.setEndTime(startTime);
////							lastGeneratedSlot.merge();
////						}
//					}
//				}
//				Date slotStartTime=startTime;
//				Calendar calendar=Calendar.getInstance();
//				calendar.setTime(startTime);
//				calendar.add(Calendar.MINUTE,roster.getSlotDuration());
//				Date slotEndTime=calendar.getTime();
//				int count=startingIndex;	
//				/**** last slot end time will be used to generate the last slot which falls short
//				 * of end time by some margin
//				 */
//				Date lastSlotEndTime=null;
//				while(slotEndTime.before(endTime)||slotEndTime.equals(endTime)){
//					Slot slot=new Slot();
//					slot.setEndTime(slotEndTime);
//					slot.setLocale(roster.getLocale());
//					slot.setRoster(roster);
//					slot.setStartTime(slotStartTime);
//					slot.setTurnedoff(false);
//					if(lastSlotReporterPosition==0){
//						slot.setReporter(reporters.get(count));
//					}else{
//						/**** Here we are trying to find out the position where to start adding reporters ****/
//						/**** We find the first reporter whose position is greater thean the position
//						 * of last generated slot reporter.If it is found then addition of reporter will
//						 * carry from it else addition of reporter will start from begining.****/
//						int innerIndex=0;
//						for(Reporter i:reporters){
//							int reporterPosition=i.getPosition();
//							if(reporterPosition>lastSlotReporterPosition){
//								count=innerIndex;
//								lastSlotReporterPosition=0;
//								slot.setReporter(reporters.get(count));
//								break;
//							}
//							innerIndex++;
//						}
//						if(slot.getReporter()==null){
//							lastSlotReporterPosition=0;
//							slot.setReporter(reporters.get(count));
//						}
//					}
//					StringBuffer buffer=new StringBuffer();
//					for(int i=1;i<=repeat;i++){
//						buffer.append(ch);
//					}
//					slot.setName(buffer.toString());									
//					slot.persist();
//					Proceeding proceeding=new Proceeding();
//					proceeding.setLocale(slot.getLocale());
//					proceeding.setSlot(slot);
//					proceeding.persist();
//					if(ch<'Z'){
//						ch++;
//					}else{
//						ch='A';
//						repeat++;
//					}
//					lastSlotEndTime=slotEndTime;
//					slotStartTime=slotEndTime;
//					calendar.add(Calendar.MINUTE, roster.getSlotDuration());
//					slotEndTime=calendar.getTime();
//					if(count==reporters.size()-1){
//						count=0;
//					}else{
//						count++;
//					}
//				}
//				/**** This is for the last slot which falls sort of end time for some margin****/
//				if(lastSlotEndTime.before(endTime)){
//					Slot slot=new Slot();
//					slot.setEndTime(endTime);
//					slot.setLocale(roster.getLocale());
//					slot.setRoster(roster);
//					slot.setStartTime(lastSlotEndTime);
//					slot.setTurnedoff(false);
//					slot.setReporter(reporters.get(count));
//					StringBuffer buffer=new StringBuffer();
//					for(int i=1;i<=repeat;i++){
//						buffer.append(ch);
//					}
//					slot.setName(buffer.toString());									
//					slot.persist();		
//					Proceeding proceeding=new Proceeding();
//					proceeding.setLocale(slot.getLocale());
//					proceeding.setSlot(slot);
//					proceeding.persist();
//				}
//				return true;
//			}else{
//				return false;
//			}
//		} catch (Exception e) {
//			logger.error("SLOT GENERATION FAILED",e);
//			return false;
//		}	
//	}
	
	@Transactional
	public Boolean generateNewSlotsAdjournment(final Roster roster,final Adjournment adjournment,
			final String reportersToBeTakenFrom) {
		 Date startTime=adjournment.getEndTime();
		 Date endTime=adjournment.getStartTime();
		 Date newSlotTime = null;
		try {
			if(startTime!=null&&endTime!=null&&roster.getSlotDuration()!=null
					&&roster.getReporters()!=null&&!roster.getReporters().isEmpty()){
				List<Reporter> reporters=new ArrayList<Reporter>();
				/**** getting all active reporters(This has to be done from the 
				 * reporters in memory rather than from database as the reporters 
				 * at this point have not been committed to memory) ****/
				for(Reporter i:roster.getReporters()){
					if(i.getIsActive()){
						reporters.add(i);
					}
				}
				/**** lastReporterPosition specifies the index of the reporter from where to start in reporters ****/
				int startingIndex=0;
				int lastSlotReporterPosition=0;
				char ch='A';
				int repeat=1;
				Slot newSlot = null;
				if(reportersToBeTakenFrom.equals("BEGINING")){
				}else if(reportersToBeTakenFrom.equals("LAST_ASSIGNED_USER")){
					Slot lastAdjournedSlot=Slot.lastAdjournedSlot(roster,adjournment);
					Slot firstAdjournedSlot=Slot.firstAdjournedSlot(roster, adjournment);
					
					Adjournment previousAdjournment=findPreviousAdjournment(roster,adjournment.getStartTime());
					Slot lastActiveSlotAfterPreviousAdjournment=null;
					if(previousAdjournment!=null && previousAdjournment.getEndTime()!=null)
						lastActiveSlotAfterPreviousAdjournment=Slot.lastActiveSlotAfterAdjournemnt(roster,previousAdjournment.getEndTime());
					
					boolean prevSlotDeleted=deleteSlotsInAdjournmetTimeSlots(lastActiveSlotAfterPreviousAdjournment,roster,adjournment,previousAdjournment);
					if(prevSlotDeleted)
						lastActiveSlotAfterPreviousAdjournment=null;
					//Following Code is required as We are not getting the expected value of First Adjourned Slot
					// Slot Previous to Adjourned Slot
					Slot slotPreviousToAdjournedSlot = Slot.slotPreviousToAdjournedSlot(roster, adjournment);
					StringBuffer nextSlotName = new StringBuffer();
					Reporter nextReporter = null;
					if(lastAdjournedSlot!=null&&lastAdjournedSlot.getReporter()!=null 
							&& slotPreviousToAdjournedSlot!=null &&slotPreviousToAdjournedSlot.getReporter()!=null){
						int reporterPreviousToAdjounedSlotPosition = slotPreviousToAdjournedSlot.getReporter().getPosition();
						//Getting the Reporters Position of the First Adjourned Slot
						for(Reporter i:reporters){
							int reporterPosition=i.getPosition();
							if(reporterPosition>reporterPreviousToAdjounedSlotPosition){
								nextReporter = i;
								break;
							}
						}
						if(nextReporter == null){
							nextReporter = reporters.get(0);
						}
						
						//Getting the Slot Name of First Adjourned Slot
						//ch = slotPreviousToAdjournedSlot.getName().charAt(0);
						ch = slotPreviousToAdjournedSlot.getName().charAt(0);
						ch++;
						/*
									
						if(ch<'Z'){
							ch++;
						}else{
							ch='A';
							repeat++;
						}*/
						
						for(int i=1;i<=slotPreviousToAdjournedSlot.getName().length();i++){
							nextSlotName.append(ch);
						}
						lastSlotReporterPosition = nextReporter.getPosition();
						
						if(lastAdjournedSlot.getName()!=null&&!lastAdjournedSlot.getName().isEmpty()){
							ch = nextSlotName.charAt(0);
							repeat = nextSlotName.length();
							newSlot = new Slot();
							newSlot.setLocale(lastAdjournedSlot.getLocale());
							newSlot.setName(nextSlotName.toString());
							newSlot.setReporter(nextReporter);
							newSlot.setRoster(roster);
							Date slotStartTime = null;
							if(roster.getCommitteeMeeting() != null){
								slotStartTime = startTime;
							}else{
								if(previousAdjournment!=null 
										&& previousAdjournment.getEndTime()!=null
										&& previousAdjournment.getEndTime().compareTo(adjournment.getStartTime())==0
										&& (lastActiveSlotAfterPreviousAdjournment==null 
										|| lastActiveSlotAfterPreviousAdjournment.getEndTime().equals(adjournment.getStartTime())) ) {
									slotStartTime=adjournment.getEndTime();
									slotStartTime = adjustDateTimeForSlotAccordingToSlotDuration(roster, slotStartTime);
								}else if(lastActiveSlotAfterPreviousAdjournment!=null 
										&& lastActiveSlotAfterPreviousAdjournment.getEndTime().compareTo(adjournment.getStartTime())>0) {
									slotStartTime=adjournment.getEndTime();
									slotStartTime = adjustDateTimeForSlotAccordingToSlotDuration(roster, slotStartTime);
								}
								else
								slotStartTime = lastAdjournedSlot.getEndTime();
							}
							// slotStartTime=startTime;//lastAdjournedSlot.getEndTime();
							Calendar calendar=Calendar.getInstance();
							calendar.setTime(slotStartTime);
							calendar.add(Calendar.MINUTE,roster.getSlotDuration());
							Date slotEndTime=calendar.getTime();
							newSlot.setStartTime(slotStartTime);//lastAdjournedSlot.getEndTime());
							newSlot.setEndTime(slotEndTime);
							newSlot.setTurnedoff(false);
							newSlot.persist();
							Proceeding proceeding=new Proceeding();
							proceeding.setLocale(newSlot.getLocale());
							proceeding.setSlot(newSlot);
							proceeding.persist();
							newSlotTime = newSlot.getEndTime();
							if(ch=='Z'){								
								ch='A';
								repeat++;
							}else{
								ch++;
							}
						}
						/**** if last generated slot end time is > start time then it must be updated ****/
//						if(lastGeneratedSlot.getEndTime()!=null&&lastGeneratedSlot.getEndTime().after(startTime)){
//							lastGeneratedSlot.setEndTime(startTime);
//							lastGeneratedSlot.merge();
//						}
					}else{
						Slot lastGeneratedSlot=Slot.lastGeneratedSlot(roster);
						if(lastGeneratedSlot!=null&&lastGeneratedSlot.getReporter()!=null){
							lastSlotReporterPosition=lastGeneratedSlot.getReporter().getPosition();			
							newSlot=lastGeneratedSlot;
							if(roster.getCommitteeMeeting() != null){
								newSlotTime = startTime;
							}else{
								if(previousAdjournment!=null 
										&& previousAdjournment.getEndTime()!=null
										&& previousAdjournment.getEndTime().compareTo(adjournment.getStartTime())==0
										&& (lastActiveSlotAfterPreviousAdjournment==null 
										|| lastActiveSlotAfterPreviousAdjournment.getEndTime().equals(adjournment.getStartTime())) ) {
									newSlotTime=adjournment.getEndTime();
									newSlotTime = adjustDateTimeForSlotAccordingToSlotDuration(roster, newSlotTime);
								}else if(lastActiveSlotAfterPreviousAdjournment!=null 
										&& lastActiveSlotAfterPreviousAdjournment.getEndTime().compareTo(adjournment.getStartTime())>0) {
									newSlotTime=adjournment.getEndTime();
									newSlotTime = adjustDateTimeForSlotAccordingToSlotDuration(roster, newSlotTime);
								}
								
								else
								newSlotTime = newSlot.getEndTime();
							}
							if(lastGeneratedSlot.getName()!=null&&!lastGeneratedSlot.getName().isEmpty()){
								ch=lastGeneratedSlot.getName().charAt(0);
								repeat=lastGeneratedSlot.getName().length();
								if(ch=='Z'){								
									ch='A';
									repeat++;
								}else{
									ch++;
								}
							}
							/**** if last generated slot end time is > start time then it must be updated ****/
//							if(lastGeneratedSlot.getEndTime()!=null&&lastGeneratedSlot.getEndTime().after(startTime)){
//								lastGeneratedSlot.setEndTime(startTime);
//								lastGeneratedSlot.merge();
//							}
						}
					}
				}
				Date slotStartTime = newSlotTime;
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(slotStartTime);
				calendar.add(Calendar.MINUTE,roster.getSlotDuration());
				Date slotEndTime=calendar.getTime();
				int count=startingIndex;	
				/**** last slot end time will be used to generate the last slot which falls short
				 * of end time by some margin
				 */
				Date lastSlotEndTime=null;
				while(slotEndTime.before(roster.getEndTime())||slotEndTime.equals(roster.getEndTime())){
					Slot slot=new Slot();
					slot.setEndTime(slotEndTime);
					slot.setLocale(roster.getLocale());
					slot.setRoster(roster);
					slot.setStartTime(slotStartTime);
					slot.setTurnedoff(false);
					if(lastSlotReporterPosition==0){
						slot.setReporter(reporters.get(count));
					}else{
						/**** Here we are trying to find out the position where to start adding reporters ****/
						/**** We find the first reporter whose position is greater thean the position
						 * of last generated slot reporter.If it is found then addition of reporter will
						 * carry from it else addition of reporter will start from begining.****/
						int innerIndex=0;
						for(Reporter i:reporters){
							int reporterPosition=i.getPosition();
							if(reporterPosition>lastSlotReporterPosition){
								count=innerIndex;
								lastSlotReporterPosition=0;
								slot.setReporter(reporters.get(count));
								break;
							}
							innerIndex++;
						}
						if(slot.getReporter()==null){
							lastSlotReporterPosition=0;
							slot.setReporter(reporters.get(count));
						}
					}
					StringBuffer buffer=new StringBuffer();
					for(int i=1;i<=repeat;i++){
						buffer.append(ch);
					}
					slot.setName(buffer.toString());									
					slot.persist();
					Proceeding proceeding=new Proceeding();
					proceeding.setLocale(slot.getLocale());
					proceeding.setSlot(slot);
					proceeding.persist();
					if(ch<'Z'){
						ch++;
					}else{
						ch='A';
						repeat++;
					}
					lastSlotEndTime=slotEndTime;
					slotStartTime=slotEndTime;
					calendar.add(Calendar.MINUTE, roster.getSlotDuration());
					slotEndTime=calendar.getTime();
					if(count==reporters.size()-1){
						count=0;
					}else{
						count++;
					}
				}
				/**** This is for the last slot which falls sort of end time for some margin****/
				if(lastSlotEndTime.before(endTime)){
					Slot slot=new Slot();
					slot.setEndTime(endTime);
					slot.setLocale(roster.getLocale());
					slot.setRoster(roster);
					slot.setStartTime(lastSlotEndTime);
					slot.setTurnedoff(false);
					slot.setReporter(reporters.get(count));
					StringBuffer buffer=new StringBuffer();
					for(int i=1;i<=repeat;i++){
						buffer.append(ch);
					}
					slot.setName(buffer.toString());									
					slot.persist();		
					Proceeding proceeding=new Proceeding();
					proceeding.setLocale(slot.getLocale());
					proceeding.setSlot(slot);
					proceeding.persist();
				}
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			logger.error("SLOT GENERATION FAILED",e);
			return false;
		}	
	}

	public Boolean deleteExistingSlots(final Long rosterId,final Date startTime,final Date endTime) {
		try {
			String strquery1="DELETE FROM proceedings  WHERE slot IN(SELECT id " +
					"FROM slots  WHERE roster=:roster AND start_time>=:startTime" +
					" AND  end_time<=:endTime AND bln_deleted=false)";
			Query query1=this.em().createNativeQuery(strquery1);
			query1.setParameter("roster",rosterId);
			query1.setParameter("startTime",startTime);
			query1.setParameter("endTime",endTime);
			query1.executeUpdate();
			
			//String strQuery="DELETE FROM Slot s WHERE s.roster.id=:roster AND s.startTime>=:startTime" +
				//	" and s.endTime<=:endTime";
			
			String strQuery="SELECT s FROM Slot s WHERE s.roster.id=:roster AND s.startTime>=:startTime" +
			" AND s.endTime<=:endTime AND s.blnDeleted=false ";			
			Query query=this.em().createQuery(strQuery);
			query.setParameter("roster",rosterId);
			query.setParameter("startTime",startTime);
			query.setParameter("endTime",endTime);
			List<Slot> slots=query.getResultList();
			for(Slot s:slots){
				s.setBlnDeleted(true);
				s.merge();
			}
			//query.executeUpdate();
			return true;			
		} catch (Exception e) {
			logger.error("DELETION OF EXISTING SLOTS FAILED",e);
			return false;
		}
	}
	
	public Boolean deleteExistingSlotsAdjournment(final Long rosterId,final Date startTime,final Date endTime) {
		try {
			String strquery1="DELETE FROM proceedings  WHERE slot IN(SELECT id " +
				"FROM slots s WHERE roster=:roster " +
				"AND s.start_time>=:startTime "+
				"AND s.end_time<=:endTime "+
				"AND s.bln_deleted=false) ";
			Query query1=this.em().createNativeQuery(strquery1);
			query1.setParameter("roster",rosterId);
			query1.setParameter("startTime",startTime);
			query1.setParameter("endTime",endTime);
			query1.executeUpdate();
			
			String strQuery="SELECT s FROM Slot s "+ 
				"WHERE s.roster.id=:roster "+ 
				"AND s.startTime>=:startTime "+
				"AND s.endTime<=:endTime "+
				"AND s.blnDeleted=false"	;		
			Query query=this.em().createQuery(strQuery);
			query.setParameter("roster",rosterId);
			query.setParameter("startTime",startTime);
			query.setParameter("endTime",endTime);
			List<Slot> slots=query.getResultList();
			for(Slot s:slots){
				s.setBlnDeleted(true);
				s.merge();
			}
			return true;			
		} catch (Exception e) {
			logger.error("DELETION OF EXISTING SLOTS FAILED",e);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Boolean toggleSlots(final Long rosterId,final Date startTime,final Date endTime,final Boolean toggle) {
		try {
			String strQuery="SELECT s FROM Slot s WHERE s.roster.id=:roster AND s.startTime>=:startTime" +
			" AND s.endTime<=:endTime AND s.blnDeleted=false";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("roster",rosterId);
			query.setParameter("startTime", startTime);
			query.setParameter("endTime", endTime);
			List<Slot> slots=query.getResultList();
			for(Slot i:slots){
				i.setTurnedoff(toggle);
				i.merge();
			}
			return true;
		} catch (Exception e) {
			logger.error("TURNING OFF SLOTS FAILED",e);
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Boolean toggleSlotsAdjournment(final Long rosterId,final Date startTime,final Date endTime,final Boolean toggle) {
		try {
//			String strQuery="SELECT s FROM Slot s WHERE s.roster.id=:roster AND ((s.startTime>=:startTime" +
//			" AND s.endTime<=:endTime) OR (s.startTime<=:startTime AND s.endTime>=:startTime)) AND s.isDeleted=false ";
//			String strQuery="SELECT s FROM Slot s WHERE s.roster.id=:roster AND s.startTime<:endTime" +
//					" AND s.endTime>:startTime AND s.isDeleted=false ";
			String strQuery="SELECT s FROM Slot s WHERE s.roster.id=:roster AND s.startTime>=:startTime" +
					" AND s.endTime<=:endTime AND s.blnDeleted=:blnDeleted AND s.turnedoff<>:turnedOff ";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("roster",rosterId);
			query.setParameter("startTime", startTime);
			query.setParameter("endTime", endTime);
			query.setParameter("blnDeleted", false);
			query.setParameter("turnedOff", true);
			List<Slot> slots=query.getResultList();
			for(Slot i:slots){
				i.setTurnedoff(toggle);
				i.merge();
			}
			return true;
		} catch (Exception e) {
			logger.error("TURNING OFF SLOTS FAILED",e);
			return false;
		}
	}

	/**** Native Query(Do not remove) ****/
	@SuppressWarnings("rawtypes")
	public boolean removeRoster(final Roster roster) {
		try {
			String strQuery="DELETE FROM proceedings where slot IN (SELECT id FROM slots where roster=:roster)";
			String query1="DELETE FROM slots WHERE roster=:roster";
			String query2="DELETE FROM adjournments WHERE roster=:roster";
			String query3="SELECT r.id FROM Roster rs JOIN rs.reporters r WHERE rs.id=:id"; 
			String query4="DELETE FROM rosters_reporters WHERE roster_id=:roster";
			String query5="DELETE FROM rosters WHERE id=:roster";
			String query6="DELETE FROM Reporter r WHERE r.id IN :reporters";
			this.em().createNativeQuery(strQuery).setParameter("roster",roster.getId()).executeUpdate();
			this.em().createNativeQuery(query1).setParameter("roster",roster.getId()).executeUpdate();
			this.em().createNativeQuery(query2).setParameter("roster",roster.getId()).executeUpdate();
			List reporters=this.em().createQuery(query3).setParameter("id",roster.getId()).getResultList();
			this.em().createNativeQuery(query4).setParameter("roster",roster.getId()).executeUpdate();
			this.em().createNativeQuery(query5).setParameter("roster",roster.getId()).executeUpdate();
			if(reporters!=null&&!reporters.isEmpty()){
				this.em().createQuery(query6).setParameter("reporters",reporters).executeUpdate();
			}
			return true;
		} catch (Exception e) {
			logger.error("DELETION FAILED",e);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Reporter findFirstReporterAtPosX(final Roster roster,final int position,final String activeStatus) {
		try {
			Boolean isActive=true;
			if(activeStatus.toLowerCase().equals("false")){
				isActive=false;
			}
			if(activeStatus.toLowerCase().equals("true")||activeStatus.toLowerCase().equals("false")){
				String strQuery="SELECT r FROM Roster rs JOIN rs.reporters r WHERE rs.id=:roster AND r.position>=:position" +
				" AND r.isActive=:isActive ORDER BY r.position";
				Query query=this.em().createQuery(strQuery);
				query.setParameter("roster",roster.getId());
				query.setParameter("position",position);
				query.setParameter("isActive",isActive);
				List<Reporter> reporters= query.getResultList();
				if(reporters!=null&&!reporters.isEmpty()){
					return reporters.get(0);
				}else{
					String strQuery1="SELECT r FROM Roster rs JOIN rs.reporters r WHERE rs.id=:roster AND r.position<:position" +
					" AND r.isActive=:isActive ORDER BY r.position";
					Query query1=this.em().createQuery(strQuery1);
					query1.setParameter("roster",roster.getId());
					query1.setParameter("position",position);
					query1.setParameter("isActive",isActive);
					List<Reporter> reporters1= query1.getResultList();
					if(reporters1!=null&&!reporters1.isEmpty()){
						return reporters1.get(0);
					}else{
						return null;
					}
				}				
			}else{
				String strQuery="SELECT r FROM Roster rs JOIN rs.reporters r WHERE rs.id=:roster AND r.position=:position";
				Query query=this.em().createQuery(strQuery);
				query.setParameter("roster",roster.getId());
				query.setParameter("position",position);
				return (Reporter) query.getSingleResult();
			}
		} catch (Exception e) {
			logger.error("REPORTER NOT FOUND",e);
			return null;
		}
	}

	public Reporter findByUser(final Roster roster,final User user) {
		try {
			String strQuery="SELECT r FROM Roster rs JOIN rs.reporters r JOIN r.user u WHERE rs.id=:roster" +
			" AND u.id=:user";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("roster",roster.getId());
			query.setParameter("user",user.getId());
			List<Reporter> reporters = query.getResultList();
			if(reporters != null && !reporters.isEmpty()){
				return reporters.get(0);
			}else{
				return null;
			}
		} catch (Exception e) {
			logger.error("REPORTER NOT FOUND",e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Reporter> findReportersOtherThan(final Roster roster,
			final List<Long> originalReporters) {
		String strQuery=null;
		Query query=null;
		if(originalReporters!=null&&!originalReporters.isEmpty()){
			strQuery="SELECT r FROM Roster rs JOIN rs.reporters r WHERE rs.id=:roster " +
			" AND r.id NOT IN :reporters";
			query=this.em().createQuery(strQuery);
			query.setParameter("roster", roster.getId());
			query.setParameter("reporters",originalReporters);
		}else{
			strQuery="SELECT r FROM Roster rs JOIN rs.reporters r WHERE rs.id=:roster " ;
			query=this.em().createQuery(strQuery);
			query.setParameter("roster", roster.getId());
		}		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Reporter> findReportersByActiveStatus(final Roster roster,
			final Boolean isActive) {
		String strQuery="SELECT r FROM Roster rs JOIN rs.reporters r WHERE rs.id=:roster AND r.isActive=:isActive ORDER BY r.position";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("roster",roster.getId());
		query.setParameter("isActive",isActive);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Roster> findAllRosterBySessionAndLanguage(Session session,Language language,
			String locale) {
		String strQuery="SELECT rs FROM Roster rs WHERE rs.session=:session and rs.language=:language AND rs.locale=:locale"; 
		Query query=this.em().createQuery(strQuery);
		query.setParameter("session", session);
		query.setParameter("language", language);
		query.setParameter("locale", locale);
		return query.getResultList();
	}

	public Roster findRosterBySessionLanguageAndDay(Session session, int day,
			Language language, String locale) {
		String strQuery="SELECT rs FROM Roster rs" +
				" WHERE rs.session=:session " +
				" AND rs.language=:language " +
				" AND rs.locale=:locale" +
				" AND rs.day=:day"; 
		Query query=this.em().createQuery(strQuery);
		query.setParameter("session", session);
		query.setParameter("language", language);
		query.setParameter("locale", locale);
		query.setParameter("day", day);
		return (Roster) query.getSingleResult();
	}

	public Roster findRosterByDate(Date sDate,Language language, Session session, String locale) {
		String strQuery="SELECT rs FROM Roster rs" +
				" WHERE (DATE(rs.startTime)=:sDate" +
				" OR DATE(rs.endTime)=:sDate)" +
				" AND rs.locale=:locale" +
				" AND rs.language.id=:languageId"+
				" AND rs.session.id=:sessionId";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("sDate", sDate);
		query.setParameter("locale", locale);
		query.setParameter("languageId",language.getId());
		query.setParameter("sessionId",session.getId());
		return (Roster) query.getSingleResult();
	}

	public List<Roster> findAllRosterByCommitteeMeeting(
			CommitteeMeeting committeeMeeting, Language language, String locale) {
		String strQuery="SELECT rs FROM Roster rs "
				+ " WHERE rs.committeeMeeting=:committeeMeeting "
				+ " AND rs.language=:language "
				+ " AND rs.locale=:locale"; 
		Query query=this.em().createQuery(strQuery);
		query.setParameter("committeeMeeting", committeeMeeting);
		query.setParameter("language", language);
		query.setParameter("locale", locale);
		return query.getResultList();
	}
	
	public Roster findRosterByCommitteeMeetingLanguageAndDay(CommitteeMeeting committeeMeeting, int day,
			Language language, String locale) {
		String strQuery="SELECT rs FROM Roster rs" +
				" WHERE rs.committeeMeeting=:committeeMeeting " +
				" AND rs.language=:language " +
				" AND rs.locale=:locale" +
				" AND rs.day=:day"; 
		Query query=this.em().createQuery(strQuery);
		query.setParameter("committeeMeeting", committeeMeeting);
		query.setParameter("language", language);
		query.setParameter("locale", locale);
		query.setParameter("day", day);
		return (Roster) query.getSingleResult();
	}

	public List<CommitteeMeeting> findCommitteeMeetingByUserId(Long userId,
			String locale) {
		List<CommitteeMeeting> committeeMeetings = new ArrayList<CommitteeMeeting>();
//		String strquery = "SELECT cm.*"
//				+" FROM committee_meetings cm"
//				+" INNER JOIN rosters r ON (cm.id=r.`committee_meeting`)"
//				+" INNER JOIN rosters_reporters rr ON (rr.roster_id=r.`id`)"
//				+" INNER JOIN reporters ro ON (ro.id=rr.reporter_id)"
//				+" INNER JOIN users u ON (u.id=ro.user)"
//				+" WHERE u.id=:userId";
		String strquery = "SELECT cm"
				+" FROM Roster r"
				+" JOIN r.reporters ro"
				+" JOIN r.committeeMeeting cm"
				+" WHERE ro.user.id=:userId";
		Query query = this.em().createQuery(strquery);
		query.setParameter("userId", userId);
		committeeMeetings = query.getResultList();
		return committeeMeetings;
	}

	public Slot findPreviousSlot(Slot slot) {
		Slot previousSlot = null;
		String strQuery = "SELECT s FROM"
				+ " Slot s WHERE s.startTime <:startTime"
				+ " AND s.roster.id=:rosterId"
				+ " AND s.blnDeleted=false"
				+ " ORDER BY s.startTime DESC";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("startTime",slot.getStartTime());
		query.setParameter("rosterId",slot.getRoster().getId());
		List<Slot> slots = query.getResultList();
		if(slots != null && !slots.isEmpty()){
			previousSlot = slots.get(0);
		}
		return previousSlot;
	}

	public Slot findNextSlot(Slot slot) {
		Slot nextSlot = null;
		String strQuery = "SELECT s FROM"
				+ " Slot s WHERE s.startTime >:startTime"
				+ " AND s.roster.id=:rosterId"
				+ " AND s.blnDeleted=false"
				+ " ORDER BY s.startTime ASC";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("startTime",slot.getStartTime());
		query.setParameter("rosterId",slot.getRoster().getId());
		List<Slot> slots = query.getResultList();
		if(slots != null && !slots.isEmpty()){
			nextSlot = slots.get(0);
		}
		return nextSlot;
	}

	public Roster findByPart(Part part, String locale) {
		Roster roster = null;
		String strQuery = "SELECT r FROM Part p"
				+ " JOIN p.proceeding proc "
				+ " JOIN proc.slot s "
				+ " JOIN s.roster r "
				+ " WHERE p.id=:partId ";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("partId",part.getId());
		List<Roster> rosters = query.getResultList();
		if(rosters != null && !rosters.isEmpty()){
			roster = rosters.get(0);
		}
				
		
		return roster;
	}
	
	public List<Adjournment> findAdjournmentForRosterAfterTime(Roster roster, Date afterTime) {
		List<Adjournment> listAdjournment=null;
		if(roster!=null && roster.getId()>0 && afterTime!=null) {
			String strQuery="SELECT a FROM Adjournment a WHERE a.roster.id=:rosterId AND a.startTime >=:afterTime";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("rosterId", roster.getId());
			query.setParameter("afterTime",afterTime);
			listAdjournment=query.getResultList();
		}
		return listAdjournment;
	}
	
	
	private List<List<Date>> calculateNewStartEndTimeForSlotsAccordingToAdjournments(List<Adjournment> adjourments,
			Roster savedRoster, Roster roster,Slot lastGeneratedSlot) {
		List<List<Date>> slotTimingBlocks=new ArrayList<List<Date>>();
		if (adjourments != null && adjourments.size() > 0 && savedRoster != null && savedRoster.getEndTime() != null
				&& roster != null && roster.getStartTime() != null && roster.getEndTime() != null
				&& lastGeneratedSlot != null && lastGeneratedSlot.getEndTime() != null) {
			Adjournment prevAdjournment=null;
			int totalAdjournment=adjourments.size()-1;
			int currentAdjournmentCount=0;
			for (Adjournment adjournment : adjourments) {
				
				if (adjournment != null && adjournment.getId() > 0
						&& adjournment.getRoster().getId() == roster.getId()) {
					
					int adjournmentTimeInMinutes = calculateAdjournmentTime(adjournment, Calendar.MINUTE);
					int diffBetweenLastSlotEndTimeAndSavedRosterTimeInMinutes = calculateDifferenceInTime(
							savedRoster.getEndTime(), lastGeneratedSlot.getEndTime(), Calendar.MINUTE);
					
					if(prevAdjournment!=null ) {
						int diffBetweenPreviousAdjournmentAndCurrentAdjournment=calculateDifferenceInTime
								(adjournment.getStartTime(), prevAdjournment.getEndTime(), Calendar.MINUTE);
						//slot between two consecutive adjournments
						if(diffBetweenPreviousAdjournmentAndCurrentAdjournment>0) {
							slotTimingBlocks.add(Arrays.asList(prevAdjournment.getEndTime(),adjournment.getStartTime()));
						}
					}
					
					if (adjournment.getStartTime().after(lastGeneratedSlot.getEndTime())) {
					
						if(prevAdjournment==null)
							slotTimingBlocks.add(Arrays.asList(lastGeneratedSlot.getEndTime(), adjournment.getStartTime()));
						/*
						 * else if(adjournment.getStartTime().after(prevAdjournment.getEndTime()))
						 * slotTimingBlocks.add(Arrays.asList(prevAdjournment.getEndTime(),
						 * adjournment.getStartTime()));
						 */
						else if(adjournment.getStartTime().equals(prevAdjournment.getEndTime()) 
								&& currentAdjournmentCount==totalAdjournment)
							slotTimingBlocks.add(Arrays.asList(adjournment.getEndTime(), roster.getEndTime()));
					
					} else if(adjournment.getStartTime().equals(lastGeneratedSlot.getEndTime())){
						if(prevAdjournment==null && currentAdjournmentCount==totalAdjournment)
							slotTimingBlocks.add(Arrays.asList(adjournment.getEndTime(),roster.getEndTime()));
						else if(adjournment.getStartTime().equals(prevAdjournment.getEndTime()) 
								&& currentAdjournmentCount==totalAdjournment)
							slotTimingBlocks.add(Arrays.asList(prevAdjournment.getEndTime(),roster.getEndTime()));
					} if (adjournment.getStartTime().after(lastGeneratedSlot.getStartTime())
							&& adjournment.getEndTime().after(lastGeneratedSlot.getEndTime())) {
						// do nothing as not need to generate slots
					} else if (adjournment.getEndTime().after(savedRoster.getEndTime())
							&& currentAdjournmentCount==totalAdjournment) {
						slotTimingBlocks.add(Arrays.asList(adjournment.getEndTime(), roster.getEndTime()));
					}
					
					if(currentAdjournmentCount==totalAdjournment) {
						if(adjournment.getEndTime().before(roster.getEndTime())) {
							slotTimingBlocks.add(Arrays.asList(adjournment.getEndTime(),roster.getEndTime()));
						}
					}
					
					prevAdjournment=adjournment;
					currentAdjournmentCount++;
				}
			}
		}
		
		
		slotTimingBlocks=correctTimingContinuation(slotTimingBlocks);
		
		/*
		 * if(slotTimingBlocks!=null && slotTimingBlocks.size()>0) { List<Date>
		 * lastElement=slotTimingBlocks.get(slotTimingBlocks.size()-1);
		 * if(lastElement!=null && lastElement.size()==2) { Date
		 * adjEndTime=lastElement.get(1); Date endTimeForSlot=roster.getEndTime();
		 * lastElement.add(0,adjEndTime); lastElement.add(1,endTimeForSlot);
		 * slotTimingBlocks.add(slotTimingBlocks.size()-1,lastElement); } }
		 */
		
		
		return slotTimingBlocks;
	}	

	private int calculateAdjournmentTime(Adjournment adjournment, int TimeUnitTypeInCalendar) {
		if(adjournment!=null && adjournment.getStartTime()!=null && adjournment.getEndTime()!=null) {			
			int adjournmentTimeInSpecifiedType = calculateDifferenceInTime(adjournment.getEndTime()
																				,adjournment.getStartTime()
																				,TimeUnitTypeInCalendar);
			return adjournmentTimeInSpecifiedType;
		}
		return -1;
	}
	
	private int calculateDifferenceInTime(Date endTime, Date startTime, int TimeUnitTypeInCalendar) {
		if(endTime!=null && startTime!=null) {
			Calendar calendar=Calendar.getInstance();
			long diffTimeInMillis=(endTime.getTime())- (startTime.getTime());
			
			calendar.setTimeInMillis(diffTimeInMillis);
			calendar.getTime();
			int diffTimeInSpecifiedType = Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(diffTimeInMillis)).intValue();
			return diffTimeInSpecifiedType;
		}
		return -1;
	}
	
	
	private List<List<Date>> correctTimingContinuation(List<List<Date>> slotTimingBlocks) {

		//sort dates
		Collections.sort(slotTimingBlocks, new Comparator<List<Date>>() {
			@Override
			public int compare(List<Date> o1, List<Date> o2) {
				Date endTime1 = o1.get(1);
				Date endTime2 = o2.get(1);
				return endTime1.compareTo(endTime2);
			}
		});

		List<List<Date>> continuationList = new ArrayList<List<Date>>();
		if (slotTimingBlocks != null && slotTimingBlocks.size() > 0) {
			for (int i = 0; i < slotTimingBlocks.size(); i++) {
				List<Date> currentDates = slotTimingBlocks.get(i);
				List<Date> nextDates = new ArrayList<Date>();
				if (slotTimingBlocks.size() > i + 1) {
					nextDates = slotTimingBlocks.get(i + 1);
				}

				if (nextDates != null && nextDates.size() > 1) {

					if (currentDates.get(0).equals(nextDates.get(0)) 
							&& currentDates.get(1).equals(nextDates.get(1))) {
						//don't add duplicates
					} else {
						Date currentEndDate = currentDates.get(1);
						Date nextStartDate = nextDates.get(0);

						if (currentEndDate.equals(nextStartDate)) {
							continuationList.add(Arrays.asList(currentDates.get(0), nextDates.get(1)));
						} else {
							continuationList.add(currentDates);
						}
					}

				} else {
					continuationList.add(currentDates);
				}
			}
		}
		return continuationList;
	}
	
	private List<List<Date>> adjustSlotTimingsForSlotDuration(List<Date> lstSlotBlock,int slotDuration,Date rosterEndTime) {
		if(lstSlotBlock!=null && lstSlotBlock.size()>1 ) {
			
			List<List<Date>> listOfDates=new ArrayList<List<Date>>();
			int availableTimeBlock=calculateDifferenceInTime(lstSlotBlock.get(1), lstSlotBlock.get(0), Calendar.MINUTE);
			int numberOfSlotAllowed=availableTimeBlock/slotDuration;
			double actualSlotsAllowed=Double.valueOf(availableTimeBlock)/Double.valueOf(slotDuration);
			
			if(actualSlotsAllowed>numberOfSlotAllowed) {
				numberOfSlotAllowed++;
			}
			
			int actualSlotBlockEndTimeInMinutes=numberOfSlotAllowed*slotDuration;
			
			Calendar calendar = Calendar.getInstance();
			
			//start time of slot
			calendar.setTime(lstSlotBlock.get(0));
			
			int startTimeMinutes=calendar.get(Calendar.MINUTE);
			if(startTimeMinutes%slotDuration!=0) {
				calendar.add(Calendar.MINUTE,-(startTimeMinutes%slotDuration));
			}
			Date newStartTime=calendar.getTime();
			calendar.add(Calendar.MINUTE,actualSlotBlockEndTimeInMinutes);
			Date newEndTime=calendar.getTime();
			listOfDates.add(Arrays.asList(newStartTime,newEndTime));
			/*
			 * if(newEndTime.after(rosterEndTime)) { calendar.setTime(rosterEndTime);
			 * calendar.add(Calendar.MINUTE, -(actualSlotBlockEndTimeInMinutes)); return
			 * Arrays.asList(calendar.getTime(),rosterEndTime); }
			 */
			
			// end time adjustment
			if(newEndTime.before(lstSlotBlock.get(1))) {
				calendar.setTime(lstSlotBlock.get(1));
				int actualEndTimeMinutes=calendar.get(Calendar.MINUTE);
				if(actualEndTimeMinutes%slotDuration!=0) {
					calendar.add(Calendar.MINUTE, -(actualEndTimeMinutes%slotDuration));
					newStartTime=calendar.getTime();
					if(actualSlotBlockEndTimeInMinutes>slotDuration)
						calendar.add(Calendar.MINUTE, (slotDuration));
					else
						calendar.add(Calendar.MINUTE, (actualSlotBlockEndTimeInMinutes));
					
					newEndTime=calendar.getTime();
					listOfDates.add(Arrays.asList(newStartTime,newEndTime));
				}
			}
			
			return listOfDates;
			
			
		}
		return null;
	}
	
	
	public Adjournment findPreviousAdjournment(Roster roster, Date startTime) {
		if(roster!=null && roster.getId()>0 && startTime!=null) {
			String strQuery="SELECT a FROM Adjournment a WHERE a.roster.id=:rosterId "
					+ " AND a.endTime<=:endTime"
					+ " ORDER BY a.endTime desc";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("rosterId", roster.getId());
			query.setParameter("endTime",startTime);
			List<Adjournment> listAdjournment = query.getResultList();
			if(listAdjournment!=null && listAdjournment.size()>0)
				return listAdjournment.get(0);
		}
		return null;
	}
	
	private Date adjustDateTimeForSlotAccordingToSlotDuration(final Roster roster, Date slotStartTime) {
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(slotStartTime);
		int minutesParts = calendar.get(Calendar.MINUTE);
		if(minutesParts%roster.getSlotDuration()>0) {
			calendar.add(Calendar.MINUTE,-(minutesParts%roster.getSlotDuration()));
			slotStartTime=calendar.getTime();
		}
		return slotStartTime;
	}
	
	private boolean deleteSlotsInAdjournmetTimeSlots(Slot slot, Roster roster,
			Adjournment adjournment,Adjournment previousAdjournment) {
		if(slot!=null && slot.getStartTime()!=null && slot.getEndTime()!=null
				&& adjournment!=null && adjournment.getStartTime()!=null && adjournment.getEndTime()!=null) {
			//if slot happens to be withing adjournment dates delete it
			if((slot.getStartTime().compareTo(adjournment.getStartTime())>=0)			
			&& (slot.getEndTime().compareTo(adjournment.getEndTime())<=0)) {
				slot.setBlnDeleted(true);
				slot.setBlnDeleted(true);
				Slot.getSlotRepository().merge(slot);
				return true;
			}else if(adjournment.getEndTime().compareTo(previousAdjournment.getStartTime())>=0
					&& adjournment.getEndTime().compareTo(previousAdjournment.getEndTime())>=0
					&& previousAdjournment.getEndTime().compareTo(adjournment.getStartTime())==0
					&& slot.getStartTime().compareTo(adjournment.getEndTime())<=0
					&& slot.getEndTime().compareTo(adjournment.getEndTime())<=0) {
				slot.setBlnDeleted(true);
				slot.setBlnDeleted(true);
				Slot.getSlotRepository().merge(slot);
				return true;
			}
		}
		return false;
		
	}
	
}
