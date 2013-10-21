package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "print_requisition_parameters")
public class PrintRequisitionParameter extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column
    private String requisitionFor;
	
	@Column
    private String parameterName;
	
//	@Column
//    private String parameterType;
	
	@Column
    private Integer parameterOrder;

	public PrintRequisitionParameter() {
		
	}

	public String getRequisitionFor() {
		return requisitionFor;
	}

	public void setRequisitionFor(String requisitionFor) {
		this.requisitionFor = requisitionFor;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

//	public String getParameterType() {
//		return parameterType;
//	}
//
//	public void setParameterType(String parameterType) {
//		this.parameterType = parameterType;
//	}

	public Integer getParameterOrder() {
		return parameterOrder;
	}

	public void setParameterOrder(Integer parameterOrder) {
		this.parameterOrder = parameterOrder;
	}	

}
