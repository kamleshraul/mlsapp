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

	/** The question v os. */
	private List<QuestionVO> questionVOs;


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
			final List<QuestionVO> questionVOs) {
		super();
		this.memberId = memberId;
		this.memberName = memberName;
		this.questionVOs = questionVOs;
	}


	//=============== UTILITY METHODS ===============
	/**
	 * Does not sort in place, returns a new list.
	 *
	 * @param chartVOs the chart v os
	 * @param sortOrder the sort order
	 * @return the list
	 */
	public static List<ChartVO> sort(final List<ChartVO> chartVOs, final String sortOrder) {
		List<ChartVO> newChartVO = new ArrayList<ChartVO>();
		newChartVO.addAll(chartVOs);

		if(sortOrder.equals(ApplicationConstants.ASC)) {
			Comparator<ChartVO> c = new Comparator<ChartVO>() {

				@Override
				public int compare(final ChartVO c1, final ChartVO c2) {
					String c1MemberName = c1.getMemberName();
					String c2MemberName = c2.getMemberName();
					return c1MemberName.compareTo(c2MemberName);
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

	/**
	 * Gets the question v os.
	 *
	 * @return the question v os
	 */
	public List<QuestionVO> getQuestionVOs() {
		return questionVOs;
	}

	/**
	 * Sets the question v os.
	 *
	 * @param questionVOs the new question v os
	 */
	public void setQuestionVOs(final List<QuestionVO> questionVOs) {
		this.questionVOs = questionVOs;
	}

}
