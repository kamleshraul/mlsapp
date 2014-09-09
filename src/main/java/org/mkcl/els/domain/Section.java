package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.util.RomanNumeral;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author dhananjayb
 *
 */
@Configurable
@Entity
@Table(name = "sections")
@JsonIgnoreProperties({"orderingSeries", "billDraft", "drafts"})
public class Section extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	//=============== BASIC ATTRIBUTES ====================	
	/** The number. */
    @Column(length = 300)
	private String number;
    
    /** The ordering series. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="orderingseries_id")
	private SectionOrderSeries orderingSeries;
    
    /** The key. */
    @Column(length = 300)
	private String hierarchyOrder;
	
	/** The language. */
    @Column(length = 300)
	private String language;
	
	/** The text. */
    @Column(length=30000)
	private String text;
    
    /** The bill draft. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bill_draft_id")
	private BillDraft billDraft;
    
    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)    
    private Date editedOn; 
    
    /** The edited by. */
    @Column(length=1000)
    private String editedBy;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;
    
  //=============== DRAFTS ====================
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="sections_drafts_association", 
    		joinColumns={@JoinColumn(name="section_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="section_draft_id", referencedColumnName="id")})
    private List<SectionDraft> drafts;
    
    @Override
    public Section persist() {
    	addSectionDraft();
    	return (Section)super.persist();
    }
    
    @Override
    public Section merge() {
    	addSectionDraft();
    	return (Section)super.merge();
    }
    
    private void addSectionDraft() {
    	SectionDraft draft = new SectionDraft();
    	draft.setLocale(this.getLocale());
    	draft.setLanguage(this.getLanguage());
    	draft.setNumber(this.getNumber());
    	draft.setOrderingSeries(this.getOrderingSeries());
    	draft.setHierarchyOrder(this.getHierarchyOrder());
    	draft.setText(this.getText());
    	draft.setBillDraft(this.getBillDraft());
    	draft.setEditedOn(this.getEditedOn());
    	draft.setEditedBy(this.getEditedBy());    	
    	draft.setEditedAs(this.getEditedAs());
    	if(this.getId() != null) {
            Section section = Section.findById(Section.class, this.getId());
            List<SectionDraft> originalDrafts = section.getDrafts();
            if(originalDrafts != null){
                originalDrafts.add(draft);
            }
            else{
                originalDrafts = new ArrayList<SectionDraft>();
                originalDrafts.add(draft);
            }
            this.setDrafts(originalDrafts);
        }
        else {
            List<SectionDraft> originalDrafts = new ArrayList<SectionDraft>();
            originalDrafts.add(draft);
            this.setDrafts(originalDrafts);
        }
    }
    
    public String findOrder() {
    	if(this.getHierarchyOrder()!=null && !this.getHierarchyOrder().isEmpty()) {
    		String[] hierarchyOrder = this.getHierarchyOrder().split("\\.");
    		return hierarchyOrder[hierarchyOrder.length-1];
    	} else {
    		return null;
    	}    	
    }
    
    @SuppressWarnings("unchecked")
	public static String findOrderInSeries(String sectionNumber, SectionOrderSeries orderingSeries, String locale) throws ELSException {
    	String orderSequence = null;
    	if(sectionNumber==null || orderingSeries==null) {
    		throw new ELSException();
    	} else {
    		orderSequence = "";
    		String[] numberArr = sectionNumber.split("\\.");
    		String numberForTheOrder = numberArr[numberArr.length-1];
    		if(orderingSeries.getIsAutonomous()) {
    			if(orderingSeries.getName().toLowerCase().contains("roman")) {
    				Integer orderSequenceInt = RomanNumeral.getIntegerEquivalent(numberForTheOrder.toUpperCase());
    				if(orderSequenceInt == null || orderSequenceInt.intValue()==-1) {
    					//throw new ELSException();
    					orderSequence = "";
    				} else {
    					orderSequence = orderSequenceInt.toString();//FormaterUtil.formatNumberNoGrouping(orderSequenceInt, locale);
    				}
    			} else {
    				Map<String, String[]> parameters = new HashMap<String, String[]>();
    				List<String> result = null;
    				String isNumber = "no";
    				try {
    					String languageLocale = "en_US";
    					if(orderingSeries.getLanguage()!=null) {
    						parameters.put("languageType", new String[]{orderingSeries.getLanguage().getType()});
    						parameters.put("locale", new String[]{""});
    						result = Query.findReport("LOCALE_FOR_LANGUAGE_QUERY", parameters);
    						if(result!=null && !result.isEmpty() && result.get(0)!=null) {
    							languageLocale = result.get(0);
    						} else {
    							throw new ELSException();
    						}
    					}
    					numberForTheOrder = FormaterUtil.getNumberFormatterNoGrouping(languageLocale).parse(numberForTheOrder).toString();				
    					isNumber = "yes";		
    				} catch(ParseException pe) { //handled for non-numeric series here
    					
    				} finally {				
    					parameters.put("orderSeriesId", new String[]{orderingSeries.getId().toString()});
    					parameters.put("numberForTheOrder", new String[]{numberForTheOrder});
    					parameters.put("isNumber", new String[]{isNumber});
    					parameters.put("locale", new String[]{""});
    					result = Query.findReport("SEQUENCE_NUMBER_IN_AUTONOMOUS_SERIES", parameters);
    					if(result!=null && !result.isEmpty()) {
    						orderSequence = result.get(0);//FormaterUtil.formatNumberNoGrouping(Integer.valueOf(result.get(0)), locale);
    					} else {
    						//throw new ELSException();
    						orderSequence = "";
    					}
    				}
    			}			
    		} else {
    			Integer sequenceNumber = SectionOrder.findSequenceNumberInSeries(orderingSeries.getId(), numberForTheOrder, locale);
    			if(sequenceNumber!=null) {
    				orderSequence = sequenceNumber.toString();//FormaterUtil.formatNumberNoGrouping(sequenceNumber, locale);
    			} else {
    				//throw new ELSException();
    				orderSequence = "";
    			}
    		}    		
    	}
    	return orderSequence;
    }
    
    public void setHierarchyOrder(Device device, String sectionNumber, String sectionOrder, String currentNumber, String currentOrder) throws ELSException {
		String[] sectionNumberArr = sectionNumber.split("\\.");
		if(sectionNumberArr.length==1) {
			Section sectionWithSameOrder = null;
			if(device instanceof Bill) {
				sectionWithSameOrder = Bill.findSectionByHierarchyOrder(device.getId(), language, sectionOrder);
			}
			if(sectionWithSameOrder!=null) {
				List<Section> sections = new ArrayList<Section>();
				if(device instanceof Bill) {
					sections = Bill.findAllSectionsInGivenLanguage(device.getId(), language);
				}				
				for(Section s: sections) {		
					
					if(Integer.parseInt(s.getHierarchyOrder().split("\\.")[0])>=Integer.parseInt(sectionOrder)) {
						String hOrder = s.getHierarchyOrder();
						if(this.getId()==null || !hOrder.startsWith(currentOrder) || !s.getNumber().startsWith(currentNumber)) {
							int hOrderFirstElement = Integer.parseInt(hOrder.split("\\.")[0]);
							hOrderFirstElement++;
							if(hOrder.split("\\.").length>1) {
								String finalHOrder = hOrder.substring(hOrder.indexOf("."), hOrder.length());
								s.setHierarchyOrder(hOrderFirstElement+""+finalHOrder);
							} else {
								s.setHierarchyOrder(hOrderFirstElement+"");
							}														
							System.out.println(s.getHierarchyOrder());
							s.setBillDraft(this.getBillDraft());
							s.setEditedAs(this.getEditedAs());
							s.setEditedBy(this.getEditedBy());
							s.setEditedOn(this.getEditedOn());
							s.merge();
						}						
					}
				}
				this.setHierarchyOrder(sectionOrder);
			} else {
				this.setHierarchyOrder(sectionOrder);
			}
		} else {
			String parentSectionNumber = "";
			for(int i=0; i<=sectionNumberArr.length-2;i++) {											
				parentSectionNumber += sectionNumberArr[i];
				if(i!=sectionNumberArr.length-2) {
					parentSectionNumber += ".";
				}
			}
			Section parentSection = null;
			if(device instanceof Bill) {
				parentSection = Bill.findSection(device.getId(), language, parentSectionNumber);
			}
			if(parentSection!=null) {
				Section sectionWithSameOrder = null;
				if(device instanceof Bill) {
					sectionWithSameOrder = Bill.findSectionByHierarchyOrder(device.getId(), language, parentSection.getHierarchyOrder()+"."+sectionOrder);
				}
				if(sectionWithSameOrder!=null) {
					List<Section> sections = null;
					if(device instanceof Bill) {
						sections = Bill.findAllInternalSections(device.getId(), language, parentSection.getHierarchyOrder());
					}
					for(Section s: sections) {
						if(Integer.parseInt(s.getHierarchyOrder().split("\\.")[sectionNumberArr.length-1])>=Integer.parseInt(sectionOrder)) {
							String hOrder = s.getHierarchyOrder();
							if(this.getId()==null || !hOrder.startsWith(currentOrder) || !s.getNumber().startsWith(currentNumber)) {
								String[] hOrderArr = hOrder.split("\\.");
								int hOrderIncrementElement = Integer.parseInt(hOrder.split("\\.")[sectionNumberArr.length-1]);
								hOrderIncrementElement++;
								String preHOrder = "";//hOrder.substring(0, sectionNumberArr.length-2);
								for(int i=0; i<=sectionNumberArr.length-2;i++) {
									preHOrder += hOrderArr[i];
									if(i!=sectionNumberArr.length-2) {
										preHOrder += ".";
									}
								}
								if(hOrder.split("\\.").length>sectionNumberArr.length) {
									String postHOrder = "";//hOrder.substring(sectionNumberArr.length, hOrder.length());
									for(int i=sectionNumberArr.length; i<hOrderArr.length;i++) {
										postHOrder += hOrderArr[i];
										if(i!=hOrderArr.length-1) {
											postHOrder += ".";
										}
									}
									s.setHierarchyOrder(preHOrder+"."+hOrderIncrementElement+"."+postHOrder);
								} else {
									s.setHierarchyOrder(preHOrder+"."+hOrderIncrementElement);
								}								
								System.out.println(s.getHierarchyOrder());
								s.setBillDraft(this.getBillDraft());
								s.setEditedAs(this.getEditedAs());
								s.setEditedBy(this.getEditedBy());
								s.setEditedOn(this.getEditedOn());
								s.merge();
							}							
						}
					}
					this.setHierarchyOrder(parentSection.getHierarchyOrder()+"."+sectionOrder);
				} else {
					this.setHierarchyOrder(parentSection.getHierarchyOrder()+"."+sectionOrder);
				}
			}
		}
	}
    
    public void updateHierarchyOrder(Device device, String currentLanguage, String currentNumber, String currentOrder, String sectionOrder) throws ELSException {
		if(!this.getNumber().equals(currentNumber) || !sectionOrder.equals(currentOrder)
				|| !this.getLanguage().equals(currentLanguage)) {
			String hierarchyOrder = this.getHierarchyOrder();
			List<Section> internalSections = Bill.findAllInternalSections(device.getId(), currentLanguage, hierarchyOrder);
			if(!sectionOrder.equals(currentOrder)) {
				this.updateSectionsWhenSectionMoved(device, currentLanguage, currentNumber, currentOrder);
				this.setHierarchyOrder(device, this.getNumber(), sectionOrder, currentNumber, currentOrder);
			} else {
				if(this.getNumber().split("\\.").length!=currentNumber.split("\\.").length) {
					this.updateSectionsWhenSectionMoved(device, currentLanguage, currentNumber, currentOrder);
					this.setHierarchyOrder(device, this.getNumber(), sectionOrder, currentNumber, currentOrder);
				}
			}
			this.updateInternalSections(device, internalSections, currentNumber, hierarchyOrder);
		}
	}
    
    private void updateInternalSections(Device device, List<Section> internalSections, String previousNumber, String previousHierarchyOrder) throws ELSException {
//    	List<Section> internalSections = Bill.findAllInternalSections(device.getId(), this.getLanguage(), previousHierarchyOrder);
    	if(internalSections!=null && !internalSections.isEmpty()) {
    		for(Section s: internalSections) {
    			String updatedHierarchyOrder = s.getHierarchyOrder().replaceFirst(previousHierarchyOrder, this.getHierarchyOrder());
    			s.setHierarchyOrder(updatedHierarchyOrder);
    			//optional: confirm from users  to comment if not needed
    			String updatedNumber = s.getNumber().replaceFirst(previousNumber, this.getNumber());
    			s.setNumber(updatedNumber);
    			s.setLanguage(this.getLanguage());
    			s.setBillDraft(this.getBillDraft());
				s.setEditedAs(this.getEditedAs());
				s.setEditedBy(this.getEditedBy());
				s.setEditedOn(this.getEditedOn());
    			s.merge();
    		}
    	}
    }
    
    public void updateSectionsWhenSectionMoved(Device device, String sectionLanguage, String sectionNumber, String sectionOrder) throws ELSException {
    	String[] sectionNumberArr = sectionNumber.split("\\.");
		if(sectionNumberArr.length==1) {
//			Section sectionWithSameOrder = null;
//			if(device instanceof Bill) {
//				sectionWithSameOrder = Bill.findSectionByHierarchyOrder(device.getId(), language, sectionOrder);
//			}
//			if(sectionWithSameOrder!=null) {
				List<Section> sections = new ArrayList<Section>();
				if(device instanceof Bill) {
					sections = Bill.findAllSectionsInGivenLanguage(device.getId(), sectionLanguage);
				}				
				for(Section s: sections) {		
					
					if(Integer.parseInt(s.getHierarchyOrder().split("\\.")[0])>=Integer.parseInt(sectionOrder)) {
						String hOrder = s.getHierarchyOrder();	
						if(!hOrder.startsWith(this.getHierarchyOrder())) {
							int hOrderFirstElement = Integer.parseInt(hOrder.split("\\.")[0]);
							hOrderFirstElement--;
							if(hOrder.split("\\.").length>1) {
								String finalHOrder = hOrder.substring(hOrder.indexOf("."), hOrder.length());
								s.setHierarchyOrder(hOrderFirstElement+""+finalHOrder);
							} else {
								s.setHierarchyOrder(hOrderFirstElement+"");
							}
							s.setBillDraft(this.getBillDraft());
							s.setEditedAs(this.getEditedAs());
							s.setEditedBy(this.getEditedBy());
							s.setEditedOn(this.getEditedOn());
							System.out.println(s.getHierarchyOrder());
							s.merge();
						}						
					}
				}				
			}
//		} 
//    	else {
			String parentSectionNumber = "";
			for(int i=0; i<=sectionNumberArr.length-2;i++) {											
				parentSectionNumber += sectionNumberArr[i];
				if(i!=sectionNumberArr.length-2) {
					parentSectionNumber += ".";
				}
			}
			Section parentSection = null;
			if(device instanceof Bill) {
				parentSection = Bill.findSection(device.getId(), sectionLanguage, parentSectionNumber);
			}
			if(parentSection!=null) {
				Section sectionWithSameOrder = null;
				if(device instanceof Bill) {
					sectionWithSameOrder = Bill.findSectionByHierarchyOrder(device.getId(), sectionLanguage, parentSection.getHierarchyOrder()+"."+sectionOrder);
				}
				if(sectionWithSameOrder!=null) {
					List<Section> sections = null;
					if(device instanceof Bill) {
						sections = Bill.findAllInternalSections(device.getId(), sectionLanguage, parentSection.getHierarchyOrder());
					}
					for(Section s: sections) {
						if(Integer.parseInt(s.getHierarchyOrder().split("\\.")[sectionNumberArr.length-1])>=Integer.parseInt(sectionOrder)) {
							String hOrder = s.getHierarchyOrder();
							if(!hOrder.startsWith(this.getHierarchyOrder())) {
								String[] hOrderArr = hOrder.split("\\.");
								int hOrderIncrementElement = Integer.parseInt(hOrder.split("\\.")[sectionNumberArr.length-1]);
								hOrderIncrementElement--;
								String preHOrder = "";//hOrder.substring(0, sectionNumberArr.length-2);
								for(int i=0; i<=sectionNumberArr.length-2;i++) {
									preHOrder += hOrderArr[i];
									if(i!=sectionNumberArr.length-2) {
										preHOrder += ".";
									}
								}
								if(hOrder.split("\\.").length>sectionNumberArr.length) {
									String postHOrder = "";//hOrder.substring(sectionNumberArr.length, hOrder.length());
									for(int i=sectionNumberArr.length; i<hOrderArr.length;i++) {
										postHOrder += hOrderArr[i];
										if(i!=hOrderArr.length-1) {
											postHOrder += ".";
										}
									}
									s.setHierarchyOrder(preHOrder+"."+hOrderIncrementElement+"."+postHOrder);
								} else {
									s.setHierarchyOrder(preHOrder+"."+hOrderIncrementElement);
								}
								
								System.out.println(s.getHierarchyOrder());
								s.merge();
							}							
						}
					}					
				}
			}
//		}
    }
    
    public static boolean isHierarchyLevelWithCustomOrder(Device device, String sectionLanguage, String sectionNumber) throws ELSException {
    	boolean isCustomOrderPresent = false;
    	List<Section> sections = new ArrayList<Section>();
		if(device instanceof Bill) {						
			sections = Bill.findAllSiblingSectionsForGivenSection(device.getId(), sectionLanguage, sectionNumber);
		}		
		for(Section s: sections) {
			if(s.getOrderingSeries()==null) {
				isCustomOrderPresent = true;
				break;
			} else {
				String orderOfSection = s.findOrder();
				if(orderOfSection==null) {
					throw new ELSException();
				}
				String orderOfSectionInSeries = Section.findOrderInSeries(s.getNumber(), s.getOrderingSeries(), s.getLocale());		
				if(!orderOfSection.equals(orderOfSectionInSeries)) {
					isCustomOrderPresent = true;
					break;
				}
			}
		}
    	return isCustomOrderPresent;
    }

    //=============== Getters & Setters ====================
    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
    public SectionOrderSeries getOrderingSeries() {
		return orderingSeries;
	}

	public void setOrderingSeries(SectionOrderSeries orderingSeries) {
		this.orderingSeries = orderingSeries;
	}

	public String getHierarchyOrder() {
		return hierarchyOrder;
	}

	public void setHierarchyOrder(String hierarchyOrder) {
		this.hierarchyOrder = hierarchyOrder;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public BillDraft getBillDraft() {
		return billDraft;
	}

	public void setBillDraft(BillDraft billDraft) {
		this.billDraft = billDraft;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public List<SectionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<SectionDraft> drafts) {
		this.drafts = drafts;
	}

}
