package tm.mtwModPatcher.sship.ai;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.OverrideDeleteFilesTask;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;
import tm.mtwModPatcher.lib.fileEntities.data.DescrCampaignDb;
import tm.mtwModPatcher.lib.fileEntities.data.DescrCharacter;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.fileEntities.data.ExportDescrCharacterTraits;
import tm.mtwModPatcher.lib.fileEntities.data.DescrSMFactions;
import tm.mtwModPatcher.lib.fileEntities.data.text.ExportVnvs;
import tm.mtwModPatcher.lib.fileEntities.data.world.maps.campaign.DescrStratSectioned;

import java.util.UUID;

/** http://www.twcenter.net/forums/showthread.php?594662-%2805-04-2016%29-My-new-and-improved-AI-errr-3-0  */
public class BeeMugCarlAITweaks extends Feature {

	protected DescrCampaignDb _DescrCampaignDb;
	protected ExportDescrBuilding _ExportDescrBuilding;
	protected ExportDescrCharacterTraits _ExportDescrCharacterTraits;
	protected DescrStratSectioned _DescrStrat;
	protected DescrSMFactions _DescrSMFactions;
	protected DescrCharacter _DescrCharacter;
	protected ExportVnvs _ExportVnvs;

	@Override
	public void executeUpdates() throws Exception {
		_DescrCampaignDb = getFileRegisterForUpdated(DescrCampaignDb.class);
		_ExportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);
		_ExportDescrCharacterTraits = getFileRegisterForUpdated(ExportDescrCharacterTraits.class);
		_DescrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		_DescrSMFactions = getFileRegisterForUpdated(DescrSMFactions.class);
		_DescrCharacter = getFileRegisterForUpdated(DescrCharacter.class);
		_ExportVnvs = getFileRegisterForUpdated(ExportVnvs.class);

		//setFactionsAttributes();
		updateStartingActionPoints();

		// Lower AI _Units revolts
		_DescrCampaignDb.setAttribute("/root/revolt/ai_revolt_modifier", 0.0001);	// org : 0.7

		// ## BOOST Core Buildins For AI ##
//		exportDescrBuilding.insertIntoCityWallsAllCapabilities("construction_cost_bonus_stone bonus 25 requires not event_counter is_the_player 1");
//		exportDescrBuilding.insertIntoCityWallsAllCapabilities("construction_cost_bonus_wooden bonus 25 requires not event_counter is_the_player 1");
//
//		exportDescrBuilding.insertIntoCastleWallsAllCapabilities("construction_cost_bonus_stone bonus 25 requires not event_counter is_the_player 1");
//		exportDescrBuilding.insertIntoCastleWallsAllCapabilities("construction_cost_bonus_wooden bonus 25 requires not event_counter is_the_player 1");


		// ### Add AI Hidden TRAITS - BOOSTS AI ####
		addTraitsForAIBoost();
	}

	protected void updateStartingActionPoints() throws PatcherLibBaseEx {
		int maxPoints = 100600;
		String startingActionPointsRegexStr = "^\\s*starting_action_points\\s+\\d+";

		LinesProcessor charactersLines = _DescrCharacter.getLines();
		// starting_action_points	170	; default value for all characters and pathfinding calculations
		int index = charactersLines.findFirstRegexLine(startingActionPointsRegexStr);
		charactersLines.replaceLine(index, "starting_action_points\t"+maxPoints+"\t; default value for all characters and pathfinding calculations");
	}

	protected void addTraitsForAIBoost() throws PatcherLibBaseEx {
		// ### Add AI Hidden TRAITS - BOOSTS AI ####
		String str="", nl = System.lineSeparator();

		// ## Traits Descriptions ##
		addTraitsDescriptions();

		// ## Trait ##
		LinesProcessor lines = _ExportDescrCharacterTraits.getLines();
		int index = lines.findFirstByRexexLines("^;=+ VNV TRAITS START HERE ","^;=+;");
		if(index < 0) throw new PatcherLibBaseEx("Unable to find start of traits");
		index+=2;

		str+=";--------------------------------------------"+nl;
		str+=";----- TM Patcher Added : AI Characters Boosts with hidden Trait with various effects - by BeeMugCarl --"+nl;
		str+="Trait AICharactersBoostHiddenTraits"+nl;
		str+=" Characters family"+nl;
		str+=""+nl;
		str+=" Level AICharactersBoostHiddenTraitsLev1"+nl;
		str+="   Description AICharactersBoostHiddenTraits_desc"+nl;
		str+="   EffectsDescription AICharactersBoostHiddenTraits_effects_desc"+nl;
		str+="   Threshold  1"+nl;
		str+=""+nl;
		str+="   Effect Fertility 1"+nl;
		str+="   Effect Command 1"+nl;
		str+="   Effect LineOfSight 6"+nl;
		str+="   Effect PersonalSecurity 2"+nl;
		str+="   Effect SiegeEngineering 50"+nl;
		str+="   Effect Attack 1"+nl;
		//str+="   Effect Loyalty 2"+nl;
		str+="   Effect Chivalry 1"+nl;
		str+="   Effect BattleSurgery 30"+nl;
		//str+="   Effect HitPoints 1"+nl;
		//str+="   Effect TaxCollection 10"+nl;
		str+="   Effect Construction 5"+nl;
		str+=""+nl;

		lines.insertAt(index, str);
		str="";

		// ## ADD TRIGGER ##
		index = lines.findFirstByRexexLines("^;=+\\s+VNV TRIGGERS START HERE","^;=+;");
		if(index < 0) throw new PatcherLibBaseEx("Unable to find start of traits triggers");
		index+=2;

		str+=""+nl;
		str+=";--------------------------------------------------------"+nl;
		str+=";----- TM Patcher Added : Army Support Region TRIGGERS --"+nl;
		str+=""+nl;
		str+="Trigger AICharactersBoostHiddenTraitsTrigger"+nl;
		str+="WhenToTest CharacterTurnEnd"+nl;
		str+=""+nl;
		str+=" Condition  not Trait AICharactersBoostHiddenTraits = 1"+nl;
		str+="  and not CharacterIsLocal"+nl;
		str+=""+nl;
		str+=" Affects AICharactersBoostHiddenTraits  1  Chance  100"+nl;
		str+=""+nl;

		lines.insertAt(index, str);
		str="";

		/*
		Effect Fertility 3
		Effect Command 4
		Effect LineOfSight 6
		Effect PersonalSecurity 2
		Effect SiegeEngineering 50
		Effect Attack 4
		Effect Loyalty 2
		Effect Chivalry 2
		Effect BattleSurgery 30
		Effect HitPoints 3
		Effect TaxCollection 80
		Effect Construction 90
		*/
	}

	private void addTraitsDescriptions() throws PatcherLibBaseEx {
		String str = "", nl = System.lineSeparator();

		str += "{AICharactersBoostHiddenTraitsLev1}	AICharactersBoostHiddenTraitsLev1" + nl;
		str += "{AICharactersBoostHiddenTraits_desc}	AICharactersBoostHiddenTraits_desc" + nl;
		str += "{AICharactersBoostHiddenTraits_effects_desc}	AICharactersBoostHiddenTraits_effects_desc" + nl;

		_ExportVnvs.insertAtStartOfFile(str);
	}

	protected void AddRecruitmentSlotsForAi() throws PatcherLibBaseEx {

		// SSHIP OLD : recruitment_slots 1 requires not event_counter freeze_recr_pool 1
		String requires = " requires not event_counter freeze_recr_pool 1 and not event_counter is_the_player 1";
		// ### CITIES ### : wooden_pallisade wooden_wall stone_wall large_stone_wall huge_stone_wall

		// ## Village ## - no bonus - no building
		int recrBonus = 1;

		// # Walls #
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", "       recruitment_slots bonus " + recrBonus + requires );
		//recrBonus++;
		
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", "       recruitment_slots bonus " + recrBonus + requires );
		recrBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", "       recruitment_slots bonus " + recrBonus + requires );
		//recrBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", "       recruitment_slots bonus " + recrBonus + requires );
		//recrBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", "       recruitment_slots bonus " + recrBonus + requires );
		//recrBonus++;

		// ### CASTLES ###
		// # Walls # - building core_castle_building
		// levels motte_and_bailey wooden_castle castle fortress citadel
		recrBonus=1;
		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", "       recruitment_slots bonus " + recrBonus + requires );
		recrBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", "       recruitment_slots bonus " + recrBonus + requires );
		//recrBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", "       recruitment_slots bonus " + recrBonus + requires );
		//recrBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", "       recruitment_slots bonus " + recrBonus + requires );
		//recrBonus++;

		_ExportDescrBuilding.insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", "       recruitment_slots bonus " + recrBonus + requires );
		//recrBonus++;
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public BeeMugCarlAITweaks() {
		super("BeeMugCarl AI tweaks");

		addCategory("Campaign");

		setDescriptionShort("Additional AI related tweaks by BeeMugCarl");
		setDescriptionUrl("http://tmsship.wikidot.com/beemugcarl-ai-tweaks");

		boolean isDevMachine = ConfigurationSettings.isDevMachine();

		if(!isDevMachine)
			addOverrideTask(new OverrideDeleteFilesTask("data\\world\\maps\\base\\map.rwm"));
	}
}
