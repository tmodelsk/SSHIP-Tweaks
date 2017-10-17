package tm.mtwModPatcher.sship.features.agentsCharacters.merchantsNerfed;

import lombok.val;
import tm.common.Ctm;
import tm.common.Tuple2;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;
import tm.mtwModPatcher.sship.lib.Buildings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tomek on 15.10.2017.
 */
public class BuildingLimits {

	private Map<String, BuildingLimit> buildingsLimits;

	private void initlizeBuildingsLimits() {
		buildingsLimits = new LinkedHashMap<>();

		val b = buildingsLimits;

		add("capital", "Capital", BuildingType.Walls, "hidden_resource capital", 1);
		add("capitalTradeRepublics", "Capital & Trade Republics", BuildingType.Walls, "factions { pisa, venice, russia,  } and hidden_resource capital", 1);

		add("merchantGuildSmall", "Merchant Guild Small", Buildings.MerchantsGuild, Buildings.MerchantsGuildLevels.get(0), SettlType.City, 1);
		add("merchantGuildMedium", "Merchant Guild Medium", Buildings.MerchantsGuild, Buildings.MerchantsGuildLevels.get(1), SettlType.City, 1);
		add("merchantGuildGrand", "Merchant Guild Grand", Buildings.MerchantsGuild, Buildings.MerchantsGuildLevels.get(2), SettlType.City, 1);

		add("explorersGuildSmall", "Explorers Guild Small", Buildings.ExplorersGuild, Buildings.MerchantsGuildLevels.get(0), SettlType.City, 0);
		add("explorersGuildMedium", "Explorers Guild Medium", Buildings.ExplorersGuild, Buildings.MerchantsGuildLevels.get(1), SettlType.City, 0);
		add("explorersGuildGrand", "Explorers Guild Grand", Buildings.ExplorersGuild, Buildings.MerchantsGuildLevels.get(2), SettlType.City, 0);

		add("slaveTradingCenter", "Slave Trading Center", "slavemarket", "slave_trading_center", SettlType.City, 0);
		add("merchantVault", "Merchant Vault", "bank", "merchant_vault", SettlType.City, 1);
	}

	public BuildingLimit get(String parName) {
		return buildingsLimits.get(parName);
	}
	public int getLimitValue(String parName) {
		if(!buildingsLimits.containsKey(parName)) throw new PatcherLibBaseEx(Ctm.msgFormat("Building parameter {0} not defined!", parName));

		val bl = buildingsLimits.get(parName);

		return bl.Limit;
	}
	public void setLimitValue(String parName, int limit) {
		if(!buildingsLimits.containsKey(parName)) throw new PatcherLibBaseEx(Ctm.msgFormat("Building parameter {0} not defined!", parName));

		val bl = buildingsLimits.get(parName);

		bl.Limit = limit;
	}

	/** Returns List of params ( paramName , DisplayName ) */
	public List<Tuple2<String, String>> getBuildingsParams() {
		val keys = buildingsLimits.keySet();
		val result = new ArrayList<Tuple2<String, String>>();
		keys.forEach( k -> result.add(new Tuple2<String, String>(k, buildingsLimits.get(k).DisplayName)));

		return result;
	}

	private void add(String parName, String displayName, String building, String level, SettlType settlType, int limit) {
		val bl = new BuildingLimit(displayName, building, level, settlType, limit);

		add(parName, bl);
	}
	private void add(String parName, String displayName, BuildingType buildingType, String requires, int limit) {
		val bl = new BuildingLimit(displayName, buildingType, requires, limit);

		add(parName, bl);
	}
	private void add(String parName, String displayName, BuildingType buildingType, int limit) {
		val bl = new BuildingLimit(displayName, buildingType, limit);

		add(parName, bl);
	}
	private void add(String parName, BuildingLimit bl) {
		val res = buildingsLimits.put(parName, bl);
		if(res != null) throw new PatcherLibBaseEx("Already inserted before, key="+parName);
	}

	public BuildingLimits() {
		initlizeBuildingsLimits();
	}
}
