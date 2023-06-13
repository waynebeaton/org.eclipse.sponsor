/*************************************************************************
 * Copyright (c) 2023 The Eclipse Foundation and others.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which accompanies this
 * distribution, and is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *************************************************************************/
package org.eclipse.sponsor.bundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.sponsor.views.SponsorViewSwt;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.eclipse.sponsor"; //$NON-NLS-1$
	private static Activator plugin;
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		final IWorkbench workbench = PlatformUI.getWorkbench();
		UIJob job = new UIJob("Open Sponsor View") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					workbench.getActiveWorkbenchWindow().getActivePage().showView(SponsorViewSwt.ID);
				} catch (WorkbenchException e) {
					return new Status(IStatus.ERROR, PLUGIN_ID, "Error while opening sponsor view", e);
				}
				return Status.OK_STATUS;
			}
		};
		job.run(new NullProgressMonitor());
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		registry.put("logo", ImageDescriptor.createFromFile(getClass(), "/images/logo.png"));
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
}
