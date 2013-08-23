package org.mkcl.els.test;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.mkcl.els.domain.BaseDomain;

import com.ibm.icu.util.IndianCalendar;

public class IndianCaledarDemo extends BaseDomain{
		
	private Map<Integer, String> months = new HashMap<Integer, String>();
	
	public IndianCaledarDemo() {
		super();
		setMonths();
	}
	
	private void setMonths(){
		months.put(0, "चैत्र");
		months.put(1, "वैशाख");
		months.put(2, "ज्येष्ठ");
		months.put(3, "आषाढ");
		months.put(4, "श्रावण");
		months.put(5, "भाद्रपद");
		months.put(6, "अश्विन");
		months.put(7, "कार्तिक");
		months.put(8, "आग्रह्नय");
		months.put(9, "पौष");
		months.put(10, "माघ");
		months.put(11, "फाल्गुन");		
	}
	
	public String getMonth(Integer month){
		if(month >= 0 && month <= 11){
			return months.get(month);
		}else{
			return "";
		}
	}
	
	public void demonstrate(){
		IndianCalendar indianCalendar = new IndianCalendar(new Locale("mr_IN"));
		indianCalendar.setTime(new Date());
		
		System.out.println("Today is " + indianCalendar.get(IndianCalendar.DAY_OF_MONTH) + ", " + this.getMonth(indianCalendar.get(IndianCalendar.MONTH)) + " " + indianCalendar.get(IndianCalendar.YEAR) + ":" + indianCalendar.get(IndianCalendar.IE) + "\n Available locales: " );
		for(Locale l: IndianCalendar.getAvailableLocales()){
			System.out.print(l.toString() + ", ");
		}
	}
	}
