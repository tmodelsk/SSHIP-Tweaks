package tm.mtwModPatcher.sship.agentsCharacters;

/**
 * Created by Tomek on 2016-11-15.
 */
public class GeneralDropZone {

	public int X;

	public int Y;

	public String GeneralUnitName;

	public GeneralDropZone(int x, int y, String generalUnitName) {
		X = x;
		Y = y;
		GeneralUnitName = generalUnitName;
	}

	public GeneralDropZone(int x, int y) {
		X = x;
		Y = y;
	}

	public GeneralDropZone() {
	}
}
