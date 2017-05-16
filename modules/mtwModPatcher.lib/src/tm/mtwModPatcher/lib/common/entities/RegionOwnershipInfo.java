package tm.mtwModPatcher.lib.common.entities;

import java.util.ArrayList;
import java.util.List;

public class RegionOwnershipInfo {

	public String RegionName;

	public List<String> FactionsList = new ArrayList<>();

	public RegionOwnershipInfo(String regionName, List<String> factionsList) {
		RegionName = regionName;
		FactionsList = factionsList;
	}

	public RegionOwnershipInfo(String regionName) {
		RegionName = regionName;
	}

	public RegionOwnershipInfo() {
	}
}
