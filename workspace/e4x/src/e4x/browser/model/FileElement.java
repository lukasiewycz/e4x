package e4x.browser.model;

import org.eclipse.swt.graphics.Image;

public interface FileElement {

	public Long getFilesize();
	
	public String getFilename();
	
	public boolean isDirectory();
	
	public Image getIcon();
	
}
