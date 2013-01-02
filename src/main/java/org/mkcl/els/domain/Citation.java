package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="citations")
public class Citation extends BaseDomain implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch=FetchType.LAZY)
    private DeviceType deviceType;

    @Column(length=10000)
    private String text;
    
    @Column(length=10000)
    private String status;

    public Citation() {
        super();
    }

    public Citation(final DeviceType deviceType, final String text,final String status) {
        super();
        this.deviceType = deviceType;
        this.text = text;
        this.status=status;
    }


    public DeviceType getDeviceType() {
        return deviceType;
    }


    public void setDeviceType(final DeviceType deviceType) {
        this.deviceType = deviceType;
    }


    public String getText() {
        return text;
    }


    public void setText(final String text) {
        this.text = text;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    
}
