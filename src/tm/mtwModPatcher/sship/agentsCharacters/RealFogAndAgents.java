package tm.mtwModPatcher.sship.agentsCharacters;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.fileEntities.data.DescrCharacter;
import tm.mtwModPatcher.lib.fileEntities.data.DescrCultures;
import tm.mtwModPatcher.lib.fileEntities.data.ExportDescrCharacterTraits;

import java.util.UUID;

/**
 * Created by Tomek on 2016-04-17.
 * Realistic FOG of War (campaign) & agentsCharacters modification
 * TODO : 	descr_character.txt - tutaj action poits i wages - upkeep costs
 * 			descr_cultures.txt - tutaj initial cost, dla każdej kulture
 *
 * 	Diplomat - sight range = 4
 * 	Spy (-5) - sight range = 6 [Trait -5 sight)
 */
public class RealFogAndAgents extends Feature {

	protected ExportDescrCharacterTraits _ExportDescrCharacterTraits;
	protected DescrCharacter _DescrCharacter;
	protected DescrCultures _DescrCultures;

	@Override
	public void executeUpdates() throws Exception {

		_ExportDescrCharacterTraits = fileEntityFactory.getFile(ExportDescrCharacterTraits.class);
		registerUpdatedFile(_ExportDescrCharacterTraits);

		_DescrCharacter = fileEntityFactory.getFile(DescrCharacter.class);
		registerUpdatedFile(_DescrCharacter);

		_DescrCultures = fileEntityFactory.getFile(DescrCultures.class);
		registerUpdatedFile(_DescrCultures);

		_ExportDescrCharacterTraits.insertNewAttribute("NaturalSpySkill","Quietly_Observant","   Effect LineOfSight -3");	// -5 " optimal : big fog
		_ExportDescrCharacterTraits.insertNewAttribute("NaturalSpySkill","Spying_Talent","   Effect LineOfSight -3");
		_ExportDescrCharacterTraits.insertNewAttribute("NaturalSpySkill","Natural_Spy","   Effect LineOfSight -3");

		// ### UPKEEP - WAGE - COST ###
		_DescrCharacter.setCharacterWage("spy", 200);		// org : 100
		_DescrCharacter.setCharacterWage("assassin", 400);	// org : 200
		_DescrCharacter.setCharacterWage("diplomat", 200);	// org : 80  ?

		// ## Initial COST ##
		_DescrCultures.setCharacterCost("spy", 750,2);			// org : 350
		_DescrCultures.setCharacterCost("assassin", 750,2);		// org : 500
		_DescrCultures.setCharacterCost("diplomat", 400,1);		// org : 200
		_DescrCultures.setCharacterCost("priest", 400, 1);		// org : 200
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public RealFogAndAgents() {
		super("Realistic - larger - Fog of War for spies");
	}
}
