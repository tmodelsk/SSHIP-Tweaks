package tm.mtwModPatcher.lib.common.core.features;

import lombok.val;
import tm.common.Tuple2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Container for Features */
public class FeatureList {

	private List<Feature> features;
	private List<Tuple2<Feature, Set<UUID>>> conflictsList;

	public void addConflictingFeature(Feature srcFeature, Set<UUID> featureIds) {
		if(featureIds != null && !featureIds.isEmpty())
			conflictsList.add(new Tuple2<>(srcFeature, featureIds));
	}

	public List<Tuple2<Feature, Feature>> getConflictingFeatures() {
		if(conflictsList == null || conflictsList.isEmpty()) return null;

		List<Tuple2<Feature, Feature>> conflicts = new ArrayList<>();

		for(val conflictDef : conflictsList) {

			val srcFeature = conflictDef.getItem1();
			val conflictedIds = conflictDef.getItem2();

			if(get(srcFeature.getId()).isEnabled() ) {
				// source Feature is enabled

				for(val secondFtId : conflictedIds) {
					val secondFt = get(secondFtId);
					if(secondFt != null && secondFt.isEnabled()) {
						conflicts.add(new Tuple2<>(srcFeature, secondFt));
					}
				}
			}
		}

		if(conflicts.isEmpty()) return null;
		return conflicts;
	}

	public boolean isFeatureEnabled(UUID id) {
		Optional<Feature> feature = features.stream().filter(f -> f.getId().equals(id) && f.isEnabled()).findFirst();

		return feature.isPresent();
	}
	public boolean isNotFeatureEnabled(UUID id) {
		return ! isFeatureEnabled(id);
	}

	public void disableFeatureIfExists(UUID id) {
		val ft = get(id);

		if(ft != null) ft.setEnabled(false);
	}
	@SuppressWarnings("unused")
	public void enableFeatureIfExists(UUID id) {
		val ft = get(id);

		if(ft != null) ft.setEnabled(true);
	}

	private Stream<Feature> whereEnabled() {
		return features.stream().filter(f -> f.isEnabled());
	}

	public List<Feature> getFeaturesList() {
		return Collections.unmodifiableList(features);
	}
	public List<Feature> getFeaturesEnabledList() {
		return Collections.unmodifiableList(whereEnabled().collect(Collectors.toList()));
	}
	public Set<UUID> getIdsSetByEnabledAndMapRemoval() {
		return whereEnabled().filter( f -> f.isMapRemovalRequirement() ).map(f -> f.getId() ).collect(Collectors.toSet());
	}
	public boolean isMapRemovalRequested() {
		return whereEnabled().filter(f -> f.isMapRemovalRequirement()).findAny().isPresent();
	}

	public Feature get(int index) {
		val ft = features.get(index);
		return ft;
	}
	public Feature getEnabled(UUID id) {
		Optional<Feature> feature = features.stream().filter(f -> f.getId().equals(id) && f.isEnabled()).findFirst();

		return feature.orElse(null);
	}
	public Feature get(UUID id) {
		Optional<Feature> feature = features.stream().filter(f -> f.getId().equals(id) ).findFirst();

		return feature.orElse(null);
	}

	public int count() {
		if(features != null) return features.size();

		return 0;
	}


	public void add(Feature feature) {
		features.add(feature);
		feature.setFeaturesContainer(this);
	}

	public FeatureList() {
		features = new ArrayList<>();
		conflictsList = new ArrayList<>();
	}
}
