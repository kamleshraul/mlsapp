/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.ChartVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;

/**
 * The Class ChartVO.
 *
 * @author amitd
 * @since v1.0.0
 */
public class ChartVO {

	//=============== ATTRIBUTES ====================
	/** The member id. */
	private Long memberId;

	/** The member name. */
	private String memberName;

	/** The Device v os. */
	private List<DeviceVO> deviceVOs;
	
	private String rejectedNotices;
	
	private Integer rejectedCount;
	
	private Integer extraCount;


	//=============== CONSTRUCTORS ==================
	/**
	 * Instantiates a new chart vo.
	 */
	public ChartVO() {
		super();
	}

	/**
	 * Instantiates a new chart vo.
	 *
	 * @param memberId the member id
	 * @param memberName the member name
	 */
	public ChartVO(final Long memberId,
			final String memberName) {
		super();
		this.memberId = memberId;
		this.memberName = memberName;
	}

	/**
	 * Instantiates a new chart vo.
	 *
	 * @param memberId the member id
	 * @param memberName the member name
	 * @param questionVOs the question v os
	 */
	public ChartVO(final Long memberId,
			final String memberName,
			final List<DeviceVO> deviceVOs) {
		super();
		this.memberId = memberId;
		this.memberName = memberName;
		this.deviceVOs = deviceVOs;
	}
	
	public ChartVO(final Long memberId,
			final String memberName,
			final List<DeviceVO> deviceVOs,
			final String rejectedNotices,
			final Integer extraCount,
			final Integer rejectedCount) {
		super();
		this.memberId = memberId;
		this.memberName = memberName;
		this.deviceVOs = deviceVOs;
		this.rejectedNotices=rejectedNotices;
		this.extraCount=extraCount;
		this.rejectedCount=rejectedCount;
	}

	


	//=============== UTILITY METHODS ===============
	/**
	 * Does not sort in place, returns a new list.
	 *
	 * @param chartVOs the chart v os
	 * @param sortOrder the sort order
	 * @return the list
	 */
	public static List<ChartVO> sort(final List<ChartVO> chartVOs, 
			final String sortOrder,
			final String deviceType) {
		List<ChartVO> newChartVO = new ArrayList<ChartVO>();
		newChartVO.addAll(chartVOs);

		if(sortOrder.equals(ApplicationConstants.ASC)) {
			Comparator<ChartVO> c = new Comparator<ChartVO>() {
				@Override
				public int compare(final ChartVO c1, final ChartVO c2) {
					if(deviceType.startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
						/*****Added by Anand to sort the chart by the number *******/
						if(c1.getDeviceVOs()!=null){
							Integer c1Number=c1.getDeviceVOs().get(0).getNumber();
							Integer c2Number=c2.getDeviceVOs().get(0).getNumber();
							return c1Number.compareTo(c2Number);
						}else{
							String c1MemberName = c1.getMemberName();
							String c2MemberName = c2.getMemberName();
							return c1MemberName.compareTo(c2MemberName);
						}
					}else if(deviceType.startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
						if(deviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
							if(!(c1.getDeviceVOs()==null)){
								Integer c1Number=c1.getDeviceVOs().get(0).getNumber();
								Integer c2Number=c2.getDeviceVOs().get(0).getNumber();
								return c1Number.compareTo(c2Number);
							}else{
								String c1MemberName = c1.getMemberName();
								String c2MemberName = c2.getMemberName();
								return c1MemberName.compareTo(c2MemberName);
							}
						}else{
							String c1MemberName = c1.getMemberName();
							String c2MemberName = c2.getMemberName();
							return c1MemberName.compareTo(c2MemberName);
						}
					}else{
						if(!(c1.getDeviceVOs()==null)){
							Integer c1Number=c1.getDeviceVOs().get(0).getNumber();
							Integer c2Number=c2.getDeviceVOs().get(0).getNumber();
							return c1Number.compareTo(c2Number);
						}else{
							String c1MemberName = c1.getMemberName();
							String c2MemberName = c2.getMemberName();
							return c1MemberName.compareTo(c2MemberName);
						}
					}
				}
			};
			Collections.sort(newChartVO, c);
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			Comparator<ChartVO> c = new Comparator<ChartVO>() {

				@Override
				public int compare(final ChartVO c1, final ChartVO c2) {
					String c1MemberName = c1.getMemberName();
					String c2MemberName = c2.getMemberName();
					return c2MemberName.compareTo(c1MemberName);
				}
			};
			Collections.sort(newChartVO, c);
		}

		return newChartVO;
	}

	//=============== GETTERS/SETTERS ===============
	/**
	 * Gets the member id.
	 *
	 * @return the member id
	 */
	public Long getMemberId() {
		return memberId;
	}

	/**
	 * Sets the member id.
	 *
	 * @param memberId the new member id
	 */
	public void setMemberId(final Long memberId) {
		this.memberId = memberId;
	}

	/**
	 * Gets the member name.
	 *
	 * @return the member name
	 */
	public String getMemberName() {
		return memberName;
	}

	/**
	 * Sets the member name.
	 *
	 * @param memberName the new member name
	 */
	public void setMemberName(final String memberName) {
		this.memberName = memberName;
	}

	public List<DeviceVO> getDeviceVOs() {
		return deviceVOs;
	}

	public void setDeviceVOs(final List<DeviceVO> deviceVOs) {
		this.deviceVOs = deviceVOs;
	}

	public String getRejectedNotices() {
		return rejectedNotices;
	}

	public void setRejectedNotices(final String rejectedNotices) {
		this.rejectedNotices = rejectedNotices;
	}

	public Integer getExtraCount() {
		return extraCount;
	}

	public void setExtraCount(final Integer extraCount) {
		this.extraCount = extraCount;
	}
	

	public Integer getRejectedCount() {
		if(this.rejectedNotices!=null){
			if(!this.rejectedNotices.isEmpty()){
				return this.rejectedNotices.split(",").length;
			}else{
				return 0;
			}
		}else{
			return 0;
		}
		
	}

	public void setRejectedCount(final Integer rejectedCount) {
		this.rejectedCount = rejectedCount;
	}
	

	

}
