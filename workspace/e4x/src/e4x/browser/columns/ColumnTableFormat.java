package e4x.browser.columns;

import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.widgets.TableColumn;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.swt.TableColumnConfigurer;
import e4x.browser.cells.CellData;
import e4x.browser.model.AdvancedFile;

public class ColumnTableFormat implements AdvancedTableFormat<AdvancedFile>, TableColumnConfigurer {
	
	protected final List<BrowserColumn<?>> columnList;

	public ColumnTableFormat(List<BrowserColumn<?>> list) {
		super();
		this.columnList = list;
	}

	@Override
	public int getColumnCount() {
		return columnList.size();
	}

	@Override
	public String getColumnName(int col) {
		return columnList.get(col).getName();
	}

	@Override
	public void configure(TableColumn tableColumn, int col) {
		tableColumn.setWidth(columnList.get(col).getWidth());
		tableColumn.setMoveable(true);
	}

	@Override
	public Object getColumnValue(AdvancedFile file, int col) {
		return columnList.get(col).getCell(file);
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return CellData.class;
	}

	@Override
	public Comparator<?> getColumnComparator(int col) {
		return columnList.get(col).getComparator();
	}

}