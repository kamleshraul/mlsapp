package org.mkcl.els.common.util;

import java.sql.SQLException;

import SmartSolutions.SrcToTarget;

public class Transliteration {
	
	public static String transliterateToMarathi(String input){
		SrcToTarget srcToTarget=null;
		String output="";
		try {
			srcToTarget=new SrcToTarget();
			output=srcToTarget.callEngToMar(input);			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return output;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return output;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return output;
		} catch (SQLException e) {
			e.printStackTrace();
			return output;
		}
		return output;
	}
}
