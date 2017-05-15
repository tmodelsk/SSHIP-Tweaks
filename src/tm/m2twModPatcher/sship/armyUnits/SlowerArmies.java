package tm.m2twModPatcher.sship.armyUnits;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.common.core.features.params.ParamId;
import tm.m2twModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.m2twModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.m2twModPatcher.lib.fileEntities.data.DescrCampaignDb;
import tm.m2twModPatcher.lib.fileEntities.data.DescrCharacter;

import java.util.Arrays;
import java.util.UUID;

/**
 * http://www.twcenter.net/forums/showthread.php?594662-%2805-04-2016%29-My-new-and-improved-AI-errr-3-0
 */
public class SlowerArmies extends Feature {

	protected DescrCampaignDb _DescrSettlementMechanics;
	protected DescrCharacter _DescrCharacter;

	@Getter	@Setter
	private int armyPoints = 140;
	@Getter	@Setter
	private double cavalryModifier = 1.75;
	@Getter	@Setter
	private double siegeModifier = 0.80;

	@Override
	public void executeUpdates() throws Exception {

		_DescrSettlementMechanics = fileEntityFactory.getFile(DescrCampaignDb.class);
		registerUpdatedFile(_DescrSettlementMechanics);
		_DescrCharacter = fileEntityFactory.getFile(DescrCharacter.class);
		registerUpdatedFile(_DescrCharacter);

		String startingActionPointsRegexStr = "^\\s*starting_action_points\\s+\\d+";
		String startingActionPointsPrefix = "starting_action_points\t";
		String startingActionPoints = startingActionPointsPrefix + Integer.toString(armyPoints);

		LinesProcessor charactersLines = _DescrCharacter.getLines();
		// starting_action_points	170	; default value for all characters and pathfinding calculations
//		index = charactersLines.findFirstRegexLine(startingActionPointsRegexStr);
//		charactersLines.replaceLine(index, "starting_action_points\t"+maxPoints+"\t; default value for all characters and pathfinding calculations");

		// type			named character
		// starting_action_points	170

		// ########## Namee Characters ######################
		// ## Generals wages = 0
		//charactersLines.replaceFirstLineByLinePath(Arrays.asList("^\\s*type\\s+named character", "^\\s*wage_base\\s+\\d"), "wage_base\t\t0");
		charactersLines.replaceFirstLineByLinePath(Arrays.asList("^\\s*type\\s+general", "^\\s*wage_base\\s+\\d"), "wage_base\t\t0");
		charactersLines.replaceFirstLineByLinePath(Arrays.asList("^\\s*type\\s+named character", startingActionPointsRegexStr), startingActionPoints);

		// ## Generals ##
		charactersLines.replaceFirstLineByLinePath(Arrays.asList("^\\s*type\\s+general", "^\\s*wage_base\\s+\\d"), "wage_base\t\t200");
		charactersLines.replaceFirstLineByLinePath(Arrays.asList("^\\s*type\\s+general", startingActionPointsRegexStr), startingActionPoints);

		// ## Admiral ##
		charactersLines.replaceFirstLineByLinePath(Arrays.asList("^\\s*type\\s+admiral", "^\\s*wage_base\\s+\\d"), "wage_base\t\t0");

		// #### Set Cavalry modifier ####
		//<cavalry_movement_points_modifier float="1.4"/>
		_DescrSettlementMechanics.setAttribute("/root/misc/cavalry_movement_points_modifier", "float", cavalryModifier);
		_DescrSettlementMechanics.setAttribute("/root/misc/siege_movement_points_modifier", "float", siegeModifier);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val params = new ArrayUniqueList<ParamId>();

		params.add(new ParamIdInteger("ArmyPoints", "Army Points",
				feature -> ((SlowerArmies) feature).getArmyPoints(),
				(feature, value) -> ((SlowerArmies) feature).setArmyPoints(value)));

		params.add(new ParamIdDouble("CavalryModifier", "Cavalry Modifier",
				feature -> ((SlowerArmies) feature).getCavalryModifier(),
				(feature, value) -> ((SlowerArmies) feature).setCavalryModifier(value)));

		params.add(new ParamIdDouble("SiegeModifier", "Siege Modifier",
				feature -> ((SlowerArmies) feature).getSiegeModifier(),
				(feature, value) -> ((SlowerArmies) feature).setSiegeModifier(value)));

		return params;
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public SlowerArmies() {
		super("Slower Armies");

		addCategory("Campaign");

		setDescriptionShort("Armies are ~ 10% slower (less range - less action points) on campaign map");
		setDescriptionUrl("http://tmsship.wikidot.com/slower-armies");

		// see http://www.twcenter.net/forums/showthread.php?594662-%2805-04-2016%29-My-new-and-improved-AI-errr-3-0"
	}
}
