package tm.mtwModPatcher.sship.map;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.Point;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.common.entities.SettlementInfo;
import tm.mtwModPatcher.lib.data.DescrCampaignDb;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrStratSectioned;
import tm.mtwModPatcher.lib.managers.SettlementManager;

import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Replaces Watchtowers with Forts if no nearest settlement
 */
public class WatchtowersToForts extends Feature {

	@Getter @Setter
	private double minimumDistance = 7.0;

	@Override
	public void executeUpdates() throws Exception {
		descrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		descrRegions = getFileRegisterForUpdated(DescrRegions.class);
		descrCampaignDb = getFileRegisterForUpdated(DescrCampaignDb.class);

		// ## Permanent Forts ##
		descrCampaignDb.setAttribute("/root/settlement/destroy_empty_forts", "bool" ,false);
		descrCampaignDb.setAttribute("/root/misc/fort_devastation_distance", "uint", 0);
		descrCampaignDb.setAttribute("/root/misc/fort_devastation_modifier", "float", 0.0);

		// ## Proceed all WatchTowers ##
		SettlementManager settlementManager = new SettlementManager(descrStrat, descrRegions);
		val settlements = settlementManager.getAllSettlements();
		val settPositions = settlements.stream().filter(s -> s.Position != null).collect(Collectors.toList());

		// watchtower 324, 195
		val rp = Pattern.compile("^\\s*watchtower\\s+(\\d+),\\s*(\\d+)");

		int index = 1, replacedCount = 0;
		val lines = descrStrat.Regions.getContent().getLines();

		while (index > 0) {
			index = lines.findFirstByRegexLine(rp, index + 1);

			if (index > 0) {
				val lineStr = lines.getLine(index);
				val m = rp.matcher(lineStr);
				if (m.find()) {
					val watchTowerPos = new Point<Integer>( Integer.parseInt(m.group(1)) , Integer.parseInt(m.group(2)) );

					double minDist = Double.MAX_VALUE, dist;
					SettlementInfo settlementNearest=null;

					for (val settPos: settPositions) {
						dist = Math.sqrt(  Math.pow(watchTowerPos.getX() - settPos.Position.getX() , 2) + Math.pow(watchTowerPos.getY() - settPos.Position.getY() , 2)   );
						if(dist <= minDist) {
							settlementNearest = settPos;
							minDist = dist;
						}
					}

					if(minDist > minimumDistance) {
						// OK, replace with fort
						val cultureTypeStr = determineCultureTypeStrBySettlement(settlementNearest);
						val lineNewStr = Ctm.msgFormat("fort {0}, {1} stone_fort_a culture {2}",
								watchTowerPos.getX(), watchTowerPos.getY(), cultureTypeStr);

						lines.replaceLine(index, lineNewStr);
						//lines.insertAt(index, lineNewStr); index++;
						replacedCount++;
					}

				} else throw new PatcherLibBaseEx("Unexpected!");
			}
		}
		consoleLogger.writeLine(Ctm.msgFormat("WatchtowersToForts Feature: replaced {0} Watchtowers with Forts",replacedCount));
	}

	private String determineCultureTypeStrBySettlement(SettlementInfo settl) {
		String result = null;

		val factionCreator = settl.CreatedByFaction;
		val faction = FactionsDefs.loadFactionInfo(factionCreator);
		result = faction.getCultureTypeStr();

		return  result;
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	private DescrStratSectioned descrStrat;
	private DescrRegions descrRegions;
	protected DescrCampaignDb descrCampaignDb;

	public WatchtowersToForts() {

		setName("Watchtowers To Forts");

		addCategory("Various");
		addCategory("Map");

		setDescriptionShort("Replaces some Watchtowers with Forts if no near settlement");
		setDescriptionUrl("http://tmsship.wikidot.com/watchtowers-to-forts");
	}
}
