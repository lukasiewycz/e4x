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
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.swt.DefaultEventTableViewer;
import ca.odell.glazedlists.swt.TableColumnConfigurer;
import ca.odell.glazedlists.swt.TableComparatorChooser;
import ca.odell.glazedlists.swt.TableItemConfigurer;
import e4x.browser.model.AdvancedFile;
import e4x.browser.model.AdvancedPath;

public class SamplePart {

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

		List<AdvancedFile> rootList = new ArrayList<AdvancedFile>();

		EventList<AdvancedFile> eventList = GlazedLists.eventList(rootList);
		SortedList<AdvancedFile> sortedList = new SortedList<AdvancedFile>(eventList, null);

		class PathTableFormat implements AdvancedTableFormat<AdvancedFile>, TableColumnConfigurer {
			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public String getColumnName(int col) {
				if (col == 0) {
					return "name";
				} else {
					return "size";
				}
			}
			

			@Override
			public void configure(TableColumn tableColumn, int col) {
				tableColumn.setWidth(200);
				tableColumn.setMoveable(true);
			}

			@Override
			public Object getColumnValue(AdvancedFile file, int col) {
				if (col == 0) {
					return file.getFilename();
				} else {
					return file.getFilesize();
				}
			}

			@Override
			public Class<?> getColumnClass(int col) {
				Class<?> result;

				switch (col) {
				case 0:
					result = String.class;
					break;
				case 1:
					result = Long.class;
					break;
				default:
					result = Object.class;
				}

				return result;
			}

			@Override
			public Comparator<?> getColumnComparator(int col) {
				Comparator<?> comparator;

				switch (col) {
				case 0:
					comparator = new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							return o1.compareTo(o2);
						}
					};
					break;
				case 1:
					comparator = new Comparator<Long>() {
						@Override
						public int compare(Long o1, Long o2) {
							return o1.compareTo(o2);
						}

					};
					break;
				default:
					comparator = new Comparator<Path>() {
						@Override
						public int compare(Path o1, Path o2) {
							return 0;
						}
					};
				}

				return comparator;
			}

		}

		Table table = new Table(parent, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.SINGLE);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));



		DefaultEventTableViewer<AdvancedFile> tableViewer = new DefaultEventTableViewer<AdvancedFile>(sortedList, table, new PathTableFormat());
		tableViewer.setTableItemConfigurer(new TableItemConfigurer<AdvancedFile>(){
			@Override
			public void configure(TableItem tableItem, AdvancedFile item, Object obj, int row, int column) {
				tableItem.setText(column, obj.toString());
				//tableItem.setForeground(0, Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				tableItem.setImage(0, item.getIcon());
			}
		});
				
		TableComparatorChooser.install(tableViewer, sortedList, false);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TransformedList<AdvancedFile, AdvancedFile> transformedList = GlazedLists.threadSafeList(eventList);

		Thread t = new Thread() {
			public void run() {
				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, filter)) {
					for (Path path : directoryStream) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								transformedList.add(new AdvancedPath(path));
							}
						});
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