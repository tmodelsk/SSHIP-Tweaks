package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.data.common.Format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Soldier attribute of Export Deccr Unit */
public class Soldier {

	public String Model;

	public int NumberOfMen;

	public int SpecialNumber;

	public double Mass;

	public String Unknown;

	public String toEduString() {
		String str = "";

		str += Model + ", ";
		str += NumberOfMen + ", ";
		str += SpecialNumber + ", ";
		str += Format.toString(Mass);

		if(Unknown != null)
			str += Unknown;

		return str;
	}

	public static Soldier parseEduEntry(String str) {
		Soldier s = null;

		// soldier NE_Trebuchet_Crew, 16, 2, 1
		Matcher m = eduPattern.matcher(str);
		if(m.find()) {
			s = new Soldier();

			s.Model = m.group(1);
			s.NumberOfMen = Integer.parseInt(m.group(2));
			s.SpecialNumber = Integer.parseInt(m.group(3));
			s.Mass = Double.parseDouble(m.group(4));//.replace("." , ","));

			// m.group(5) is inside group(4) !!

			s.Unknown = m.group(6);
		}
		else {
			// ## SSHIP bugfix, wrong entry soldier          Scots_Pike_Militia, 80, 1.2
			m = eduWrongPattern.matcher(str);
			if(m.find()) {
				s = new Soldier();

				s.Model = m.group(1);
				if(s.Model.equals("Scots_Pike_Militia")) {
					s.NumberOfMen = Integer.parseInt(m.group(2));
					s.SpecialNumber = 0; //Integer.parseInt(m.group(3));
					s.Mass = Double.parseDouble(m.group(3));//.replace("." , ","));
				}
				else throw new PatcherLibBaseEx("Unable to parse: "+str);
			}
			else throw new PatcherLibBaseEx("Unable to parse: "+str);
		}

		return s;
	}

	private static final Pattern eduPattern = Pattern.compile("\\s*(\\w+),\\s*(\\d+),\\s*(\\d+),\\s*([+-]?([0-9]*[.])?[0-9]+)(\\s*,.*)*");
	private static final Pattern eduWrongPattern = Pattern.compile("\\s*(\\w+),\\s*(\\d+),\\s*([+-]?([0-9]*[.])?[0-9]+)");
}
