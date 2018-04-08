package tm.mtwModPatcher.sship.features.tools;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.IfBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.MonitorEventBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.RegionBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.ControlCmd;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.PerfectSpyCmd;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction.FactionTypeSlave;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.faction.IIsFactionAIControlled;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.CompareCounter;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.IHotSeatEnabled;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.DeclareVariable;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.keywords.IncrementVariable;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;

import java.util.Arrays;
import java.util.UUID;

import static tm.mtwModPatcher.lib.managers.FactionsDefs.*;

public class AutoRunHotseat extends Feature {
	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);

		RegionBlock rootRegion = new RegionBlock("AutoRunHotseat");

		val factions = Arrays.asList(SCOTLAND, NORWAY, NOVOGROD);
		val startMon = new MonitorEventBlock(EventType.FactionTurnStart, IHotSeatEnabled.I);

		startMon.add(PerfectSpyCmd.I);
		for(val f : factions) {
			val iff = new IfBlock(new IIsFactionAIControlled(f.symbol).not());
			iff.add(new ControlCmd(f.symbol));

			startMon.add(iff);
		}
		startMon.terminateMonitor();
		rootRegion.add(startMon);

		val turnNumberVar = "HotSeatTurnNumber";
		rootRegion.add(new DeclareVariable(turnNumberVar));
		val turnMonitor = new MonitorEventBlock(EventType.FactionTurnEnd, FactionTypeSlave.I);
		turnMonitor.add(new IncrementVariable(turnNumberVar, 1));

		val interval = 25;
		val totalTurns = 400;

		for(int i = totalTurns; i > 1 ; i -= interval) {
			val ifTurn = new IfBlock(new CompareCounter(turnNumberVar, "=", i));
			ifTurn.add(new ControlCmd(NORWAY.symbol));
			turnMonitor.add(ifTurn);
		}

		rootRegion.add(turnMonitor);

		campaignScript.insertAtEndOfFile(rootRegion.getScriptBlock().getLines());
	}

	private CampaignScript campaignScript;

	public AutoRunHotseat() {
		super("AutoRunHotseat");

		addCategory("Tools");

		setDescriptionShort("AutoRunHotseat mode, stopsonce per 25 turns");
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("13713ab9-f88e-465f-a9d2-bd18bf0bcc2a");
}
