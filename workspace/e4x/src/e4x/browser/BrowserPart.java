/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package e4x.browser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.swt.DefaultEventTableViewer;
import ca.odell.glazedlists.swt.GlazedListsSWT;
import ca.odell.glazedlists.swt.TableColumnConfigurer;
import ca.odell.glazedlists.swt.TableComparatorChooser;
import ca.odell.glazedlists.swt.TableItemConfigurer;
import e4x.browser.cells.CellData;
import e4x.browser.cells.CustomTableItemConfigurer;
import e4x.browser.columns.AdvancedFileTypeComparator;
import e4x.browser.columns.BasenameColumn;
import e4x.browser.columns.BrowserColumn;
import e4x.browser.columns.ColumnTableFormat;
import e4x.browser.columns.ExtensionColumn;
import e4x.browser.columns.FilesizeColumn;
import e4x.browser.model.FileElement;
import e4x.browser.model.PathElement;
import e4x.browser.model.ParentElement;

public class BrowserPart {

	@Inject
	private MDirtyable dirty;

	@PostConstruct
	public void createComposite(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		parent.setLayout(layout);

		Path directory = Paths.get(System.getProperty("user.home"));
		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
			@Override
			public boolean accept(Path path) throws IOException {
				return !Files.isHidden(path) && Files.isReadable(path);
			}
		};

		List<BrowserColumn<?>> columnList = new ArrayList<BrowserColumn<?>>();
		// columnList.add(new TypeColumn());
		columnList.add(new BasenameColumn());
		columnList.add(new ExtensionColumn());
		columnList.add(new FilesizeColumn());

		List<FileElement> rootList = new ArrayList<FileElement>();
		EventList<FileElement> eventList = GlazedLists.eventList(rootList);
		SortedList<FileElement> sortedList = new SortedList<FileElement>(eventList, GlazedLists.chainComparators(new AdvancedFileTypeComparator()));

		Table table = new Table(parent, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.SINGLE);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		SortedList<FileElement> groupingList = new SortedList<FileElement>(sortedList, new AdvancedFileTypeComparator());

		DefaultEventTableViewer<FileElement> tableViewer = GlazedListsSWT.eventTableViewerWithThreadProxyList(groupingList, table, new ColumnTableFormat(
				columnList));
		tableViewer.setTableItemConfigurer(new CustomTableItemConfigurer());

		TableComparatorChooser<FileElement> tcc = TableComparatorChooser.install(tableViewer, sortedList, false);

		table.setHeaderVisible(true);
		//table.setLinesVisible(true);

		TransformedList<FileElement, FileElement> transformedList = GlazedLists.threadSafeList(eventList);

		Thread t = new Thread() {
			public void run() {
				transformedList.add(new ParentElement());
				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, filter)) {
					for (Path path : directoryStream) {
						transformedList.add(new PathElement(path));
						Thread.sleep(100);
					}
				} catch (IOException ex) {
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	@Focus
	public void setFocus() {

	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}
}