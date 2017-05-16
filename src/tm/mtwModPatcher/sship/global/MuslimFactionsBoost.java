package tm.mtwModPatcher.sship.global;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdBoolean;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.fileEntities.data.DescrCampaignDb;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.managers.UnitsManager;

import javax.xml.xpath.XPathExpressionException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Gives benefits to muslim factions
 */
public class MuslimFactionsBoost extends Feature {

	@Getter @Setter
	private boolean ahdathMilitiaBoost = false;

	@Override
	public void executeUpdates() throws Exception {
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);
		exportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		descrCampaignDb = getFileRegisterForUpdated(DescrCampaignDb.class);

		unitsReplenishRatesBoost();
		religiousConversionTempleBonus();
		jihadRequirement();
	}

	private void unitsReplenishRatesBoost() {

		List<Pattern> unitsToExclude = Arrays.asList(Pattern.compile(".*[Cc]hristian.*"));

		String factionsFilterCsv = FactionsDefs.islamFactionsCsv() + FactionsDefs.slaveCsv();
		UnitsManager unitsManager = new UnitsManager();

		unitsManager.addToAllUnitsReplenishRates(factionsFilterCsv, 2.0, -1.0, unitsToExclude, exportDescrBuilding);

		if (ahdathMilitiaBoost) {
			val ahdathMilitia = exportDescrUnit.loadUnit("Ahdath Militia");
			ahdathMilitia.StatCost.Cost *= 0.90;
			ahdathMilitia.StatCost.RecruitTurns = 1;
		}

		// ## Additional Upgrade replenish for Ahdath Militia
		//unitsManager.addToReplenishRates("Ahdath Militia" , factionsFilterCsv , 2.0 , -1.0 , exportDescrBuilding);

		// ## Additional Upgrade replenish for :
		//unitsManager.addToReplenishRates("Fari Lancers" , factionsFilterCsv , 2.0 , -1.0 , exportDescrBuilding);
		//unitsManager.addToReplenishRates("Tawashi Light Cavalry" , factionsFilterCsv , 2.0 , -1.0 , exportDescrBuilding);
		//unitsManager.addToReplenishRates("Arab Cavalry" , factionsFilterCsv , 2.0 , -1.0 , exportDescrBuilding);


		// Ahdath Militia - cheaper recruitment
	}

	private void religiousConversionTempleBonus() throws PatcherLibBaseEx {

		// ### Religion Conversion bonus Muslims Mosques  ###

		String attribStr = "        religion_level bonus ";

		// # City #
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim", "small_masjid", "city", attribStr + 1);
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim", "masjid", "city", attribStr + 1);
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim", "minareted_masjid", "city", attribStr + 1);
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim", "jama", "city", attribStr + 1);
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim", "great_jama", "city", attribStr + 1);
		// # Castle #
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim_castle", "c_small_masjid", "castle", attribStr + 1);
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_muslim_castle", "c_masjid", "castle", attribStr + 1);
	}

	private void jihadRequirement() throws XPathExpressionException {
		descrCampaignDb.setAttribute("/root//crusades/required_jihad_piety", "int", 4);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdBoolean("AhdathMilitiaBoost", "Ahdath Militia Boost",
				feature -> ((MuslimFactionsBoost) feature).isAhdathMilitiaBoost(),
				(feature, value) -> ((MuslimFactionsBoost) feature).setAhdathMilitiaBoost(value)));

		return pars;
	}

	protected ExportDescrBuilding exportDescrBuilding;
	protected ExportDescrUnitTyped exportDescrUnit;
	protected DescrCampaignDb descrCampaignDb;

	@Override
	public UUID getId() {
		return Id;
	}

	public static UUID Id = UUID.randomUUID();

	public MuslimFactionsBoost() {
		setName("Muslim Factions Boost");

		addCategory("Campaign");

		setDescriptionShort("Gives various benefits to muslim factions");
		setDescriptionUrl("http://tmsship.wikidot.com/muslim-factions-boost");
	}
}
