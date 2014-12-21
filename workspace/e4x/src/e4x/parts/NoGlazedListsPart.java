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
package e4x.parts;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

public class NoGlazedListsPart {

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

		
		List<Path> pathList = new ArrayList<Path>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, filter)) {
			for (Path path : directoryStream) {
				pathList.add(path);
			}
		} catch (IOException ex) {
		}



		TableViewer v = new TableViewer(parent, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.SINGLE);
		v.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		class MyContentProvider implements IStructuredContentProvider {
			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public Object[] getElements(Object arg0) {
				return pathList.toArray();
			}
		}
		v.setContentProvider(new MyContentProvider());
		v.setInput(pathList);
		MyViewerComparator comparator = new MyViewerComparator();
		v.setComparator(comparator);

		TableViewerColumn colFirstName = new TableViewerColumn(v, SWT.NONE);
		colFirstName.getColumn().setMoveable(true);
		colFirstName.getColumn().setWidth(200);
		colFirstName.getColumn().setText("Filename");
		colFirstName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Path p = (Path) element;
				return p.getFileName().toString();
			}
		});

		TableViewerColumn colFirstId = new TableViewerColumn(v, SWT.NONE);
		colFirstId.getColumn().setMoveable(true);
		colFirstId.getColumn().setWidth(200);
		colFirstId.getColumn().setText("Size");
		colFirstId.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Path p = (Path) element;
				try {
					if(Files.isDirectory(p)){
						return "";
					}
					return ""+Files.size(p);
				} catch (IOException e) {
					return "";
				}
			}
		});
		colFirstId.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(1);
				int dir = comparator.getDirection();
				v.getTable().setSortDirection(dir);
				v.getTable().setSortColumn(colFirstId.getColumn());
				v.refresh();
			}
		});

		final Table table = v.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

	}


	public static int BASE = 0;
	public static int EXTENSION = 1;

	private String[] getFilename(Path path) {
		return ("" + path.getFileName()).split("\\.(?=[^\\.]+$)");
	}

	@Focus
	public void setFocus() {

	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}
}