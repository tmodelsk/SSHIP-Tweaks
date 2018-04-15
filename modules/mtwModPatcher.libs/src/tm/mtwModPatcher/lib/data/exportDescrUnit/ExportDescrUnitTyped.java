package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;
import org.xml.sax.SAXException;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.PatcherNotSupportedEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.FileEntity;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.data.common.Format;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Tomek on 2016-07-12.
 */
public class ExportDescrUnitTyped extends FileEntity {

	public void setAttributeForAllUnits(String attribute) throws PatcherLibBaseEx {
		for(UnitDef unit : units) unit.addAttribute(attribute);
	}
	public void removeAttributeForAllUnits(String attribute) throws PatcherLibBaseEx {
		for(UnitDef unit : units) unit.removeAttribute (attribute);
	}

	public void addOwnershipAll(String unitName, String factionName) throws PatcherLibBaseEx {
		addOwnership(unitName, factionName);
		addOwnership(unitName, factionName, 0);
		addOwnership(unitName, factionName, 1);
	}
	public void addOwnership(String unitName, String factionName) throws PatcherLibBaseEx {
		UnitDef unit = loadUnit(unitName);
		unit.addOwnership(factionName);
	}
	public void addOwnership(String unitName, String factionName, int eraNumber) throws PatcherLibBaseEx {
		UnitDef unit = loadUnit(unitName);
		unit.addOwnership(factionName, eraNumber);
	}

	public void updateCostAndUpkeepByMultipliers(String unitName, double costMultiplier, double upkeepMultiplier) throws PatcherLibBaseEx {
		UnitDef unit = loadUnit(unitName);

		unit.StatCost.Cost = (int) (unit.StatCost.Cost * costMultiplier);
		unit.StatCost.Upkeep = (int) (unit.StatCost.Upkeep * upkeepMultiplier);
	}

	public void updateUpkeepByMultiplier(String unitName, double multiplier) throws PatcherLibBaseEx {
		UnitDef unit = loadUnit(unitName);

		unit.StatCost.Upkeep = (int) (unit.StatCost.Upkeep * multiplier);
	}

	public UnitDef loadUnit(String unitName) throws PatcherLibBaseEx {
		val unitOpt  = units.stream().filter(u -> u.Name.equals(unitName) ).findFirst();

		if(!unitOpt.isPresent()) throw new PatcherLibBaseEx("Unit '" + unitName  + "' not found");

		val unit = unitOpt.get();

		return unit;
	}

	@Override
	public void load() throws ParserConfigurationException, IOException, SAXException, PatcherLibBaseEx {
		LinesProcessor lines = new LinesProcessor();
		lines.setLines(loadAsTextLines());

		// #### Loop throught all lines

		Pattern typePattern = Pattern.compile("^type\\s+\\w+");


		int index = 0;
		index = lines.findExpFirstRegexLine(typePattern , index);

		headerComments = lines.subsetCopy(0 , index-1);

		while (index >= 0) {
			UnitDef unit = parseUnit(index, lines);

			units.add(unit);

			index = lines.findFirstByRegexLine(typePattern , index+1);
			if(index < 0 || index > lines.count())
				break;
		}
	}

	protected UnitDef parseUnit(int index, LinesProcessor lines) throws PatcherLibBaseEx {
		UnitDef u = new UnitDef();


		// # Determine end of Unit block #

		Pattern typePtr = Pattern.compile("^type\\s+\\w+");
		int startIndex = lines.findExpFirstRegexLine(typePtr, index);
		int endIndex = lines.findFirstByRegexLine(typePtr, startIndex+1);
		if(endIndex < 0) endIndex = lines.count();

		LinesProcessor unitLines = lines.subsetCopy(index, endIndex-1);

		u.Name = getValue("type" , unitLines);

		u.Dictionary = getValue("dictionary" , unitLines);
		u.Category = getValue("category" , unitLines).trim();
		u.Class = getValue("class" , unitLines).trim();
		u.VoiceType = getValue("voice_type" , unitLines);

		u.BannerFaction = getValue("banner faction" , unitLines);
		u.BannerUnit = getValue("banner unit" , unitLines);
		u.BannerHoly = getValue("banner holy" , unitLines);

		u.Accent = getValue("accent" , unitLines);
		u.Soldier = Soldier.parseEduEntry(getValue("soldier" , unitLines));
		u.Officer1 = getValue("officer" , unitLines);
		u.Officer2 = getValue("officer" , 2 , unitLines);
		u.Officer3 = getValue("officer" , 3 , unitLines);
		u.Mount = getValue("mount" , unitLines);
		u.MountEffect =  MountsEffects.parse(getValue("mount_effect" , unitLines));


		u.Ship = getValue("ship" , unitLines);

		u.Engine = getValue("engine" , unitLines);
		u.Attributes = getValue("attributes" , unitLines);


		u.MoveSpeedMod = parseMoveSpeedMod(getValue("move_speed_mod" , unitLines));
		u.Formation = getValue("formation" , unitLines);

		u.StatHealth = getValue("stat_health" , unitLines);

		u.StatPri =  parseWeaponStat( getValue("stat_pri" , unitLines) , u.Name );
		if(!u.StatPri.IsParsed) throw new PatcherNotSupportedEx(Ctm.format("Unable to parse EDU '{0}' StatPri: {1}", u.Name, u.StatPri.SourceStr));

		u.StatPriAttr = WeaponAttributes.parseStr( getValue("stat_pri_attr" , unitLines) );

		u.StatSec = parseWeaponStat( getValue("stat_sec" , unitLines) , u.Name );
		if(!u.StatSec.IsParsed) throw new PatcherNotSupportedEx(Ctm.format("Unable to parse EDU '{0}' StatSec: {1}", u.Name, u.StatSec.SourceStr));

		u.StatSecAttr = getValue("stat_sec_attr" , unitLines);
		u.StatTer = getValue("stat_ter" , unitLines);
		u.StatTerAttr = getValue("stat_ter_attr" , unitLines);

		u.StatPriArmour = StatPriArmor.parseStr(getValue("stat_pri_armour" , unitLines));
		u.StatSecArmour = getValue("stat_sec_armour" , unitLines);

		u.StatHeat =  (int)Float.parseFloat(getValue("stat_heat", true , unitLines));
		u.StatGround = getValue("stat_ground" , unitLines);
		//u.StatMental = getValue("stat_mental" , unitLines);
		u.StatMental = StatMental.parseStr(getValue("stat_mental" , unitLines));
		u.StatChargeDist = getValue("stat_charge_dist" , unitLines);
		u.StatFireDelay = getValue("stat_fire_delay" , unitLines);
		u.StatFood = getValue("stat_food" , unitLines);
		u.StatCost =  parseStatCost( getValue("stat_cost" , unitLines) );
		u.StatStl = getValue("stat_stl" , unitLines);

		u.ArmourUgLevels = getValue("armour_ug_levels" , unitLines);
		u.ArmourUgModels = getValue("armour_ug_models" , unitLines);

		u.Ownership = getValue("ownership" , unitLines);
		u.OwnershipEra0 = getValue("era 0" , unitLines);
		u.OwnershipEra1 = getValue("era 1" , unitLines);
		u.OwnershipEra2 = getValue("era 2" , unitLines);
		u.OwnershipEra3 = getValue("era 3" , unitLines);

		u.RecruitPriorityOffset = (int) Float.parseFloat(getValue("recruit_priority_offset", unitLines));
		u.LastComment = unitLines.getLine(_LastCommentPtr);

		return u;
	}


	protected WeaponStat parseWeaponStat(String statPriStr, String unitName) throws PatcherLibBaseEx {
		WeaponStat sp = new WeaponStat();
		sp.SourceStr = statPriStr;

		Matcher m;
		boolean founded = false, isFullPattern = false;

		m = statPriShorterPattern.matcher(statPriStr);
		if(m.find()) founded = true;
		else {
			m = statPriFullPattern.matcher(statPriStr);
			if(m.find()) {
				founded = true;
				isFullPattern = true;
			}
		}
		// Short : 18, 0, no, 0, 0, melee, melee_simple, slashing, none, 0, 1
		// Full: Early Reiters : 9, 4, quality_early_pistol_bullet, 50, 4, missile, missile_gunpowder, piercing, none, musket_shot_set, 50, 1
		//                       1  2                3               4  5      6         7               8         9       10           11  12

		if(founded) {
			sp.IsParsed = true;

			sp.Damage = Integer.parseInt(m.group(1));
			sp.ChargeBonus = Integer.parseInt(m.group(2));

			sp.Ammunition = m.group(3);
			sp.MissleRange = Integer.parseInt(m.group(4));
			sp.AmmunitionCount = Integer.parseInt(m.group(5));

			sp.WeaponType = m.group(6);
			sp.TechType = m.group(7);
			sp.DamageType = m.group(8);
			sp.SoundType = m.group(9);

			int offset = 0;
			if(isFullPattern) {
				offset = 1;
				sp.SoundFireType = m.group(9 + offset);
			}

			sp.AttacksDelay = Integer.parseInt(m.group(10 + offset));
			sp.WeaponPreference = Double.parseDouble(m.group(11 + offset));

			sp.Comments = m.group(12 + offset);
		}
		else {
			sp.IsParsed = false;
		}
		return sp;
	}
	protected Pattern statPriShorterPattern = Pattern.compile("^(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\w+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\w+)\\s*,\\s*(\\w+)\\s*,\\s*(\\w+)\\s*,\\s*(\\w+)\\s*,\\s*(\\d+)\\s*,\\s*([\\d\\.]+)(\\s*;.*)*");
	protected Pattern statPriFullPattern = Pattern.compile("^(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\w+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\w+)\\s*,\\s*(\\w+)\\s*,\\s*(\\w+)\\s*,\\s*(\\w+)\\s*,\\s*(\\w+)\\s*,\\s*(\\d+)\\s*,\\s*([\\d\\.]+)(\\s*;.*)*");
	protected String serializeWeaponStat(WeaponStat sp) {
		String s="";

		if(sp.IsParsed) {
			// 18, 0, no, 0, 0, melee, melee_simple, slashing, none, 0, 1
			s += sp.Damage + ", ";
			s += sp.ChargeBonus + ", ";

			s += sp.Ammunition + ", ";
			s += sp.MissleRange + ", ";
			s += sp.AmmunitionCount + ", ";

			s += sp.WeaponType + ", ";
			s += sp.TechType + ", ";
			s += sp.DamageType + ", ";
			s += sp.SoundType + ", ";

			if(sp.SoundFireType != null)
				s += sp.SoundFireType + ", ";

			s += sp.AttacksDelay + ", ";
			s += Format.toString(sp.WeaponPreference);

			if(sp.Comments != null)
				s += sp.Comments;
		}
		else s = sp.SourceStr;

		return s;
	}


	protected UnitStatCost parseStatCost(String statCostStr) {
		UnitStatCost sc = null;

		// 2, 429, 294, 100, 218, 218, 4, 120

		Matcher m = _StatCostPattern.matcher(statCostStr);

		if(m.find()) {

			sc = new UnitStatCost();

			sc.RecruitTurns = Integer.parseInt(m.group(1));
			sc.Cost = Integer.parseInt(m.group(2));
			sc.Upkeep = Integer.parseInt(m.group(3));
			sc.WeaponUpgradeCost = Integer.parseInt(m.group(4));
			sc.ArmorUpgradeCost = Integer.parseInt(m.group(5));
			sc.CustomBattleCost = Integer.parseInt(m.group(6));
			sc.CustomBattleNumberLimit = Integer.parseInt(m.group(7));
			sc.CustomBattleNumberLimitCost = Integer.parseInt(m.group(8));
			sc.Comment = m.group(9);
		}

		return sc;
	}
	protected Pattern _StatCostPattern = Pattern.compile("^(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)(\\s*;*.*)");
	//Pattern.compile("^(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*");

	protected String serializeStatCost(UnitStatCost sc) {
		String s = "";

		// 2, 429, 294, 100, 218, 218, 4, 120

		s += sc.RecruitTurns + ", ";

		s += sc.Cost + ", ";
		s += sc.Upkeep + ", ";
		s += sc.WeaponUpgradeCost + ", ";
		s += sc.ArmorUpgradeCost + ", ";
		s += sc.CustomBattleCost + ", ";
		s += sc.CustomBattleNumberLimit + ", ";
		s += sc.CustomBattleNumberLimitCost;

		if(sc.Comment != null) s += sc.Comment;

		return s;
	}


	private Double parseMoveSpeedMod(String strValue) {
		if(strValue == null) return null;

		val doubleVal = Double.parseDouble(strValue);

		return  doubleVal;
	}
	private String serializeDoubleOrNull(Double doubleValue) {
		if(doubleValue == null) return null;

		return Format.toString(doubleValue);
	}
	private String serializeDoubleOrNull(Double doubleValue, int minPrecision) {
		if(doubleValue == null) return null;

		return Format.toString(doubleValue, minPrecision);
	}

	protected String getValue(String name, boolean ommitComment, LinesProcessor lines) {
		val tmp = getValue(name, lines);

		if(!ommitComment) return tmp;

		val splitted = tmp.split(";");
		return splitted[0];
	}
	protected String getValue(String name , LinesProcessor lines) throws PatcherLibBaseEx {
		return getValue(name , 1 , lines);
	}
	protected String getValue(String name , int occurrenceNumber, LinesProcessor lines) throws PatcherLibBaseEx {

		Pattern p =  Pattern.compile("^"+name+"\\s+(\\S.*)");

		int index = lines.findFirstRegexLine(p);
		if(index < 0)
			return null;

		if(occurrenceNumber > 1) {
			index = lines.findFirstByRegexLine(p, index+1);
			if (index < 0)
				return null;

			if(occurrenceNumber > 2) {
				index = lines.findFirstByRegexLine(p, index+1);
				if (index < 0)
					return null;
			}
		}

		String line = lines.getLine(index);


		Matcher matcher = p.matcher(line);

		if(matcher.find())
			return matcher.group(1);

		throw new PatcherLibBaseEx("Attribute " + name + "not found in "+line);
	}

	//protected Pattern _ValuePattern = Pattern.compile("^");

	@Override
	public void saveChanges() throws TransformerException, IOException {
		File fout = new File(getFullPath());
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		// ## Save header ##
		headerComments.saveChanges(bw);
		bw.newLine();

		for (UnitDef unit : units) {

			saveUnit(unit , bw);
			bw.newLine();
		}

		bw.close();
		fos.close();
	}

	protected void saveUnit(UnitDef unit, BufferedWriter bw) throws IOException {

		writeUnitAttrib("type" , unit.Name , bw);
		writeUnitAttrib("dictionary" , unit.Dictionary , bw);
		writeUnitAttrib("category" , unit.Category , bw);
		writeUnitAttrib("class" , unit.Class , bw);

		writeUnitAttrib("voice_type" , unit.VoiceType , bw);
		writeUnitAttrib("accent" , unit.Accent , bw);

		writeUnitAttrib("banner faction" , unit.BannerFaction , bw);
		writeUnitAttrib("banner unit" , unit.BannerUnit , bw);
		writeUnitAttrib("banner holy" , unit.BannerHoly , bw);

		writeUnitAttrib("soldier" , unit.Soldier.toEduString() , bw);
		writeUnitAttrib("officer" , unit.Officer1 , bw);
		writeUnitAttrib("officer" , unit.Officer2 , bw);
		writeUnitAttrib("officer" , unit.Officer3 , bw);

		writeUnitAttrib("engine" , unit.Engine , bw);
		writeUnitAttrib("mount" , unit.Mount , bw);
		writeUnitAttrib("mount_effect" , unit.MountEffect.serialize(), bw);

		writeUnitAttrib("ship" , unit.Ship , bw);
		writeUnitAttrib("attributes" , unit.Attributes , bw);
		writeUnitAttrib("move_speed_mod" , serializeDoubleOrNull(unit.MoveSpeedMod, 1) , bw);
		writeUnitAttrib("formation" , unit.Formation , bw);

		writeUnitAttrib("stat_health" , unit.StatHealth , bw);
		writeUnitAttrib("stat_pri" , serializeWeaponStat( unit.StatPri ) , bw);
		writeUnitAttrib("stat_pri_attr" , unit.StatPriAttr.serialize() , bw);
		writeUnitAttrib("stat_sec" , serializeWeaponStat( unit.StatSec ), bw);
		writeUnitAttrib("stat_sec_attr" , unit.StatSecAttr , bw);
		writeUnitAttrib("stat_ter" , unit.StatTer , bw);
		writeUnitAttrib("stat_ter_attr" , unit.StatTerAttr , bw);

		writeUnitAttrib("stat_pri_armour" , unit.StatPriArmour.serialize() , bw);
		writeUnitAttrib("stat_sec_armour" , unit.StatSecArmour , bw);

		writeUnitAttrib("stat_heat" , unit.StatHeat , bw);
		writeUnitAttrib("stat_ground" , unit.StatGround , bw);
		writeUnitAttrib("stat_mental" , unit.StatMental.serialize() , bw);
		writeUnitAttrib("stat_charge_dist" , unit.StatChargeDist , bw);
		writeUnitAttrib("stat_fire_delay" , unit.StatFireDelay, bw);
		writeUnitAttrib("stat_food" , unit.StatFood , bw);
		writeUnitAttrib("stat_cost" ,  serializeStatCost(unit.StatCost) , bw);
		writeUnitAttrib("stat_stl" , unit.StatStl , bw);

		writeUnitAttrib("armour_ug_levels" , unit.ArmourUgLevels , bw);
		writeUnitAttrib("armour_ug_models" , unit.ArmourUgModels , bw);
		writeUnitAttrib("ownership" , unit.Ownership , bw);
		writeUnitAttrib("era 0" , unit.OwnershipEra0 , bw);
		writeUnitAttrib("era 1" , unit.OwnershipEra1 , bw);
		writeUnitAttrib("era 2" , unit.OwnershipEra2 , bw);
		writeUnitAttrib("era 3" , unit.OwnershipEra3 , bw);
		writeUnitAttrib("recruit_priority_offset" , unit.RecruitPriorityOffset, bw);

		bw.write(unit.LastComment);
		bw.newLine();

//		writeUnitAttrib("" , unit. , bw);
//		writeUnitAttrib("" , unit. , bw);
//		writeUnitAttrib("" , unit. , bw);

	}

	protected void writeUnitAttrib(String attrName, int value , BufferedWriter bw) throws IOException {
		writeUnitAttrib(attrName, Integer.toString(value) , bw);
	}
	protected void writeUnitAttrib(String attrName, String value , BufferedWriter bw) throws IOException {
		if(value != null) {

			int spacesNeeded;

			spacesNeeded= 18 - attrName.length();
			if(spacesNeeded <= 0) spacesNeeded = 4;

			String attrValue = attrName;

			for (int i = 1; i <= spacesNeeded ; i++) {
				attrValue += " ";
			}

			bw.write(attrValue + value);
			bw.newLine();
		}
	}

	public List<UnitDef> getUnits() {
		return units;
	}
	public UnitDef getUnit(String eduName) {
		val un = getUnits().stream().filter( u -> u.Name.equals(eduName) ).collect(Collectors.toList());
		if(un.size() == 0) return null;
		else if(un.size() == 1) return un.get(0);
		else throw new PatcherLibBaseEx("EDU multiple units "+eduName);
	}

	protected List<UnitDef> units = new ArrayList<>();
	protected LinesProcessor headerComments = new LinesProcessor();

	public ExportDescrUnitTyped() { super("data\\export_descr_unit.txt"); }

	protected Pattern _LastCommentPtr = Pattern.compile("^;\\d+\\s+");
}
