package tm.mtwModPatcher.sship.features.agentsCharacters;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data._root.ExportDescrCharacterTraits;

import java.util.UUID;

/**
 * Created by Tomek on 2016-04-22.
 */
public class NoDreadOnAssasinations extends Feature {

/*	 NOTATKI
--------------------
Trigger Priest_Visit_Mecca_Doctrine
 WhenToTest CharacterTurnEnd

 Condition IsRegionOneOf 199 Mecca_Province
       and AgentType = priest
       and CharacterReligion islam
       and TimeInRegion > 1
       and Trait PriestIslamicDoctrine = 0

 Affects PriestIslamicDoctrine 1 Chance 100

;------------------------------------------
Trigger Priest_Visit_JerusalemRegion_All
 WhenToTest CharacterTurnEnd

 Condition IsRegionOneOf 190 Jerusalem_Province
       and AgentType = priest
       and not CharacterReligion pagan
       and TimeInRegion > 1
       and Trait StrongFaith <= 4

 Affects StrongFaith 1 Chance 33

---------------------

	Trigger DamnCrusaders
	WhenToTest CharacterTurnEnd

	Condition EndedInSettlement
	and IsCrusadeTarget
	and not Religion catholic  <-----------------------------

	Affects HatesCatholic 1 Chance 25


Trigger 0112_Destroy_Evil_Allies_Happy_Destroyed_Allies_Unhappy
    WhenToTest LeaderDestroyedFaction

    Condition TargetReligion islam	  <-----------------------------

    FactionStanding allies normalise 1.0 50
    FactionStanding target_allies normalise -1.0 50
    FactionStanding target_enemies normalise 1.0 200

####################################################################################
######## Events, Conditions & Commands 										########
######## http://mtwitalia.freeforumzone.com/discussione.aspx?idd=10153612 	########
####################################################################################

Identifier: SpyMission
Event: A spy mission has completed
Exports: nc_character_record, character_record, faction, region_id, character_type, target_faction, mission_success_level, target_religion
Class: ET_SPY_MISSION

Identifier: ExecutesASpyOnAMission
Event: An spying mission has failed and the spy is executed by the target
Exports: nc_character_record, character_record, faction, region_id, character_type
Class: ET_EXECUTES_A_SPY_ON_A_MISSION

Identifier: LeaderOrderedSpyingMission
Event: A Faction leader has ordered a spying mission
Exports: nc_character_record, character_record, faction, region_id, character_type, target_faction, mission_success_level, target_religion
Class: ET_LEADER_ORDERED_SPYING_MISSION

Identifier: AssassinationMission
Event: An assassination mission has completed
Exports: nc_character_record, character_record, faction, region_id, character_type, mission_success_level
Class: ET_ASSASSINATION_MISSION

Identifier: ExecutesAnAssassinOnAMission
Event: An assassination mission has failed and the assassin is executed by the target
Exports: nc_character_record, character_record, faction, region_id, character_type
Class: ET_EXECUTES_AN_ASSASSIN_ON_A_MISSION

Identifier: AssassinCaughtAttackingPope
Event: An assassination mission against the pope has failed and the assassin is executed
Exports: faction
Class: ET_ASSASSIN_CAUGHT_ATTACKING_POPE

Identifier: LeaderOrderedAssassination
Event: A Faction leader has ordered an assassination mission
Exports: nc_character_record, character_record, faction, region_id, character_type, target_faction, mission_success_level
Class: ET_LEADER_ORDERED_ASSASSINATION_MISSION

Identifier: SufferAssassinationAttempt
Event: Someone has had an attempt on their life
Exports: nc_character_record, character_record, faction, region_id, character_type
Class: ET_SUFFER_ASSASSINATION_ATTEMPT



#################################################################################
##########    AGENT TYTPE		= AgentType										#########
########## http://www.twcenter.net/wiki/AgentType_%28M2-Scripting%29 	#########
    Type: Condition
    Trigger Requirements: character_record
    Parameters: character type (spy, assassin, diplomat, admiral, general, named character, family)
    CA Example: AgentType = diplomat
    CA Description: Test to see if a character is of a particular type (spy, admiral, named character &c.)
    Battle/Strat: Strat
    Class: CHARACTER_TYPE_TEST
################################################################################################################


Identifier: AgentType
Trigger requirements: character_record
Parameters: character type (spy, assassin, diplomat, admiral, general, named character, family)
Sample use: AgentType = diplomat
Description: Test to see if a character is of a particular type (spy, admiral, named character &c.)
Battle or Strat: Strat
Class: CHARACTER_TYPE_TEST

*/

	protected ExportDescrCharacterTraits exportDescrCharacterTraits;

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		exportDescrCharacterTraits = fileEntityFactory.getFile(ExportDescrCharacterTraits.class);
		registerForUpdate(exportDescrCharacterTraits);

		val edct = exportDescrCharacterTraits;

		// ## Disable Dread from Trait effects ##
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("SpyMaster", "Master_of_Espionage");
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("AssassinMaster", "Open_to_Murder");
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("AssassinMaster", "Mixes_with_Killers");
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("AssassinMaster", "Master_of_Assassins");

		// ## Disable Strategy Dread chance from Triggetsd
		edct.commentAffectTraitInTrigger("King_Employs_Spies_Success", "StrategyDread");
		edct.commentAffectTraitInTrigger("King_Employes_Assassins_Success", "StrategyDread");
		edct.commentAffectTraitInTrigger("King_Employes_Assassins_Failure", "StrategyDread");

		edct.setAffectTraitChanceInTrigger("governing10", "StrategyDread", 1);
		edct.setAffectTraitChanceInTrigger("governing11", "StrategyDread", 1);
	}

	private void BattleStrategyDread() {
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("StrategyDread", "Mean_Leader");
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("StrategyDread", "Cruel_Leader");
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("StrategyDread", "Merciless_Leader");
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("StrategyDread", "Malevolent_Leader");
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("StrategyDread", "Tyranical_Leader");


		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("BattleDread", "Winning_First");
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("BattleDread", "Cruel_and_Cunning");
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("BattleDread", "Merciless_Mauler");
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("BattleDread", "Field_Tyrant");
		exportDescrCharacterTraits.commentNegativeChivalryFromTraitEffect("BattleDread", "Warlord_of_Terror");
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public final static UUID Id = UUID.fromString("b06e41ea-cf85-40a7-b40b-fe9ff4782af5");

	public NoDreadOnAssasinations() {
		super("No Dread on assasinations");

		addCategory("Agents");

		setDescriptionShort("Assassinations don't cause Dread. Training spyies & assassins don't cause Dread");
		setDescriptionUrl("http://tmsship.wikidot.com/no-dread-on-assasinations");

	}
}
