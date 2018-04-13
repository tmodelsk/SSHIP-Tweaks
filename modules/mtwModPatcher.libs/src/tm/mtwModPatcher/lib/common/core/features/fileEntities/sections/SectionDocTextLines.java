package tm.mtwModPatcher.lib.common.core.features.fileEntities.sections;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by Tomek on 2016-04-18.
 */
public class SectionDocTextLines extends Section {


	public SectionTextLines getHeader() {
		return _Header;
	}
	public void setHeader(SectionTextLines _Header) {
		this._Header = _Header;
	}

	public SectionTextLines content() {
		return _Content;
	}
	public void setContent(SectionTextLines _Content) {
		this._Content = _Content;
	}

	public SectionTextLines getFooter() {
		return _Footer;
	}
	public void setFooter(SectionTextLines _Footer) {
		this._Footer = _Footer;
	}

	protected SectionTextLines _Header;
	protected SectionTextLines _Content;
	protected SectionTextLines _Footer;


	@Override
	public void SaveChanges(BufferedWriter bw) throws IOException {

		boolean isNewLineNeeded = false;

		if(_Header != null) {
			_Header.SaveChanges(bw);
			isNewLineNeeded = true;
		}
		if(_Content != null) {

			if(isNewLineNeeded) {
				bw.newLine();
				isNewLineNeeded = false;
			}

			_Content.SaveChanges(bw);
			isNewLineNeeded = true;
		}
		if(_Footer!= null) {
			if(isNewLineNeeded) {
				bw.newLine();
				isNewLineNeeded = false;
			}
			_Footer.SaveChanges(bw);
			isNewLineNeeded = true;
		}
	}

	public SectionDocTextLines() {
//		_Header = new SectionTextLines();
//		_Content = new SectionTextLines();
//		_Footer = new SectionTextLines();
	}
}
