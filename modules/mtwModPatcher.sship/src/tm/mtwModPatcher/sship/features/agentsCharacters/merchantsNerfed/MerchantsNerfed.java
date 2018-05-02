package tm.mtwModPatcher.sship.features.agentsCharacters.merchantsNerfed;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.PatcherNotSupportedEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.params.*;
import tm.mtwModPatcher.lib.data._root.DescrSettlementMechanics;
import tm.mtwModPatcher.lib.data._root.ExportDescrGuilds;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.BuildingLevel;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;
import tm.mtwModPatcher.sship.features.agentsCharacters.MerchantsRemoved;
import tm.mtwModPatcher.sship.lib.Buildings;
import tm.mtwModPatcher.sship.lib.HiddenResources;

import javax.xml.xpath.XPathExpressionException;
import java.util.*;
import java.util.regex.Pattern;

/** Created by tomek on 15.10.2017. */
public class MerchantsNerfed extends Feature {

	@Override
	public void setParamsCustomValues() {
		tradeMulti = 1.3;	// 1.85
		miningMulti = 2.0;
		merchantGuildLevelsStr = "35 60 120";
		removeStartingMerchants = false;
		buildings = new BuildingLimits();

		if(ConfigurationSettings.isDevEnvironment()) {
			removeStartingMerchants = true;
		}
	}

	@Override
	public void executeUpdates() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		exportDescrGuilds = getFileRegisterForUpdated(ExportDescrGuilds.class);
		descrSettlementMechanics = getFileRegisterForUpdated(DescrSettlementMechanics.class);
		descrStrat = getFile(DescrStratSectioned.class);

		removeBuildingMerchantCapabilities();

		addMerchantLimits();
		updateSettlementEconomyParams();
		updateGuildRequirements();

		if(removeStartingMerchants) removeStartingMerchants();
	}

	private void addMerchantLimits() {
		// ## Merchants can be produced only on city/castle walls / markets buildings
		val walls = new ArrayList<BuildingLevel>();
		Buildings.WallsCityLevels.forEach( l -> walls.add(new BuildingLevel(Buildings.WallsCitySpec.Name, l, SettlType.City)));
		Buildings.WallsCastleLevels.forEach( l -> walls.add(new BuildingLevel(Buildings.WallsCastleSpec.Name, l, SettlType.Castle)));
		for (val wallLevel: walls)
			ensureAgentReruitmentExists(wallLevel, HiddenResources.HiddenResource + HiddenResources.Capital);

		val markets = new ArrayList<BuildingLevel>();
		Buildings.MarketCityLevels.forEach( m -> markets.add(new BuildingLevel(Buildings.MarketCity, m , SettlType.City)));
		Buildings.MarketCastleLevels.forEach( m -> markets.add(new BuildingLevel(Buildings.MarketCastle, m , SettlType.Castle)));
		for (val marketLevel: markets)
			ensureAgentReruitmentExists(marketLevel, null);

		val limitParams = buildings.getBuildingsParams();

		for (val paramDef : limitParams) {
			val bl = buildings.get(paramDef.getItem1());

			if(bl.Limit > 0) {
				val buildingLevels = resolveBuidlingNameLevels(bl);
				for (val bLevel: buildingLevels) {
					//ensureAgentReruitmentExists(bLevel, bl.Requires);

					String limitLine = "agent_limit merchant " + bl.Limit;
					if(bl.Requires != null && !bl.Requires.isEmpty()) limitLine += " requires " + bl.Requires;
					edb.addCapabilities(bLevel, limitLine);
				}
			}
		}
	}
	private void ensureAgentReruitmentExists(BuildingLevel buildingLevel, String require) {
		val range = edb.getCapabilitiesStartEnd(buildingLevel);
		val regex = Pattern.compile("^\\s*agent\\s+merchant\\s");

		val index = edb.getLines().findFirstRegexLine(regex, range);
		if(index < 0) {
			// we need to insert
			val lines = Arrays.asList("agent merchant  0  requires factions { northern_european, middle_eastern, eastern_european, greek, southern_european, }");

			if(require == null) require = "";
			else require = " and " + require;
			for (val line : lines) {
				edb.addCapabilities(buildingLevel, line + require);
			}
		}
	}

	private List<BuildingLevel> resolveBuidlingNameLevels(BuildingLimit bl) {
		val res = new ArrayList<BuildingLevel>();

		if(bl.Building != null) {
			res.add(new BuildingLevel(bl.Building, bl.Level, bl.SettlType));
		}
		else if(bl.BuildingType != null) {
			switch (bl.BuildingType) {
				case Walls:
					Buildings.WallsCityLevels.forEach( l -> res.add(new BuildingLevel(Buildings.WallsCitySpec.Name, l, SettlType.City)));
					Buildings.WallsCastleLevels.forEach( l -> res.add(new BuildingLevel(Buildings.WallsCastleSpec.Name, l, SettlType.Castle)));
					break;
				default:
					throw new PatcherNotSupportedEx("BuildingType: " + bl.BuildingType);
			}
		} else throw new PatcherNotSupportedEx("BuildingType & Name & Level is null" + bl.BuildingType);

		return res;
	}

	private void updateSettlementEconomyParams() throws XPathExpressionException {
		// ## Boost MINING income ##
		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_MINING']/pip_modifier", "value", getMiningMulti());

		// ## Boost TRADE income - to equalize removed mwechants ##
		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_TRADE']/pip_modifier", "value", getTradeMulti());
	}

	private void updateGuildRequirements() {
		// ## Lower requirement for Merchant Guild by 4 times (no bonuses from merchants on resources) ##
		val merchantGuildLine = exportDescrGuilds.FindFirstExactLine("Guild merchants_guild");
		if(merchantGuildLine < 0) throw new PatcherLibBaseEx("No Guild merchants_guild marker");

		// ORG : levels 100 250 400
		exportDescrGuilds.ReplaceLine(merchantGuildLine+2, " levels " + merchantGuildLevelsStr);
	}

	private void removeBuildingMerchantCapabilities() {
		//         agent_limit merchant 1
		edb.getLines().removeAllRegexLines("^\\s*agent_limit\\s+merchant\\s+");

		// ## Remove all merchants production capabilities: agent merchant  0  requires ....
		edb.getLines().removeAllRegexLines("^\\s*agent\\s+merchant\\s");
	}

	private void removeStartingMerchants() {

		registerForUpdate(descrStrat);

		LinesProcessor descrStratChars = descrStrat.Factions.content().lines();
		int index = 0, lastLine=0;

		// ## Disable all starting merchant agentsCharacters - Campaing starting data
		while(index >= 0) {
			// Merchant add ex :
			// character	Panelo Zorzi, merchant, male, age 30, x 239, y 113
			index = descrStratChars.findFirstRegexLine("^character\\s.+,\\s*merchant\\s*,.+age\\s+", lastLine);

			if(index >= 0) {
				lastLine = index+2;

				descrStratChars.commentLine(index);
				descrStratChars.commentLine(index+1);
			}
		}
	}

	// ## Parameters ##
	@Getter @Setter private double tradeMulti;
	@Getter @Setter private double miningMulti;
	@Getter @Setter private String merchantGuildLevelsStr;
	@Getter @Setter private boolean removeStartingMerchants;
	private BuildingLimits buildings;

	public int getLimitValue(String parName) {
		return buildings.getLimitValue(parName);
	}
	public void setLimitValue(String parName, int limit) {
		buildings.setLimitValue(parName, limit);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val parIds = new ArrayUniqueList<ParamId>();

		parIds.add(new ParamIdDouble("TradeMulti", "Trade Multiplier",
				f -> ((MerchantsNerfed)f).getTradeMulti(), (f, value) -> ((MerchantsNerfed)f).setTradeMulti(value) ));

		parIds.add(new ParamIdDouble("MiningMulti", "Mining Multiplier",
				f -> ((MerchantsNerfed)f).getMiningMulti(), (f, value) -> ((MerchantsNerfed)f).setMiningMulti(value) ));

		parIds.add(new ParamIdString("MerchantGuildLevels", "Merchant Guild Levels",
				f -> ((MerchantsNerfed)f).getMerchantGuildLevelsStr(),
				(f, value) -> ((MerchantsNerfed)f).setMerchantGuildLevelsStr(value) ));

		parIds.add(new ParamIdBoolean("RemoveStartingMerchants", "Remove Starting Merchants",
				f -> ((MerchantsNerfed)f).isRemoveStartingMerchants(), (f, value) -> ((MerchantsNerfed)f).setRemoveStartingMerchants(value) ));

		// ## Define buildings params
		val buildingsPars = buildings.getBuildingsParams();
		for (val bp: buildingsPars) {
			val parName = bp.getItem1();
			val displayName = bp.getItem2();
			parIds.add(new ParamIdInteger(parName, displayName,
					f -> ((MerchantsNerfed)f).getLimitValue(parName),
					(f, value) -> ((MerchantsNerfed)f).setLimitValue(parName, value) ));
		}

		return parIds;
	}

	private ExportDescrBuilding edb;
	private DescrSettlementMechanics descrSettlementMechanics;
	private ExportDescrGuilds exportDescrGuilds;
	private DescrStratSectioned descrStrat;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("b7021c52-e7ba-4fb3-8e03-7d0b694ecd63");

	public MerchantsNerfed() {
		super("Merchants Nerfed");

		addCategory("Agents");

		setDescriptionShort("Merchants availability is much smaller");
		setDescriptionUrl("http://tmsship.wikidot.com/merchants-nerfed");

		buildings = new BuildingLimits();
	}

	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(MerchantsRemoved.Id);

		return conflicts;
	}
}
