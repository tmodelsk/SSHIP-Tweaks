package tm.mtwModPatcher.lib.managers;

import lombok.val;
import tm.common.Point;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.entities.SettlementInfo;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Combines Settlement Logic from various files
 * */
public class SettlementManager {
	public List<SettlementInfo> getAllSettlements() throws PatcherLibBaseEx {

		List<SettlementInfo> settlements = DescrStrat.getSettlementInfoList();

		Map<String, Point<Integer>> overrides = new HashMap<>();
		overrides.put("Vienna", new Point<>(198, 193));
		overrides.put("Durazzo", new Point<>(218, 142));
		overrides.put("Al_Aqaba", new Point<>(329, 59));
		overrides.put("Ani", new Point<>(393, 136));
		overrides.put("Derbent", new Point<>(425, 146));
		overrides.put("Caesarea", new Point<>(333, 124));
		overrides.put("Al_Ahsa", new Point<>(435, 29));
		overrides.put("Sives", new Point<>(344, 132));
		overrides.put("Al-Mahdiya", new Point<>(156, 101));
		overrides.put("Baku", new Point<>(434, 134));
		overrides.put("Bulgar", new Point<>(434, 241));

		for (SettlementInfo settl : settlements) {
			settl.Resources = DescrRegions.getResources(settl.ProvinceName);
			settl.Name = DescrRegions.getSettlementName(settl.ProvinceName);

			String settlName = settl.Name;
			if(settlName.equals("Edinburgh")) settlName = "Edinburg";

			settl.Position = findPosition(settlName);

			if(overrides.containsKey(settlName)) settl.Position = overrides.get(settlName);

		}

		return settlements;
	}

	private Point<Integer> findPosition(String settlementName) {
		Point<Integer> res = null;

		val patt = Pattern.compile("^character\\s+.+x\\s+(\\d+).+y\\s+(\\d+).+;\\s*"+settlementName+"\\s*$");

		val lines = DescrStrat.Factions.getContent().getLines();
		val index = lines.findFirstRegexLine(patt);
		if(index > 0) {
			// found
			val lineStr = lines.getLine(index);
			val m = patt.matcher(lineStr);
			if(m.find()) {
				res = new Point<>();
				res.setX(Integer.parseInt(m.group(1)));
				res.setY(Integer.parseInt(m.group(2)));
			} else throw new PatcherLibBaseEx("Should find, unexpected");
		}
		return res;
	}

	public Map<String, List<SettlementInfo>> groupByHiddenResources() {
		val siList = getAllSettlements();

		Set<String> hiddenResources = new HashSet<>();
		siList.forEach( si -> hiddenResources.addAll(si.Resources));

		Map<String, List<SettlementInfo>> res = new HashMap<>();
		for(val hr : hiddenResources) {
			val grouped = siList.stream().filter( f -> f.Resources.contains(hr) ).collect(Collectors.toList());
			res.put(hr, grouped);
		}

		return res;
	}

	private DescrStratSectioned DescrStrat;
	private tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions DescrRegions;

	public SettlementManager(DescrStratSectioned descrStrat, DescrRegions descrRegions) {
		this.DescrStrat = descrStrat;
		this.DescrRegions = descrRegions;
	}
}
