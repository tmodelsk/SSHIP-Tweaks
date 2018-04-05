package tm.mtwModPatcher.lib.data.exportDescrUnit;

/**
 * Created by tomek on 03.11.2017.
 */
public class UnitClass {

	private final String label;


	@Override
	public String toString() {
		return label;
	}

	public UnitClass(String label) {
		this.label = label;
	}
}
