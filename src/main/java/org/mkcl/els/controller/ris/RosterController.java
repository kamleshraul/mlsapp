package org.mkcl.els.controller.ris;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

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
			model.addAttribute("error", e.getParameter());
			e.printStackTrace();
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
		List<Language> languages;
		try {
			languages = Language.findAllLanguagesByModule("RIS",locale);
			model.addAttribute("languages",languages);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
			e.printStackTrace();
		}
		
	}

	@Override
	protected void populateNew(final ModelMap model, final Roster domain, final String locale,
			final HttpServletRequest request) {
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

		/**** Users ****/
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"RIS_ROLES_ALLOTED_TO_SLOT","");
		if(customParameter!=null){
			List<User> allRISUsers;
			try {
				allRISUsers = User.findByRole(false,customParameter.getValue(),language.getName(),locale);
				model.addAttribute("eligibles", allRISUsers);		
				List<User> selectedUsers=domain.getUsers();
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
							if(!contains){
								notSelectedUsers.add(i);
							}
						}
					}
					model.addAttribute("selectedItemsCount",0);	
					model.addAttribute("selectedItems",selectedUsers);
					model.addAttribute("allItems", notSelectedUsers);
					model.addAttribute("allItemsCount", notSelectedUsers.size());
				}else{
					model.addAttribute("selectedItemsCount",0);	
					model.addAttribute("selectedItems",selectedUsers);
					model.addAttribute("allItems", allRISUsers);
					model.addAttribute("allItemsCount", allRISUsers.size());
				}
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
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

		/**** Session,Registry No,Start Time,End Time****/
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
				Roster lastCreatedRoster = null;
				try {
					lastCreatedRoster = Roster.findLastCreated(selectedSession,locale);
				} catch (ELSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(lastCreatedRoster!=null){
					domain.setRegisterNo(lastCreatedRoster.getRegisterNo());
					/**** get the date part ****/
					SimpleDateFormat format=FormaterUtil.getDateFormatter("en_US");
					String strStartTime=format.format(lastCreatedRoster.getStartTime());
					Date startDate=null;
					try {
						startDate=format.parse(strStartTime);
						Date nextDate=Holiday.getNextSessionDate(selectedSession,startDate, 1, locale);
						if(nextDate!=null
								&&selectedSession.getStartDate()!=null
								&&selectedSession.getEndDate()!=null
								&&(nextDate.after(selectedSession.getStartDate())
										||nextDate.equals(selectedSession.getStartDate()))
										&&(nextDate.before(selectedSession.getEndDate())
												||nextDate.equals(selectedSession.getEndDate()))){
							SimpleDateFormat localizedFormat=FormaterUtil.getDateFormatter(domain.getLocale());
							String formattedNextDate=localizedFormat.format(nextDate);
							CustomParameter localizedStartTime=CustomParameter.findByName(CustomParameter.class,"SESSION_START_TIME_"+houseType.getType().toUpperCase(), locale);
							CustomParameter localizedEndTime=CustomParameter.findByName(CustomParameter.class,"SESSION_END_TIME_"+houseType.getType().toUpperCase(), locale);
							if(localizedStartTime!=null){
								model.addAttribute("startTime",formattedNextDate+" "+localizedStartTime.getValue());
							}else{
								model.addAttribute("startTime",formattedNextDate+" "+"11:00:00");
							}
							if(localizedEndTime!=null){
								model.addAttribute("endTime",formattedNextDate+" "+localizedEndTime.getValue());
							}else{
								model.addAttribute("endTime",formattedNextDate+" "+"18:00:00");
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
						model.addAttribute("startTime",formattedNextDate+" "+"11:00:00");
					}
					if(localizedEndTime!=null){
						model.addAttribute("endTime",formattedNextDate+" "+localizedEndTime.getValue());
					}else{
						model.addAttribute("endTime",formattedNextDate+" "+"18:00:00");
					}
					/**** First Roster Entry In Selected Session(Registry No.) ****/
					Session previousSession;
					try {
						previousSession = Session.findPreviousSession(selectedSession);
						
						if(previousSession!=null){
							Roster lastCreatedRosterPreviouseSession=Roster.findLastCreated(previousSession,locale);
							if(lastCreatedRosterPreviouseSession!=null){
								domain.setRegisterNo(lastCreatedRosterPreviouseSession.getRegisterNo()+1);
							}
						}	
						
					} catch (ELSException e) {
						model.addAttribute("error", e.getParameter());
						e.printStackTrace();
					}							
				}					
			}else{
				logger.error("**** Session is null ****");
				model.addAttribute("errorcode","selectedSessionIsNull");	
			}
		}
	}

	@Override
	protected void populateEdit(final ModelMap model, final Roster domain,
			final HttpServletRequest request) {		
		/**** Session ****/
		Session selectedSession=domain.getSession();
		model.addAttribute("session",selectedSession.getId());	

		/**** Start Time and End Time ****/
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT","");
		if(customParameter!=null){
			SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(), domain.getLocale());
			if(domain.getStartTime()!=null){
				model.addAttribute("startTime",format.format(domain.getStartTime()));
			}
			if(domain.getEndTime()!=null){
				model.addAttribute("endTime",format.format(domain.getEndTime()));
			}
		}

		/**** Language ****/
		if(domain.getLanguage()!=null){
			model.addAttribute("language",domain.getLanguage().getId());
		}

		/**** Users ****/
		CustomParameter customParameter1=CustomParameter.findByName(CustomParameter.class,"RIS_ROLES_ALLOTED_TO_SLOT","");
		if(customParameter1!=null){
			List<User> allRISUsers;
			try {
				allRISUsers = User.findByRole(false,customParameter1.getValue(),domain.getLanguage().getName(),domain.getLocale());
				model.addAttribute("eligibles", allRISUsers);		
				List<User> selectedUsers=domain.getUsers();
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
					model.addAttribute("selectedItemsCount",selectedUsers.size());	
					model.addAttribute("selectedItems",selectedUsers);
					model.addAttribute("allItems", notSelectedUsers);
					model.addAttribute("allItemsCount", notSelectedUsers.size());
				}else{
					model.addAttribute("selectedItemsCount",0);	
					model.addAttribute("selectedItems",selectedUsers);
					model.addAttribute("allItems", allRISUsers);
					model.addAttribute("allItemsCount", allRISUsers.size());
				}
				
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}				
		}

		/**** Slot Duration ****/
		CustomParameter slotDurationParam=CustomParameter.findByName(CustomParameter.class,"SLOT_DURATION_"+domain.getSession().getHouse().getType().getType().toUpperCase(),"");
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
	}

	@Override
	protected void customValidateCreate(final Roster domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
		}		
		/**** Session ****/
		if(domain.getSession()==null){
			result.rejectValue("session","SessionEmpty");
		}
		/**** Registry No.****/
		if(domain.getRegisterNo()==null){
			result.rejectValue("registerNo","RegistryEmpty");
		}		
		/**** Start Time and End Time****/
		String strStartTime=request.getParameter("selectedStartTime");
		String strEndTime=request.getParameter("selectedEndTime");
		if(strStartTime!=null&&!strStartTime.isEmpty()
				&&strEndTime!=null&&!strEndTime.isEmpty()){
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT","");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setStartTime(format.parse(strStartTime));
					domain.setEndTime(format.parse(strEndTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setStartTime(defaultFormat.parse(strStartTime));
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
		if(domain.getEndTime()==null){
			result.rejectValue("endTime","EndTimeEmpty");
		}		
		/**** Slot Duration ****/
		if(domain.getSlotDuration()==null){
			result.rejectValue("slotDuration","SlotDurationEmpty");
		}

	}

	@Override
	protected void customValidateUpdate(final Roster domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch");
		}		
		/**** Session ****/
		if(domain.getSession()==null){
			result.rejectValue("session","SessionEmpty");
		}
		/**** Registry No.****/
		if(domain.getRegisterNo()==null){
			result.rejectValue("registerNo","RegistryEmpty");
		}		
		/**** Start Time and End Time****/
		String strStartTime=request.getParameter("selectedStartTime");
		String strEndTime=request.getParameter("selectedEndTime");
		if(strStartTime!=null&&!strStartTime.isEmpty()
				&&strEndTime!=null&&!strEndTime.isEmpty()){
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT","");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setStartTime(format.parse(strStartTime));
					domain.setEndTime(format.parse(strEndTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setStartTime(defaultFormat.parse(strStartTime));
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
		if(domain.getEndTime()==null){
			result.rejectValue("endTime","EndTimeEmpty");
		}		
		/**** Slot Duration ****/
		if(domain.getSlotDuration()==null){
			result.rejectValue("slotDuration","SlotDurationEmpty");
		}
		/**** Action ****/
		String operation=request.getParameter("operation");
		if(operation!=null&&!operation.isEmpty()&&operation.equals("GENERATE_SLOT")){
			if(domain.getAction()==null){
				result.rejectValue("action","ActionEmpty");
			}else if(domain.getAction().isEmpty()){
				result.rejectValue("action","ActionEmpty");
			}else if(domain.getAction().equals("-")){
				result.rejectValue("action","ActionEmpty");
			}			
		}
	}	

	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final Roster domain,
			final HttpServletRequest request) {
		String[] usersArr=request.getParameterValues("selectedItems");
		List<User> users=new ArrayList<User>();
		if(usersArr!=null&&usersArr.length>0){
			for(String i:usersArr){
				User user=User.findById(User.class,Long.parseLong(i));
				users.add(user);
			}
			domain.setUsers(users);
		}		
	}

	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final Roster domain,
			final HttpServletRequest request) {
		String[] usersArr=request.getParameterValues("selectedItems");
		List<User> users=new ArrayList<User>();
		if(usersArr!=null&&usersArr.length>0){
			for(String i:usersArr){
				User user=User.findById(User.class,Long.parseLong(i));
				users.add(user);
			}
			domain.setUsers(users);
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
			
			returnValue = "roster/rosterreport";
					
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		return returnValue;
	}
}
