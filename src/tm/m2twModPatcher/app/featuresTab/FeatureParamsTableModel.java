package tm.m2twModPatcher.app.featuresTab;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.params.ParamValue;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Map;

/**
 * Created by tomek on 13.04.2017.
 */
public class FeatureParamsTableModel extends AbstractTableModel {

	private String[] columnNames = {"Parameter", "Value", "Type"};

	@Getter @Setter
	private List<ParamValue> pars;

	@Override
	public int getRowCount() {
		return pars.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		val par = pars.get(rowIndex);

		Object result=null;

		switch (columnIndex) {
			case 0:
				result = par.getName();
				break;
			case 1:
				result = par.getValue();
				break;
			case 2:
				val typeFullStr = par.getInnerType().toString();
				val splitted = typeFullStr.split("\\.");
				val typeStr = splitted[splitted.length-1];

				result = dataTypeDescription.getOrDefault(typeStr, null);
				if(result == null) throw new PatcherLibBaseEx("Type "+typeStr+" is not supported!");

				break;
			default:
				throw new PatcherLibBaseEx("Column "+columnIndex+" not implemented");
		}

		return result;
	}

	private static Map<String, String> dataTypeDescription = ImmutableMap.of(
			"Double", "Real number, ex.: " + 3.14 ,
			"String", "String, any characters" ,
			"Integer", "Integer, ex: " + 99 ,
			"Boolean", "Enabled/Disabled, ex: "+true);

	public void setValueAt(Object value, int row, int col) {
		val par = pars.get(row);
		val valueStr=value.toString();
		par.setValueStr(valueStr);
	}

	public boolean isCellEditable(int row, int col) {
		if(col == 1) return true;

		return false;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

//	public Class getColumnClass(int c) {
//		return getValueAt(0, c).getClass();
//	}

}
