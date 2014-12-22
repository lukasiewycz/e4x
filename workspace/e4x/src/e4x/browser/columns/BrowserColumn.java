package e4x.browser.columns;

import java.util.Comparator;

import e4x.browser.cells.CellData;
import e4x.browser.model.AdvancedFile;

public interface BrowserColumn<T> {

	public CellData<T> getCell(AdvancedFile file);
	
	public Comparator<CellData<T>> getComparator();
	
	public String getName();
	
	public int getWidth();
	
}
