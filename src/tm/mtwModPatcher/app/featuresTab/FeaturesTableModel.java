package tm.mtwModPatcher.app.featuresTab;

import lombok.Getter;
import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.FeatureList;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomek on 06.04.2017.
 */
public class FeaturesTableModel extends AbstractTableModel {

	private String[] columnNames = {"On / Off",
			"Feature",
			//"Description",
			"Description Url",
			"Options"};

	public void applySearchFilter(String filter) {
		FeaturesFiltered = new ArrayList<>();

		filter = filter.toLowerCase();

		String name, descShort, categoriesString;
		List<String> categories;

		for(val ft : featureList.getFeaturesList()) {

			name = ft.getName();
			descShort = ft.getDescriptionShort();

			if(name != null) name = name.toLowerCase();
			if(descShort != null) descShort = descShort.toLowerCase();

			categories = ft.getCategories();
			categoriesString = "";
			for(val s : categories) categoriesString += s.toLowerCase() + " ";

			if( (name != null && name.contains(filter)) ||
					(descShort != null && descShort.contains(filter)) ||
					(categoriesString != null && categoriesString.contains(filter))
					) {
				FeaturesFiltered.add(ft);
			}
		}
	}

	@Override
	public int getRowCount() {

		if (FeaturesFiltered == null) return 0;

		return FeaturesFiltered.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		val ft = getFeature(rowIndex);
		Object res = null;

		switch (columnIndex) {
			case 0:
				res = new Boolean(  ft.isEnabled() );
				break;
			case 1:
				res = ft.name;
				break;
			case 2:

				String urlStr = ft.getDescriptionUrl();
				if(urlStr != null) urlStr = urlStr.replaceAll("http://", "");;

				res = urlStr;
				break;
			case 3:
				if(ft.isParametersAvailable())
					res = "Options";
				else
					res = null;
				break;
		}

		return res;
	}

	public Feature getFeature(int rowIndex) {
		//val ft = featureList.getFeaturesList().getEnabled(rowIndex);

		val ft = FeaturesFiltered.get(rowIndex);

		return ft;
	}

	public void setValueAt(Object value, int row, int col) {

		val f = FeaturesFiltered.get(row); //featureList.getFeaturesList().getEnabled(row);

		switch (col) {
			case 0:
				val flag = (Boolean)value;
				f.setEnabled(flag);
				break;
		}
	}

	public boolean isCellEditable(int row, int col) {
		if(col == 0 || col == 2 || col == 3) return true;

		return false;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	@Getter
	private FeatureList featureList;

	public void setFeatureList(FeatureList featureList) {
		this.featureList = featureList;
		FeaturesFiltered = new ArrayList<>(featureList.getFeaturesList());
	}

	private List<Feature> FeaturesFiltered;
}
