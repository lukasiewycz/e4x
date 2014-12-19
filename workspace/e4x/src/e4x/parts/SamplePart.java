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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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
	    TableColumn tc3 = new TableColumn(t, SWT.CENTER);
	    tc1.setText("First Name");
	    tc2.setText("Last Name");
	    tc3.setText("Address");
	    tc1.setWidth(70);
	    tc2.setWidth(70);
	    tc3.setWidth(80);
	    t.setHeaderVisible(true);

	    for(int i=0;i<10000;i++){
	    	TableItem item1 = new TableItem(t, SWT.NONE);
		    item1.setText(new String[] { "Tim", "Hatton", "Kentucky+"+i });
	    }
	    
	    
		
	}

	@Focus
	public void setFocus() {
		
	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}
}