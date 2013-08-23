package org.mkcl.els.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

class Calendar{
	static String[] DAYS = {"Sunday", "Monday", "Tuesday", "Wednesday",
							"Thursday", "Friday", "Saturday"};
	static int[] MONTHS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
	public static String getDay(int day, int month, int year){
		int index = 0;
		boolean isLeapYear = isLeapYear(year);
		
		if(isLeapYear){
			MONTHS[1] = 29;
		}else{
			MONTHS[1] = 28;
		}
		
		for(int i = 0; i < month - 1; i++){
			index += MONTHS[i];
		}
		
		index += (day + year + (year / 4) - 2);
		
		index %= 7;
		
		if(isLeapYear){
			index -= 1;
			if(index < 0){
				index += 7;
			}
		}
		
		return DAYS[index];
	}
	
	public static void printCalendar(final int month, final String firstDay){
		System.out.println("");
		int firstDayIndex = -1;
		
		for(int i = 0; i < DAYS.length; i++){
			
			System.out.print(DAYS[i].charAt(0)+"\t");
						
			if(DAYS[i].equals(firstDay)){
				firstDayIndex = i;
			}
		}
		
		System.out.println("");
		int newLiner = 0;
		
		for(int k = 0; k < firstDayIndex; k++){
			System.out.print("\t");
			newLiner++;
		}
		
		for(int j = 1; j <= MONTHS[month - 1]; j++){
			if(newLiner == DAYS.length){
				System.out.println("");
				newLiner = 0;
			}
			System.out.print(j+"\t");
			newLiner++;
		}
	}
	
	private static boolean isLeapYear(int year){
		return (((year % 400 == 0) || ((year % 100 != 0) && (year % 4== 0)))? true: false);
	}
}

public class CalendarProblems {

	public static void main(String[] args) {
		int day, month, year;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String date = null;
		String ans = "y";
		while(ans.equalsIgnoreCase("y")){
			try{
				System.out.print("Enter valid date in dd/mm/yyyy format: ");
				date = br.readLine();
				String[] fullDate = date.split("/");
				day = Integer.valueOf(fullDate[0]);
				month = Integer.valueOf(fullDate[1]);
				year = Integer.valueOf(fullDate[2]);
				
				String calculatedDay = Calendar.getDay(1, month, year);
				System.out.println("Day is: " + calculatedDay);
				Calendar.printCalendar(month, calculatedDay);
				
				System.out.println("\nCountinue? (y/n): ");
				ans = br.readLine();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
