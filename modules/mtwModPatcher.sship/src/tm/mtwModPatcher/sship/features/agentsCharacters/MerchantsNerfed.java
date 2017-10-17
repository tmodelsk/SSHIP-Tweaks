package tm.mtwModPatcher.sship.features.agentsCharacters;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.PatcherNotSupportedEx;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdString;
import tm.mtwModPatcher.lib.data._root.DescrSettlementMechanics;
import tm.mtwModPatcher.lib.data._root.ExportDescrGuilds;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.BuildingLevel;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;
import tm.mtwModPatcher.sship.features.agentsCharacters.merchantsNerfed.BuildingLimit;
import tm.mtwModPatcher.sship.features.agentsCharacters.merchantsNerfed.BuildingLimits;
import tm.mtwModPatcher.sship.lib.Buildings;

import javax.xml.xpath.XPathExpressionException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by tomek on 15.10.2017.
 */
public class MerchantsNerfed extends Feature {

	@Override
	public void setParamsDefaultValues() {
		tradeMulti = 1.4;	// 1.85
		miningMulti = 2.0;
		merchantGuildLevelsStr = "30 60 120";
		buildings = new BuildingLimits();

		super.setParamsDefaultValues();
	}

	public int getLimitValue(String parName) {
		return buildings.getLimitValue(parName);
	}
	public void setLimitValue(String parName, int limit) {
		buildings.setLimitValue(parName, limit);
	}

	@Override
	public void executeUpdates() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		exportDescrGuilds = getFileRegisterForUpdated(ExportDescrGuilds.class);
		descrSettlementMechanics = getFileRegisterForUpdated(DescrSettlementMechanics.class);

		removeLimitUpdates();

		addMerchantLimits();
		updateSettlementEconomyParams();
		updateGuildRequirements();
	}

	private void addMerchantLimits() {
		val limitParams = buildings.getBuildingsParams();

		for (val paramDef : limitParams) {
			val bl = buildings.get(paramDef.getItem1());

			if(bl.Limit > 0) {
				val buildingLevels = resolveBuidlingNameLevels(bl);
				for (val bLevel: buildingLevels) {
					ensureAgentReruitmentExists(bLevel, bl.Requires);

					String limitLine = "agent_limit merchant " + bl.Limit;
					if(bl.Requires != null && !bl.Requires.isEmpty()) limitLine += " requires " + bl.Requires;
					edb.insertIntoBuildingCapabilities(bLevel, limitLine);
				}
			}
		}
	}
	private void ensureAgentReruitmentExists(BuildingLevel buildingLevel, String require) {
		val range = edb.getBuildingCapabilitiesStartEnd(buildingLevel);
		val regex = Pattern.compile("^\\s*agent\\s+merchant\\s");

		val index = edb.getLines().findFirstRegexLine(regex, range);
		if(index < 0) {
			// we need to insert
			val lines = Arrays.asList("agent merchant  0  requires factions { northern_european, }",
					"agent merchant  0  requires factions { middle_eastern, }",
					"agent merchant  0  requires factions { eastern_european, }",
					"agent merchant  0  requires factions { greek, }",
					"        agent merchant  0  requires factions { southern_european, }");

			if(require == null) require = "";
			else require = " and " + require;
			for (val line : lines) {
				edb.insertIntoBuildingCapabilities(buildingLevel, line + require);
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
					Buildings.WallsCityLevels.forEach( l -> res.add(new BuildingLevel(Buildings.WallsCity.Name, l, SettlType.City)));
					Buildings.WallsCastleLevels.forEach( l -> res.add(new BuildingLevel(Buildings.WallsCastle.Name, l, SettlType.Castle)));
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

	private void removeLimitUpdates() {
		edb.getLines().removeAllRegexLines("^\\s*agent_limit\\s+merchant\\s+");
	}

	// ## Parameters ##
	@Getter @Setter
	private double tradeMulti;
	@Getter @Setter
	private double miningMulti;
	@Getter @Setter
	private String merchantGuildLevelsStr;
	private BuildingLimits buildings;

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


	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

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

		conflicts.add(MerchantsRemovedFtr.Id);

		return conflicts;
	}
}
