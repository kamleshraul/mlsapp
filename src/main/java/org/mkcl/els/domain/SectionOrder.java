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
import org.mkcl.els.repository.SectionOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author dhananjayb
 *
 */
@Configurable
@Entity
@Table(name="sectionorders")
@JsonIgnoreProperties({"sectionOrderSeries"})
public class SectionOrder extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 4393916246964316389L;

	//=============== ATTRIBUTES ===============
	/** The name. */
	@Column(length=100)
	private String name;
	
	/** The number. */
//	@Column(name="sequence_number")
	private Integer sequenceNumber;
	
	/** The section order series. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sectionorderseries_id")
	private SectionOrderSeries sectionOrderSeries;
	
	/** The section order repository. */
    @Autowired
    private transient SectionOrderRepository sectionOrderRepository;
	
	//=============== CONSTRUCTORS =============
	public SectionOrder() {
		super();
	}		
	
//	public SectionOrder(String name, String sequenceNumber,SectionOrderSeries sectionOrderSeries) {
//		super();
//		this.name = name;
//		this.sequenceNumber = sequenceNumber;
//		this.sectionOrderSeries = sectionOrderSeries;
//	}
	
	//=============== DOMAIN METHODS ==============
	public static SectionOrderRepository getSectionOrderRepository() {
		SectionOrderRepository sectionOrderRepository = new SectionOrder().sectionOrderRepository;
		if(sectionOrderRepository == null) {
			throw new IllegalStateException(
                    "SectionOrderRepository has not been injected in SectionOrder Domain");
		}
		return sectionOrderRepository;
	}
	
	public static Integer findSequenceNumberInSeries(final Long seriesId, final String name, final String locale) {
		return getSectionOrderRepository().findSequenceNumberInSeries(seriesId, name, locale);
	}

	//=============== GETTERS/SETTERS ==========
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public SectionOrderSeries getSectionOrderSeries() {
		return sectionOrderSeries;
	}

	public void setSectionOrderSeries(SectionOrderSeries sectionOrderSeries) {
		this.sectionOrderSeries = sectionOrderSeries;
	}
	
}