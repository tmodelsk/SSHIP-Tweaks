package tm.mtwModPatcher.sship.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data._root.DescrProjectiles;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**  */
public class ArchersCrossbowsFirepowerBalancing extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		_ExportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		_DescrProjectiles = getFileRegisterForUpdated(DescrProjectiles.class);
		_ExportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);

		CrossbowmenBalancing();
		ArchersBalancing();
	}

	private void CrossbowmenBalancing() throws PatcherLibBaseEx {
		// ## Add bp attribute ##
		List<UnitDef> crossbowmens = _ExportDescrUnit.getUnits().stream().filter(u -> u.StatPri.IsParsed && u.StatPri.IsMissleUnit() && u.StatPri.IsCrossbowUnit() ).collect(Collectors.toList());

		crossbowmens.stream().forEach(unitDef -> unitDef.addStatPriAttribute("bp"));

		// ## Update *bolt* projectiles ##
		List<String> projectilneNames =  _DescrProjectiles.getAllProjectileNames();
		List<String> boltProjectiles = projectilneNames.stream().filter( n -> n.contains("bolt") ).collect(Collectors.toList());

		for (String projectilneName : boltProjectiles) {
			_DescrProjectiles.setAttributeValue(projectilneName , "radius" , "1.0");
			_DescrProjectiles.setAttributeValue(projectilneName , "mass" , "0.00001");
		}

		// Crossbow Damage lowered -1 / -2
		val crossbowByDamage = crossbowmens.stream().sorted( (c1, c2) -> Integer.compare(c1.StatPri.Damage, c2.StatPri.Damage)).collect(Collectors.toList());


		for(val crossBw : crossbowByDamage) {
			if(crossBw.StatPri.Damage < 9) crossBw.StatPri.Damage -= 3;
			else if(crossBw.StatPri.Damage >= 9) crossBw.StatPri.Damage -= 4;
			else throw new PatcherLibBaseEx("Not Supported / Not Implemented / Fatal Error");
		}
	}

	protected void ArchersBalancing() throws PatcherLibBaseEx {
		// ### Archers Attack -2 (-1 for Attack = 3)
		List<UnitDef> archers = _ExportDescrUnit.getUnits().stream().filter(u -> u.StatPri.IsParsed && u.StatPri.IsMissleUnit() && u.StatPri.IsArcherUnit() ).collect(Collectors.toList());

		for(UnitDef unit : archers.stream().filter( u -> u.StatPri.Damage == 3 ).collect(Collectors.toList()) ) {
			unit.StatPri.Damage--;
		}
		for(UnitDef unit : archers.stream().filter( u -> u.StatPri.Damage >= 4 ).collect(Collectors.toList()) ) {
			unit.StatPri.Damage -= 2;
		}

		// ## Archers Projectiles updates ##
		List<String> projectilneNames =  _DescrProjectiles.getAllProjectileNames();
		List<String> arrowProjectilnes = projectilneNames.stream().filter( n -> n.contains("arrow") ).collect(Collectors.toList());

		for (String projectilneName : arrowProjectilnes) {
			_DescrProjectiles.setAttributeValue(projectilneName , "radius" , "1.0");
			_DescrProjectiles.setAttributeValue(projectilneName , "mass" , "0.00001");
		}

		
	}

	protected ExportDescrUnitTyped _ExportDescrUnit;
	protected DescrProjectiles _DescrProjectiles;
	protected ExportDescrBuilding _ExportDescrBuilding;

	public ArchersCrossbowsFirepowerBalancing() {
		super("Archers & Crossbowman Firepower/Projectiles Balancing");
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();
}
