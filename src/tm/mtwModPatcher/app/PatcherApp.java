package tm.mtwModPatcher.app;

import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.app.featuresTab.FeaturesTableModel;
import tm.mtwModPatcher.app.featuresTab.NameColRenderer;
import tm.mtwModPatcher.lib.common.core.features.FileSystemResourcesProvider;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.FeatureList;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.FileInputStreamProvider;
import tm.mtwModPatcher.lib.engines.*;
import tm.mtwModPatcher.lib.engines.userSettings.SettingsEngine;
import tm.mtwModPatcher.lib.engines.userSettings.SettingsRepository;
import tm.mtwModPatcher.lib.managers.UnitsManager;
import tm.mtwModPatcher.lib.managers.garrisons.GarrisonManager;
import tm.mtwModPatcher.lib.managers.garrisons.GarrisonMetaManager;
import tm.mtwModPatcher.sship.features.SsHipFeatures;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tomek on 2016-04-11.
 */
public class PatcherApp {
	private JPanel mainPanel;
	private JTabbedPane mainTabPane;
	private JPanel mainTabPanel;
	private JButton startPatchingButton;
	private JButton restoreBackupButton;
	private JTable featuresTable;
	private JScrollPane scrollPane;
	private JTextArea logTextArea;
	private JPanel logTabPanel;
	private JButton selectAllBt;
	private JButton clearAllBtn;
	private JButton selectDefaultsBtn;
	private JLabel statusLbl;
	private JScrollPane logTextAreaScrollPane;
	private JLabel introLbl;
	private JTextField featuresSearchFieldTb;
	private JPanel mainHeaderPanel;
	private JPanel filtersPanel;
	private JPanel selectAllDIsabeAllPanel;
	private JPanel commandsPanel;
	private JButton descriptionUrlBtn;
	private JLabel picLbl;
	private JList FeaturesList;
	//private String OverrideRootPath = "c:\\Tomek\\Projects\\SSHIP-Tweaks\\SSHIP-Tweaks-Res\\overrides";
	//private String DestinationRootPath = "c:\\Gry\\Steam\\steamapps\\common\\Medieval II Total War\\mods\\SSHIP-TM";
	//private String BackupRootPath = "c:\\Tomek\\Projects\\SSHIP-Tweaks\\SSHIP-Tweaks-Res\\Backup";
	private FeatureList featuresList;

	private ConsoleLogger consoleLogger;
	private PatcherEngine patcherEngine;
	private FeaturesTableModel featuresTableModel;
	private Map<UUID, JFrame> optionsOpenedMap;
	private SsHipFeatures ssHipFeatures;

	public PatcherApp() {

		try {
			initialize();
			setUpFeaturesTable();
			setUpButtons();
		} catch (Exception ex) {
			//ex.fillInStackTrace();
			ex.printStackTrace();
			consoleLogger.writeLine("Errors : "+ex.getMessage() + " "+ex.getStackTrace().toString());
		}
		consoleLogger.writeLine("Program started.");
		featuresSearchFieldTb.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);

				featuresTableModel.applySearchFilter(featuresSearchFieldTb.getText());
				featuresTableModel.fireTableDataChanged();
			}
		});
	}

	// #### Buttons Actions ###
	private void applyFeaturesAction() {
		try {

			consoleLogger.writeLine("++++++++++++++++++++++++++++++++");

			if (validateConflictsWithMsgBox()) return;

			buttonsAllSetEnabled(false);
			mainTabPane.setSelectedIndex(1);	// GoTo console log tab

			val applyWorker = new ApplyFeaturesWorker(patcherEngine, featuresList, consoleLogger);

			applyWorker.addPropertyChangeListener(evt -> {
				switch (evt.getPropertyName()) {
					case "state":
						switch((SwingWorker.StateValue)evt.getNewValue()) {
							case DONE:
								onDoneEvent(applyWorker);
								break;
						}
						break;
				}
			});

			applyWorker.execute();

		} catch (Exception e1) {
			e1.printStackTrace();
			consoleLogger.writeLine("Unhandled Error : \n " + e1.toString());
			JOptionPane.showMessageDialog(null, "Unhandled Error : \n " + e1.toString());
		}
	}

	private void onDoneEvent(ApplyFeaturesWorker applyWorker) {
		if(applyWorker.getException() != null) {
			buttonsAllSetEnabled(true);

			Exception ex = applyWorker.getException();
			writeExceptionToConsole(ex);

			val featureEx = Ctm.as(FeatureEx.class, ex);
			if(featureEx != null) {
				String msg="";
				msg += "Feature ["+featureEx.getFeatureName()+"] Error !" +nl;
				msg += featureEx.getMessage() +nl+nl;
				msg += "You can exclude this feature & try again" +nl;
				msg += "If you want to report bug, please attach output log";

				JOptionPane.showMessageDialog(mainPanel, msg);
			}
			else {
				JOptionPane.showMessageDialog(mainPanel, "Error : Unhandled exception " + ex.getMessage());
			}

			consoleLogger.getWriter().scrollToEnd();
		}
		else {
			// ok
			buttonsAllSetEnabled(true);
			mainTabPane.setSelectedIndex(0);	// Go back
		}
	}

	private boolean validateConflictsWithMsgBox() {
		val conflicts = featuresList.getConflictingFeatures();
		if(conflicts == null) consoleLogger.writeLine("Features enabled: no conflicts detected.");
		else {
			consoleLogger.writeLine(Ctm.msgFormat("Features enabled: found {0} features in conflict! ", conflicts.size()));
			for(val conflict : conflicts)
				consoleLogger.writeLine( Ctm.msgFormat("Conflicting features: #1: {0} , #2: {1}",
						conflict.getItem1().getName(), conflict.getItem2().getName()));
			consoleLogger.writeLine("Please disable some of the above features to resolve conflict!");
			consoleLogger.writeLine("Stopped.");

			val conflict = conflicts.get(0);
			String msg="", nl = System.lineSeparator();

			msg += "Problem: Conflicting features are enabled !" + nl;
			msg += Ctm.msgFormat("#1: {0}", conflict.getItem1().getName()) +nl;
			msg += Ctm.msgFormat("#2: {0}", conflict.getItem2().getName()) +nl;
			msg += "Please disable some of the above features to resolve conflict!";
			JOptionPane.showMessageDialog(null, msg);
			return true;
		}
		return false;
	}

	private void writeExceptionToConsole(Exception ex) {
		StackTraceElement[] stack = ex.getStackTrace();
		consoleLogger.writeLine("Exception: " + ex.getMessage());
		consoleLogger.writeLine(ex.getClass().toString());
		consoleLogger.writeLine("Stack trace: ");
		for(StackTraceElement stackEl : stack) {
			//stackTraceStr += "Exception: " + ex.getMessage() + System.lineSeparator();
			//stackTraceStr += "in: " + System.lineSeparator();
			//consoleLogger.writeLine("in: ");
			//stackTraceStr += "Class: "+stackEl.getClassName()+", method: "+stackEl.getMethodName()+", line: "+stackEl.getLineNumber() + ", file: " + stackEl.getFileName() + System.lineSeparator();
			consoleLogger.writeLine("Class: "+stackEl.getClassName()+", method: "+stackEl.getMethodName()+", line: "+stackEl.getLineNumber() + " in " + "File: " + stackEl.getFileName());
		}
		if(ex.getCause() != null) {
			consoleLogger.writeLine("  Inner Exception");
			writeExceptionToConsole((Exception) ex.getCause());
		}
	}

	private void restoreBackupAction() {
		BackupEngine backupEngine = new BackupEngine(consoleLogger);
		backupEngine.DestinationRootPath = ConfigurationSettings.DestinationRootPath(); // DestinationRootPath;
		backupEngine.BackupRootPath = ConfigurationSettings.BackupRootPath();	// BackupRootPath;

		try {
			consoleLogger.writeLine("++++++++++++++++++++++++++++++++");
			backupEngine.restoreBackup();
			backupEngine.cleanBackup();
			consoleLogger.writeLine("Restoring backup: Done");
		} catch (IOException e1) {
			e1.printStackTrace();
			consoleLogger.writeLine("Unhandled Error : \n " + e1.toString());
			JOptionPane.showMessageDialog(null, "Unhandled Error : \n " + e1.toString());
		}
	}
	public void optionButtonAction(ActionEvent e) {
		int row = Integer.valueOf(e.getActionCommand());
		val feature = featuresTableModel.getFeature(row);

		if(feature.isParametersAvailable()) {

			if(optionsOpenedMap.containsKey(feature.getId())) {
				// already opened, bring focus
				val optionsFrame = optionsOpenedMap.get(feature.getId());
				optionsFrame.toFront();
				//optionsFrame.repaint();
			}
			else{
				val optionsFrame = new JFrame( feature.name + " Options");

				optionsFrame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						super.windowClosed(e);
						optionsFrameClosing(e);
					}
				});

				val featureOptions = new FeatureOptionsForm();
				featureOptions.setFeature(feature);
				featureOptions.refresh();

				optionsFrame.setContentPane(featureOptions.getMainPanel());
				//newFrame.setDefaultCloseOperation(JFrame.);
				optionsFrame.pack();

				optionsOpenedMap.put(feature.getId(), optionsFrame);

				optionsFrame.setVisible(true);
			}

		}
	}

	private void optionsFrameClosing(WindowEvent windowEvent) {
		val frame = Ctm.as( JFrame.class , windowEvent.getSource());

		val optionsPane = frame.getContentPane();

		// Determine FeatureId
		UUID featureId=UUID.randomUUID();
		val components = optionsPane.getComponents();
		for (val component : components) {
			val componentName = component.getName();
			if(componentName != null && component.getName().equals(FeatureOptionsForm.FeatureIdLbName)) {
				val label = (JLabel)component;
				featureId = UUID.fromString(label.getText());
				break;
			}
		}

		// # Remove opened feature options from pool #
		optionsOpenedMap.remove(featureId);
	}

	private void buttonsAllSetEnabled(boolean value) {
		startPatchingButton.setEnabled(value);
		restoreBackupButton.setEnabled(value);

		selectAllBt.setEnabled(value);
		selectDefaultsBtn.setEnabled(value);
		clearAllBtn.setEnabled(value);
	}

	// #### Create & Initialize Form dialog
	private void initialize() throws Exception {
		val consoleWriter = new ConsoleLogTextAreaWriter();
		consoleWriter.setTextArea(logTextArea);
		consoleWriter.setTextAreaScroll(logTextAreaScrollPane);
		consoleWriter.setStatusBar(statusLbl);

		consoleLogger = new ConsoleLogger(consoleWriter);

		val inputStreamProvider = new FileInputStreamProvider();
		val resourcesProvider = new FileSystemResourcesProvider(inputStreamProvider);
		val fileEntityFactory = new FileEntityFactory();
		fileEntityFactory.rootPath = ConfigurationSettings.DestinationRootPath();	// this.DestinationRootPath;

		val garrisonMetaManager = new GarrisonMetaManager(fileEntityFactory);
		val garrisonManager = new GarrisonManager(garrisonMetaManager);
		val unitsManager = new UnitsManager();

		ssHipFeatures = new SsHipFeatures(garrisonManager, unitsManager, resourcesProvider, fileEntityFactory, consoleLogger);
		featuresList = ssHipFeatures.configureFeatures();
		ssHipFeatures.enableDefaults();

		val settingsEngine = new SettingsEngine(new SettingsRepository(), consoleLogger);

		patcherEngine = new PatcherEngine(consoleLogger, fileEntityFactory, settingsEngine);
		patcherEngine.OverrideRootPath = ConfigurationSettings.OverrideRootPath();			// OverrideRootPath;
		patcherEngine.DestinationRootPath = ConfigurationSettings.DestinationRootPath();	// DestinationRootPath;
		patcherEngine.BackupRootPath = ConfigurationSettings.BackupRootPath();				// BackupRootPath;
		patcherEngine.initialize(featuresList);

		optionsOpenedMap = new HashMap<>();

		consoleLogger.writeLine("App version: " +PatcherAppVersion.Version);
		consoleLogger.writeLine("Working directory: " +Ctm.getWorkingDirectory());
	}
	private void setUpFeaturesTable() throws PatcherLibBaseEx, ClassNotFoundException {

		featuresTableModel = new FeaturesTableModel();
		featuresTableModel.setFeatureList(featuresList);

		val urlAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				val table = (JTable) e.getSource();
				int row = Integer.valueOf(e.getActionCommand());
				val feature = featuresTableModel.getFeature(row);

				val urlStr = feature.getDescriptionUrl();

				if(urlStr != null) {
					try {
						Desktop.getDesktop().browse(URI.create(urlStr));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};

		val optionsAction = new AbstractAction() { public void actionPerformed(ActionEvent e) { optionButtonAction(e); } };

		featuresTable.setModel(featuresTableModel);

		val nameColRenderer = new NameColRenderer();
		nameColRenderer.setFeaturesTableModel(featuresTableModel);
		featuresTable.getColumnModel().getColumn(1).setCellRenderer(nameColRenderer);

		val urlButtonCol = new ButtonColumn(featuresTable, urlAction, 2);
		urlButtonCol.setHorizontalAlignment(SwingUtilities.LEFT);
		urlButtonCol.setBackgroundColor(Color.WHITE);	// Color.WHITE
		urlButtonCol.setForegroundColor(LayoutDef.LinkUrlColor);
		urlButtonCol.setToolTipValuePrefix("Click to go to: ");
		//urlButtonCol.setMnemonic();

		val optionsButtonCol = new ButtonColumn(featuresTable, optionsAction, 3);
		optionsButtonCol.setToolTipOverride("Click to modify options");
		//optionsButtonCol.setMnemonic(KeyEvent.VK_D);

		featuresTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		featuresTable.getColumnModel().getColumn(1).setPreferredWidth(300);
		featuresTable.getColumnModel().getColumn(2).setPreferredWidth(300);
		featuresTable.getColumnModel().getColumn(3).setPreferredWidth(50);
		featuresTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	}
	private void setUpButtons() {
		startPatchingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyFeaturesAction();
			}
		});

		restoreBackupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restoreBackupAction();
			}
		});

		selectAllBt.addActionListener(e -> {
			ssHipFeatures.enableAll();
			featuresTableModel.fireTableDataChanged();
		});
		selectDefaultsBtn.addActionListener(e -> {
			ssHipFeatures.enableDefaults();
			featuresTableModel.fireTableDataChanged();
		});
		clearAllBtn.addActionListener(e -> {
			ssHipFeatures.disableAll();
			featuresTableModel.fireTableDataChanged();
		});

		descriptionUrlBtn.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(URI.create("http://" + descriptionUrlBtn.getText()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}


	public static void main(String[] args) {
		JFrame frame = new JFrame("SSHIP Tweaks by TM , version " + PatcherAppVersion.Version);
		frame.setContentPane(new PatcherApp().mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here

		descriptionUrlBtn.setBackground(mainPanel.getBackground());
		descriptionUrlBtn.setForeground(LayoutDef.LinkUrlColor);

	}

	private static final String nl = System.lineSeparator();
}
