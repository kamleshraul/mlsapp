package org.mkcl.els.comparator;

import java.util.Comparator;

import org.mkcl.els.common.vo.BallotEntryVO;

public class BallotEntryVOFirstBatchComparator implements Comparator<BallotEntryVO>{

	@Override
	public int compare(BallotEntryVO o1, BallotEntryVO o2) {
		int result=0;
		if(o1.isAttendance() && o2.isAttendance()){
			result=0;
		}else if(!o1.isAttendance() && o2.isAttendance()){
			result=-1;
		}else if(o1.isAttendance() && !o2.isAttendance()){
			result=1;
		}
		if(result==0){
			result=o1.getRound().compareTo(o2.getRound());
			if(result==0){
				result=o1.getPosition().compareTo(o2.getPosition());
				if(result==0){
					result=o1.getRound().compareTo(o2.getRound());
					if(result==0){
						result=o1.getChoice().compareTo(o2.getChoice());
					}
				}
			}
		}
		return result;
	}
}
