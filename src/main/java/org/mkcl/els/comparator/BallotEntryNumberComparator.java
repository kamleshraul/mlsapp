package org.mkcl.els.comparator;

import java.util.Comparator;

import org.mkcl.els.common.vo.BallotEntryVO;

public class BallotEntryNumberComparator implements Comparator<BallotEntryVO>{

	@Override
	public int compare(BallotEntryVO o1, BallotEntryVO o2) {
		int result=0;		
		result=o1.getDeviceNumber().compareTo(o2.getDeviceNumber());		
		return result;
	}
}
