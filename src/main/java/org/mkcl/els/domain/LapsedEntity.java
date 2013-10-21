package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.repository.LapsedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="lapsed_entities")
public class LapsedEntity  extends BaseDomain implements Serializable{
	
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The position. */
    private Integer position;

    /** The device. */
    @ManyToOne(fetch=FetchType.EAGER)
    private Device device;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private DeviceType deviceType;
    
    @Autowired
    private transient LapsedEntityRepository lapsedEntityRepository;
    
    /**
     * Instantiates a new lapsed entity.
     */
    public LapsedEntity() {
        super();
    }
    
    public static LapsedEntityRepository getLapsedEntityRepository() {
    	LapsedEntityRepository lapsedEntityRepository = new LapsedEntity().lapsedEntityRepository;
        if (lapsedEntityRepository == null) {
            throw new IllegalStateException(
                    "LapsedEntityRepository has not been injected in Lapsed Entity Domain");
        }
        return lapsedEntityRepository;
    }

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
    
	public static Boolean referLapsed(final String device, final Long primaryId, final Long lapsedId, final String locale) {
		return getLapsedEntityRepository().referLapsed(device, primaryId,lapsedId,locale);
	}
	
	public static Boolean deReferLapsed(final String device, final Long primaryId,final Long referencedId,final String locale) {
		return getLapsedEntityRepository().deReferLapsed(device, primaryId,referencedId,locale);
	}
	
	public static List<BillSearchVO> fullTextSearchReferLapsedBill(final String param, final Bill bill, 
			final String language, final int start,final int noOfRecords,final String locale) {
		return getLapsedEntityRepository().fullTextSearchReferLapsedBill(param, bill, language, start, noOfRecords, locale);		
	}
}
