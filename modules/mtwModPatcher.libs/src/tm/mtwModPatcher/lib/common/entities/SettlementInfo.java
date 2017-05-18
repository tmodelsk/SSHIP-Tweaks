package tm.mtwModPatcher.lib.common.entities;

import tm.common.Point;

import java.util.ArrayList;
import java.util.List;


public class SettlementInfo {

	public String Name;
	public String ProvinceName;

	public SettlementLevel Level;

	public String CreatedByFaction;

	public List<String> Resources;

	public Point<Integer> Position;

	@Override
	public String toString() {

		String str = Name+", "+Level;
		if(Position != null) str += ", " + Position.toString();

		return  str;
	}

	public SettlementInfo(String name, SettlementLevel level, String createdByFaction) {
		this();
		Name = name;
		Level = level;
		CreatedByFaction = createdByFaction;
	}

	public SettlementInfo(String name, String provinceName, SettlementLevel level, String createdByFaction) {
		this();
		Name = name;
		ProvinceName = provinceName;
		Level = level;
		CreatedByFaction = createdByFaction;
	}

	public SettlementInfo(String name, SettlementLevel level) {
		this();

		Name = name;
		Level = level;
	}

	public SettlementInfo() {
		Resources = new ArrayList<>();
	}
}
