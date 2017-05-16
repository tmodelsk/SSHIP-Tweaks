package tm.mtwModPatcher.lib.fileEntities.data.world.maps.campaign;

import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tomek on 2016-11-12.
 */
public class DescrRegionsAndSettlementNameLookup extends LinesProcessorFileEntity {

	private static Set<String> _SettlementNames = null;

	public Set<String> getSettlementNames() {

		if(_SettlementNames == null) {
			// Load, lazyloading

			_SettlementNames = new HashSet<>();

			LinesProcessor lines = getLines();

			for (String line : lines.getLines()) {
				if(!line.contains("_Province"))
					_SettlementNames.add(line);
			}
		}

		return  _SettlementNames;
	}

	public DescrRegionsAndSettlementNameLookup() {
		super("data\\world\\maps\\campaign\\imperial_campaign\\descr_regions_and_settlement_name_lookup.txt");
	}
}
