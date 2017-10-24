package tm.mtwModPatcher.lib.engines.userSettings;

import lombok.Getter;
import lombok.Setter;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tomek on 17.10.2017.
 */
public class FeatureSettings {

	//@Attribute
	private String Id;
	public void setId(UUID id) {
		this.Id = id.toString();
	}
	public UUID getId(){
		return UUID.fromString(this.Id);
	}

	//@Element
	@Getter @Setter
	private String Name;

	@Getter @Setter
	private String Version;

	@Getter @Setter
	private boolean Enabled;

	@Getter
	private List<FeatureParam> Parameters = null;

	public void add(FeatureParam featureParam) {
		if(Parameters == null) Parameters = new ArrayList<>();

		Parameters.add(featureParam);
	}

}
