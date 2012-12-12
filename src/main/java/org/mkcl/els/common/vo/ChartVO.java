package org.mkcl.els.common.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;

public class ChartVO {

	//=============== ATTRIBUTES ====================
	private Long memberId;
	private String memberName;
	private List<QuestionVO> questionVOs;
	
	
	//=============== CONSTRUCTORS ==================
	public ChartVO() {
		super();
	}
	
	public ChartVO(final Long memberId, 
			final String memberName) {
		super();
		this.memberId = memberId;
		this.memberName = memberName;
	}
	
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
	 */
	public static List<ChartVO> sort(final List<ChartVO> chartVOs, final String sortOrder) {
		List<ChartVO> newChartVO = new ArrayList<ChartVO>();
		newChartVO.addAll(chartVOs);
		
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			Comparator<ChartVO> c = new Comparator<ChartVO>() {
				
				@Override
				public int compare(ChartVO c1, ChartVO c2) {
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
				public int compare(ChartVO c1, ChartVO c2) {
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
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	
	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public List<QuestionVO> getQuestionVOs() {
		return questionVOs;
	}

	public void setQuestionVOs(List<QuestionVO> questionVOs) {
		this.questionVOs = questionVOs;
	}
	
}
