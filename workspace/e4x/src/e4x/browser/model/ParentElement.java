package e4x.browser.model;

import org.eclipse.swt.graphics.Image;

public class ParentElement implements FileElement {

	@Override
	public Long getFilesize() {
		return 0l;
	}

	@Override
	public String getFilename() {
		return "..";
	}

	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public Image getIcon() {
		return null;
	}

}
