package tm.mtwModPatcher.lib.common.entities;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

/**
 * Created by Tomek on 2016-11-15.
 */
public class FactionInfo {

	public String symbol;

	public String description;

	public CultureType culture;

	public Religion religion;

	// Now just used for xxx_Edu - Education traits
	public EducationStyle education;

	public String bodyguardUnitName;

	public boolean isSlave;
	public boolean isPapacy() {
		return symbol.equals("papal_states");
	}
	public boolean isMongols() {
		return symbol.equals("mongols");
	}

	public String getCultureTypeStr() {
		return convertCulture(this.culture);
	}

	public FactionInfo(String symbol, String description, Religion religion, EducationStyle education, String bodyguardUnitName) {
		this.symbol = symbol;
		this.description = description;
		this.religion = religion;
		this.education = education;
		this.bodyguardUnitName = bodyguardUnitName;

		isSlave = false;
	}

	public FactionInfo(String symbol, String description, CultureType culture, Religion religion, EducationStyle education, String bodyguardUnitName) {
		this(symbol, description, religion, education, bodyguardUnitName);
		this.culture = culture;
	}

	public FactionInfo() {
		isSlave = false;
	}


	public static String convertCulture(CultureType cultureType) {

		if(cultureType == null) return null;

		String res=null;
		switch (cultureType) {

			case NORTHERN_EUROPEAN:
				res = NOTHERN_EUROPEAN;
				break;
			case EASTERN_EUROPEAN:
				res = EASTERN_EUROPEAN;
				break;
			case SOUTHERN_EUROPEAN:
				res = SOUTHERN_EUROPEAN;
				break;
			case MIDDLE_EASTERN:
				res = MIDDLE_EASTERN;
				break;
			case GREEK:
				res = GREEK;
				break;
			default:
				throw new PatcherLibBaseEx("Not Implemented "+cultureType);
		}

		return res;
	}

	public static CultureType convertCulture(String cultureTypeStr) {
		if(cultureTypeStr == null) return null;

		CultureType res = null;

		switch (cultureTypeStr) {

			case NOTHERN_EUROPEAN:
				res = CultureType.NORTHERN_EUROPEAN;
				break;
			case EASTERN_EUROPEAN:
				res = CultureType.EASTERN_EUROPEAN;
				break;
			case SOUTHERN_EUROPEAN:
				res = CultureType.SOUTHERN_EUROPEAN;
				break;
			case MIDDLE_EASTERN:
				res = CultureType.MIDDLE_EASTERN;
				break;
			case GREEK:
				res = CultureType.GREEK;
				break;
			default:
				throw new PatcherLibBaseEx("Not Implemented "+cultureTypeStr);
		}

		return res;
	}

	@Override
	public int hashCode() {
		return symbol.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(! (obj instanceof  FactionInfo)) return  false;

		val objTyped = (FactionInfo)obj;
		return  symbol.equals(objTyped.symbol);
	}

	public static  final String NOTHERN_EUROPEAN = "northern_european";
	public static  final String EASTERN_EUROPEAN = "eastern_european";
	public static  final String SOUTHERN_EUROPEAN = "southern_european";
	public static  final String MIDDLE_EASTERN = "middle_eastern";
	public static  final String GREEK = "greek";
}
