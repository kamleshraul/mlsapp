package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.util.ApplicationConstants;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dhananjayb
 *
 */
@Configurable
@Entity
@Table(name="sectionorderseries")
@JsonIgnoreProperties({"language"})
public class SectionOrderSeries extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 4393916246964316389L;

	//=============== ATTRIBUTES ===============
	/** The name. */
	@Column(length=100)
	private String name;
	
	/** The language. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="language_id")
	private Language language;
	
	private Boolean isAutonomous;
	
	//=============== CONSTRUCTORS =============
	public SectionOrderSeries() {
		super();
	}

	public SectionOrderSeries(String name) {
		super();
		this.name = name;
	}
	
	@Transactional(readOnly = true)
    public boolean isDuplicate(final String fieldName, final String fieldValue){
		if(fieldName!=null && fieldName.equals("name")) {
			SectionOrderSeries duplicateParameter = null;
	        if (this.getLocale().isEmpty()) {
	            duplicateParameter = getBaseRepository().findByFieldName(
	                    this.getClass(), "name", fieldValue, "");
	        }
	        else {
	            duplicateParameter = getBaseRepository().findByFieldName(
	                    this.getClass(), "name", fieldValue, this.getLocale());
	        }
	        if (duplicateParameter != null) {
	        	//case sensitive check for name
	            if (!duplicateParameter.getId().equals(this.getId()) && duplicateParameter.getName().equals(fieldValue)) {
	                return true;
	            }
	        }
	        return false;			
		} else {
			return super.isDuplicate(fieldName, fieldValue);
		}
    }
	
	public boolean checkDuplicate() {
		boolean isDuplicate = false;
		List<SectionOrderSeries> duplicateSeriesAll = SectionOrderSeries.findAllByFieldName(SectionOrderSeries.class, "name", this.getName(), "id", ApplicationConstants.ASC, this.getLocale());
        if(duplicateSeriesAll!=null && !duplicateSeriesAll.isEmpty()) {
        	for(SectionOrderSeries i: duplicateSeriesAll) {
        		if(this.getId()==null || !i.getId().equals(this.getId())) {
        			if(i.getName().equals(this.getName()) && i.getLanguage().equals(this.getLanguage())) {
        				isDuplicate = true;
        				break;
        			}
        		}
        	}
        }
		return isDuplicate;
	}

	//=============== GETTERS/SETTERS ==========
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Boolean getIsAutonomous() {
		return isAutonomous;
	}

	public void setIsAutonomous(Boolean isAutonomous) {
		this.isAutonomous = isAutonomous;
	}
	
}