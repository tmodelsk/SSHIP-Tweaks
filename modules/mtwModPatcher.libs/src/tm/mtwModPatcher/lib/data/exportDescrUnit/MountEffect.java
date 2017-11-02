package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.Getter;
import lombok.val;
import tm.common.Ctm;

/** Created by tomek on 29.10.2017 */
public class MountEffect {

	@Getter
	private String Type;

	public static final String HORSE = "horse";
	public static final String CAMEL = "camel";

	public int Value;

	@Override
	public boolean equals(Object obj) {

		if(obj == null) return false;

		val objTyped = Ctm.as(MountEffect.class, obj);
		if(objTyped == null) return false;

		return Type.equals(objTyped.Type);
	}

	@Override
	public int hashCode() {
		return Type.hashCode();
	}


	public MountEffect(String type, int value) {
		Type = type;
		Value = value;
	}

	public MountEffect() {
	}
}
