package tm.mtwModPatcher.lib.fileEntities.data.common;

/**  */
public class Format {

	public static String toString(Double d) {

		if(d == d.intValue())
			return ""+d.intValue();
		else
			//return new DecimalFormat("#.0###", DecimalFormatSymbols.getInstance( Locale.ENGLISH )).msgFormat(d);
		{
			String temp = String.format("%.3f", d).replaceAll("(\\.\\d+?)0*$", "$1").replace(',','.');

			String[] tab= temp.split("\\.");

			String digitsStr = tab[1];

			while ( digitsStr.length() > 1 && digitsStr.lastIndexOf("0") == digitsStr.length()-1 )
			{
				temp = removeLast(temp, '0');

				digitsStr = temp.split("\\.")[1];
			}

			return temp;
		}

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
