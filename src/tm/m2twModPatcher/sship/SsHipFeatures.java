package tm.m2twModPatcher.sship;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.FeatureList;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.InputStreamProvider;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;
import tm.mtwModPatcher.lib.engines.ConsoleLogger;
import tm.mtwModPatcher.lib.engines.FileEntityFactory;
import tm.mtwModPatcher.lib.managers.UnitsManager;
import tm.mtwModPatcher.lib.managers.garrisons.GarrisonManager;
import tm.m2twModPatcher.sship.agentsCharacters.*;
import tm.m2twModPatcher.sship.ai.BeeMugCampaignAI;
import tm.m2twModPatcher.sship.ai.BeeMugCarlAITweaks;
import tm.m2twModPatcher.sship.ai.BetterLogging;
import tm.m2twModPatcher.sship.armyUnits.*;
import tm.m2twModPatcher.sship.buildings.*;
import tm.m2twModPatcher.sship.garrisons.GarrisonNoUnguardedSettlements;
import tm.m2twModPatcher.sship.garrisons.GarrisonOnSiegeRaising;
import tm.m2twModPatcher.sship.global.*;
import tm.m2twModPatcher.sship.global.factionFate.*;
import tm.m2twModPatcher.sship.layout.MenuScreenSS;
import tm.m2twModPatcher.sship.layout.SplashLoadingScreen;
import tm.m2twModPatcher.sship.layout.WatchtowersToVillages;
import tm.m2twModPatcher.sship.map.*;

/** Christianitas Mod SSHIP Feature */
public class SsHipFeatures {

	private FeatureList features;

	public FeatureList configureFeatures() throws Exception {

		features = new FeatureList();

		features.add(new SelectHeirScript(inputStreamProvider));

		features.add(new LongerBattles());
		features.add(new CavalrySpeedBonus());
		features.add(new SnowStormNerfed());
		features.add(new MercenariesCosts());
		features.add(new ArchersLevyBoost());
		features.add(new ArchersCrossbomanRecruitmentBalancing());
		features.add(new FatiqueLowered());
		features.add(new OrderKnightsAvailable());
		features.add(new PeasantsRecruitmentRemoved());

		features.add(new MerchantsRemovedFtr());
		features.add(new WorldEconomyScaling());
		features.add(new SettlementUnrestLowered());
		features.add(new RecruitmentSlotsBoost());

		features.add(new ArmySuppliesCosts());
		features.add(new MaxTreasuryLimited());
		features.add(new FightForSurvival(unitsManager));
		features.add(new BeeMugCampaignAI());
		features.add(new BeeMugCarlAITweaks());

		features.add(new BetterLogging());

		features.add(new CastleEasierGrowth());

		features.add(new CatholicIberiaUnitsRecruitmentIncreased());

		features.add(new MenuScreenSS());
		features.add(new SplashLoadingScreen());
		features.add(new WatchtowersToVillages());

		features.add(new LandBridgeGibraltarLaManche());
		features.add(new LandBridgeGibraltar());
		features.add(new AragonChapterHousesBoost());
		features.add(new CrusadeJihadMoreSettl());
		features.add(new WatchtowersToForts());

		features.add(new NobilityManyGeneralsGovernors());

		//features.add(new NoRegionBordersOnMiniMap());  TODO : zrobic !!

		features.add(new CrusaderMercsToLevant());
		features.add(new CrusaderMercsToIberia());
		features.add(new LowerMorale());

		features.add(new SlowerArmies());

		features.add(new BuildingTweaks());
		features.add(new TemplesTweaks());
		features.add(new FactionBalancingFtr());

		features.add(new AgentsNumbersLimited());

		features.add(new ProvincialTitlesFixJoC());
		features.add(new GeneralsEducationTraitsFixes());

		features.add(new AssasinsHomeProtectors());
		features.add(new NoDreadOnAssasinations());
		features.add(new StrategyDreadBuildingsDisable());
		features.add(new BattleDreadLowered());

		FactionsSpecifics factionsSpecifics = new FactionsSpecifics(true);
//		features.add(new Civilizations(true , factionsSpecifics));
		features.add(factionsSpecifics);

		features.add(new MoorsFate());
		features.add(new EgyptFate());
		features.add(new TurksFate());
		features.add(new ZengidsFate());
		features.add(new SpainFate());
		features.add(new CrusaderStatesFate());

		features.add(new GarrisonNoUnguardedSettlements(garrisonManager));
		features.add(new GarrisonOnSiegeRaising(garrisonManager));

		features.add(new PopulationResourcesLimited());	// Should be one of the last, globally modifies replenish & max stack rates
		features.add(new MuslimFactionsBoost());
		features.add(new CatholicFactionsBoost());

//		features.add(new AgentsCostsInEnemyLands());

		// ##### Obsolete ?? ######
		//features.add(new RealFogAndAgents());
		//features.add(new CatharsHereticTemple());

		initializeFeatures(features);

		return features;
	}

	public void enableAll() {
		for (val ft : features.getFeaturesList()) {
			ft.setEnabled(true);
		}

	}
	public void enableDefaults() {
		enableAll();

		features.getEnabled( LandBridgeGibraltar.Id).disable();

		if(!ConfigurationSettings.isDevMachine()) {
			features.getEnabled( LandBridgeGibraltarLaManche.Id).disable();
			features.getEnabled( BeeMugCarlAITweaks.Id).disable();
			features.getEnabled( CrusadeJihadMoreSettl.Id).disable();
			features.getEnabled( AragonChapterHousesBoost.Id).disable();
			features.getEnabled( BetterLogging.Id).disable();
		}
	}
	public void disableAll() {
		for (val ft : features.getFeaturesList()) {
			ft.setEnabled(false);
		}
	}

	private void initializeFeatures(FeatureList featureList) {
		for (Feature feature : featureList.getFeaturesList()) {
			feature.initialize(fileEntityFactory, consoleLogger);
		}
	}


	@Deprecated
	protected void oldUnused(){
		//		// ### UNUSED - OLD
// 		//features.add(new FortsConstructionAllowedFtr());
//		//features.add(new FreeUpkeepEconomy());
//		//features.add(new PamplonaToCastle());
//		//features.add(new RecruitmentSlotsBoost());
//		//features.add(new PopulationGrowthPenaltiesLower());
		//features.add(new MultiGeneralsGonvernors());
		//features.add(new ArchersCrossbowsFirepowerBalancing());
		//features.add(new SaragossaFortifiedFtr());

		// # Installed permanenlty - performance of creating deleted map.rwm on game start ! #
		//features.add(new LoadingScreens());


	}

	private GarrisonManager garrisonManager;
	private UnitsManager unitsManager;

	private InputStreamProvider inputStreamProvider;
	private FileEntityFactory fileEntityFactory;
	private ConsoleLogger consoleLogger;

	public SsHipFeatures(GarrisonManager garrisonManager, UnitsManager unitsManager,
						 InputStreamProvider inputStreamProvider,
						 FileEntityFactory fileEntityFactory, ConsoleLogger logger) {
		this.garrisonManager = garrisonManager;
		this.unitsManager = unitsManager;

		this.inputStreamProvider = inputStreamProvider;

		this.fileEntityFactory = fileEntityFactory;
		this.consoleLogger = logger;
	}
}
