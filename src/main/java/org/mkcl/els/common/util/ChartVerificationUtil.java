package org.mkcl.els.common.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.domain.Chart;
import org.mkcl.els.domain.ChartEntry;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.Question;

public class ChartVerificationUtil {
	
	public static void rectify(final Chart chart) {	
		Date answeringDate = chart.getAnsweringDate();
		
		List<ChartEntry> chartEntries = chart.getChartEntries();
		int size = chartEntries.size();
		System.out.println(">>>>> Size of Chart Entries: " + size);
		for(int i = 0; i < size; i++) {
			ChartEntry ce = chartEntries.get(i);
			
			System.out.println(">>>>> Processing chart entry: " + (i + 1));
			ChartVerificationUtil.rectify(ce, answeringDate);
		}
	}
	
	private static void rectify(final ChartEntry chartEntry,
			final Date answeringDate) {
		boolean isFaulty = false;
		
		List<Question> questionList = 
			ChartVerificationUtil.convertToQuestions(chartEntry.getDevices());
		
		List<Question> correctOrderQuestionList =
			ChartVerificationUtil.reorderQuestions(questionList, answeringDate);
		
		int size = questionList.size();
		for(int i = 0; i < size; i++) {
			Question question = questionList.get(i);
			Long questionId = question.getId();
			
			Question correctOrderQuestion = correctOrderQuestionList.get(i);
			Long correctOrderQuestionId = correctOrderQuestion.getId();
			
			if(! questionId.equals(correctOrderQuestionId)) {
				isFaulty = true;
				break;
			}
		}
		
		if(isFaulty) {
			List<Device> devices = 
				ChartVerificationUtil.convertToDevices(correctOrderQuestionList);
			chartEntry.setDevices(devices);
			chartEntry.merge();
			System.out.println(">>>>> Chart entry modified: " + chartEntry.getId());
		}
	}
	
	private static List<Device> convertToDevices(
			final List<Question> questions) {
		List<Device> devices = new ArrayList<Device>();
		
		for(Question q : questions) {
			Device d = q;
			devices.add(d);
		}
		
		return devices;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
//	public static void verify(final Chart chart) 
//		throws ELSException, IOException {
//		String fileName = ChartVerificationUtil.getFileName(chart);
//		FileOutputStream fout = new FileOutputStream(fileName, true);
//		try {
//			Date answeringDate = chart.getAnsweringDate();
//			
//			List<ChartEntry> chartEntries = chart.getChartEntries();
//			int size = chartEntries.size();
//			System.out.println(">>>>> Size of Chart Entries: " + size);
//			for(int i = 0; i < size; i++) {
//				ChartEntry ce = chartEntries.get(i);
//				
//				System.out.println("Processing chart entry: " + (i + 1));
//				fout.write("\n".getBytes());
//				fout.write("\n".getBytes());
//				fout.flush();
//				List<Device> deviceList = ce.getDevices();
//				ChartVerificationUtil.verify(deviceList, answeringDate, fout);
//			}
//		}
//		finally {
//			if(fout != null) {
//				fout.close();
//			}
//		}
//		
//		ChartVerificationUtil.prettyFormatter(chart);
//	}
	
//	private static void prettyFormatter(final Chart chart) throws IOException {
//		String inputFilename = ChartVerificationUtil.getFileName(chart);
//		String outputFilename = ChartVerificationUtil.getNewFileName(chart);
//		
//		FileReader fr = new FileReader(inputFilename); 
//		BufferedReader br = new BufferedReader(fr); 
//		FileWriter fw = new FileWriter(outputFilename); 
//		String line;
//
//		while((line = br.readLine()) != null)
//		{ 
//		    line = line.trim(); // remove leading and trailing whitespace
//		    if (! line.equals("")) // don't write out blank lines
//		    {
//		        fw.write(line, 0, line.length());
//		    }
//		} 
//		fr.close();
//		fw.close();
//	}
	
//	private static String getFileName(final Chart chart) {
//		Integer groupNumber = chart.getGroup().getNumber();
//		String houseType = chart.getSession().getHouse().getType().getType();
//		
//		Date date = chart.getAnsweringDate();
//		String strDate = date.toString().replaceAll("/", "").replaceAll(":", "");
//		
//		StringBuffer sb = new StringBuffer();
//		sb.append("D:\\chart");
//		sb.append("_id-" + chart.getId().toString());
//		sb.append("_houseType-" + houseType);
//		sb.append("_group-" + groupNumber.toString());
//		sb.append("_answeringDate-" + strDate);
//		sb.append(".txt");
//		
//		return sb.toString();
//	}
//	
//	private static String getNewFileName(final Chart chart) {
//		Integer groupNumber = chart.getGroup().getNumber();
//		String houseType = chart.getSession().getHouse().getType().getType();
//		
//		Date date = chart.getAnsweringDate();
//		String strDate = date.toString().replaceAll("/", "").replaceAll(":", "");
//		
//		StringBuffer sb = new StringBuffer();
//		sb.append("D:\\final\\chart");
//		sb.append("_id-" + chart.getId().toString());
//		sb.append("_houseType-" + houseType);
//		sb.append("_group-" + groupNumber.toString());
//		sb.append("_answeringDate-" + strDate);
//		sb.append(".txt");
//		
//		return sb.toString();
//	}
	
//	private static void verify(final List<Device> deviceList,
//			final Date answeringDate,
//			final FileOutputStream fout) throws IOException {
//		boolean isFaulty = false;
//		
//		List<Question> questionList = 
//			ChartVerificationUtil.convertToQuestions(deviceList);
//		
//		List<Question> correctOrderQuestionList =
//			ChartVerificationUtil.reorderQuestions(questionList, answeringDate);
//		
//		int size = questionList.size();
//		for(int i = 0; i < size; i++) {
//			Question question = questionList.get(i);
//			Long questionId = question.getId();
//			
//			Question correctOrderQuestion = correctOrderQuestionList.get(i);
//			Long correctOrderQuestionId = correctOrderQuestion.getId();
//			
//			if(! questionId.equals(correctOrderQuestionId)) {
//				isFaulty = true;
//				break;
//			}
//		}
//		
//		if(isFaulty) {
//			fout.write("\n".getBytes());
//			fout.write("Incorrect order is: ".getBytes());
//			for(Question q : questionList) {
//				fout.write(q.getNumber().toString().getBytes());
//				fout.write("\t".getBytes());
//				fout.flush();
//			}
//			
//			fout.write("\n".getBytes());
//			fout.write("Correct order is: ".getBytes());
//			for(Question q : correctOrderQuestionList) {
//				fout.write(q.getNumber().toString().getBytes());
//				fout.write("\t".getBytes());
//				fout.flush();
//			}
//		}
//	}

	private static List<Question> convertToQuestions(
			final List<Device> deviceList) {
		List<Question> questionList = new ArrayList<Question>();
		
		for(Device d : deviceList) {
			Question q = (Question) d;
			questionList.add(q);
		}
		
		return questionList;
	}

	private static List<Question> reorderQuestions(
			final List<Question> onChartQuestions,
			final Date answeringDate) {
		List<Question> datedQList = new ArrayList<Question>();
		List<Question> beforeDatedQList = new ArrayList<Question>();
		List<Question> afterDatedQList = new ArrayList<Question>();
		List<Question> nonDatedQList = new ArrayList<Question>();
		
		for(Question q : onChartQuestions) {
			if(q.getAnsweringDate() == null) {
				nonDatedQList.add(q);
			}
			else {
				if(q.getAnsweringDate().getAnsweringDate().compareTo(answeringDate) < 0) {
					beforeDatedQList.add(q);
				}
				else if(q.getAnsweringDate().getAnsweringDate().compareTo(answeringDate) > 0) {
					afterDatedQList.add(q);
				}
				else {// q.getAnsweringDate().getAnsweringDate().compareTo(answeringDate) == 0
					datedQList.add(q);
				}
				
			}
		}
		
		datedQList = Question.sortByNumber(datedQList, ApplicationConstants.ASC);
		beforeDatedQList = Question.sortByAnsweringDate(beforeDatedQList, ApplicationConstants.ASC);
		nonDatedQList = Question.sortByNumber(nonDatedQList, ApplicationConstants.ASC);
		afterDatedQList = Question.sortByAnsweringDate(afterDatedQList, ApplicationConstants.ASC);
		
		List<Question> candidateQList = new ArrayList<Question>();
		candidateQList.addAll(datedQList);
		candidateQList.addAll(beforeDatedQList);
		candidateQList.addAll(nonDatedQList);
		candidateQList.addAll(afterDatedQList);

		return candidateQList;
	}
	
}