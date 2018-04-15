package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.PatcherUnexpectedEx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatMental {

	public int Morale;

	public String Discipline;

	public String Training;

	public boolean IsLockMorale;

	public String Comments = null;

	public static final String DISCIPLINE_NORMAL = "normal";
	public static final String DISCIPLINE_DISCIPLINED = "disciplined";

	public static StatMental parseStr(String statMentalStr) {
		val sm = new StatMental();

		Matcher m;
		boolean isLongPattern = false, founded = false;

		m = longPattern.matcher(statMentalStr);
		if(m.find()) {
			founded = true;
			isLongPattern = true;
		}
		else {
			m = shortPattern.matcher(statMentalStr);
			if(m.find()) founded = true;
			else throw new PatcherLibBaseEx("Can't parse "+statMentalStr);
		}

		if(founded) {
			sm.Morale = Integer.parseInt(m.group(1));
			sm.Discipline = m.group(2);
			sm.Training = m.group(3);

			if(!isLongPattern) {
				sm.IsLockMorale = false;
				sm.Comments = m.group(4);
			}
			else {
				if(!m.group(4).equals("lock_morale")) throw new PatcherUnexpectedEx("Can't parse StatMental "+statMentalStr);

				sm.IsLockMorale = true;
				sm.Comments = m.group(5);
			}
		}

		return sm;
	}
	private static Pattern shortPattern = Pattern.compile("^(\\d+)\\s*,\\s*(\\w+)\\s*,\\s*(\\w+)(\\s*;.*)*");
	private static Pattern longPattern = Pattern.compile("^(\\d+)\\s*,\\s*(\\w+)\\s*,\\s*(\\w+),\\s*(lock_morale+)(\\s*;.*)*");
	public String serialize() {
		val sm = this;

		String s="";

		s += sm.Morale + ", ";
		s += sm.Discipline + ", ";
		s += sm.Training;

		if(sm.IsLockMorale) s += ", lock_morale";

		if(sm.Comments != null) s += sm.Comments;

		return s;
	}


}
