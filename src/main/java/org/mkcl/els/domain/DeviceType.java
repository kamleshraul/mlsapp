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

    @Autowired
    private transient DeviceTypeRepository deviceTypeRepository;

    // ---------------------------------Constructors----------------------//

    public DeviceType() {
        super();
    }


    public DeviceType(final String name, final Map<String, String> parameters, final String type) {
        super();
        this.name = name;
        this.parameters = parameters;
        this.type = type;
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

    public static List<DeviceType> findDeviceTypesStartingWith(final String pattern,final String locale){
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


	public static List<DeviceType> getAllowedTypesInStarredClubbing(final String locale) {
		return getDeviceTypeRepository().getAllowedTypesInStarredClubbing(locale);
	}


}
