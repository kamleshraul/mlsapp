;(function($){
/**
 * jqGrid English Translation
 * Tony Tomov tony@trirand.com
 * http://trirand.com/blog/ 
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
**/
$.jgrid = {
	defaults : {
		recordtext: "देखें {0} - {2} में से {1}",
		emptyrecords: "कोई रिकॉर्ड नहीं मिला",
		loadtext: "लोडिंग...",
		pgtext : "पेज {1} में से {0}"
	},
	search : {
		caption: "खोजिए...",
		Find: "खोजिए",
		Reset: "रीसेट",
		odata : ['बरोबर', 'ह्याच्या बरोबर नाही', 'कमी', 'कमी किंवा बरोबर','जास्त','जास्त किंवा बरोबर', 'सुरू होते','सुरू होत नाही','ह्यात आहे','ह्यात नाही','संपते','संपत नाही','समाविष्ट आहे','समाविष्ट नाही'],
		groupOps: [	{ op: "AND", text: "सब" },	{ op: "OR",  text: "कोई" }	],
		matchText: " मैच",
		rulesText: " निकष"
	},
	edit : {
		addCaption: "नोंद जोडा",
		editCaption: "एडीट नोंद",
		bSubmit: "जमा करणे",
		bCancel: "रद्द करणे",
		bClose: "बंद करणे",
		saveData: "माहिती बदलली आहे! बदल सेव करावे का?",
		bYes : "हो",
		bNo : "नाही",
		bExit : "रद्द करणे",
		msg: {
			required:"फील्ड  आवश्यक आहे",
			number:"कृपया, वैद्य संख्या भरणे",
			minValue:"जास्त किंवा बरोबर असलं पाहिजे ",
			maxValue:"कमी किंवा बरोबर असलं पाहिजे",
			email: "ई-मेल वैद्य नाही",
			integer: "कृपया, वैद्य पूर्णांक भरणे",
			date: "कृपया, वैद्य दिनांक भरणे",
			url: "यु आर ल  वैद्य नाही. उपसर्ग  आवश्यक ('http://' or 'https://')",
			nodefined : " ह्याची व्याख्या नाही आहे!",
			novalue : " रीटर्न वैलयू आवश्यक आहे!",
			customarray : "कस्टम  फनसन एंरे रीटर्न केला पाहिजे!",
			customfcheck : "Custom function should be present in case of custom checking!"
			
		}
	},
	view : {
		caption: "नोंद पहा",
		bClose: "बंद करणे"
	},
	del : {
		caption: "खोडन टाकणे",
		msg: "निवडलेली नोंदणी वगळणे का?",
		bSubmit: "खोडन टाकणे",
		bCancel: "रद्द करणे"
	},
	nav : {
		edittext: "",
		edittitle: "एडीट निवडलेली नोंद",
		addtext:"",
		addtitle: "नवीन नोंद जोडा",
		deltext: "",
		deltitle: "निवडलेली नोंद खोडन टाकणे",
		searchtext: "",
		searchtitle: "नोंद शोधा",
		refreshtext: "",
		refreshtitle: "रीलोड ग्रीड",
		alertcap: "चेतावणी",
		alerttext: "कृपया, नोंद निवडा",
		viewtext: "",
		viewtitle: "पहा निवडलेली नोंद"
	},
	col : {
		caption: "कॉलम निवडा",
		bSubmit: "ओके",
		bCancel: "रद्द करणे"
	},
	errors : {
		errcap : "चुक",
		nourl : "यु आर ल आवश्यक आहे",
		norecords: "प्रक्रीये साठी नोंद नाही",
		model : "Length of colNames <> colModel!"
	},
	formatter : {
		integer : {thousandsSeparator: " ", defaultValue: '0'},
		number : {decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 2, defaultValue: '0.00'},
		currency : {decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 2, prefix: "", suffix:"", defaultValue: '0.00'},
		date : {
			dayNames:   [
				"Sun", "Mon", "Tue", "Wed", "Thr", "Fri", "Sat",
				"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
			],
			monthNames: [
				"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
				"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
			],
			AmPm : ["am","pm","AM","PM"],
			S: function (j) {return j < 11 || j > 13 ? ['st', 'nd', 'rd', 'th'][Math.min((j - 1) % 10, 3)] : 'th'},
			srcformat: 'Y-m-d',
			newformat: 'd/m/Y',
			masks : {
				ISO8601Long:"Y-m-d H:i:s",
				ISO8601Short:"Y-m-d",
				ShortDate: "n/j/Y",
				LongDate: "l, F d, Y",
				FullDateTime: "l, F d, Y g:i:s A",
				MonthDay: "F d",
				ShortTime: "g:i A",
				LongTime: "g:i:s A",
				SortableDateTime: "Y-m-d\\TH:i:s",
				UniversalSortableDateTime: "Y-m-d H:i:sO",
				YearMonth: "F, Y"
			},
			reformatAfterEdit : false
		},
		baseLinkUrl: '',
		showAction: '',
		target: '',
		checkbox : {disabled:true},
		idName : 'id'
	}
};
})(jQuery);
