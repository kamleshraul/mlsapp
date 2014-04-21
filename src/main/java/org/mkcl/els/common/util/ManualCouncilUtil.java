package org.mkcl.els.common.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Chart;
import org.mkcl.els.domain.ChartEntry;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;

public class ManualCouncilUtil {
	
	/**
	 * STEP 1
	 */
	// groupNo: 1, 2, 3, 4, or 5
	// chartNo: 1, 2, 3, 4, or 5
	public void createChart(final Integer groupNo, final Integer chartNo) {
		if(groupNo.equals(new Integer(1))) {
			if(chartNo.equals(new Integer(1))) {
				createChartHelper(group1, 
						group1Chart1, group1Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				createChartHelper(group1, 
						group1Chart2, group1Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				createChartHelper(group1, 
						group1Chart3, group1Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				createChartHelper(group1, 
						group1Chart4, group1Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(2))) {
			if(chartNo.equals(new Integer(1))) {
				createChartHelper(group2, 
						group2Chart1, group2Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				createChartHelper(group2, 
						group2Chart2, group2Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				createChartHelper(group2, 
						group2Chart3, group2Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				createChartHelper(group2, 
						group2Chart4, group2Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(3))) {
			if(chartNo.equals(new Integer(1))) {
				createChartHelper(group3, 
						group3Chart1, group3Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				createChartHelper(group3, 
						group3Chart2, group3Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				createChartHelper(group3, 
						group3Chart3, group3Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				createChartHelper(group3, 
						group3Chart4, group3Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(4))) {
			if(chartNo.equals(new Integer(1))) {
				createChartHelper(group4, 
						group4Chart1, group4Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				createChartHelper(group4, 
						group4Chart2, group4Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				createChartHelper(group4, 
						group4Chart3, group4Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				createChartHelper(group4, 
						group4Chart4, group4Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(5))) {
			if(chartNo.equals(new Integer(1))) {
				createChartHelper(group5, 
						group5Chart1, group5Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				createChartHelper(group5, 
						group5Chart2, group5Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				createChartHelper(group5, 
						group5Chart3, group5Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				createChartHelper(group5, 
						group5Chart4, group5Chart4AnsweringDate);
			}
		}
	}
	
	/**
	 * STEP 2
	 */
	public void clubQuestions() {
		Session session = Session.findById(Session.class, sessionId);
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.STARRED_QUESTION, 
				ApplicationConstants.DEFAULT_LOCALE);
		int length = clubbings.length;
		for(int i = 0; i < length; i++) {
			clubEach(session, deviceType, clubbings[i]);
		}
	}
	
	/**
	 * STEP 3
	 */
	// groupNo: 1, 2, 3, 4, or 5
	// chartNo: 1, 2, 3, 4, or 5
	public void updateChart(final Integer groupNo, final Integer chartNo) 
		throws ELSException {
		if(groupNo.equals(new Integer(1))) {
			if(chartNo.equals(new Integer(1))) {
				updateChartHelper(group1, group1Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				updateChartHelper(group1, group1Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				updateChartHelper(group1, group1Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				updateChartHelper(group1, group1Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(2))) {
			if(chartNo.equals(new Integer(1))) {
				updateChartHelper(group2, group2Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				updateChartHelper(group2, group2Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				updateChartHelper(group2, group2Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				updateChartHelper(group2, group2Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(3))) {
			if(chartNo.equals(new Integer(1))) {
				updateChartHelper(group3, group3Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				updateChartHelper(group3, group3Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				updateChartHelper(group3, group3Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				updateChartHelper(group3, group3Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(4))) {
			if(chartNo.equals(new Integer(1))) {
				updateChartHelper(group4, group4Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				updateChartHelper(group4, group4Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				updateChartHelper(group4, group4Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				updateChartHelper(group4, group4Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(5))) {
			if(chartNo.equals(new Integer(1))) {
				updateChartHelper(group5, group5Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				updateChartHelper(group5, group5Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				updateChartHelper(group5, group5Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				updateChartHelper(group5, group5Chart4AnsweringDate);
			}
		}
	}
	
	//========== FIELDS TO BE POPULATED ====
	// Each entry in the array is a comma separated String.
	// The first token refers to Member id and the remaining
	// tokens refer to Question numbers.
	// If a Member does not have any Question, then there will
	// be only one token in the String, that of Member id.
	private String[] group1Chart1 = {"326, 48859, 48861, 48862, 48863, 48864",
			"339, 48898, 48905, 48907, 48908, 48910",
			"347, 48921, 48926, 48930, 48932, 48933",
			"298, 48956, 48958, 48971, 48972",
			"856, 49008, 48985, 48997",
			"855, 49014, 49015, 49016, 49017, 49018",
			"310, 49051, 49053, 49055, 49063, 49064",
			"305, 49103",
			"355, 49107, 49111, 49120, 49121, 49122",
			"904, 49132, 49133, 49134",
			"356, 49173, 49179, 49180",
			"295, 49194, 49196, 49197, 49199, 49200",
			"363, 49225, 49227, 49228, 49239, 49241",
			"358, 49275, 49284",
			"321, 49289, 49306, 49307",
			"349, 49330, 49340, 49344",
			"909, 49361, 49364, 49376, 50269",
			"906, 49379, 49390, 49393, 49398, 49399",
			"852, 49418, 49421, 49431, 49433",
			"296, 49443, 49444, 49451, 49459",
			"907, 49466, 49472, 49474, 49476, 49477",
			"322, 49544, 49545, 49546, 49547, 49548",
			"372, 49555, 49556, 49557, 49558",
			"344, 49589, 49591, 49600, 49610, 49614",
			"307, 49617, 49618, 49620, 49630",
			"364, 49649, 49650, 49651, 49652",
			"851, 49707, 49708, 49709",
			"911, 49710, 49711",
			"325, 49741, 49742, 49743, 49744, 49745",
			"314, 49797, 49798, 49799, 49800",
			"912, 49811, 49812, 49822, 49823, 49826",
			"3002, 49835",
			"900, 49848, 49849, 49850, 49851, 49852",
			"348, 49894, 49909",
			"910, 49919, 49937",
			"902, 49942, 49951, 49959, 49969, 49971",
			"350, 50012, 50014, 50020, 50028, 50029",
			"365, 50034, 50036, 50044, 50050, 50051",
			"345, 50065, 50066, 50067, 50068, 50070",
			"301, 50106, 50110, 50111, 50117",
			"341, 50130, 50131, 50135, 50136, 50138",
			"346, 50188, 50193, 50199, 50200",
			"366, 50225, 50239, 50244",
			"353, 50250, 50262, 50263, 50265, 50266",
			"903, 49977, 49979, 49990, 49997, 49999",
			"338, 49519",
			"299",
			"306",
			"312",
			"313",
			"308",
			"319",
			"323",
			"333",
			"334",
			"342",
			"343",
			"351",
			"354",
			"357",
			"905",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000"};
	
	private String[] group1Chart2 = {"339, 48911, 48914",
			"347, 48934, 48936, 48943, 48951",
			"855, 49044",
			"310, 49067, 49072, 49073, 49075",
			"355, 49123, 49124, 49126, 49127",
			"295, 49202, 49205, 49208, 49215, 49219",
			"363, 49246, 49252",
			"907, 49478, 49487, 49491, 49494, 49495",
			"372, 49560",
			"364, 49653, 49659, 49661",
			"314, 49802",
			"912, 49829",
			"900, 49853, 49873",
			"350, 50031, 50033",
			"365, 50055, 50058, 50062",
			"345, 50071",
			"341, 50134, 50145, 50149, 50152",
			"346, 50205",
			"322, 49554",
			"344, 49592",
			"356, 49175",
			"903, 50000, 50002",
			"296",
			"298",
			"299",
			"301",
			"305",
			"306",
			"307",
			"312",
			"313",
			"318",
			"319",
			"321",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"342",
			"343",
			"348",
			"349",
			"351",
			"353",
			"354",
			"357",
			"358",
			"366",
			"851",
			"852",
			"853",
			"856",
			"902",
			"904",
			"905",
			"906",
			"909",
			"910",
			"911",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group1Chart3 = {"346, 50192, 50213",
			"295, 49207",
			"907, 49482, 49496",
			"296",
			"298",
			"299",
			"301",
			"305",
			"306",
			"307",
			"310",
			"312",
			"313",
			"314",
			"318",
			"319",
			"321",
			"322",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"344",
			"345",
			"347",
			"348",
			"349",
			"350",
			"351",
			"353",
			"354",
			"355",
			"356",
			"357",
			"358",
			"363",
			"364",
			"365",
			"366",
			"372",
			"851",
			"852",
			"853",
			"855",
			"856",
			"900",
			"902",
			"903",
			"904",
			"905",
			"906",
			"909",
			"910",
			"911",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group1Chart4 = {"295",
			"296",
			"298",
			"299",
			"301",
			"305",
			"306",
			"307",
			"310",
			"312",
			"313",
			"314",
			"318",
			"319",
			"321",
			"322",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"344",
			"345",
			"346",
			"347",
			"348",
			"349",
			"350",
			"351",
			"353",
			"354",
			"355",
			"356",
			"357",
			"358",
			"363",
			"364",
			"365",
			"366",
			"372",
			"851",
			"852",
			"853",
			"855",
			"856",
			"900",
			"902",
			"903",
			"904",
			"905",
			"906",
			"907",
			"909",
			"910",
			"911",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group2Chart1 = {"326,48860,48865,48866,48868,48867",
			"339,48904,48913,48915,48916,48917",
			"347,48923,48929,48936,48938,48947",
			"298,48957",
			"856,48987,48993,48999,49001",
			"855,49019,49020,49021,49022,49026",
			"305,49079,49080,49092,49094,49096",
			"355,49108,49112,49118,49125,49128",
			"904,49131,49135,49136,49137,49138",
			"356,49177",
			"295,49201,49222",
			"363,49245,49250",
			"358,49261,49262,49270,49272,49274",
			"321,49291,49295,49316",
			"349,49323,49328,49338,49349",
			"909,49355,49362,49368,49369",
			"906,49378,49381,49382,49388,49389",
			"852,49408,49409,49410,49412,49417",
			"296,49437",
			"902,49485,49492",
			"322,49524,49525,49545,49549,49550",
			"372,49561,49562,49563,49564,49565",
			"344,49587",
			"307,49621,49622,49623,49624,49629",
			"364,49657,49658,49660,49662,49663",
			"851,49697,49698,49699,49700,49701",
			"911,49712,49713,49714,49715",
			"314,49775,49791,49792,49793,49795",
			"912,49815,49828,49830",
			"3002,49843,49844",
			"900,49856,49857",
			"348,49887,49888,49889,49895,49900",
			"910,49921,49929,49939,49940",
			"902,49941,49947,49949,49954,49956",
			"350,50003,50016,50021,50022,50023",
			"365,50042,50045,50046,50049,50063",
			"345,50080,50081,50082,50083,50084",
			"301,50119",
			"346,50194,50198",
			"366,50241,50242,50243",
			"353,50253,50256,50264",
			"903,49972,49986,49987,49994,50001",
			"299",
			"306",
			"310",
			"312",
			"313",
			"318",
			"319",
			"323",
			"325",
			"333",
			"334",
			"338",
			"341",
			"342",
			"343",
			"351",
			"354",
			"357",
			"853",
			"905",
			"907",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group2Chart2 = {"326,48870,48869, 48888",
			"347,48949",
			"855,49024,49043",
			"305,49097,49098,49099,49100",
			"355,49130",
			"904,49139,49154",
			"358,49283,49285",
			"906,49395,49396,49400,49406",
			"852,49425,49426,49430",
			"322,49551,49552,49553",
			"372,49566",
			"851,49702,49703,49705,49706",
			"314,49796",
			"902,49960,49962,49970",
			"350,50025,50030",
			"346,50184,50207,50208,50211",
			"307,49638",
			"856,49012",
			"295",
			"296",
			"298",
			"299",
			"301",
			"306",
			"310",
			"312",
			"313",
			"318",
			"319",
			"321",
			"323",
			"325",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"344",
			"345",
			"348",
			"349",
			"351",
			"353",
			"354",
			"356",
			"357",
			"363",
			"364",
			"365",
			"366",
			"853",
			"900",
			"903",
			"905",
			"907",
			"909",
			"910",
			"911",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group2Chart3 = {"305,49105",
			"346,50186,50190,50210",
			"295",
			"296",
			"298",
			"299",
			"301",
			"306",
			"307",
			"310",
			"312",
			"313",
			"314",
			"318",
			"319",
			"321",
			"322",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"344",
			"345",
			"347",
			"348",
			"349",
			"350",
			"351",
			"353",
			"354",
			"355",
			"356",
			"357",
			"358",
			"363",
			"364",
			"365",
			"366",
			"372",
			"851",
			"852",
			"853",
			"855",
			"856",
			"900",
			"902",
			"903",
			"904",
			"905",
			"906",
			"907",
			"909",
			"910",
			"911",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group2Chart4 = {"346,50209,50212",
			"295",
			"296",
			"298",
			"299",
			"301",
			"305",
			"306",
			"307",
			"310",
			"312",
			"313",
			"314",
			"318",
			"319",
			"321",
			"322",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"344",
			"345",
			"347",
			"348",
			"349",
			"350",
			"351",
			"353",
			"354",
			"355",
			"356",
			"357",
			"358",
			"363",
			"364",
			"365",
			"366",
			"372",
			"851",
			"852",
			"853",
			"855",
			"856",
			"900",
			"902",
			"903",
			"904",
			"905",
			"906",
			"907",
			"909",
			"910",
			"911",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group3Chart1 = {"326,48871,48872,48873,48874,48875",
			"339,48901,48909,48920",
			"347,48925,48927,48928,48935,48940",
			"298,48955",
			"856,48984,48986,48990,48991,48992",
			"855,49025,49028,49029,49030",
			"310,49054,49056,49057,49065,49070",
			"305,49077,49081,49083,49084,49085",
			"355,49109,49129",
			"904,49140,49141,49142,49143,49144",
			"356,49171,49174,49186",
			"295,49193,49195,49206,49210,49214",
			"363,49224,49248,49249,49247,49251",
			"358,49256,49258,49259,49265,49266",
			"321,49290,49293,49296,49299,49302",
			"349,49319,49321,49324,49325,49326",
			"909,49348,49352,49353,49356,49357",
			"906,49387,49407,49402,49385",
			"852,49419,49420,49422",
			"296,49446,49457",
			"907,49469,49470,49471,49473,49480",
			"338,49497,49499,49520,49523",
			"322,49527",
			"372,49567,49568,49569,49570,49571",
			"344,49588,49590,49593,49594,49595",
			"307,49619,49625,49626,49627,49628",
			"364,49655,49656,49664,49665,49666",
			"851,49685,49686,49687,49688,49689",
			"911,49716,49717,49718,49719,49720",
			"314,49779,49780,49781,49782,49783",
			"912,49814,49816,49825,49820,49821",
			"3002,49834,49836,49838,49839,49841",
			"900,49858,49859,49860,49861,49862",
			"348,49890,49891,49897,49898.49902",
			"910,49913,49915,49920,49923,49926",
			"902,49943,49944,49963",
			"350,50005,50006,50008,50011,50015",
			"365,50035,50037,50038,50039,50041",
			"345,50072,50073,50074,50075,50076",
			"342,50128,50133,50136",
			"346,50185,50197",
			"353,50246,50251",
			"903,49981,49982,49983,49985,49988",
			"299",
			"301",
			"306",
			"312",
			"313",
			"318",
			"319",
			"323",
			"325",
			"333",
			"334",
			"341",
			"343",
			"346",
			"348",
			"351",
			"354",
			"357",
			"366",
			"853",
			"902",
			"905",
			"910",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000"};
	
	private String[] group3Chart2 = {"326,48876,48877,48878",
			"347,48941,48942,48944,48950",
			"856,48994,48995,48996,48998,49000",
			"305,49086,49088,49089,49090,49093",
			"295,49216,49217,49220",
			"358,49267,49268,49269,49276,49278",
			"321,49303,49304,49305,49309,49313",
			"349,49327,49329,49333,49334,49335",
			"909,49358,49359,49363,49365,49366",
			"907,49481,49483,49484,49486,49490",
			"372,49572,49573,49582",
			"344,49596,49598,49601,49602,49608",
			"307,49631,49632,49633",
			"851,49690,49691,49692,49693,49694",
			"911,49721,49722,49723,49724,49725",
			"314,49784,49785,49786,49787,49788",
			"912,49831,49833",
			"3002,49831,49833",
			"900,49855,49863,49864,49865,49867",
			"910,49930,49934,49935",
			"350,50024,50027,50032",
			"365,50056,50057,50059,50061",
			"903,49996,49989,49985",
			"364,49648",
			"348,49883",
			"296",
			"298",
			"299",
			"301",
			"306",
			"310",
			"312",
			"313",
			"318",
			"319",
			"322",
			"323",
			"325",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"345",
			"346",
			"351",
			"353",
			"354",
			"355",
			"356",
			"357",
			"363",
			"366",
			"852",
			"853",
			"855",
			"902",
			"904",
			"905",
			"906",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000"};
	
	private String[] group3Chart3 = {"856,49003,49004,49005,49007,49010",
			"305,49095,49101,49102,49104,49106",
			"358,49280,49281,49282",
			"349,49336,49337,49339,49343,49345",
			"909,49370,49372,49374,49375",
			"344,49609,49611,49613,49616",
			"307,49634",
			"851,49695,49696,49704",
			"911,49726,49727,49728,49729,49730",
			"314,49789,49790,49794,49801,49773",
			"295",
			"296",
			"298",
			"299",
			"301",
			"306",
			"310",
			"312",
			"313",
			"318",
			"319",
			"321",
			"322",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"345",
			"346",
			"347",
			"348",
			"350",
			"351",
			"353",
			"354",
			"355",
			"356",
			"357",
			"363",
			"364",
			"365",
			"366",
			"372",
			"852",
			"853",
			"855",
			"900",
			"902",
			"903",
			"904",
			"905",
			"906",
			"907",
			"910",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group3Chart4 = {"856,49013,49006",
			"911,49731",
			"305,49078",
			"295",
			"296",
			"298",
			"299",
			"301",
			"306",
			"307",
			"310",
			"312",
			"313",
			"314",
			"318",
			"319",
			"321",
			"322",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"344",
			"345",
			"346",
			"347",
			"348",
			"349",
			"350",
			"351",
			"353",
			"354",
			"355",
			"356",
			"357",
			"358",
			"363",
			"364",
			"365",
			"366",
			"372",
			"851",
			"852",
			"853",
			"855",
			"900",
			"902",
			"903",
			"904",
			"905",
			"906",
			"907",
			"909",
			"910",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group4Chart1 = {"326, 48879, 48881, 48882, 48883",
			"339, 48890, 48891, 48892, 48893, 48894",
			"347, 48945, 48948",
			"298, 48952, 48960, 48966, 48967, 48969",
			"856, 48988, 48989",
			"855, 49026, 49032, 49033, 49034, 49035",
			"310, 49046, 49047, 49049, 49050, 49052",
			"305, 49091, 49098",
			"355, 49115, 49119",
			"904, 49145, 49146, 49147, 49148, 49149",
			"356, 49162, 49163, 49164, 49165, 49166",
			"295, 49198, 49204, 49211, 49212, 49213",
			"363, 49226, 49229, 49230, 49231, 49242",
			"358, 49260, 49279",
			"321, 49286, 49287, 49292, 49297, 49300",
			"349, 49317, 49318, 49320, 49342, 49347",
			"909, 49349, 49350, 49354, 49360, 49364",
			"906, 49383, 49386, 49391, 49394",
			"296, 49439, 49440, 49441, 49442, 49448",
			"907, 49467, 49479, 49488",
			"338, 49500, 49501, 49502, 49503, 49504",
			"322, 49529, 49530, 49531, 49533, 49534",
			"372, 49574, 49575, 49576, 49577, 49578",
			"344, 49586, 49597, 59599, 49603, 49607",
			"307, 49635, 49636, 49637, 49639, 49640",
			"364, 49654, 49667, 49668, 49671",
			"851, 49684",
			"911, 49733, 49734, 49735, 49736",
			"325, 49747, 49748, 49749, 49750, 49751",
			"314, 49772, 49774, 49776, 49777, 49778",
			"912, 49803, 49806, 49807, 49808, 49809",
			"3002, 49837, 49840",
			"100, 49868, 49869, 49870, 49871, 49872",
			"348, 49879, 49882, 49893, 49896, 49901",
			"910, 49911, 49912, 49914, 49917, 49925",
			"902, 49946, 49948, 49955, 49965, 49966",
			"350, 50004, 50009, 50010, 50013, 50017",
			"365, 50043, 50047, 50048, 50052, 50053",
			"345, 50069, 50077, 50078, 50079, 50093",
			"301, 50096, 50097, 50098, 50099, 50100",
			"341, 50127, 50132, 50139, 50140, 50141",
			"346, 50195, 50201",
			"366, 50214, 50215, 50216, 50217, 50218",
			"353, 50245, 50247, 50257, 50258, 50260",
			"852, 49414, 49432",
			"903, 49973, 49974,49975, 49976, 49984",
			"299",
			"306",
			"312",
			"313",
			"318",
			"319",
			"323",
			"333",
			"334",
			"342",
			"343",
			"351",
			"354",
			"357",
			"366",
			"853",
			"905",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000"
			};
	
	private String[] group4Chart2 = {"339,48895,48896,48899,48900,48902",
			"298,48970,48977,48978,48979,48981",
			"855,49036",
			"310,49058,49061,49062,49066,49068",
			"904,49150,49151,49152,49153,49155", 
			"356,49167,49168,49169,49170,49172",
			"363,49233,49236,49237,49240,49243",
			"321,49311,49312",
			"909,49373,49531",
			"906,49397,49404",
			"296,49449,49450,49456,49458",
			"338,49505,49506,49509,49510,49511",
			"322,49528**,49535,49536,49537",
			"344,49604,49615",
			"307,49649,49642",
			"325,49752,49753,49754,49755,49756",
			"912,49810,49813,49817,49818,49819",
			"900,49874",
			"910,50018,50026",
			"365,50060,50064",
			"301,50101,50102,50103,50104,50105",
			"341,50142,50144,50146,50154,50156",
			"366,50219,50220,50221,50222,50223",
			"353,50261,50268",
			"903,49991,49992",
			"348,49885,49904",
			"295",
			"299",
			"305",
			"306",
			"312",
			"313",
			"314",
			"318",
			"319",
			"323",
			"326",
			"333",
			"334",
			"342",
			"343",
			"345",
			"346",
			"347",
			"349",
			"350",
			"351",
			"354",
			"355",
			"357",
			"358",
			"364",
			"372",
			"851",
			"852",
			"853",
			"856",
			"902",
			"905",
			"907",
			"911",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group4Chart3 = {"339,48903,48912,48918,48919",
			"298,48980",
			"310,49069,49071,49074",
			"356,49176,49178,49181,49182,49183",
			"363,49244,49253",
			"296,49460,49464,49465",
			"338,49512,49513,49514,49515,49516",
			"325,49756,49758,49759,49762,49763",
			"301,50108,50509,50115,50116",
			"341,50157",
			"366,50224,50227,50228,50229,50230",
			"912,49804",
			"295",
			"299",
			"305",
			"306",
			"307",
			"312",
			"313",
			"314",
			"318",
			"319",
			"321",
			"322",
			"323",
			"326",
			"333",
			"334",
			"342",
			"343",
			"344",
			"345",
			"346",
			"347",
			"348",
			"349",
			"350",
			"351",
			"353",
			"354",
			"355",
			"357",
			"358",
			"364",
			"365",
			"372",
			"851",
			"852",
			"853",
			"855",
			"856",
			"900",
			"902",
			"903",
			"904",
			"905",
			"906",
			"907",
			"909",
			"910",
			"911",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group4Chart4 = {"356,49184,49185,49186,49188,49189,49190,49191,49192",
			"338,49516,49518,49522",
			"325,49746,49764,49765,49766",
			"301,50118,50121,50122,50123,50124,50125,50126",
			"366,50231,50232,50233,50234,50235,50236,50237,50238,50240",
			"295",
			"296",
			"298",
			"299",
			"305",
			"306",
			"307",
			"310",
			"312",
			"313",
			"314",
			"318",
			"319",
			"321",
			"322",
			"323",
			"326",
			"333",
			"334",
			"339",
			"341",
			"342",
			"343",
			"344",
			"345",
			"346",
			"347",
			"348",
			"349",
			"350",
			"351",
			"353",
			"354",
			"355",
			"357",
			"358",
			"363",
			"364",
			"365",
			"372",
			"851",
			"852",
			"853",
			"855",
			"856",
			"900",
			"902",
			"903",
			"904",
			"905",
			"906",
			"907",
			"909",
			"910",
			"911",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group5Chart1 = {"326, 48884, 48885, 48886, 48887, 48889",
			"339, 48897, 48906, 48939",
			"347, 48922, 48924, 48931, 48939, 48946",
			"298, 48953, 48954, 48959, 48961, 48962",
			"856, 48983, 49002, 49009",
			"855, 49036, 49038, 49039, 49040, 49041",
			"305, 49076, 49082, 49087",
			"355, 49110, 49113, 49114, 49116, 49117",
			"310, 49045, 49048, 49057, 49060",
			"904, 49156, 49157, 49158, 49159, 49160",
			"295, 49203, 49209, 49218, 49221, 49223",
			"363, 49232, 49234, 49235, 49238, 49254",
			"358, 49255, 49263, 49264, 49271, 49273",
			"321, 49288, 49294, 49298, 49301, 49308",
			"323, 49322",
			"349, 49331, 49332, 49346",
			"909, 49371, 49377",
			"906, 49380, 49384, 49392, 49401, 49403",
			"852, 49411, 49413, 49415, 49416, 49423",
			"296, 49435, 49436, 49445, 49447, 49452",
			"907, 49468, 49475, 49489, 49493",
			"338, 49498, 49507, 49508, 49521",
			"322, 49538, 49539, 49540, 49542, 49543",
			"372, 49581, 49583, 49584, 49585",
			"344, 49605, 49606, 49612",
			"307, 49643, 49644, 49645, 49646, 49647",
			"364, 49672, 49674, 49675, 49676, 49677",
			"851, 49679, 49680, 49681, 49682, 49683",
			"911, 49733, 49737, 49738, 49739, 49740",
			"325, 49760, 49761, 49767, 49768, 49769",
			"912, 49805, 49824, 49827, 49832",
			"900, 49866, 49875, 49876, 49877, 49878",
			"348, 49880, 49881, 49884",
			"910, 49910, 49916, 49918, 49922, 49924",
			"902, 49945, 49950, 49952, 49953",
			"350, 50007, 50019",
			"365, 50040, 50045, 50054",
			"345, 50085, 50086, 50088, 50089, 50090",
			"301, 50107, 50113, 50114, 50120",
			"341, 50129, 50143, 50147",
			"346, 50183, 50187, 50188, 50189, 50202",
			"366, 50226",
			"353, 50248, 50254, 50255, 50259, 50267",
			"903, 49978, 49980, 49993, 49998",
			"299",
			"306",
			"312",
			"313",
			"314",
			"318",
			"319",
			"333",
			"334",
			"342",
			"343",
			"351",
			"354",
			"365",
			"357",
			"853",
			"905",
			"2500",
			"2501",
			"2504",
			"3000",
			"3002"};
	
	private String[] group5Chart2 = {"298,48963,48964,48965,48968,48982",
			"855, 49027, 49031, 49042",
			"904, 49161",
			"321, 49310, 49314, 49315",
			"906, 49405, 50270",
			"852, 49424, 49427,49428, 49429, 49434",
			"296, 49453, 49454, 49461, 49462, 49463",
			"364, 49669, 49670, 49673, 49678",
			"325, 49770",
			"348, 49886, 49903, 49905, 49906",
			"910, 49927, 49928, 49931, 49938",
			"902, 49958, 49961, 49964, 49967, 49968",
			"345, 50087, 50091, 50092, 50094, 50095",
			"341, 50148, 50150, 50151, 50153, 50155",
			"346, 50191, 50196, 50203, 50204",
			"322, 49526, 49532, 49541",
			"372, 49559, 49580",
			"358, 49257, 49277",
			"301, 50112",
			"353, 50249, 50252",
			"295",
			"299",
			"305",
			"306",
			"307",
			"310",
			"312",
			"313",
			"314",
			"318",
			"319",
			"323",
			"326",
			"333",
			"334",
			"338",
			"339",
			"342",
			"343",
			"344",
			"347",
			"349",
			"350",
			"351",
			"354",
			"355",
			"356",
			"357",
			"363",
			"365",
			"366",
			"851",
			"853",
			"856",
			"900",
			"903",
			"905",
			"907",
			"909",
			"911",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002",
			
			};
	
	private String[] group5Chart3 = {"298, 48973, 48974, 48975, 48976",
			"348, 49892, 49899, 49907, 49908",
			"346, 50206",
			"902, 49957",
			"296, 49455",
			"295",
			"299",
			"301",
			"305",
			"306",
			"307",
			"310",
			"312",
			"313",
			"314",
			"318",
			"319",
			"321",
			"322",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"344",
			"345",
			"347",
			"349",
			"350",
			"351",
			"353",
			"354",
			"355",
			"356",
			"357",
			"358",
			"363",
			"364",
			"365",
			"366",
			"372",
			"851",
			"852",
			"853",
			"855",
			"856",
			"900",
			"903",
			"904",
			"905",
			"906",
			"907",
			"909",
			"910",
			"911",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	private String[] group5Chart4 = {"295",
			"296",
			"298",
			"299",
			"301",
			"305",
			"306",
			"307",
			"310",
			"312",
			"313",
			"314",
			"318",
			"319",
			"321",
			"322",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"344",
			"345",
			"346",
			"347",
			"348",
			"349",
			"350",
			"351",
			"353",
			"354",
			"355",
			"356",
			"357",
			"358",
			"363",
			"364",
			"365",
			"366",
			"372",
			"851",
			"852",
			"853",
			"855",
			"856",
			"900",
			"902",
			"903",
			"904",
			"905",
			"906",
			"907",
			"909",
			"910",
			"911",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3002"};
	
	// Comma separated list of Question numbers
	// First token is the parent question and subsequent tokens
	// are child questions in the order specified.
	private String[] clubbings = {
			"48860, 49921",
			"48864, 48907, 49849, 49215",
			"48877, 48994",
			"48884, 49059, 49114, 49117, 49294, 49827, 49161, 49673, 49580, 49277",
			"48885, 50089",
			"48887, 49218",
			"48889, 48906, 49116, 49160, 49384, 49583, 50040",
			"48893, 49145, 49503",
			"48894, 50013",
			"48901, 49842",
			"48905, 49544, 49741, 49560",
			"48910, 49852",
			"48920, 49299, 49686, 50039",
			"48951, 50205",
			
			"48959, 49082, 49255, 49423, 49521, 50085, 50248, 50204, 50249",
			"48991, 49598",
			"48997, 49246",
			
			"49008, 50068",
			"49015, 50244",
			"49016, 50239",
			"49020, 50241",
			"49021, 50242, 49130",
			"49063, 49443",
			"49072, 49207",
			"49091, 49212",
			"49109, 49142, 49402, 49567, 49820, 50037, 49278",
			"49115, 50004",
			"49125, 49094, 49856",
			"49129, 49304",
			"49141, 49471",
			"49180, 50269",
			"49211, 49342",
			"49226, 49984",
			"49248, 49588, 49836",
			"49267, 49335",
			"49274, 49657",
			"49284, 49745",
			"49289, 49556, 50031",
			"49290, 49321",
			"49291, 49369",
			"49293, 49319, 49595, 49839",
			"49295, 49368, 50025",	
			"49296, 49594, 49329",
			"49298, 50054",
			"49306, 49127, 50055",
			"49307, 49344, 49376, 49650, 49126",
			"49320, 49975",
			"49322, 49371, 49928",
			"49325, 49608",
			"49326, 49313",
			"49330, 50029",
			"49332, 49310",
			"49340, 49617, 50020, 49977, 48911",
			"49347, 49603",
			"49350, 49667",
			"49356, 49602",
			"49357,49858",
			"49367, 49976",
			"49387, 49573",
			"49390, 49433, 49591, 50131",
			"49409, 49417",
			"49421, 50262",
			"49475, 49760, 48976",
			"49499, 49841",
			"49545, 49919",
			"49549, 49941",
			"49557, 49798",
			"49569, 49216",
			"49600, 49979",
			"49610, 49829",
			"49626, 49690",
			"49649, 49651",
			"49716, 49722, 49724",
			"49734, 49911",
			"49767, 48968",
			"49781, 49359",
			"49786, 49846",
			"49814, 49582",
			"49816, 49333, 49363",
			"49981, 49982",
			"50081, 50243",
			"50011, 49089",
			"50198, 49098",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",};
	
	//========== INTERNAL METHODS ==========
	private void createChartHelper(final Long groupId, 
			final String[] groupChart,
			final String groupChartAnsweringDate) {
		Session session = Session.findById(Session.class, sessionId);
		Group group = Group.findById(Group.class, groupId);
		Date answeringDate = FormaterUtil.formatStringToDate(
				groupChartAnsweringDate, formatType);;
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.STARRED_QUESTION, 
				ApplicationConstants.DEFAULT_LOCALE);
		
		QuestionDates chartAnsweringDate = 
			group.findQuestionDatesByGroupAndAnsweringDate(answeringDate);
		
		Chart chart = new Chart(session, group, answeringDate, deviceType, 
				ApplicationConstants.DEFAULT_LOCALE);
		
		List<ChartEntry> memberChartEntries = 
			this.memberChartEntries(session, chartAnsweringDate, groupChart);		
		chart.getChartEntries().addAll(memberChartEntries);		
		chart.persist();
	}
	
	private List<ChartEntry> memberChartEntries(final Session session,
			final QuestionDates chartAnsweringDate,
			final String[] chart) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		int length = chart.length;
		for(int i = 0; i < length; i++) {
			String entry = chart[i];
			ChartEntry chartEntry = 
				this.chartEntry(session, chartAnsweringDate, entry);
			chartEntries.add(chartEntry);
		}
		
		return chartEntries;
	}
	
	private ChartEntry chartEntry(final Session session, 
			final QuestionDates chartAnsweringDate,
			final String entry) {
		String[] tokens = entry.split(",");
		
		// An entry will always have atleast 1 token
		String strMemberId = tokens[0].trim();
		Long memberId = Long.valueOf(strMemberId);
		Member member = Member.findById(Member.class, memberId);
		
		int length = tokens.length;		
		// Member has no Questions
		if(length == 1) {
			ChartEntry chartEntry = new ChartEntry();
			chartEntry.setLocale(ApplicationConstants.DEFAULT_LOCALE);
			chartEntry.setMember(member);
			return chartEntry;
		}
		// Member has Questions
		else {
			List<Device> memberQList = new ArrayList<Device>();
			
			// Since token 0 is Member id, hence start from token 1
			for(int i = 1; i < length; i++) {
				String strQuestionNumber = tokens[i].trim();
				Integer questionNumber = Integer.valueOf(strQuestionNumber);
				Question q = Question.find(session, questionNumber);
				q.setChartAnsweringDate(chartAnsweringDate);
				q.simpleMerge();
				
				memberQList.add(q);
			}
			
			ChartEntry chartEntry = new ChartEntry(member, memberQList, 
					ApplicationConstants.DEFAULT_LOCALE);
			return chartEntry;
		}
	}
	
	private void clubEach(final Session session, 
			final DeviceType deviceType,
			final String entry) {
		String[] tokens = entry.split(",");
		int length = tokens.length;
		
		// First token is the parent question
		String strParentQuestionNumber = tokens[0].trim();
		Integer parentQuestionNumber = Integer.valueOf(strParentQuestionNumber);
		Question parentQuestion = Question.find(session, parentQuestionNumber);
		
		List<ClubbedEntity> clubbedQList = new ArrayList<ClubbedEntity>();
		Status CLUBBED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_CLUBBED, 
				ApplicationConstants.DEFAULT_LOCALE);
		// Since token 0 is Parent Question number, hence start from token 1	
		for(int i = 1; i < length; i++) {	
			String strChildQuestionNumber = tokens[i].trim();
			Integer childQuestionNumber = 
				Integer.valueOf(strChildQuestionNumber);
			
			Question childQuestion = Question.find(session, childQuestionNumber);
			childQuestion.setParent(parentQuestion);
			childQuestion.setInternalStatus(CLUBBED);
			childQuestion.setRecommendationStatus(CLUBBED);
			childQuestion.simpleMerge();
			
			ClubbedEntity clubbedQ = new ClubbedEntity();
			clubbedQ.setPosition(i);
			clubbedQ.setDeviceType(deviceType);
			clubbedQ.setQuestion(childQuestion);
			clubbedQ.persist();
			
			clubbedQList.add(clubbedQ);
		}
		
		// Add the clubbedQList to Parent Question and merge
		parentQuestion.setClubbedEntities(clubbedQList);
		parentQuestion.simpleMerge();
	}
	
	private void updateChartHelper(final Long groupId,
			final String groupChartAnsweringDate) throws ELSException {
		Session session = Session.findById(Session.class, sessionId);
		Group group = Group.findById(Group.class, groupId);
		Date answeringDate = FormaterUtil.formatStringToDate(
				groupChartAnsweringDate, formatType);;
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.STARRED_QUESTION, 
				ApplicationConstants.DEFAULT_LOCALE);
		
		Status TO_BE_PUT_UP = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, 
				ApplicationConstants.DEFAULT_LOCALE);
		int toBePutUpPriority = TO_BE_PUT_UP.getPriority();
		
		Status CLUBBED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_CLUBBED, 
				ApplicationConstants.DEFAULT_LOCALE);
		
		List<Question> chartedQuestions = 
			Chart.findQuestions(session, group, answeringDate, deviceType, 
					ApplicationConstants.DEFAULT_LOCALE);
		for(Question q : chartedQuestions) {
			Status internalStatus = q.getInternalStatus();
			int internalStatusPriority = internalStatus.getPriority();
			if(internalStatusPriority < toBePutUpPriority
					&& ! (internalStatus.getType().equals(CLUBBED.getType()))) {
				q.setInternalStatus(TO_BE_PUT_UP);
				q.setRecommendationStatus(TO_BE_PUT_UP);
				q.simpleMerge();
			}
		}
	}

	// Id of the current Council session
	private Long sessionId = new Long(500);
	
	// Ids of Council groups as stored in the database.
	private Long group1 = new Long(2500);
	private Long group2 = new Long(2501);
	private Long group3 = new Long(2502);
	private Long group4 = new Long(2503);
	private Long group5 = new Long(2504);
	
	// AnsweringDate for each Chart of each Group
	private String group1Chart1AnsweringDate = "02/06/2014";
	private String group1Chart2AnsweringDate = "09/06/2014";
	private String group1Chart3AnsweringDate = "16/06/2014";
	private String group1Chart4AnsweringDate = "23/06/2014";
	
	private String group2Chart1AnsweringDate = "03/06/2014";
	private String group2Chart2AnsweringDate = "10/06/2014";
	private String group2Chart3AnsweringDate = "17/06/2014";
	private String group2Chart4AnsweringDate = "24/06/2014";
	
	private String group3Chart1AnsweringDate = "04/06/2014";
	private String group3Chart2AnsweringDate = "11/06/2014";
	private String group3Chart3AnsweringDate = "18/06/2014";
	private String group3Chart4AnsweringDate = "25/06/2014";
	
	private String group4Chart1AnsweringDate = "05/06/2014";
	private String group4Chart2AnsweringDate = "12/06/2014";
	private String group4Chart3AnsweringDate = "19/06/2014";
	private String group4Chart4AnsweringDate = "26/06/2014";
	
	private String group5Chart1AnsweringDate = "06/06/2014";
	private String group5Chart2AnsweringDate = "13/06/2014";
	private String group5Chart3AnsweringDate = "20/06/2014";
	private String group5Chart4AnsweringDate = "27/06/2014";
		
	// Date format type
	private String formatType = "dd/MM/yyyy";
}