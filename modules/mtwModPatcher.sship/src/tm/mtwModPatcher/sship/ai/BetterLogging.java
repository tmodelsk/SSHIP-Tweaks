package tm.mtwModPatcher.sship.ai;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.*;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.LogLevel;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.WriteToLog;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.ButtonPressed;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.TurnNumber;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.ScriptElement;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;

import java.util.UUID;

/**
 * Created by Tomek on 2016-11-22.
 */
public class BetterLogging extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		_CampaignScript = getFileRegisterForUpdated(CampaignScript.class);

		RegionBlock rootRegion = new RegionBlock("Button End Turn Pressed Tag, do not remove!");
		MonitorEventBlock endTurnPressed = new MonitorEventBlock(EventType.ButtonPressed, new ButtonPressed("end_turn"));

		//endTurnPressed.add(new WriteToLog(LogLevel.Always, "##########!!  E N D   T U R N  BUTTON Pressed !!##########"));

		endTurnPressed.add(getBlockForRange(1,1000));

		rootRegion.add(endTurnPressed);
		_CampaignScript.insertAtEndOfFile(rootRegion.getScriptBlock().getLines());
	}

	private ScriptBlock getBlockForRange(int min, int max) {
		val c = new ContainerBlock();

//		if(min == max) {
//			c.add(getWriteHistory(min));
//		}
//		else
			if(min + 8 >= max) {

			for(int i=min; i<= max; i++) {
				val if1 = new IfBlock(new TurnNumber("=", i));
				if1.add(getWriteHistory(i));
				c.add(if1);
			}

//			val if1 = new IfBlock(new TurnNumber("=", min));
//			if1.add(getWriteHistory(min));
//
//			val if2 = new IfBlock(new TurnNumber("=", max));
//			if2.add(getWriteHistory(max));
//
//			c.add(if1);
//			c.add(if2);
		} else {
			int mediana = (min + max)/2;

			val ifLower = new IfBlock(new TurnNumber("<=" , mediana));
			ifLower.add(getBlockForRange(min, mediana));
			c.add(ifLower);

			val ifUpper = new IfBlock(new TurnNumber(">" , mediana));
			ifUpper.add(getBlockForRange(mediana+1, max));
			c.add(ifUpper);
		}

		return c;
	}

	private ScriptElement getWriteHistory(int turnNumber) {
		return new WriteToLog(LogLevel.Always, "##########!!  E N D   T U R N  BUTTON Pressed (EndTurn "+turnNumber+")!!##########");
	}

	protected CampaignScript _CampaignScript;
	protected String nl = System.lineSeparator();

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public BetterLogging() {
		super("Better campaign logging");

		addCategory("Various");

		setDescriptionShort("Better campaign logging - creates some additional helper system.log.txt entries -> End of Turn, etc. ");
		setDescriptionUrl("http://tmsship.wikidot.com/better-campaign-logging");
	}
}
