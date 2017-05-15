package tm.m2twModPatcher.sship.global;

import lombok.val;
import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.fileEntities.data.world.maps.campaign.CampaignScript;

import java.util.UUID;

/**
 * Created by tomek on 22.04.2017.
 */
public class OrderKnightsAvailable extends Feature {

	@SuppressWarnings("UnusedAssignment")
	@Override
	public void executeUpdates() throws Exception {
		campaignScript = getFileRegisterForUpdated(CampaignScript.class);

		val index = campaignScript.getLines().findExpFirstRegexLine("^\\s*and GlobalStanding >.+");

		if(index <=0) throw new PatcherLibBaseEx("Not found line to comment '  and GlobalStanding >'");

		campaignScript.getLines().commentLine(index);
	}

	private CampaignScript campaignScript;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public OrderKnightsAvailable() {

		setName("Order Knights Available");
		addCategory("Campaign");
		addCategory("Units");
		setDescriptionShort("Order Knights available in Chapter Houses, only Excommunication disables recruitment");
		setDescriptionUrl("http://tmsship.wikidot.com/order-knights-available");
	}
}
