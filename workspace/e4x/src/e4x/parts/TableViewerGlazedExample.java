package e4x.parts;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.matchers.Matcher;

public class TableViewerGlazedExample {

	private TableViewer				_viewer;
	private Table					_table;

	private List<RowObject>			_rootList;
	private EventList<RowObject>	_eventList;
	private SortedList<RowObject>	_sortedList;
	private FilterList<RowObject>	_filterList;

	private String					_filterText;

	public static void main(String[] args) {
		new TableViewerGlazedExample();
	}

	public TableViewerGlazedExample() {
		try {
			// create the usual setup
			Display display = new Display();
			final Shell shell = new Shell(display);
			shell.setText("TableViewer Glazed Lists Example");
			shell.setSize(700, 400);
			shell.setLayout(new FillLayout());

			ViewForm outer = new ViewForm(shell, SWT.NONE);

			Composite filterComp = new Composite(outer, SWT.NONE);
			filterComp.setLayout(new GridLayout(3, false));
			final Text filterForm = new Text(filterComp, SWT.BORDER);
			filterForm.addListener(SWT.KeyDown, new Listener() {

				@Override
				public void handleEvent(Event e) {
					if (e.keyCode == SWT.CR || e.keyCode == SWT.LF) {
						_filterText = filterForm.getText();
						updateFilter();
					}
				}

			});
			Button filterButton = new Button(filterComp, SWT.PUSH);
			filterButton.setText("Filter");
			filterButton.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event arg0) {
					_filterText = filterForm.getText();
					updateFilter();
				}

			});

			Button clearButton = new Button(filterComp, SWT.PUSH);
			clearButton.setText("Clear");
			clearButton.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event arg0) {
					_filterText = null;
					updateFilter();
				}

			});

			outer.setTopLeft(filterComp);

			_viewer = new TableViewer(outer, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.SINGLE);
			_viewer.setLabelProvider(new GlazedLabelProvider());
			_viewer.setContentProvider(new GlazedContentProvider());
			_viewer.setUseHashlookup(true);

			_table = _viewer.getTable();
			_table.setHeaderVisible(true);
			_table.setLinesVisible(true);
			outer.setContent(_table);

			// let's create some columns
			for (int i = 0; i < 10; i++) {
				final TableColumn tvc = new TableColumn(_table, SWT.NONE);
				tvc.setText("Column " + i);
				tvc.pack();
				final int col = i;
				tvc.addListener(SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event arg0) {
						sortColumn(col);
					}
				});
			}

			_rootList = new ArrayList<RowObject>();
			// let's create loads of rows
			for (int i = 0; i < 1000; i++) {
				_rootList.add(new RowObject(i));
			}

			// hook up everything with glazed lists
			_eventList = GlazedLists.eventList(_rootList);
			_sortedList = new SortedList<RowObject>(_eventList, null);
			_filterList = new FilterList<RowObject>(_sortedList);
			_filterList.addListEventListener(new ListEventListener<RowObject>() {

				@Override
				public void listChanged(ListEvent<RowObject> listChanges) {
					try {
						_table.setRedraw(false);

						// get the list PRIOR to looping, otherwise it won't be the same list as it's modified continuously
						final List<RowObject> changeList = listChanges.getSourceList();

						while (listChanges.next()) {
							int sourceIndex = listChanges.getIndex();
							int changeType = listChanges.getType();
							
							switch (changeType) {
								case ListEvent.DELETE:
									// note the remove of the object fetched from the event list here, we need to remove by index which the viewer does not support
									// and we're removing from the raw list, not the filtered list
									_viewer.remove(_eventList.get(sourceIndex));
									_viewer.refresh(_eventList.get(sourceIndex), true);
									break;
								case ListEvent.INSERT:
									final RowObject obj = changeList.get(sourceIndex);
									_viewer.insert(obj, sourceIndex);
									break;
								case ListEvent.UPDATE:
									break;
							}
						}						
					}
					catch (Exception err) {
						err.printStackTrace();
					}
					finally {
						// most important, we update the table size after the update
						_viewer.setItemCount(_filterList.size());

						_table.setRedraw(true);
						
						// we could do detailed refreshes, but this isn't much of a performance hit
						_viewer.refresh(true);
					}
				}

			});

			// populate initial table
			_viewer.setInput(_eventList);
			_viewer.setItemCount(_eventList.size());

			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		}
		catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void updateFilter() {
		_filterList.getReadWriteLock().writeLock().lock();
		if (_filterText == null || _filterText.length() == 0) {
			_filterList.setMatcher(null);
		}
		else {
			_filterList.setMatcher(new GlazedMatcher());
		}
		_filterList.getReadWriteLock().writeLock().unlock();
		
		_viewer.refresh(true);
	}

	private void sortColumn(int col) {
		int dir = SWT.UP;
		int current = _table.getSortDirection();
		TableColumn tc = _table.getColumn(col);
		if (_table.getSortColumn() == tc) {
			dir = (current == SWT.UP ? SWT.DOWN : SWT.UP);
		}

		_table.setSortColumn(tc);
		_table.setSortDirection(dir);

		// now tell the sorted list we've updated
		_sortedList.setComparator(new GlazedSortComparator(col, dir));
	}

	class GlazedMatcher implements Matcher<RowObject> {
		@Override
		public boolean matches(RowObject row) {
			for (int i = 0; i < _table.getColumnCount(); i++) {
				if (row.getColumnText(i).indexOf(_filterText) > -1)
					return true;
			}

			return false;
		}
	}

	class GlazedSortComparator implements Comparator<RowObject> {

		private int	_col;
		private int	_direction;

		public GlazedSortComparator(int col, int direction) {
			_col = col;
			_direction = direction;
		}

		@Override
		public int compare(RowObject o1, RowObject o2) {
			int ret = Integer.valueOf(o1.getColumnText(_col)).compareTo(Integer.valueOf(o2.getColumnText(_col)));

			if (_direction == SWT.DOWN)
				ret = -ret;

			return ret;
		}

	}

	class GlazedContentProvider implements IStructuredContentProvider {
				
		@Override
		public Object[] getElements(Object inputElement) {
			return _filterList.toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	class GlazedLabelProvider implements ITableLabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			return ((RowObject) element).getColumnText(columnIndex);
		}

	}

	class RowObject {

		private int	_row;

		public RowObject(int row) {
			_row = row;
		}

		public int getRow() {
			return _row;
		}

		public String getColumnText(int col) {
			return "" + (_row + col);
		}

		@Override
		public String toString() {
			return "[RowObject: " + _row + "]";
		}

		
		
	}

}