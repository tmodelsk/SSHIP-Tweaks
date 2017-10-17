package tm.mtwModPatcher.lib.common.core.features;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.Ctm;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.FileEntity;
import tm.mtwModPatcher.lib.common.core.features.params.*;
import tm.mtwModPatcher.lib.engines.ConsoleLogger;
import tm.mtwModPatcher.lib.engines.FileEntityFactory;

import java.util.*;

/**
 * Feature - base abstract class for all Features (Tweaks)
 * Defines common fields & methods & methods to override
 */
public abstract class Feature {

	@Getter @Setter
	public String Name;

	@Getter
	private List<String> categories;

	@Getter @Setter
	private String descriptionShort;

	@Getter @Setter
	protected String descriptionUrl;
	@Getter @Setter
	protected String bugReportingUrl;

	@Getter @Setter
	protected boolean Enabled = true;

	@Getter
	private ListUnique<OverrideTask> overrideTasks;

	protected FileEntityFactory fileEntityFactory;

	protected Set<FileEntity> filesUpdated;
	public Set<FileEntity> getFilesUpdated() {
		return filesUpdated;
	}

	protected FeatureList featuresContainer;
	public void setFeaturesContainer(FeatureList features) {
		featuresContainer = features;
	}

	protected ConsoleLogger consoleLogger;

	@Getter
	private ListUnique<ParamId> parameterIds;
	private ListUnique<ParamValue> parameterValues;

	public void initialize(FileEntityFactory fileEntityFactory, ConsoleLogger logger) {
		this.fileEntityFactory = fileEntityFactory;
		consoleLogger = logger;
		parameterIds = defineParamsIds();
		initializeParamValues();
		setParamsDefaultValues();
		featuresContainer.addConflictingFeature(this, getConflictingFeatures());
	}

	public Set<UUID> getConflictingFeatures() { return new HashSet<>(); }
	public void preExecuteUpdates() {
		filesUpdated.clear();
	}
	public abstract void executeUpdates() throws Exception;// {}

	protected void addOverrideTask(OverrideTask task) {
		overrideTasks.add(task);
	}
	protected void clearOverrideTasks() {
		overrideTasks.clear();
	}


	// ### Parameters Section Start ###
	public ListUnique<ParamId> defineParamsIds() {
		return new ArrayUniqueList<ParamId>();
	}

	public ListUnique<ParamValue> getPars() {
		if(parameterIds == null) return null;

		if(parameterValues != null) {
			// Refresh - resync data
			reloadParametersFromFeature();
			return parameterValues;
		}
		// parameters still not ready, initialize
		initializeParamValues();

		return parameterValues;
	}
	private final void initializeParamValues() {
		val pars = new ArrayUniqueList<ParamValue>();
		for (val parId : parameterIds) {
			ParamValue paramValue;

			// # Create ParamValue typed instance
			val innerType = parId.getInnerType();
			if(innerType.equals(Double.class)) {
				paramValue = new ParamValueDouble(parId);
			}
			else if(innerType.equals(String.class)) {
				paramValue = new ParamValueString(parId);
			}
			else if(innerType.equals(Integer.class)) {
				paramValue = new ParamValueInteger(parId);
			}
			else if(innerType.equals(Boolean.class))
				paramValue = new ParamValueBoolean(parId);

			else throw new PatcherLibBaseEx("Param Inner type ["+innerType+"] not supported!");

			paramValue.reloadValueFromFeature(this);

			pars.add(paramValue);
		}
		parameterValues = pars;
	}
	private void reloadParametersFromFeature() {
		for (val parValue : parameterValues) {
			parValue.reloadValueFromFeature(this);
		}
	}
	public void setParValue(ParamValue parValue) {

		parValue.saveValueToFeature(this);

//		ParamId paramId = null;
//
//		for (val paramTmpId : parameterIds) {
//			if(paramTmpId.equalsByDefinition(parValue.getParamId())) {
//				paramId = paramTmpId;
//				break;
//			}
//		}
//		if(paramId == null) throw new PatcherLibBaseEx("Parameter ["+ parValue.getParamId().getSymbol() +"] not supported!");
//
//		paramId.getValueSetterFromParam().setValueFromParam(this, parValue.getValue());
	}
	public void setParValues(List<ParamValue> parValues) {
		for (val parValue : parValues) {
			setParValue(parValue);
		}
	}
	public void setParamsDefaultValues() {
		reloadParametersFromFeature();
	}
	// ### Parameters Section End ###

	public boolean isParametersAvailable() {
		val params = getPars();

		return ( params != null && params.size() > 0 );
	}

	public void addCategory(String category) {
		categories.add(category);
	}

	public void disable() {
		setEnabled(false);
	}
	public void enable() {
		setEnabled(true);
	}

	protected  <T extends FileEntity> T getFileRegisterForUpdated(Class<T> fileEntityClz) throws Exception {

		T file = fileEntityFactory.getFile(fileEntityClz);

		if(!filesUpdated.contains(file))
			filesUpdated.add(file);

		return file;
	}

	protected void registerUpdatedFile(FileEntity file) {

		if(!filesUpdated.contains(file))
			filesUpdated.add(file);
	}

	public abstract UUID getId();

	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;

		val objTyped = Ctm.as(Feature.class, obj);
		if(objTyped == null) return false;

		return getId().equals(objTyped.getId());
	}

	@Override
	public String toString() {
		return Name;
	}

	public Feature(String name) {
		this();
		Name = name;
	}

	public Feature(String name , boolean enabled) {
		this();
		Name = name;
		setEnabled(enabled);
	}

	public Feature(String name, OverrideTask overrideTask) {
		this();

		Name = name;

        if(overrideTask != null)
            addOverrideTask(overrideTask);
	}

	public Feature(String name, boolean enabled, OverrideTask overrideTask) {
		this();

		Name = name;
		setEnabled(enabled);

		if(overrideTask != null)
			addOverrideTask(overrideTask);
	}

	public Feature() {
        super();

		overrideTasks = new ArrayUniqueList<>();
		filesUpdated = new HashSet<>();

		categories = new ArrayList<>();
	}
}
