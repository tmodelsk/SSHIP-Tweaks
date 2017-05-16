package tm.m2twModPatcher.app;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.m2twModPatcher.app.featuresTab.FeatureParamsTableModel;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;

/** Feature Options dialog form  */
@SuppressWarnings({"unused", "WeakerAccess"})
public class FeatureOptionsForm {
	private JPanel mainPanel;
	private JLabel headerLabel;
	private JTabbedPane mainTabbedPane;
	private JPanel mainTabPanel;
	private JButton descriptionUrlBtn;
	private JButton okButton;
	private JButton cancelButton;
	private JTable paramsTable;
	private JLabel featureIdLb;
	private JLabel descriptionShortLabel;

	@SuppressWarnings("WeakerAccess")
	public static String FeatureIdLbName="featureIdLb";

	@Getter @Setter
	private Feature feature;
	private FeatureParamsTableModel paramsTableModel;

	@SuppressWarnings("WeakerAccess")
	public void refresh() {
		assert SwingUtilities.isEventDispatchThread();
		featureIdLb.setText(feature.getId().toString());
		headerLabel.setText("Options for "+feature.Name);
		descriptionShortLabel.setText(feature.getDescriptionShort());

		descriptionUrlBtn.setText(feature.getDescriptionUrl());
		descriptionUrlBtn.setToolTipText("Click to go: "+feature.getDescriptionUrl());

		setUpParamsTable();
	}
	private void setUpParamsTable() {

		paramsTableModel = new FeatureParamsTableModel();
		paramsTableModel.setPars(feature.getPars());

		paramsTable.setModel(paramsTableModel);

	}

	public void confirmAction() throws PatcherLibBaseEx {

		//val pars = paramsTableModel.getParams();
		//feature.setParamValue(pars);

		val pars = paramsTableModel.getPars();
		feature.setParValues(pars);

		closeWindow();
	}

	public FeatureOptionsForm() {

		descriptionUrlBtn.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(URI.create(feature.getDescriptionUrl()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		okButton.addActionListener(e -> {
			try {
				confirmAction();
			} catch (PatcherLibBaseEx patcherLibBaseEx) {

				patcherLibBaseEx.printStackTrace();
			}
		});
		cancelButton.addActionListener(e -> closeWindow());
	}
	public  JPanel getMainPanel() {
		return  mainPanel;
	}
	private void closeWindow() {
		val topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
		topFrame.dispatchEvent(new WindowEvent(topFrame, WindowEvent.WINDOW_CLOSING));
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here

		descriptionUrlBtn.setBackground(mainPanel.getBackground());
		descriptionUrlBtn.setForeground(LayoutDef.LinkUrlColor);

	}
}
