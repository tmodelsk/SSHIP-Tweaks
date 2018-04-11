package tm.mtwModPatcher.sship.features.agentsCharacters;

import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdString;
import tm.mtwModPatcher.lib.data._root.DescrSettlementMechanics;
import tm.mtwModPatcher.lib.data._root.ExportDescrGuilds;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;
import tm.mtwModPatcher.sship.features.agentsCharacters.merchantsNerfed.MerchantsNerfed;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Removes all merchants */
public class MerchantsRemovedFtr extends Feature {

	@Override
	public void setParamsCustomValues() {
		tradeMulti = 1.85;	// 1.85
		miningMulti = 2.0;
		merchantGuildLevelsStr = "30 60 120";
	}

	@Override
	public void executeUpdates() throws Exception {

		descrStrat = fileEntityFactory.getFile(DescrStratSectioned.class);
		registerForUpdate(descrStrat);

		edb = fileEntityFactory.getFile(ExportDescrBuilding.class);
		registerForUpdate(edb);

		descrSettlementMechanics = getFileRegisterForUpdated(DescrSettlementMechanics.class);

		exportDescrGuilds = fileEntityFactory.getFile(ExportDescrGuilds.class);
		registerForUpdate(exportDescrGuilds);

		// ### Disable ALL MERCHANTS ###
		DisableAllMerchants_LowerGuildReqs();

		// ##### Boost various Incomes - to equalize removed mwechants #####
		// ## Boost MINING income ##
		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_MINING']/pip_modifier", "value", miningMulti);

		// ## Boost TRADE income - to equalize removed mwechants ##
		descrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_TRADE']/pip_modifier", "value", tradeMulti);

		// ## Boost Farming income ##
		//_DescrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_FARMS']/pip_modifier", "value", 1.15);

		// ## Boost Taxes income ##
		//_DescrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_TAXES']/pip_modifier", "value", 1.1);

		// ## Boost Buildings Trade bonuses ##
		//BoostBuildingTradeBonuses(1);

		//UpdateKingsPurses();

		// ## Boost GARRISON influenc to City ORDER
		//_DescrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SOF_GARRISON']/pip_modifier", "value", 2.0);

		//_DescrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SOF_BUILDINGS_LAW']/pip_modifier", "value", 1.1);
		//_DescrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SOF_BUILDINGS_FUN']/pip_modifier", "value", 1.1);

		// ## Boost Corruption - to hit large empires ##
		//_DescrSettlementMechanics.updateAttributeByMultiplier("/root/factor_modifiers/factor[@name='SIF_CORRUPTION']/pip_modifier", "value", 1.25);
	}

	private void DisableAllMerchants_LowerGuildReqs() throws PatcherLibBaseEx {
		int lastLine = 0, index=0;

		LinesProcessor descrStratChars = descrStrat.Factions.getContent().getLines();

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

		// ## Building Definitions - remove all merchants production capabilities ##
		//         agent merchant  0  requires factions { northern_european, }
		//edb.getLines().removeAllRegexLines("^\\s*agent\\s+merchant\\s");
		//         agent_limit merchant 1
		//edb.getLines().removeAllRegexLines("^\\s*agent_limit\\s+merchant\\s+");

		// ## Lower requirement for Merchant Guild by 4 times (no bonuses from merchants on resources) ##
		//int merchantGuildLine = exportDescrGuilds.FindFirstExactLine("Guild merchants_guild");
		//if(merchantGuildLine < 0) throw new PatcherLibBaseEx("No Guild merchants_guild marker");
		// ORG : levels 100 250 400
		//exportDescrGuilds.ReplaceLine(merchantGuildLine+2, " levels " + merchantGuildLevelsStr);
	}

	@Deprecated()
	protected void BoostBuildingTradeBonuses(int addBonus) throws PatcherLibBaseEx {
		int index = 0;
		String tradeBonusRegexStr="(^\\s*trade_base_income_bonus\\s+bonus\\s+)(\\d+)(.*)", bonusPrefix, bonusSufix, bonusValueStr;
		Pattern pattern = Pattern.compile(tradeBonusRegexStr);
		int bonusValue;
		boolean isContiniue = true;
		while(isContiniue) {
			//     		trade_base_income_bonus bonus 2 requires event_counter is_the_ai 4
			index = edb.getLines().findFirstRegexLine(tradeBonusRegexStr, index);
			if(index >= 0) {
				String orgLine = edb.getLines().getLine(index);

				Matcher matcher = pattern.matcher(orgLine);
				if(!matcher.find()) throw new PatcherLibBaseEx("Expected to find trade_base_income_bonus but not found");

				bonusPrefix = matcher.group(1);
				bonusValueStr = matcher.group(2);
				bonusValue = Integer.parseInt(bonusValueStr);
				bonusSufix = matcher.group(3);

				String newLine = bonusPrefix+Integer.toString(bonusValue+addBonus)+bonusSufix;

				edb.getLines().replaceLine(index, newLine);
				index++;
			}
			else isContiniue = false;
		}
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val parIds = new ArrayUniqueList<ParamId>();

		ParamIdDouble parDouble;
		parDouble= new ParamIdDouble("TradeMulti", "Trade Multiplier", (f) -> {
			MerchantsRemovedFtr thisFtr = (MerchantsRemovedFtr)f;
			return thisFtr.tradeMulti;
		}, ((f, value) -> {
			MerchantsRemovedFtr thisFtr = (MerchantsRemovedFtr)f;
			thisFtr.tradeMulti = value;
		} ));
		parIds.add(parDouble);

		parDouble= new ParamIdDouble("MiningMulti", "Mining Multiplier", (f) -> {
			MerchantsRemovedFtr thisFtr = (MerchantsRemovedFtr)f;
			return thisFtr.miningMulti;
		}, ((f, value) -> {
			MerchantsRemovedFtr thisFtr = (MerchantsRemovedFtr)f;
			thisFtr.miningMulti = value;
		} ));
		parIds.add(parDouble);

		val parString= new ParamIdString("MerchantGuildLevels", "Merchant Guild Levels", (f) -> {
			MerchantsRemovedFtr thisFtr = (MerchantsRemovedFtr)f;
			return thisFtr.merchantGuildLevelsStr;
		}, ((f, value) -> {
			MerchantsRemovedFtr thisFtr = (MerchantsRemovedFtr)f;
			thisFtr.merchantGuildLevelsStr = value;
		} ));
		parIds.add(parString);

		return parIds;
	}

	private Double tradeMulti;
	private Double miningMulti;
	private String merchantGuildLevelsStr;

	protected DescrStratSectioned descrStrat;
	protected ExportDescrBuilding edb;
	private DescrSettlementMechanics descrSettlementMechanics;
	private ExportDescrGuilds exportDescrGuilds;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("a0aa32d2-f475-4207-a0b5-cee3f5649994");

	public MerchantsRemovedFtr() {

		super("Remove merchants");
		addCategory("Economy");
		addCategory("Agents");

		setDescriptionShort("All merchants are removed & economy is rebalanced to fulfill missing income");
		setDescriptionUrl("http://tmsship.wikidot.com/merchants-removed");
	}

	@Override
	public Set<UUID> getConflictingFeatures() {
		val conflicts = new HashSet<UUID>();

		conflicts.add(MerchantsNerfed.Id);

		return conflicts;
	}
}
