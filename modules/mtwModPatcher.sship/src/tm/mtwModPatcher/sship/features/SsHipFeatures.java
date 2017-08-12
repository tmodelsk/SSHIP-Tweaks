package tm.mtwModPatcher.sship.features;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.FeatureList;
import tm.mtwModPatcher.lib.common.core.features.ResourcesProvider;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.InputStreamProvider;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;
import tm.mtwModPatcher.lib.engines.ConsoleLogger;
import tm.mtwModPatcher.lib.engines.FileEntityFactory;
import tm.mtwModPatcher.lib.managers.UnitsManager;
import tm.mtwModPatcher.lib.managers.garrisons.GarrisonManager;
import tm.mtwModPatcher.sship.features.agentsCharacters.*;
import tm.mtwModPatcher.sship.features.ai.*;
import tm.mtwModPatcher.sship.features.armyUnits.*;
import tm.mtwModPatcher.sship.features.buildings.*;
import tm.mtwModPatcher.sship.features.garrisons.GarrisonNoUnguardedSettlements;
import tm.mtwModPatcher.sship.features.garrisons.GarrisonOnSiegeRaising;
import tm.mtwModPatcher.sship.features.global.*;
import tm.mtwModPatcher.sship.features.global.factionFate.*;
import tm.mtwModPatcher.sship.features.layout.CampaignMapHudToggler;
import tm.mtwModPatcher.sship.features.layout.MenuScreenSS;
import tm.mtwModPatcher.sship.features.layout.SplashLoadingScreen;
import tm.mtwModPatcher.sship.features.layout.WatchtowersToVillages;
import tm.mtwModPatcher.sship.features.map.*;

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
		features.add(new TrebuchetEarlyAdoption());
		features.add(new VeryHugeUnitSize());

		features.add(new MerchantsRemovedFtr());
		features.add(new WorldEconomyScaling());
		features.add(new SettlementUnrestLowered());
		features.add(new RecruitmentSlotsBoost());

		features.add(new ArmySuppliesCosts());
		features.add(new MaxTreasuryLimited());
		features.add(new BeeMugCarlAITweaks());
		//features.add(new SkynetBattleAi(inputStreamProvider));

		features.add(new CampaignAisCompilation(resourcesProvider));
		features.add(new BeeMugCarlCampaignAi(resourcesProvider));
		features.add(new QuieterAi(resourcesProvider));
		features.add(new SkynetCampaignAi(resourcesProvider));
		features.add(new QuieterAiFactionStanding());

		features.add(new BetterLogging());

		features.add(new CastleEasierGrowth());

		features.add(new CatholicIberiaUnitsRecruitmentIncreased());

		features.add(new MenuScreenSS());
		features.add(new SplashLoadingScreen());
		features.add(new WatchtowersToVillages());
		features.add(new CampaignMapHudToggler(inputStreamProvider));

		features.add(new LandBridgeGibraltarLaManche());
		features.add(new LandBridgeGibraltar());
		features.add(new AragonChapterHousesBoost());
		features.add(new CrusadeJihadMoreSettl());
		features.add(new WatchtowersToForts());
		features.add(new RimlandHeartland());
		features.add(new AuthenticFactionNamesCharacterTitles());

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

		features.add(new NobilityManyGeneralsGovernors());
		features.add(new MuslimFactionsBoost());
		features.add(new CatholicFactionsBoost());
		features.add(new FightForSurvival(unitsManager));	// adds replenish rates entries for existing stuff, should be almost last
		features.add(new PopulationResourcesLimited());	// Should be one of the last, globally modifies replenish & max stack rates

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

		features.disableFeatureIfExists( LandBridgeGibraltar.Id);
		features.disableFeatureIfExists( BeeMugCarlCampaignAi.Id);
		features.disableFeatureIfExists( QuieterAi.Id);
		features.disableFeatureIfExists( SkynetBattleAi.Id);
		features.disableFeatureIfExists( SkynetCampaignAi.Id);
		features.disableFeatureIfExists( WatchtowersToForts.Id);
		features.disableFeatureIfExists(VeryHugeUnitSize.Id);

		if(!ConfigurationSettings.isDevEnvironment()) {

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
		// ##### Obsolete ?? ######
		//features.add(new AgentsCostsInEnemyLands());
		//features.add(new RealFogAndAgents());
		//features.add(new CatharsHereticTemple());

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
	private ResourcesProvider resourcesProvider;
	private FileEntityFactory fileEntityFactory;
	private ConsoleLogger consoleLogger;

	public SsHipFeatures(GarrisonManager garrisonManager, UnitsManager unitsManager,
						 ResourcesProvider resourcesProvider ,
						 FileEntityFactory fileEntityFactory, ConsoleLogger logger) {
		this.garrisonManager = garrisonManager;
		this.unitsManager = unitsManager;

		this.inputStreamProvider = resourcesProvider.getInputStreamProvider();
		this.resourcesProvider = resourcesProvider;

		this.fileEntityFactory = fileEntityFactory;
		this.consoleLogger = logger;
	}
}
