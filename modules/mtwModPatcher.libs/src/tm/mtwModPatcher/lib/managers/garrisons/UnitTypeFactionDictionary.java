package tm.mtwModPatcher.lib.managers.garrisons;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.entities.FactionInfo;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Tomek on 2016-11-13.
 */
public class UnitTypeFactionDictionary {

	public void add(UnitType type, List<String> factionNames, String unitName, int experience) throws PatcherLibBaseEx {
		add(type, factionNames, new UnitGarrisonInfo(unitName, experience));
	}

	public void add(UnitType type, List<String> factionNames, String unitName) throws PatcherLibBaseEx {
		add(type, factionNames, new UnitGarrisonInfo(unitName));
	}

	public void add(UnitType type, List<String> factionNames, UnitGarrisonInfo unitInfo) throws PatcherLibBaseEx {
		for (String facionName : factionNames) {
			add(type, facionName, unitInfo);
		}
	}

	public void add(UnitType type, String factionName, String unitName, int experience) throws PatcherLibBaseEx {
		add(type, factionName, new UnitGarrisonInfo(unitName, experience));
	}

	public void add(UnitType type, String factionName, String unitName) throws PatcherLibBaseEx {
		add(type, factionName, new UnitGarrisonInfo(unitName));
	}
	public void add(UnitType type, FactionInfo faction, String unitName) throws PatcherLibBaseEx {
		add(type, faction.symbol, new UnitGarrisonInfo(unitName));
	}
	public void add(UnitType type, FactionInfo faction, String unitName, String hiddenRes) throws PatcherLibBaseEx {
		add(type, faction.symbol, new UnitGarrisonInfo(unitName, hiddenRes));
	}
	public void add(UnitType type, FactionInfo faction, UnitGarrisonInfo unitInfo) throws PatcherLibBaseEx {
		add(type, faction.symbol, unitInfo);
	}
	public void add(UnitType type, String factionName, UnitGarrisonInfo unitInfo) throws PatcherLibBaseEx {

		UnitTypeFactionKey key = new UnitTypeFactionKey(type, factionName);
		add(key, unitInfo);
	}

	public void add(UnitTypeFactionKey key, UnitGarrisonInfo unit) throws PatcherLibBaseEx {

		//if(_Dict.getFirst(key) != null) throw new PatcherLibBaseEx("Dictionaty already contains UnitType: "+key.Type + " Faction: "+key.FactionName);

		List<UnitGarrisonInfo> units;

		units = _Dict.get(key);
		if(units == null) {
			units = new ArrayList<>();
			_Dict.put(key, units);
		}

		units.add(unit);

	}

	public UnitGarrisonInfo loadFirst(UnitType type, String factionName) throws PatcherLibBaseEx {
		return loadFirst(new UnitTypeFactionKey(type, factionName));
	}
	public UnitGarrisonInfo loadFirst(UnitTypeFactionKey key) throws PatcherLibBaseEx {
		UnitGarrisonInfo unit = _Dict.get(key).get(0);

		 if(unit == null) throw new PatcherLibBaseEx("UnitTypeKey: ( "+key.Type + " , "+key.FactionName + " ) not found in Dictionary");

		return unit;
	}

	public List<UnitGarrisonInfo> load(UnitType type, String factionName) throws PatcherLibBaseEx {
		return load(new UnitTypeFactionKey(type, factionName));
	}
	public List<UnitGarrisonInfo> load(UnitTypeFactionKey key) throws PatcherLibBaseEx {
		List<UnitGarrisonInfo> units = _Dict.get(key);

		if(units == null) throw new PatcherLibBaseEx("UnitTypeKey: ( "+key.Type + " , "+key.FactionName + " ) not found in Dictionary");

		return units;
	}

	public UnitGarrisonInfo getFirst(UnitType type, String factionName) throws PatcherLibBaseEx {
		return getFirst(new UnitTypeFactionKey(type, factionName));
	}
	public UnitGarrisonInfo getFirst(UnitTypeFactionKey key) throws PatcherLibBaseEx {
		UnitGarrisonInfo unit = _Dict.get(key).get(0);

		return unit;
	}


	private Dictionary<UnitTypeFactionKey, List<UnitGarrisonInfo>> _Dict;

	public UnitTypeFactionDictionary() {
		this._Dict = new Hashtable<>();
	}
}
