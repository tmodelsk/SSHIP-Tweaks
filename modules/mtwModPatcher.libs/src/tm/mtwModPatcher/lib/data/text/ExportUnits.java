package tm.mtwModPatcher.lib.data.text;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;

import java.util.regex.Pattern;

public class ExportUnits extends LinesProcessorFileEntity {

	public static final String DESCRIPTION_SUFFIX = "_descr";

	public void replaceName(UnitDef unit, String mainEntry) {
		val symbol = unit.Dictionary.value;

		replaceEntry(symbol, mainEntry);
	}
	public void replaceDescription(UnitDef unit, String mainEntry) {
		val symbol = unit.Dictionary.value + DESCRIPTION_SUFFIX;

		replaceEntry(symbol, mainEntry);
	}
	public String loadDescription(UnitDef unit) {
		val symbol = unit.Dictionary.value + DESCRIPTION_SUFFIX;

		return loadEntry(symbol);
	}

	public void replaceEntry(String symbol, String newValue) {
		val lines = getLines();
		val symbolInfo = normalizeSymbol(symbol);

		val index = lines.findExpFirstRegexLine(symbolInfo.regex);

		lines.replaceLine(index, symbolInfo.symbolTag + newValue);
	}

	public String loadEntry(String symbol) {

		val symbolInfo = normalizeSymbol(symbol);
		val lines = getLines();
		val lineStr = lines.getLine(Pattern.compile(symbolInfo.regex));
		String entryStr = lineStr.replaceAll(symbolInfo.regex, "");

		entryStr = entryStr.replace(System.lineSeparator(), "");

		return entryStr;
	}

	private SymbolRegexPair normalizeSymbol(String symbol) {
		String symbolPatched = symbol.replaceAll(" ", "_");
		val rawSymbol = symbolPatched.replace("{", "" ).replace("}" , "");

		String symbolTag, regex;
		symbolTag = "{"+ symbolPatched +"}";
		regex = "^\\{"+rawSymbol+"\\}";

		return new SymbolRegexPair(rawSymbol, symbolTag, regex);
	}

	public ExportUnits() {
		super("data\\text\\export_units.txt", "UnicodeLittle");
	}

	private static class SymbolRegexPair {
		public final String symbol;
		public final String symbolTag;
		public final String regex;

		public SymbolRegexPair(String symbol, String symbolTag, String regex) {
			this.symbol = symbol;
			this.symbolTag = symbolTag;
			this.regex = regex;
		}
	}
}
