package e4x.browser.cells;

import org.eclipse.swt.graphics.Image;

import e4x.browser.model.FileElement;

public interface CellData<T> {

	public T getContent();
	
	public Image getIcon();
	
	public String getText();
	
	public FileElement getFile();
	
}
