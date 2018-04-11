package tm.mtwModPatcher.lib.data.common;

import lombok.val;

/**  */
public class Format {

	public static String toString(Double d, int minPrecision) {
		val minimumPrecStr = String.format("%."+minPrecision+"f", d).replace(',', '.');
		val minimumStr = toString(d);

		return minimumPrecStr.length() > minimumStr.length() ? minimumPrecStr : minimumStr;
	}

	public static String toString(Double d) {
		if(d.isInfinite())
			throw new IllegalArgumentException("Input number is infinite!");

		if(d == d.intValue())
			return ""+d.intValue();
		else {
			String resultStr = toStringForcePrecision(d, 4);

			if(!checkStrValue(d, resultStr)) {
				int precision = 4;
				do{
					precision++;
					resultStr = toStringForcePrecision(d, precision);
				}
				while( !checkStrValue(d, resultStr) && precision <= 6 );

				if(!checkStrValue(d, resultStr)) {
					//throw new RuntimeException("Unable to format "+d);
					String x="breakpoint low precision";
					String y=x;
				}
			}

			return resultStr;
		}
	}

	private static String toStringForcePrecision(Double d, int precision) {
		if(d == d.intValue())
			return ""+d.intValue();
		else {
			String resultStr = String.format("%."+precision+"f", d).replaceAll("(\\.\\d+?)0*$", "$1").replace(',','.');
			String[] tab= resultStr.split("\\.");
			String digitsStr = tab[1];

			while ( digitsStr.length() > 1 && digitsStr.lastIndexOf("0") == digitsStr.length()-1 ) {
				resultStr = removeLast(resultStr, '0');
				digitsStr = resultStr.split("\\.")[1];
			}

			return resultStr;
		}
	}

	private static boolean checkStrValue(Double d, String strValue) {
		val doubleParsed = Double.parseDouble(strValue);

		return d.equals(doubleParsed);
	}

	public static double round(double src, int precision) {
		double multi = Math.pow(10, precision);
		double res = src * multi;

		res = Math.round(res);
		res = res / multi;

		return res;
	}

	public static String removeLast(String str, Character character) {
		if (str != null && str.length() > 0 && str.charAt(str.length()-1)==character) {
			str = str.substring(0, str.length()-1);
		}
		return str;
	}
}
