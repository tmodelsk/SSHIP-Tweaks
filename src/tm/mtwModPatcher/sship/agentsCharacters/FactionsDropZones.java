package tm.mtwModPatcher.sship.agentsCharacters;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by Tomek on 2016-11-15.
 */
public class FactionsDropZones {

	public GeneralDropZone getDefaultZOne(String factionName) {


		return _Dict.get(factionName);

	}

	public void add(String factionName, int x, int y, String unitName) {

		_Dict.put(factionName, new GeneralDropZone(x,y, unitName));
	}


	public FactionsDropZones() {
		_Dict = new Hashtable<>();
	}

	private Dictionary<String, GeneralDropZone> _Dict;
}
