package com.intersuite.instasearch.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IViewActionDelegate;

public class ExpandAllActionDelegate extends InstaSearchActionDelegate
		implements IViewActionDelegate {

	@Override
	public void run(IAction action) {
		view.expandAll();
	}

}
