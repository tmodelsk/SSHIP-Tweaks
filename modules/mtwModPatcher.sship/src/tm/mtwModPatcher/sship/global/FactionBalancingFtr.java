package tm.mtwModPatcher.sship.global;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrStratSectioned;

import java.util.UUID;

/**
 * Created by Tomek on 2016-05-10.
 */
public class FactionBalancingFtr extends Feature {

	protected DescrStratSectioned _DescrStrat;

	@Override
	public void executeUpdates() throws Exception {

		_DescrStrat = fileEntityFactory.getFile(DescrStratSectioned.class);
		registerUpdatedFile(_DescrStrat);

		//_DescrStrat.addKingsPurse("KIEVAN RUS",2000);
		_DescrStrat.addKingsPurse("NOVGOROD",2000);

		//_DescrStrat.addKingsPurse("LITHUANIA",500);
		_DescrStrat.addKingsPurse("POLAND",1500);

		//_DescrStrat.addKingsPurse("NORWAY",1500);
		_DescrStrat.addKingsPurse("DENMARK",500);

		//_DescrStrat.addKingsPurse("SCOTLAND",1000);
		//_DescrStrat.addKingsPurse("ENGLAND",-1000);
		//_DescrStrat.addKingsPurse("FRANCE",1500); // Topowa frakcja w zarobkach

		//_DescrStrat.addKingsPurse("ARAGON",1500);
		//_DescrStrat.addKingsPurse("PORTUGAL",2000);
		//_DescrStrat.addKingsPurse("CASTILE & LEON",500);
		//_DescrStrat.addKingsPurse("ALMORAVID EMPIRE", 3000);	// MOORS

		//_DescrStrat.addKingsPurse("PISA",2000);
		//_DescrStrat.addKingsPurse("VENICE",1000);
		_DescrStrat.addKingsPurse("SERBIA",3000);
		_DescrStrat.addKingsPurse("PAPAL STATES",500);
		//_DescrStrat.addKingsPurse("SICILY",-2000);
		//_DescrStrat.addKingsPurse("SERBIA",2000);

		_DescrStrat.addKingsPurse("BYZANTINE EMPIRE",-1000);		// Byzantie ciagle zywi !!!!

		//_DescrStrat.addKingsPurse("CRUSADER STATES", -1000);
		//_DescrStrat.addKingsPurse("SULTANATE OF RUM",4000);
		//_DescrStrat.addKingsPurse("EGYPT",5000);
		//_DescrStrat.addKingsPurse("ABBASSIDS",3000);
		//_DescrStrat.addKingsPurse("ZENGIDS",3000);

		_DescrStrat.addKingsPurse("CUMANS",2000);					// Ok Cumans się rozwijaja

	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public FactionBalancingFtr() {
		super("Faction Balancing");

		addCategory("Campaign");

		setDescriptionShort("Some minor Faction Balancing - Kings Purse - add / remove");
		setDescriptionUrl("http://tmsship.wikidot.com/faction-balancing");
	}
}
