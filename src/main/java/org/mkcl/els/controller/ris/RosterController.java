package org.mkcl.els.controller.ris;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.controller.NotificationController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CommitteeMeeting;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Reporter;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/roster")
public class RosterController extends GenericController<Roster>{

	@Override
	protected void populateModule(final ModelMap model,final HttpServletRequest request,
			final String locale,final AuthUser currentUser) {	

		
		/**** House Types ****/
		List<HouseType> houseTypes = new ArrayList<HouseType>();
		String houseType=this.getCurrentUser().getHouseType();
		if(houseType.equals("lowerhouse")){
			houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
		}else if(houseType.equals("upperhouse")){
			houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
		}else if(houseType.equals("bothhouse")){
			houseTypes=HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
		}
		model.addAttribute("houseTypes", houseTypes);
		if(houseType.equals("bothhouse")){
			houseType="lowerhouse";
		}
		model.addAttribute("houseType",houseType);

		/**** Session Types ****/
		List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
		HouseType authUserHouseType=HouseType.findByFieldName(HouseType.class, "type",houseType, locale);
		Session lastSessionCreated = null;
		try {
			lastSessionCreated = Session.findLatestSession(authUserHouseType);
		} catch (ELSException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
		}
		Integer year=new GregorianCalendar().get(Calendar.YEAR);
		if(lastSessionCreated.getId()!=null){
			year=lastSessionCreated.getYear();
			model.addAttribute("sessionType",lastSessionCreated.getType().getId());
		}else{
			model.addAttribute("errorcode","nosessionentriesfound");
		}
		model.addAttribute("sessionTypes",sessionTypes);

		/**** Years ****/
		CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
		List<Integer> years=new ArrayList<Integer>();
		if(houseFormationYear!=null){
			Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
			for(int i=year;i>=formationYear;i--){
				years.add(i);
			}
		}else{
			model.addAttribute("errorcode", "houseformationyearnotset");
		}
		model.addAttribute("years",years);
		model.addAttribute("sessionYear",year);	

		/**** Language ****/
		List<Language> languages = new ArrayList<Language>();
		try {
			languages = Language.findAllLanguagesByModule("RIS",locale);
		} catch (ELSException e) {
			
			e.printStackTrace();
		}
		model.addAttribute("languages",languages);
		
		/*** CommitteeType ***/
		List<CommitteeType> committeeTypes = CommitteeType.findAll(CommitteeType.class, "name", "ASC", locale);
		model.addAttribute("committeeTypes", committeeTypes);
	}

	@Override
	protected void populateNew(ModelMap model, Roster domain, String locale,
			HttpServletRequest request) {
		/**** Locale ****/
		domain.setLocale(locale);

		/**** House Type ****/
		String selectedHouseType=request.getParameter("houseType");
		HouseType houseType=null;
		if(selectedHouseType!=null&&!selectedHouseType.isEmpty()){
			try {
				Long houseTypeId=Long.parseLong(selectedHouseType);
				houseType=HouseType.findById(HouseType.class,houseTypeId);
			} catch (NumberFormatException e) {
				houseType=HouseType.findByFieldName(HouseType.class,"type",selectedHouseType, locale);
			}			
		}else{
			logger.error("**** Check request parameter 'houseType' for null value ****");
			model.addAttribute("errorcode","selectedHouseTypeIsNull");
		}
		
		
		/**** Language ****/
		String selectedLanguage=request.getParameter("language");
		Language language=null;
		if(selectedLanguage!=null&&!selectedLanguage.isEmpty()){
			language=Language.findById(Language.class,Long.parseLong(selectedLanguage));
			model.addAttribute("language",language.getId());
		}else{
			logger.error("**** Check request parameter 'sessionType' for null value ****");
			model.addAttribute("errorcode","selectedSessionTypeIsNull");
		}
		
		/*** CommitteeMeeting ****/
		String strCommitteeMeeting = request.getParameter("committeeMeeting");
		if(strCommitteeMeeting != null && !strCommitteeMeeting.isEmpty() && !strCommitteeMeeting.equals("0")){
			CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(CommitteeMeeting.class, Long.parseLong(strCommitteeMeeting));
			if(committeeMeeting != null){
				String meetingDate = FormaterUtil.formatDateToString(committeeMeeting.getMeetingDate(), ApplicationConstants.SERVER_DATEFORMAT);
				String startTime = committeeMeeting.getStartTime();
				String endTime = committeeMeeting.getEndTime();
				model.addAttribute("startTime",meetingDate + " " +startTime);
				model.addAttribute("endTime", meetingDate + " " +endTime);
				model.addAttribute("committeeMeeting",committeeMeeting.getId());
				domain.setDay(1);
			}
			
		}else{
			model.addAttribute("committeeMeeting","");
			/**** Session Year ****/
			String selectedYear=request.getParameter("sessionYear");
			Integer sessionYear=0;
			if(selectedYear!=null&&!selectedYear.isEmpty()){			
				sessionYear=Integer.parseInt(selectedYear);		
			}else{
				logger.error("**** Check request parameter 'sessionYear' for null value ****");
				model.addAttribute("errorcode","selectedSessionYearIsNull");
			}  

			/**** Session Type ****/
			String selectedSessionType=request.getParameter("sessionType");
			SessionType sessionType=null;
			if(selectedSessionType!=null&&!selectedSessionType.isEmpty()){
				sessionType=SessionType.findById(SessionType.class,Long.parseLong(selectedSessionType));				
			}else{
				logger.error("**** Check request parameter 'sessionType' for null value ****");
				model.addAttribute("errorcode","selectedSessionTypeIsNull");
			}
			
			/**** Session,Registry No,Start Time,End Time,Day****/
			Session selectedSession=null;
			if(houseType!=null&&selectedYear!=null&&sessionType!=null){
				try {
					selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				} catch (ELSException e1) {
					model.addAttribute("error", e1.getParameter());
					e1.printStackTrace();
				}
				if(selectedSession!=null){
					model.addAttribute("session",selectedSession.getId());
					/**** Next Roster Entry In Current Session (Registry Number) ****/
					Roster lastCreatedRoster=Roster.findLastCreated(selectedSession,language,locale);
					if(lastCreatedRoster!=null){
						domain.setDay(lastCreatedRoster.getDay()+1);
						domain.setRegisterNo(lastCreatedRoster.getRegisterNo());
						/**** get the date part ****/
						SimpleDateFormat format=FormaterUtil.getDateFormatter("en_US");
						String strStartTime=format.format(lastCreatedRoster.getStartTime());
						Date startDate=null;
						try {
							startDate=format.parse(strStartTime);
							Date nextDate=selectedSession.getNextSessionDate(startDate, 1, locale);
							if(nextDate!=null){
								SimpleDateFormat localizedFormat=FormaterUtil.getDateFormatter(domain.getLocale());
								String formattedNextDate=localizedFormat.format(nextDate);
								CustomParameter localizedStartTime=CustomParameter.findByName(CustomParameter.class,"SESSION_START_TIME_"+houseType.getType().toUpperCase(), locale);
								CustomParameter localizedEndTime=CustomParameter.findByName(CustomParameter.class,"SESSION_END_TIME_"+houseType.getType().toUpperCase(), locale);
								if(localizedStartTime!=null){
									model.addAttribute("startTime",formattedNextDate+" "+localizedStartTime.getValue());
								}else{
									model.addAttribute("startTime",formattedNextDate+" "+"11:00");
								}
								if(localizedEndTime!=null){
									model.addAttribute("endTime",formattedNextDate+" "+localizedEndTime.getValue());
								}else{
									model.addAttribute("endTime",formattedNextDate+" "+"18:00");
								}
							}

						} catch (ParseException e) {
							logger.error("Unparsable Date:",e);
						}

					}else{
						SimpleDateFormat localizedFormat=FormaterUtil.getDateFormatter(domain.getLocale());
						String formattedNextDate=localizedFormat.format(selectedSession.getStartDate());
						CustomParameter localizedStartTime=CustomParameter.findByName(CustomParameter.class,"SESSION_START_TIME_"+houseType.getType().toUpperCase(), locale);
						CustomParameter localizedEndTime=CustomParameter.findByName(CustomParameter.class,"SESSION_END_TIME_"+houseType.getType().toUpperCase(), locale);
						if(localizedStartTime!=null){
							model.addAttribute("startTime",formattedNextDate+" "+localizedStartTime.getValue());
						}else{
							model.addAttribute("startTime",formattedNextDate+" "+"11:00");
						}
						if(localizedEndTime!=null){
							model.addAttribute("endTime",formattedNextDate+" "+localizedEndTime.getValue());
						}else{
							model.addAttribute("endTime",formattedNextDate+" "+"18:00");
						}
						/**** First Roster Entry In Selected Session(Registry No.) ****/
						Session previousSession = null;
						try {
							previousSession = Session.findPreviousSession(selectedSession);
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
						if(previousSession!=null){
							Roster lastCreatedRosterPreviouseSession=Roster.findLastCreated(previousSession,language,locale);
							if(lastCreatedRosterPreviouseSession!=null){
								domain.setRegisterNo(lastCreatedRosterPreviouseSession.getRegisterNo()+1);
							}
						}	
						domain.setDay(1);
					}					
				}else{
					logger.error("**** Session is null ****");
					model.addAttribute("errorcode","selectedSessionIsNull");	
				}
			}
		}

		/**** Slot Duration ****/
		CustomParameter slotDurationParam=CustomParameter.findByName(CustomParameter.class,"SLOT_DURATION_"+houseType.getType().toUpperCase(),"");
		if(slotDurationParam!=null){
			String[] slotDurationArr=slotDurationParam.getValue().split(",");
			if(slotDurationArr.length==3){
				model.addAttribute("start", slotDurationArr[0]);
				model.addAttribute("step", slotDurationArr[1]);
				model.addAttribute("end", slotDurationArr[2]);
			}else{
				model.addAttribute("start", 0);
				model.addAttribute("step", 1);
				model.addAttribute("end", 1);
			}
		}
		if(domain.getSlotDuration()!=null){
			model.addAttribute("slotDuration",domain.getSlotDuration());
		}



		/**** Users ****/
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"RIS_ROLES_ALLOTED_TO_SLOT","");
		if(customParameter!=null){
			List<User> allRISUsers=new ArrayList<User>();
			List<User> houseTypeUsers=new ArrayList<User>();
			if(houseType!=null){
				if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
					allRISUsers=User.findByRole(false,customParameter.getValue(),language.getName(),"houseType,joiningDate,lastName",ApplicationConstants.ASC+","+ApplicationConstants.ASC+","+ApplicationConstants.ASC,locale,"");
					houseTypeUsers=User.findByRole(false,customParameter.getValue(),language.getName(),"houseType,joiningDate,lastName",ApplicationConstants.ASC+","+ApplicationConstants.ASC+","+ApplicationConstants.ASC,locale,houseType.getType());
				}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
					allRISUsers=User.findByRole(false,customParameter.getValue(),language.getName(),"houseType,joiningDate,lastName",ApplicationConstants.DESC+","+ApplicationConstants.ASC+","+ApplicationConstants.ASC,locale,"");
					houseTypeUsers=User.findByRole(false,customParameter.getValue(),language.getName(),"houseType,joiningDate,lastName",ApplicationConstants.ASC+","+ApplicationConstants.ASC+","+ApplicationConstants.ASC,locale,houseType.getType());
				}
			}
			model.addAttribute("eligibles", allRISUsers);
			/**** houseType users will be displayed in anticlockwise direction ****/
			List<User> toBeSentToBottom=new ArrayList<User>();
			List<User> toBeSentToTop=new ArrayList<User>();
			int day=domain.getDay();
			for(int i=0;i<houseTypeUsers.size();i++){
				if(i<day-1){
					toBeSentToBottom.add(houseTypeUsers.get(i));
				}else{
					toBeSentToTop.add(houseTypeUsers.get(i));
				}				
			}
			List<User> selectedUsers=new ArrayList<User>();
			selectedUsers.addAll(toBeSentToTop);
			selectedUsers.addAll(toBeSentToBottom);
			List<User> notSelectedUsers=new ArrayList<User>();
			if(selectedUsers!=null&&!selectedUsers.isEmpty()){
				model.addAttribute("selectedItemsCount",selectedUsers.size());	
				for(User i:allRISUsers){
					boolean contains=false;
					for(User j:selectedUsers){
						if(i.getId()==j.getId()){
							contains=true;
							break;
						}						
					}
					if(!contains){
						notSelectedUsers.add(i);
					}
				}
				model.addAttribute("selectedItems",selectedUsers);
				model.addAttribute("allItems", notSelectedUsers);
				model.addAttribute("allItemsCount", notSelectedUsers.size());
			}else{
				model.addAttribute("selectedItemsCount",0);	
				model.addAttribute("selectedItems",selectedUsers);
				model.addAttribute("allItems", allRISUsers);
				model.addAttribute("allItemsCount", allRISUsers.size());
			}				
		}	
	}

	@Override
	protected void populateEdit(ModelMap model, Roster domain,
			HttpServletRequest request) {		
		
		CommitteeMeeting committeeMeeting = domain.getCommitteeMeeting();
		if(committeeMeeting != null){
			model.addAttribute("committeeMeeting", committeeMeeting.getId());
		}else{
			model.addAttribute("committeeMeeting","");
		}
		
		/**** Session ****/
		Session selectedSession = domain.getSession();
		if(selectedSession != null){
			model.addAttribute("session",selectedSession.getId());	
		}
		

		/**** Start Time and End Time ****/
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_DATETIMEFORMAT","");
		if(customParameter!=null){
			SimpleDateFormat localizedFormat=FormaterUtil.getDateFormatter(customParameter.getValue(), domain.getLocale());
			SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(), "en_US");

			if(domain.getStartTime()!=null){
				model.addAttribute("startTime",localizedFormat.format(domain.getStartTime()));
				model.addAttribute("defaultStartTime",format.format(domain.getStartTime()));
			}
			if(domain.getEndTime()!=null){
				model.addAttribute("endTime",localizedFormat.format(domain.getEndTime()));
				model.addAttribute("defaultEndTime",format.format(domain.getEndTime()));
			}
			if(domain.getSlotDurationChangedFrom()!=null){
				model.addAttribute("slotDurationChangedFrom",localizedFormat.format(domain.getSlotDurationChangedFrom()));
			}
			if(domain.getReporterChangedFrom()!=null){
				model.addAttribute("reporterChangedFrom",localizedFormat.format(domain.getReporterChangedFrom()));
			}
		}

		/**** Language ****/
		if(domain.getLanguage()!=null){
			model.addAttribute("language",domain.getLanguage().getId());
		}

		String strHouseType = "";
		if(domain.getCommitteeMeeting() != null){
			strHouseType = this.getCurrentUser().getHouseType();
		}else{
			strHouseType = domain.getSession().getHouse().getType().getType();
		}
		/**** Users ****/
		CustomParameter customParameter1=CustomParameter.findByName(CustomParameter.class,"RIS_ROLES_ALLOTED_TO_SLOT","");
		if(customParameter1!=null){
			//HouseType houseType=selectedSession.getHouse().getType();
			List<User> allRISUsers=new ArrayList<User>();
			allRISUsers=User.findByRole(false,customParameter1.getValue(),domain.getLanguage().getName(),"houseType,joiningDate,lastName",ApplicationConstants.ASC+","+ApplicationConstants.ASC+","+ApplicationConstants.ASC,domain.getLocale(),"");
			//if(houseType!=null){
//				if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
//					allRISUsers=User.findByRole(false,customParameter1.getValue(),domain.getLanguage().getName(),"houseType,joiningDate,lastName",ApplicationConstants.ASC+","+ApplicationConstants.ASC+","+ApplicationConstants.ASC,domain.getLocale(),"");
//				}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
//					allRISUsers=User.findByRole(false,customParameter1.getValue(),domain.getLanguage().getName(),"houseType,joiningDate,lastName",ApplicationConstants.DESC+","+ApplicationConstants.ASC+","+ApplicationConstants.ASC,domain.getLocale(),"");
//				}
			//}
			List<Reporter> reporters=domain.getReporters();
			List<Integer> allRISUsersPositions=new ArrayList<Integer>();
			model.addAttribute("eligibles", allRISUsers);
			for(User u:allRISUsers){
				boolean got = false;
				for(Reporter j:reporters){
					if(j.getUser().getId() == u.getId()){
						allRISUsersPositions.add(j.getPosition());
						got = true;
						break;
					}
				}
				if(!got){
					allRISUsersPositions.add(null);
				}
			}
			
			model.addAttribute("allRisUserPositions", allRISUsersPositions);
			List<User> selectedUsers=new ArrayList<User>();
			List<User> notSelectedUsers=new ArrayList<User>();
			List<Integer> selectedUserPositions = new ArrayList<Integer>();
			List<Integer> nonSelectedUserPositions = new ArrayList<Integer>();
			/**** Here add all the selected users i.e. reporters selected in the previous slot ****/
			if(reporters!=null&&!reporters.isEmpty()){
				for(Reporter i:reporters){
					if(i.getIsActive()){
						selectedUsers.add(i.getUser());
						selectedUserPositions.add(i.getPosition());
					}
				}
			}
			/**** Here find the user who was not selected in the previous slot ****/
			if(selectedUsers!=null&&!selectedUsers.isEmpty()){
				model.addAttribute("selectedItemsCount",selectedUsers.size());	
				for(User i:allRISUsers){
					boolean contains=false;
					for(User j:selectedUsers){
						if(i.getId()==j.getId()){
							contains=true;//indicates whether user is selected
							break;
						}						
					}
					
					boolean addedPosition = false;//indicates whether the current user in the context
													// who is not selected having position/not having position
													// is added to nonSelectedUserPosition
					/****Id user is not selected then check for if its an active reporter and add its position
					 * to nonSelectedUsers as well as its position
					 */
					if(!contains){
						for(Reporter j:reporters){
							//If user is reporter i.e. previously selected user
							if(j.getUser().getId() == i.getId()){
								
								if(!j.getIsActive()){
									nonSelectedUserPositions.add(j.getPosition());
									addedPosition = true;
								}
								break;
							}
						}
						//if user was not in the previous slot i.e. not reporter
						//then also add its position as null
						//so as to match the positional value
						if(!addedPosition){
							nonSelectedUserPositions.add(null);
						}
						notSelectedUsers.add(i);
					}
				}
				
				model.addAttribute("selectedItemsCount",selectedUsers.size());
				model.addAttribute("selectedItems",selectedUsers);
				model.addAttribute("selectedUserPositions", selectedUserPositions);
				
				model.addAttribute("nonSelectedUserPositions", nonSelectedUserPositions);
				
				model.addAttribute("allItems", notSelectedUsers);
				model.addAttribute("allItemsCount", notSelectedUsers.size());
			}else{
				model.addAttribute("selectedItemsCount",0);	
				model.addAttribute("selectedItems",selectedUsers);
				model.addAttribute("allItems", allRISUsers);
				model.addAttribute("allItemsCount", allRISUsers.size());
			}				
		}

		/**** Slot Duration ****/
		
		CustomParameter slotDurationParam=CustomParameter.findByName(CustomParameter.class,"SLOT_DURATION_" + strHouseType.toUpperCase(), "");
		if(slotDurationParam!=null){
			String[] slotDurationArr=slotDurationParam.getValue().split(",");
			if(slotDurationArr.length==3){
				model.addAttribute("start", slotDurationArr[0]);
				model.addAttribute("step", slotDurationArr[1]);
				model.addAttribute("end", slotDurationArr[2]);
			}else{
				model.addAttribute("start", 0);
				model.addAttribute("step", 1);
				model.addAttribute("end", 1);
			}
		}
		if(domain.getSlotDuration()!=null){
			model.addAttribute("slotDuration",domain.getSlotDuration());
		}
		/**** Slot Has been created ****/
		if(Roster.slotsAlreadyCreated(domain)){
			model.addAttribute("slots_created","yes");
		}else{
			model.addAttribute("slots_created","no");
		}
		
		if(!domain.getReporters().isEmpty()){
			List<Reporter> reporters=Roster.findReportersByActiveStatus(domain, true);
			model.addAttribute("reporterSize", reporters.size());
		}
	}

	@Override
	protected void customValidateCreate(Roster domain, BindingResult result,
			HttpServletRequest request) {
		/**** Populate Reporters ****/
		String[] usersArr=request.getParameterValues("selectedItems");
		List<Reporter> reporters=new ArrayList<Reporter>();
		int position=1;
		if(usersArr!=null&&usersArr.length>0){
			for(String i:usersArr){
				User user=User.findById(User.class,Long.parseLong(i));
				Reporter reporter=new Reporter();
				reporter.setUser(user);
				reporter.setPosition(position);
				reporter.setIsActive(true);
				reporter.setLocale(domain.getLocale());
				reporter.persist();
				reporters.add(reporter);
				position++;
			}
			domain.setReporters(reporters);
		}	
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
		}		
		/**** Session ****/
		if(domain.getCommitteeMeeting()==null && domain.getSession()==null){
			result.rejectValue("session","SessionEmpty");
		}
		/**** Registry No.****/
		if(domain.getCommitteeMeeting()==null && domain.getRegisterNo()==null){
			result.rejectValue("registerNo","RegistryEmpty");
		}		
		/**** Start Time and End Time****/
		String strStartTime=request.getParameter("selectedStartTime");
		String strEndTime=request.getParameter("selectedEndTime");
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_DATETIMEFORMAT","");
		if(strStartTime!=null&&!strStartTime.isEmpty()){			
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setStartTime(format.parse(strStartTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setStartTime(defaultFormat.parse(strStartTime));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strStartTime+","+strEndTime,e1);;
					}
				}
			}
		}
		if(strEndTime!=null&&!strEndTime.isEmpty()){
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setEndTime(format.parse(strEndTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setEndTime(defaultFormat.parse(strEndTime));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strStartTime+","+strEndTime,e1);;
					}
				}
			}
		}
		if(domain.getStartTime()==null){
			result.rejectValue("startTime","StartTimeEmpty");
		}
		/**** Start Time can be changed only if it is a future date ****/
//		if(domain.getStartTime()!=null&&domain.getStartTime().before(new Date()) && domain.getCommitteeMeeting()==null){
//			result.rejectValue("startTime","StartTimeIsPastTime");
//		}
		if(domain.getEndTime()==null){
			result.rejectValue("endTime","EndTimeEmpty");
		}		
		/**** End Time can be changed only if it is a future date ****/
//		if(domain.getEndTime()!=null&&domain.getEndTime().before(new Date()) && domain.getCommitteeMeeting()==null){
//			result.rejectValue("endTime","EndTimeIsPastTime");
//		}
		/**** Start Time cannot be > end time ****/
		if(domain.getStartTime()!=null&&domain.getEndTime()!=null
				&&domain.getStartTime().after(domain.getEndTime())){
			result.rejectValue("startTime","StartTimeGTEndTime");
		}
		/**** End Time cannot be < start time ****/
		if(domain.getStartTime()!=null&&domain.getEndTime()!=null
				&&domain.getEndTime().before(domain.getStartTime())){
			result.rejectValue("endTime","EndTimeLTStartTime");
		}
		/**** Slot Duration ****/
		if(domain.getSlotDuration()==null){
			result.rejectValue("slotDuration","SlotDurationEmpty");
		}
		if(domain.getSlotDuration()!=null&&domain.getSlotDuration()==0){
			result.rejectValue("slotDuration","SlotDurationEmpty");
		}
		/**** Action ****/
		if(domain.getAction()==null){
			result.rejectValue("action","ActionEmpty");
		}else if(domain.getAction().isEmpty()){
			result.rejectValue("action","ActionEmpty");
		}else if(domain.getAction().equals("-")){
			result.rejectValue("action","ActionEmpty");
		}	
		/**** Reporters ****/
		if(domain.getReporters()==null){
			result.rejectValue("action","ReporterEmpty");
		}else if(domain.getReporters().isEmpty()){
			result.rejectValue("action","ReporterEmpty");
		}	
		/**** Day ****/
		if(domain.getDay()==null){
			result.rejectValue("day","DayEmpty");
		}else if(domain.getDay()!=null&&domain.getDay()==0){
			result.rejectValue("day","DayEmpty");
		}else if(domain.getDay()!=null&&domain.getDay()<0){
			result.rejectValue("day","DayLessThanZero");
		}
		/**** Language ****/
		if(domain.getLanguage()==null){
			result.rejectValue("language","LanguageEmpty");
		}
	}

	@Override
	protected void customValidateUpdate(Roster domain, BindingResult result,
			HttpServletRequest request) {
		Roster roster=Roster.findById(Roster.class,domain.getId());			
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
		}		
		/**** Session ****/
		/*if(domain.getSession()==null){
			result.rejectValue("session","SessionEmpty");
		}*/
		/**** Registry No.****/
		/*if(domain.getRegisterNo()==null){
			result.rejectValue("registerNo","RegistryEmpty");
		}*/		
		/**** Start Time and End Time****/
		String strStartTime=request.getParameter("selectedStartTime");
		String strEndTime=request.getParameter("selectedEndTime");
		String strSlotTimeChangedFrom=request.getParameter("selectedSlotDurationChangedFrom");
		String strreporterChangedFrom=request.getParameter("selectedReporterChangedFrom");
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_DATETIMEFORMAT","");
		if(strStartTime!=null&&!strStartTime.isEmpty()){			
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setStartTime(format.parse(strStartTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setStartTime(defaultFormat.parse(strStartTime));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strStartTime,e1);;
					}
				}
			}
		}
		if(strEndTime!=null&&!strEndTime.isEmpty()){
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setEndTime(format.parse(strEndTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setEndTime(defaultFormat.parse(strEndTime));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strEndTime,e1);;
					}
				}
			}
		}
		if(strSlotTimeChangedFrom!=null&&!strSlotTimeChangedFrom.isEmpty()){
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setSlotDurationChangedFrom(format.parse(strSlotTimeChangedFrom));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setSlotDurationChangedFrom(defaultFormat.parse(strSlotTimeChangedFrom));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strSlotTimeChangedFrom,e1);;
					}
				}
			}
		}
		if(strreporterChangedFrom!=null&&!strreporterChangedFrom.isEmpty()){
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setReporterChangedFrom(format.parse(strreporterChangedFrom));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setReporterChangedFrom(defaultFormat.parse(strreporterChangedFrom));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strreporterChangedFrom,e1);;
					}
				}
			}
		}
		if(domain.getStartTime()==null){
			result.rejectValue("startTime","StartTimeEmpty");
		}
		/**** Start Time can be changed only if it is a future date ****/
		/*if(domain.getStartTime()!=null&&domain.getStartTime().before(new Date())){
			result.rejectValue("startTime","StartTimeIsPastTime");
		}*/
		if(domain.getEndTime()==null){
			result.rejectValue("endTime","EndTimeEmpty");
		}		
		/**** End Time can be changed only if it is a future date ****/
//		if(domain.getEndTime()!=null&&domain.getEndTime().before(new Date()) && domain.getCommitteeMeeting()==null){
//			result.rejectValue("endTime","EndTimeIsPastTime");
//		}
		/**** Start Time cannot be > end time ****/
		if(domain.getStartTime()!=null&&domain.getEndTime()!=null
				&&domain.getStartTime().after(domain.getEndTime())){
			result.rejectValue("startTime","StartTimeGTEndTime");
		}
		/**** End Time cannot be < start time ****/
		if(domain.getStartTime()!=null&&domain.getEndTime()!=null
				&&domain.getEndTime().before(domain.getStartTime())){
			result.rejectValue("endTime","EndTimeLTStartTime");
		}
		/**** Slot Duration ****/
		if(domain.getSlotDuration()==null){
			result.rejectValue("slotDuration","SlotDurationEmpty");
		}
		if(domain.getSlotDuration()!=null&&domain.getSlotDuration()==0){
			result.rejectValue("slotDuration","SlotDurationEmpty");
		}
		if(domain.getSlotDuration()!=null
				&&!(roster.getSlotDuration().equals(domain.getSlotDuration()))
				&&domain.getSlotDurationChangedFrom()==null){
			result.rejectValue("slotDurationChangedFrom","SlotDurationChangedFromEmpty");
		}
		/*if(domain.getSlotDuration()!=null
				&&!(roster.getSlotDuration().equals(domain.getSlotDuration()))
				&&domain.getSlotDurationChangedFrom()!=null
				&&domain.getSlotDurationChangedFrom().before(new Date())){
			result.rejectValue("slotDurationChangedFrom","SlotDurationChangedFromIsPastTime");
		}*/
		if(domain.getSlotDuration()!=null
				&&!(roster.getSlotDuration().equals(domain.getSlotDuration()))
				&&domain.getSlotDurationChangedFrom()!=null
				&&domain.getSlotDurationChangedFrom().before(domain.getStartTime())){
			result.rejectValue("slotDurationChangedFrom","SlotDurationChangedFromIsLTStartTime");
		}
		if(domain.getSlotDuration()!=null
				&&!(roster.getSlotDuration().equals(domain.getSlotDuration()))
				&&domain.getSlotDurationChangedFrom()!=null
				&&domain.getSlotDurationChangedFrom().after(domain.getEndTime())){
			result.rejectValue("slotDurationChangedFrom","SlotDurationChangedFromIsGTEndTime");
		}
		/**** Action ****/
		if(domain.getAction()==null){
			result.rejectValue("action","ActionEmpty");
		}else if(domain.getAction().isEmpty()){
			result.rejectValue("action","ActionEmpty");
		}else if(domain.getAction().equals("-")){
			result.rejectValue("action","ActionEmpty");
		}			
		/**** Day ****/
		if(domain.getDay()==null){
			result.rejectValue("day","DayEmpty");
		}else if(domain.getDay()!=null&&domain.getDay()==0){
			result.rejectValue("day","DayEmpty");
		}else if(domain.getDay()!=null&&domain.getDay()<0){
			result.rejectValue("day","DayLessThanZero");
		}
		/**** Language ****/
		if(domain.getLanguage()==null){
			result.rejectValue("language","LanguageEmpty");
		}
		/**** Populate Reporters ****/
		String slotsCreated=request.getParameter("slots_created");
		if(slotsCreated!=null&&!slotsCreated.isEmpty()){
			String[] usersArr=request.getParameterValues("selectedItems");
			List<Reporter> reporters=new ArrayList<Reporter>();
			List<Long> originalReporters=new ArrayList<Long>();
			/**** We will first get the position of the last reporter of the stored roster whose
			 * id is same as that of domain.This will then give the position from which position
			 * of newly added reporters will be set****/
			int lastReporterPosition=1;
			if(slotsCreated.equals("yes")){
				Reporter lastReporter=null;
				if(roster!=null&&roster.getReporters()!=null&&!roster.getReporters().isEmpty()){
					lastReporter=Roster.findFirstReporterAtPosX(roster,roster.getReporters().size(),"both");
				}
				if(lastReporter!=null){
					lastReporterPosition=lastReporter.getPosition()+1;				
				}
			}
			if(usersArr!=null&&usersArr.length>0){
				for(String i:usersArr){
					User user=User.findById(User.class,Long.parseLong(i));
					Reporter reporter=Roster.findByUser(domain,user);
					if(reporter==null){
						reporter=new Reporter();
						reporter.setUser(user);
						reporter.setPosition(lastReporterPosition);
						reporter.setIsActive(true);
						reporter.setLocale(domain.getLocale());
						reporter.persist();
						lastReporterPosition++;
					}else{
						/**** This will be used to fetch those reporters who
						 * were present in original list and now have been disabled
						 */
						reporter.setIsActive(true);
						if(slotsCreated.equals("no")){
							reporter.setPosition(lastReporterPosition);
							lastReporterPosition++;
						}
						reporter.merge();
						originalReporters.add(reporter.getId());
					}
					reporters.add(reporter);
				}	
				/**** Reporters that were present in original list and now have been made inactive ****/
				List<Reporter> toBeDisabledReporters=Roster.findReportersOtherThan(domain,originalReporters);
				for(Reporter i:toBeDisabledReporters){
					if(slotsCreated.equals("yes")){
						i.setIsActive(false);
						i.merge();
						reporters.add(i);				
					}else{
						i.remove();
					}
				}				
				domain.setReporters(reporters);
			}
		}
		/**** Reporters ****/
		if(domain.getReporters()==null){
			result.rejectValue("action","ReporterEmpty");
		}else if(domain.getReporters().isEmpty()){
			result.rejectValue("action","ReporterEmpty");
		}	
	}
	
	@Override
	protected void populateCreateIfNoErrors(ModelMap model, Roster domain,
			HttpServletRequest request) {
		/** set 'handled by' for the roster **/
		domain.setHandledBy(this.getCurrentUser().getActualUsername());
	}

	@Override
	protected void populateAfterCreate(ModelMap model, Roster domain,
			HttpServletRequest request) {
		if(!domain.getAction().equals("save_without_creating_slots")){
			String reporterAction=request.getParameter("reporterAction");
			Boolean status=domain.generateSlot(reporterAction);		
			CommitteeMeeting committeeMeeting = domain.getCommitteeMeeting();
			if(committeeMeeting!=null) {
				if(committeeMeeting.getId()!=null) {
					NotificationController.sendCommitteeMeetingRosterEntryNotification(domain, domain.getLocale());
				}
			}
		}
	}

	@Override
	protected void populateUpdateIfNoErrors(ModelMap model, Roster domain,
			HttpServletRequest request) {
		if(!domain.getAction().equals("save_without_creating_slots")){
			String reporterAction=request.getParameter("reporterAction");
			Boolean status=domain.generateSlot(reporterAction);		
		}
		/** set 'handled by' for the roster **/
		domain.setHandledBy(this.getCurrentUser().getActualUsername());
	}
	
	@Override
	protected void populateAfterUpdate(ModelMap model, Roster domain,
			HttpServletRequest request) {
		if(!domain.getAction().equals("save_without_creating_slots")){
			CommitteeMeeting committeeMeeting = domain.getCommitteeMeeting();
			if(committeeMeeting!=null) {
				if(committeeMeeting.getId()!=null) {
					NotificationController.sendCommitteeMeetingRosterEntryNotification(domain, domain.getLocale());
				}
			}		
			
		}
	}
	
	
	@RequestMapping(value="/{id}/roster_rep", method=RequestMethod.GET)
	public String viewRoster(final @PathVariable("id") Long id, final HttpServletRequest request, final ModelMap model, final Locale locale){
		String returnValue = "roster/error"; 
		try{
			model.addAttribute("rosterId", id);
			
			Roster roster = Roster.findById(Roster.class, id);
			Map<String, String[]> parametersMap = null;
			
			parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("rosterId", new String[]{id.toString()});
			parametersMap.put("startTime", new String[]{roster.getStartTime().toString()});
			parametersMap.put("endTime", new String[]{roster.getEndTime().toString()});
			
			List report = org.mkcl.els.domain.Query.findReport("RIS_ROSTER_REPORT_ROSTER", parametersMap);
			model.addAttribute("report", report);
			model.addAttribute("committeName", roster.getCommitteeMeeting().getCommittee().getCommitteeName().getDisplayName());
			
			model.addAttribute("rosterDate", FormaterUtil.formatDateToString(roster.getStartTime(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
			
			Map reportFields = simplifyRosterSlotReport(report);
			
			//generate report
			generateReportUsingFOP(new Object[]{reportFields}, "template_roster", "PDF", "roster slots report", locale.toString());
			returnValue = "roster/rosterreport";
					
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		return returnValue;
	}
	
	
	@RequestMapping(value="/roster_totalWorkRep", method=RequestMethod.GET)
	public String viewTotalWorkRep( final HttpServletRequest request, final ModelMap model, final Locale locale){
		String returnValue = "roster/error"; 
		try{

			/**** Locale ****/
	

			/**** House Type ****/
			String selectedHouseType=request.getParameter("houseType");
			HouseType houseType=null;
			if(selectedHouseType!=null&&!selectedHouseType.isEmpty()){
				try {
					Long houseTypeId=Long.parseLong(selectedHouseType);
					houseType=HouseType.findById(HouseType.class,houseTypeId);
				} catch (NumberFormatException e) {
					houseType=HouseType.findByFieldName(HouseType.class,"type",selectedHouseType, locale.toString());
				}			
			}else{
				logger.error("**** Check request parameter 'houseType' for null value ****");
				model.addAttribute("errorcode","selectedHouseTypeIsNull");
			}
			
				
				/**** Session Year ****/
				String selectedYear=request.getParameter("sessionYear");
				Integer sessionYear=0;
				if(selectedYear!=null&&!selectedYear.isEmpty()){			
					sessionYear=Integer.parseInt(selectedYear);		
				}else{
					logger.error("**** Check request parameter 'sessionYear' for null value ****");
					model.addAttribute("errorcode","selectedSessionYearIsNull");
				}  

				/**** Session Type ****/
				String selectedSessionType=request.getParameter("sessionType");
				SessionType sessionType=null;
				if(selectedSessionType!=null&&!selectedSessionType.isEmpty()){
					sessionType=SessionType.findById(SessionType.class,Long.parseLong(selectedSessionType));				
				}else{
					logger.error("**** Check request parameter 'sessionType' for null value ****");
					model.addAttribute("errorcode","selectedSessionTypeIsNull");
				}
				
				/**** Language ****/
				String selectedLanguage=request.getParameter("language");
				Language language=null;
				if(selectedLanguage!=null&&!selectedLanguage.isEmpty()){
					language=Language.findById(Language.class,Long.parseLong(selectedLanguage));
					model.addAttribute("language",language.getId());
				}else{
					logger.error("**** Check request parameter 'sessionType' for null value ****");
					model.addAttribute("errorcode","selectedSessionTypeIsNull");
				}
						
				Session selectedSession=null;
				if(houseType!=null&&selectedYear!=null&&sessionType!=null){
					try {
						selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					} catch (ELSException e1) {
						model.addAttribute("error", e1.getParameter());
						e1.printStackTrace();
					}
				}
			/**** find report data ****/
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			queryParameters.put("locale", new String[]{locale.toString()});
			queryParameters.put("sessionId", new String[]{selectedSession.getId().toString()});
			queryParameters.put("languageId", new String[]{selectedLanguage});
			
	
			List<Object[]> reportData = Query.findReport("RIS_ROSTER_TOTALWORK_REPORT_ROSTER", queryParameters);
			
			model.addAttribute("report", reportData);
			List<Object[]> reportData1 = Query.findReport("RIS_ROSTER_AVGTOTALWORK_REPORT_ROSTER", queryParameters);
			model.addAttribute("report1", reportData1);
			
			model.addAttribute("sessionId", selectedSession.getId().toString());
			//generate report
			//generateReportUsingFOP(new Object[]{reportData}, "template_ris_totalwork", "PDF", "Total Work Report", locale.toString());
			returnValue = "roster/totalworkreport";
					
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		return returnValue;
	}
	
	

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@RequestMapping(value="/viewAdhawa", method = RequestMethod.GET)
		public void houseitemreport(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
			
			//String retVal = "motion/report";
			try{
				
	
				String strReportFormat = request.getParameter("outputFormat");	
				
				
					Map<String, String[]> parameters = request.getParameterMap();
					
					List reportData = Query.findReport(request.getParameter("reportQuery"), parameters);	
					List reportData1 = Query.findReport(request.getParameter("reportQuery")+"_EXTRA_DETAILS", parameters);	
					String templateName = request.getParameter("templateName");
					File reportFile = null;				
					
					reportFile = generateReportUsingFOP(new Object[] {((Object[])reportData.get(0))[0], reportData,reportData1}, templateName, strReportFormat, request.getParameter("reportName"), locale.toString());
					openOrSaveReportFileFromBrowser(response, reportFile, strReportFormat);
					
					model.addAttribute("info", "general_info");;
					//retVal = "motion/info";
						
			}catch(Exception e){
				logger.error("error", e);
			}
			
			//return retVal;
		}
	private Map simplifyRosterSlotReport(final List report){
		Map<String, List> rosterData = new LinkedHashMap<String, List>();
		String repName = "";
		List<List<Object>> rosterSlotsForReporter = new ArrayList<List<Object>>();
		int index = 0;
		for(Object o: report){
			Object[] objArr = (Object[])o;
			List<Object> slotFields = new ArrayList<Object>();
			if(!repName.equals(objArr[0].toString())){
				if(!repName.isEmpty()){
					rosterData.put(repName, rosterSlotsForReporter);
					rosterSlotsForReporter = null;
					rosterSlotsForReporter = new ArrayList<List<Object>>();
				}
				repName = objArr[0].toString(); 						
				slotFields.add(objArr[1]);
				slotFields.add(objArr[2]);
				rosterSlotsForReporter.add(slotFields);
				
			}else{
				slotFields.add(objArr[1]);
				slotFields.add(objArr[2]);
				rosterSlotsForReporter.add(slotFields);
			}
			slotFields = null;
		}
		
		rosterData.put(repName, rosterSlotsForReporter);
		
		return rosterData;
	}
	
	@RequestMapping(value="/{id}/publish", method=RequestMethod.POST)
	public @ResponseBody Boolean publishRoster(final @PathVariable("id") Long id, final HttpServletRequest request, final ModelMap model, final Locale locale){
		Boolean returnValue = false;
		try{
			Roster roster = Roster.findById(Roster.class, id);
			roster.setPublish(true);
			roster.setPublishedDate(new Date());
			roster.merge();
			returnValue = true;
					
		}catch (Exception e) {
			returnValue = false;
			e.printStackTrace();
		}
		return returnValue;
	}

}
