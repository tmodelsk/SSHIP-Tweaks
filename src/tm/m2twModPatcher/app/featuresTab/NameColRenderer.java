package tm.m2twModPatcher.app.featuresTab;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by tomek on 16.04.2017.
 */
public class NameColRenderer extends DefaultTableCellRenderer {

	public Component getTableCellRendererComponent(
			JTable table, Object value,
			boolean isSelected, boolean hasFocus,
			int row, int column) {

		JLabel c = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		val ft = featuresTableModel.getFeature(row);
		val descriptionShort = ft.getDescriptionShort();
		c.setToolTipText(descriptionShort);

		return  c;
	}

	@Getter @Setter
	private FeaturesTableModel featuresTableModel;

}
