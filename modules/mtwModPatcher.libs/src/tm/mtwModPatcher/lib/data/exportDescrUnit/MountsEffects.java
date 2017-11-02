package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Created by tomek on 29.10.2017. */
public class MountsEffects {

	private Map<String, MountEffect> effects = null; //

	public void addEffectOffset(String type, int value) {

		if(effects == null) effects = new LinkedHashMap<>();

		if(effects.containsKey(type)) {
			val oldValue = effects.get(type);
			oldValue.Value += value;

			effects.replace(type, oldValue);
		}
		else {
			if(effects.size() > 3) throw new PatcherLibBaseEx("Max 3 mount effects, unable to add next!");

			effects.put(type, new MountEffect(type, value));
		}
	}

	public List<MountEffect> getList() {
		return new ArrayList<>(effects.values());
	}

	public static MountsEffects parse(String sourceValue) {

		if(sourceValue == null) return new MountsEffects();

		val res = new MountsEffects();

		val splitted = sourceValue.split(",");

		// horse +2, camel +2
		for (val pair : splitted) {
			val pairTrimmed = pair.trim();

			if(pairTrimmed.length() > 0) {
				val factors = pairTrimmed.split(" ");
				val type = factors[0];
				val valueInt = Integer.parseInt(factors[1]);

				res.addEffectOffset(type, valueInt);
			}
		}

		return res;
	}

	public String serialize() {
		if(effects == null || effects.size() == 0) return null;

		boolean isFirst = true;
		String str="";
		for (val me : getList()) {
			if(!isFirst) str += ", ";

			str += me.getType()+" ";
			if(me.Value >=0) str +="+";
			str += me.Value;

			isFirst = false;
		}

		return str;
	}

	@Override
	public String toString() {
		if(effects == null || effects.size() == 0) return "";

		String str="";
		for (val eff:effects.values())
			str += Ctm.msgFormat("{0}{1} ", eff.getType(), eff.Value);

		return str;
	}
}
