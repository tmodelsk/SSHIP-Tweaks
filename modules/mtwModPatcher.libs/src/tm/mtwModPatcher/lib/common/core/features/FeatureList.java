package tm.mtwModPatcher.lib.common.core.features;

import lombok.val;
import tm.common.Tuple2;

import java.util.*;

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
					if(secondFt.isEnabled()) {
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

	public void disableFeatureIfExists(UUID id) {
		val ft = get(id);

		if(ft != null) ft.setEnabled(false);
	}

	public List<Feature> getFeaturesList() {
		return Collections.unmodifiableList(features);
	}

	public Feature get(int index) {
		val ft = features.get(index);
		return ft;
	}
	public Feature getEnabled(UUID id) {
		Optional<Feature> feature = features.stream().filter(f -> f.getId().equals(id) && f.isEnabled()).findFirst();

		if(feature.isPresent()) return feature.get();

		return null;
	}
	public Feature get(UUID id) {
		Optional<Feature> feature = features.stream().filter(f -> f.getId().equals(id) ).findFirst();

		if(feature.isPresent()) return feature.get();

		return null;
	}

	public int Count() {
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
