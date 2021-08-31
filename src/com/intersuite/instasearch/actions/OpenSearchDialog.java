/*
 * Copyright (c) 2009 Andrejs Jermakovics.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrejs Jermakovics - initial implementation
 */
package com.intersuite.instasearch.actions;

import com.intersuite.instasearch.ui.InstaSearchPage;
import com.intersuite.instasearch.ui.InstaSearchUI;

import org.eclipse.jface.action.IAction;
import org.eclipse.search.ui.NewSearchUI;

public class OpenSearchDialog extends InstaSearchActionDelegate {
	
	public OpenSearchDialog() {
	}
	
	public void run(IAction action) {
		// set editor as active so we can choose Enclosing project's in the search dialog
		InstaSearchUI.getWorkbenchWindow().getActivePage().activate( InstaSearchUI.getActiveEditor() );
		NewSearchUI.openSearchDialog(InstaSearchUI.getWorkbenchWindow(), InstaSearchPage.ID);
	}

}