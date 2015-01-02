package e4x.browser.columns;

import java.util.Comparator;

import e4x.browser.cells.CellData;
import e4x.browser.model.Element;

public interface BrowserColumn<T> {

	public CellData<T> getCell(Element file);
	
	public Comparator<CellData<T>> getComparator();
	
	public String getName();
	
	public int getWidth();
	
	public int compare(CellData<T> c1, CellData<T> c2);
	
}
