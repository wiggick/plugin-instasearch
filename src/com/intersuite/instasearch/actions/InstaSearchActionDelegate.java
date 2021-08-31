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

import com.intersuite.instasearch.InstaSearch;
import com.intersuite.instasearch.InstaSearchPlugin;
import com.intersuite.instasearch.ui.InstaSearchView;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public abstract class InstaSearchActionDelegate implements IViewActionDelegate
{
	protected InstaSearchView view;
	
	public void init(IViewPart view)
	{
		this.view = (InstaSearchView) view;
	}
	
	public abstract void run(IAction action);

	public InstaSearch getInstaSearch() {
		return InstaSearchPlugin.getInstaSearch();
	}
	
	public void selectionChanged(IAction action, ISelection selection)
	{
		//no-op
	}

}
