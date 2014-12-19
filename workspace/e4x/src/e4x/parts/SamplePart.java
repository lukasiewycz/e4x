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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class SamplePart {

	private Text txtInput;

	@Inject
	private MDirtyable dirty;

	public class Data {

		protected String name;
		protected int id;

		public Data(String name, int id) {
			super();
			this.name = name;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

	}

	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		txtInput = new Text(parent, SWT.BORDER);
		txtInput.setMessage("Enter text to mark part as dirty");
		txtInput.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dirty.setDirty(true);
			}
		});
		txtInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Table t = new Table(parent, SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_BOTH));

		TableColumn tc1 = new TableColumn(t, SWT.CENTER);
		TableColumn tc2 = new TableColumn(t, SWT.CENTER);
		tc1.setText("File");
		tc2.setText("Type");
		tc1.setWidth(270);
		tc2.setWidth(70);
		t.setHeaderVisible(true);

		Path this_dir = Paths.get(System.getProperty("user.home"));

		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
			@Override
			public boolean accept(Path path) throws IOException {
				return !Files.isHidden(path) && Files.isReadable(path);
			}
		};

		// PathMatcher matcher =
		// FileSystems.getDefault().getPathMatcher("glob:.java");

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(this_dir, filter)) {
			for (Path path : directoryStream) {
				
				
				if(Files.isDirectory(path)){
					TableItem item1 = new TableItem(t, SWT.NONE);
					item1.setText(new String[] { "[" + path.getFileName()+"]", ""});
				} else {
					String filename = "" + path.getFileName();
					String basename = FilenameUtils.getBaseName(filename);
					String extension = FilenameUtils.getExtension(filename);
					if(basename.equals("") && extension.length()>0){
						basename = extension;
						extension = "";
					}
					
					TableItem item1 = new TableItem(t, SWT.NONE);
					item1.setText(new String[] { basename, extension});
				}
				
				
				String ext = Files.probeContentType(path);
				
			}
		} catch (IOException ex) {
		}
	}

	
	public static int BASE = 0;
	public static int EXTENSION = 1;
	
	private String[] getFilename(Path path) {
		return (""+path.getFileName()).split("\\.(?=[^\\.]+$)");
	}

	@Focus
	public void setFocus() {

	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}
}