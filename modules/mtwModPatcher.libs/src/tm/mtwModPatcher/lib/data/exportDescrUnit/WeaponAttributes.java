package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Created by tomek on 01.11.2017. */
public class WeaponAttributes {
	private Set<String> attributes = null;

	public List<String> values() {
		val arrayList = new ArrayList<String>();

		for (val att : attributes.toArray())
			arrayList.add((String)att);

		return arrayList;
	}

	public boolean contains(String attr) {
		if(attributes == null) return false;

		return attributes.contains(attr);
	}
	public boolean containsSpear() {
		return contains(SPEAR) || contains(LIGHT_SPEAR);
	}
	public boolean containsSpearOrPike() {
		return containsSpear() || contains(LONG_PIKE);
	}



	public void add(String attr) {
		if(attributes != null && attributes.contains(attr) ) return;

		if(attributes == null) attributes = new LinkedHashSet<>();

		if(attributes.contains("no")) attributes.remove("no");

		attributes.add(attr);
	}

	public static WeaponAttributes parseStr(String srcValue) {
		val wa = new WeaponAttributes();

		//  ap, area

		val splitted = srcValue.split(",");
		for (val factor : splitted)
			wa.add(factor.trim());

		return wa;
	}

	public String serialize() {
		String res="";

		if(attributes == null || attributes.size() == 0) return res;

		for (val attr : attributes) {
			if(res.length() > 0) res += ", ";
			res += attr;
		}

		return res;
	}

	@Override
	public String toString() {
		return serialize();
	}

	public static final String SPEAR = "spear";
	public static final String LIGHT_SPEAR = "light_spear";
	public static final String LONG_PIKE = "long_pike";

}
