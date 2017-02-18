package org.mkcl.els.common.util;

//taken from jodd.org open source & has no dependency on the same
public class RomanNumeral {
	
	private static final int[] VALUES = new int[] {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
	private static final String[] LETTERS = new String[] {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

	/**
	 * Get Roman Equivalent of the given long number.
	 */
	public static String getRomanEquivalent(Long value) {
		StringBuilder roman = new StringBuilder();
		Long n = value;
		for (int i = 0; i < LETTERS.length; i++) {
			while (n >= VALUES[i]) {
				roman.append(LETTERS[i]);
				n -= VALUES[i];
			}
		}
		return roman.toString();
	}
	
	/**
	 * Get Roman Equivalent of the given integer number.
	 */
	public static String getRomanEquivalent(Integer value) {
		StringBuilder roman = new StringBuilder();
		Integer n = value;
		for (int i = 0; i < LETTERS.length; i++) {
			while (n >= VALUES[i]) {
				roman.append(LETTERS[i]);
				n -= VALUES[i];
			}
		}
		return roman.toString();
	}

	/**
	 * Get Number Equivalent of the given roman numeral as long.
	 */
	public static Long getLongEquivalent(String roman) {
		Long start = new Long(0), value = new Long(0);
		for (int i = 0; i < LETTERS.length; i++) {
			while (roman.startsWith(LETTERS[i], start.intValue())) {
				value += VALUES[i];
				start += LETTERS[i].length();
			}
		}
		return start == roman.length() ? value : -1;
	}
	
	/**
	 * Get Number Equivalent of the given roman numeral as integer.
	 */
	public static Integer getIntegerEquivalent(String roman) {
		Integer start = 0, value = 0;
		for (int i = 0; i < LETTERS.length; i++) {
			while (roman.startsWith(LETTERS[i], start)) {
				value += VALUES[i];
				start += LETTERS[i].length();
			}
		}
		return start == roman.length() ? value : -1;
	}

	/**
	 * Checks if some string is valid roman number.
	 */
	public static boolean isValidRomanNumber(String roman) {
		return roman.equals(getRomanEquivalent(getLongEquivalent(roman)));
	}

    public static void test(Long n) {
        System.out.println(n + " = " + getRomanEquivalent(n));
    }
    
    public static void test(Integer n) {
        System.out.println(n + " = " + getRomanEquivalent(n));
    }
    
    public static void test(String n) {
        System.out.println(n + " = " + getIntegerEquivalent(n));
        System.out.println(n + " = " + getLongEquivalent(n));
    }

    public static void main(String[] args) {
        test(new Long(1999));
        test(new Long(25));
        test(new Long(944));
        test(new Long(0));
        test(1999);
        test(25);
        test(944);
        test(0);
    	test(new String("mcmxcix").toUpperCase());
        test("XXV");
        test("CMXLIV");
        test("");
        
    }
    
//  ============================== Old Code ===============================
//  enum Numeral {
//  I(1), IV(4), V(5), IX(9), X(10), XL(40), L(50), XC(90), C(100), CD(400), D(500), CM(900), M(1000);
//  int weight;
//
//  Numeral(int weight) {
//      this.weight = weight;
//  }
//};
//
//public static String getRomanEquivalent(long n) {
//
//  if( n <= 0) {
//      throw new IllegalArgumentException();
//  }
//
//  StringBuilder buf = new StringBuilder();
//
//  final Numeral[] values = Numeral.values();
//  for (int i = values.length - 1; i >= 0; i--) {
//      while (n >= values[i].weight) {
//          buf.append(values[i]);
//          n -= values[i].weight;
//      }
//  }
//  return buf.toString();
//}

}
