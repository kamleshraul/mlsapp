package org.mkcl.els.comparator;

import java.util.Comparator;

import org.mkcl.els.common.vo.BallotEntryVO;

public class BallotEntryVOSecondBatchComparator implements Comparator<BallotEntryVO>{

	@Override
	public int compare(BallotEntryVO o1, BallotEntryVO o2) {
		int result=0;
		if(o1.getChartAnsweringDate().before(o2.getChartAnsweringDate())){
			result=1;
		}else if(o1.getChartAnsweringDate().after(o2.getChartAnsweringDate())){
			result=-1;
		}else{
			result=0;
		}
		if(result==0){
			result=o1.getPriority().compareTo(o2.getPriority());
			if(result==0){
				result=o1.getDeviceNumber().compareTo(o2.getDeviceNumber());				
			}
		}		
		return result;
	}

}
