package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by Tomek on 2016-07-21 */
public class StatPriArmor {

	public int Armour;
	public int DefenceSkill;
	public int Shield;
	public String Sound;
	public String Comments = null;

	private static final Pattern statPriArmourPattern = Pattern.compile("^(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\w+)(\\s*;.*)*");
	public static StatPriArmor parseStr(String statPriArmourStr) throws PatcherLibBaseEx {
		StatPriArmor spa;

		Matcher m = statPriArmourPattern.matcher(statPriArmourStr);

		if(m.find()) {

			spa = new StatPriArmor();
			spa.Armour = Integer.parseInt(m.group(1));
			spa.DefenceSkill = Integer.parseInt(m.group(2));
			spa.Shield = Integer.parseInt(m.group(3));

			spa.Sound = m.group(4);

			spa.Comments = m.group(5);
		} else throw new PatcherLibBaseEx("Unable to parse");

		return spa;
	}
	public String serialize() {
		val spa = this;

		String s = "";

		s += spa.Armour + ", ";
		s += spa.DefenceSkill + ", ";
		s += spa.Shield + ", ";
		s += spa.Sound;

		if(spa.Comments != null)
			s += spa.Comments;

		return s;
	}

	public int defence() {
		return Armour + DefenceSkill + Shield;
	}

	@Override
	public String toString() {
		return Integer.toString(defence());
	}
}
