package tm.mtwModPatcher.sship.features.global;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.Ctm;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdBoolean;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.data._root.ExportDescrCharacterTraits;
import tm.mtwModPatcher.lib.data.text.ExportVnvs;

import java.util.UUID;


public class TraitsBoostForSpecificFactions extends Feature {

	@Override
	public void setParamsCustomValues() {
		aiOnly = false;
		loyaltyBonus = 3;
	}

	@Override
	public void executeUpdates() throws Exception {
		exportVnvs = getFileRegisterForUpdated(ExportVnvs.class);
		exportDescrCharacterTraits = getFileRegisterForUpdated(ExportDescrCharacterTraits.class);

		addTraitsForAIBoost();
		addTraitsDescriptions();
	}

	protected void addTraitsForAIBoost() throws PatcherLibBaseEx {
		// ### Add AI Hidden TRAITS - BOOSTS AI ####
		String str = "";

		// ## Trait ##
		LinesProcessor lines = exportDescrCharacterTraits.getLines();
		int index = lines.findFirstByRexexLines("^;=+ VNV TRAITS START HERE ", "^;=+;");
		if (index < 0) throw new PatcherLibBaseEx("Unable to find start of traits");
		index += 2;

		str += ";--------------------------------------------" + nl;
		str += ";----- TM Patcher Added : AI Characters Boosts with hidden Trait with various effects - by BeeMugCarl --" + nl;
		str += format("Trait {0}" + nl, TRAIT_PREFIX);
		str += " Characters family" + nl;
		str += "" + nl;
		str += format(" Level {0}Lev1" + nl, TRAIT_PREFIX);
		str += format("   Description {0}_desc" + nl, TRAIT_PREFIX );
		str += format("   EffectsDescription {0}_effects_desc" + nl, TRAIT_PREFIX );
		str += "   Threshold  1" + nl;
		str += "" + nl;
		str += Ctm.format("   Effect Loyalty {0}"+ nl , loyaltyBonus);
		//str += "   Effect Command 1" + nl;
		str += " " + nl+nl;

		lines.insertAt(index, str);
		str = "";
		addTrigger(str, lines);


	}

	private void addTrigger(String str, LinesProcessor lines) {
		int index;// ## ADD TRIGGER ##
		index = lines.findFirstByRexexLines("^;=+\\s+VNV TRIGGERS START HERE", "^;=+;");
		if (index < 0) throw new PatcherLibBaseEx("Unable to find start of traits triggers");
		index += 2;

		str += "" + nl;
		str += ";--------------------------------------------------------" + nl;
		str += ";----- TM Patcher Added : " + TRAIT_PREFIX +" TRIGGERS --" + nl;
		str += "" + nl;
		str += format("Trigger {0}Trigger" + nl, TRAIT_PREFIX );
		str += "WhenToTest CharacterTurnEnd" + nl;
		str += "" + nl;
		str += format(" Condition  not Trait {0} = 1" + nl, TRAIT_PREFIX );
		if(aiOnly) {
			str += "  and not CharacterIsLocal" + nl;
		}


		// and FactionType lithuania
		str += "" + nl;
		str += format(" Affects {0}  1  Chance  100" + nl, TRAIT_PREFIX);
		str += "" + nl;

		lines.insertAt(index, str);
	}

	private void addTraitsDescriptions() throws PatcherLibBaseEx {
		String str = "", nl = System.lineSeparator();

		str += format("[{0}Lev1]	{0}Lev1" + nl, TRAIT_PREFIX);
		str += format("[{0}_desc]	{0}_desc" + nl, TRAIT_PREFIX);
		str += format("[{0}_effects_desc]	{0}_effects_desc" + nl, TRAIT_PREFIX);

		exportVnvs.insertAtStartOfFile(str);
	}

	private String format(String s, Object... args) {
		return replaceBrackets(Ctm.format(s, args) );
	}

	private static String replaceBrackets(String src) {
		return src.replace('[', '{').replace(']', '}');
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdBoolean("IsAIOnly", "Is AI Only",
				f -> ((TraitsBoostForSpecificFactions)f).isAiOnly(), (f,value) -> ((TraitsBoostForSpecificFactions)f).setAiOnly(value) ));

		pars.add(new ParamIdInteger("LoyaltyBonus", "Loyalty Bonus",
				f -> ((TraitsBoostForSpecificFactions)f).getLoyaltyBonus(), (f,value) -> ((TraitsBoostForSpecificFactions)f).setLoyaltyBonus(value) ));

		return pars;
	}

	@Getter @Setter private int loyaltyBonus;
	@Getter @Setter private boolean aiOnly;

	private static final String TRAIT_PREFIX = "AICharsBoostLtHiddenTraits";
	public static final String nl = System.lineSeparator();
	private ExportVnvs exportVnvs;
	protected ExportDescrCharacterTraits exportDescrCharacterTraits;

	public TraitsBoostForSpecificFactions() {
		super("Traits Boost For Specific Factions");

		addCategory(CATEGORY_CAMPAIGN);

		setDescriptionShort("Traits Boost For Specific Factions: + 1 Loyalty, ... ");
		setDescriptionUrl("http://tmsship.wikidot.com");
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("28145812-b3c5-4b50-a1c6-091cdac53f4a");
}
