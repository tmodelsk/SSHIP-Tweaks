package tm.mtwModPatcher.lib.common.entities;

import tm.mtwModPatcher.lib.common.core.features.PatcherNotSupportedEx;

public enum Religion {

	Catholic,
	Orthodox,
	Islam,
	Pagan;

	public String toStrLabel() {
		switch (this) {
			case Catholic:
				return "catholic";
			case Orthodox:
				return "orthodox";
			case Islam:
				return "islam";
			case Pagan:
				return "pagan";

			default:
				throw new PatcherNotSupportedEx("Not supported: "+this);
		}
	}


}
