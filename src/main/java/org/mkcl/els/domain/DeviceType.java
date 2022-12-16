/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.QuestionType.java
 * Created On: 19 Jun, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.DeviceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class QuestionType.
 *
 * @author Dhananjay
 * @since v1.1.0
 */
@Configurable
@Entity
@Table(name = "devicetypes")
@JsonIgnoreProperties({"parameters"})
public class DeviceType extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//

    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 150)
    @NotEmpty
    private String name;

    @ElementCollection
    @MapKeyColumn(name="parameter_key")
    @Column(name="parameter_value",length=10000)
    @CollectionTable(name="devicetype_parameters")
    private Map<String,String> parameters;

    @Column(length=100)
    @NotEmpty
    private String type;   
    
    @Column(length=100)
    private String device;

	private Integer priority;
	
	/** The name as displayed in lowerhouse. */
    @Column(length = 150)
    private String name_lowerhouse;
    
    /** The name as displayed in upperhouse. */
    @Column(length = 150)
    private String name_upperhouse;
	
	private Integer supportOrder;
    
    @Autowired
    private transient DeviceTypeRepository deviceTypeRepository;

    // ---------------------------------Constructors----------------------//

    public DeviceType() {
        super();
    }


    public DeviceType(final String name, final Map<String, String> parameters, final String type, final String device) {
        super();
        this.name = name;
        this.parameters = parameters;
        this.type = type;
        this.device=device;
    }

    // ----------------------------Domain Methods-------------------------//

    public static DeviceTypeRepository getDeviceTypeRepository() {
        DeviceTypeRepository deviceTypeRepository = new DeviceType().deviceTypeRepository;
        if (deviceTypeRepository == null) {
            throw new IllegalStateException(
                    "DeviceTypeRepository has not been injected in DeviceType Domain");
        }
        return deviceTypeRepository;
    }

    public static List<DeviceType> findDeviceTypesStartingWith(final String pattern,final String locale) throws ELSException{
        return getDeviceTypeRepository().findDeviceTypesStartingWith(pattern, locale);
    }

    public String getParameterValue(final String key){
        Map<String,String> params=this.getParameters();
        if(params!=null){
        if(params.containsKey(key)){
            return params.get(key);
        }else{
            return "";
        }
        }else{
            return "";
        }
    }
    
    public static DeviceType findByType(String type, String locale) {
    	DeviceType deviceType = DeviceType.findByFieldName(DeviceType.class, "type", type, locale);
    	return deviceType;
    }
    
    public static DeviceType findByName(final String deviceTypeName, final String locale) {
    	DeviceType deviceType = 
    			DeviceType.findByFieldName(DeviceType.class,"name", deviceTypeName, locale);
    	return deviceType;
    }
    
    public static List<DeviceType> findAllowedTypesForUser(String deviceTypeNameParam, String delimiter, final String locale) throws ELSException {
 		return getDeviceTypeRepository().getAllowedTypesForUser(deviceTypeNameParam,delimiter,locale);
 	}
    
    public static List<DeviceType> findAllowedTypesInStarredClubbing(final String locale) throws ELSException {
		return getDeviceTypeRepository().getAllowedTypesInStarredClubbing(locale);
	}
    
    public static List<DeviceType> findAllowedTypesInMotionClubbing(final String locale) throws ELSException {
    	return getDeviceTypeRepository().getAllowedTypesInMotionClubbing(locale);
    }
    
    public static List<DeviceType> findDevicesContainedIn(final String strDevices, final String locale){
    	return getDeviceTypeRepository().getDevicesContainedIn(strDevices, locale);
    }
    
    public static List<DeviceType> findOriginalDeviceTypesForGivenDeviceType(DeviceType deviceType) {
    	List<DeviceType> originalDeviceTypesForGivenDeviceType = null;
    	
    	if(deviceType!=null) {
    		originalDeviceTypesForGivenDeviceType = new ArrayList<DeviceType>();
    		originalDeviceTypesForGivenDeviceType.add(deviceType);
    		
    		CustomParameter csptOriginalDeviceTypesForGivenDeviceType = CustomParameter.findByName(CustomParameter.class, "ORIGINAL_DEVICETYPES_FOR_"+deviceType.getType().toUpperCase(), "");
    		if(csptOriginalDeviceTypesForGivenDeviceType!=null && csptOriginalDeviceTypesForGivenDeviceType.getValue()!=null) {
    			for(String dt: csptOriginalDeviceTypesForGivenDeviceType.getValue().split(",")) {
    				if(!dt.isEmpty() && !dt.trim().isEmpty() && !dt.trim().equals(deviceType.getType().trim())) {
    					DeviceType originalDeviceType = DeviceType.findByType(dt.trim(), deviceType.getLocale());
        				if(originalDeviceType!=null) {
        					originalDeviceTypesForGivenDeviceType.add(originalDeviceType);
        				}
    				}    				
    			}    			
    		}
    	}
    	
		return originalDeviceTypesForGivenDeviceType;
    }

    // ----------------------------Getters/Setters------------------------//
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDevice() {
		return device;
	}

	 public void setDevice(String device) {
		this.device = device;
	 }
	 
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getName_lowerhouse() {
		if(name_lowerhouse!=null) {
			return name_lowerhouse;
		} else {
			return name;
		}		
	}

	public void setName_lowerhouse(String name_lowerhouse) {
		this.name_lowerhouse = name_lowerhouse;
	}

	public String getName_upperhouse() {
		if(name_upperhouse!=null) {
			return name_upperhouse;
		} else {
			return name;
		}
	}

	public void setName_upperhouse(String name_upperhouse) {
		this.name_upperhouse = name_upperhouse;
	}


	public Integer getSupportOrder() {
		return supportOrder;
	}


	public void setSupportOrder(Integer supportOrder) {
		this.supportOrder = supportOrder;
	}
	
}