package tm.mtwModPatcher.sship.features.agentsCharacters.merchantsNerfed;

import lombok.val;
import tm.common.Ctm;
import tm.common.Tuple2;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;
import tm.mtwModPatcher.sship.lib.Buildings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BuildingLimits {

	private Map<String, BuildingLimit> buildingsLimits;

	private void initlizeBuildingsLimits() {
		buildingsLimits = new LinkedHashMap<>();

		int limit;
		val tradeFactionsReq = "factions { pisa, venice, russia, }";
		val capitalHrReq = "hidden_resource capital";
		String requirments;

		add("capital", "Capital", BuildingType.Walls, Ctm.format("not {0} and {1}", tradeFactionsReq , capitalHrReq), 1);
		add("capitalTradeRepublics", "Capital & Trade Republics", BuildingType.Walls, Ctm.format("{0} and {1}", tradeFactionsReq, capitalHrReq), 2);

		limit = ConfigurationSettings.isDevEnvironment() ? 1 : 1;
		requirments = Ctm.format("not "+capitalHrReq);
		add("merchantGuildSmall", "Merchant Guild Small", Buildings.MerchantGuild, Buildings.MerchantGuildLevels.get(0), SettlType.City, requirments, limit);
		add("merchantGuildMedium", "Merchant Guild Medium", Buildings.MerchantGuild, Buildings.MerchantGuildLevels.get(1), SettlType.City, requirments, 1 + limit);
		add("merchantGuildGrand", "Merchant Guild Grand", Buildings.MerchantGuild, Buildings.MerchantGuildLevels.get(2), SettlType.City, requirments,1 + limit);

		requirments = Ctm.format(capitalHrReq);
		add("merchantGuildSmallCapital", "Merchant Guild Small & Capital", Buildings.MerchantGuild, Buildings.MerchantGuildLevels.get(0), SettlType.City, requirments, limit+1);
		add("merchantGuildMediumCapital", "Merchant Guild Medium & Capital", Buildings.MerchantGuild, Buildings.MerchantGuildLevels.get(1), SettlType.City, requirments, 1+limit+1);
		add("merchantGuildGrandCapital", "Merchant Guild Grand 7 Capital", Buildings.MerchantGuild, Buildings.MerchantGuildLevels.get(2), SettlType.City, requirments, 1+limit+1);

		add("explorersGuildSmall", "Explorers Guild Small", Buildings.ExplorersGuild, Buildings.ExplorersLevels.get(0), SettlType.City, 0);
		limit = ConfigurationSettings.isDevEnvironment() ? 0 : 1;
		add("explorersGuildMedium", "Explorers Guild Medium", Buildings.ExplorersGuild, Buildings.ExplorersLevels.get(1), SettlType.City, limit);
		add("explorersGuildGrand", "Explorers Guild Grand", Buildings.ExplorersGuild, Buildings.ExplorersLevels.get(2), SettlType.City, limit);

		limit = ConfigurationSettings.isDevEnvironment() ? 0 : 1;
		add("slaveTradingCenter", "Slave Trading Center", "slavemarket", "slave_trading_center", SettlType.City, limit);
		add("merchantVault", "Merchant Vault", "bank", "merchant_vault", SettlType.City, 1);
	}

	public BuildingLimit get(String parName) {
		return buildingsLimits.get(parName);
	}
	public int getLimitValue(String parName) {
		if(!buildingsLimits.containsKey(parName)) throw new PatcherLibBaseEx(Ctm.format("Building parameter {0} not defined!", parName));

		val bl = buildingsLimits.get(parName);

		return bl.Limit;
	}
	public void setLimitValue(String parName, int limit) {
		if(!buildingsLimits.containsKey(parName)) throw new PatcherLibBaseEx(Ctm.format("Building parameter {0} not defined!", parName));

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

	private void add(String parName, String displayName, String building, String level, SettlType settlType, String requires, int limit) {
		val bl = new BuildingLimit(displayName, building, level, settlType, requires, limit);

		add(parName, bl);
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
