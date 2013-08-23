package org.mkcl.els.test;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.apache.commons.collections.iterators.EntrySetMapIterator;
import org.mkcl.els.domain.VDepartment;
import org.mkcl.els.domain.VEmployee;
import org.mkcl.els.domain.VProject;
import org.springframework.transaction.annotation.Transactional;

public class MyRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String strData = "विकास";
		/*for(Entry<String, Charset> e:Charset.availableCharsets().entrySet()){
			System.out.println(e.getKey()+ " " + e.getValue().displayName() + " " + e.getValue().name());
		}*/
		/*System.out.println(strData+" : " +Charset.defaultCharset());
		byte[] charByte = strData.getBytes(Charset.forName("UTF-8"));
		for(int i = 0; i < charByte.length; i++){
			System.out.println(charByte[i] + "= " + (char)charByte[i]);
		}*/
		demo();
		
	}
	
	//@Transactional
	private static void demo(){
		/*String[] deptLocations = {"Mumbai", "Hydrabad", "Mysore", "Delhi", "Kolkata"};
		{
			for(int i = 0 ; i < 10; i++){
				VDepartment dept = new VDepartment();
				VProject prj = new VProject();
				
				dept.setDeptName("1" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(0, i + 1));
				dept.setDeptLocation(deptLocations[((int)(Math.random() * 3488)) % deptLocations.length]);
				dept.setVersion(0L);
				dept.setLocale("mr_IN");
				
				dept.persist();				
				
				prj.setVersion(0L);
				prj.setLocale("mr_IN");
				prj.setProjectName("Prj_" + ((char)65 + i));
				
				
			}
		}*/		
		
	}

}
