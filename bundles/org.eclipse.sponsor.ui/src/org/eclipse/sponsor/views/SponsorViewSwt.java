/*************************************************************************
 *  Copyright (c) 2000, 2016 IBM Corporation and others.
 *  Copyright (c) 2023 The Eclipse Foundation and others.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which accompanies this
 * distribution, and is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *************************************************************************/
package org.eclipse.sponsor.views;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import org.eclipse.sponsor.bundle.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class SponsorViewSwt extends ViewPart {
	public static final String ID = "org.eclipse.sponsor.views.SponsorView";

	static String text = "If you value the Eclipse IDE, please consider supporting "
			+ "future development. Contributions from users like you help fund the "
			+ "operations of the Eclipse open source projects that build the Eclipse IDE.\n\uFFFC";
	static int MARGIN = 5;

	@Inject
	Shell shell;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		assembleWidgets(parent);
	}

	void addControl(StyledText styledText, Control control, int offset) {
		StyleRange style = new StyleRange();
		style.start = offset;
		style.length = 1;
		style.data = control;
		control.pack();
		Rectangle rect = control.getBounds();
		int ascent = 2 * rect.height / 3;
		int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN, rect.width + 2 * MARGIN);
		styledText.setStyleRange(style);
	}

	private void assembleWidgets(Composite composite) {
		var styledText = new StyledText(composite, SWT.WRAP | SWT.READ_ONLY);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		styledText.setBackground(null);
		styledText.setText(text);

		addBackgroundImage(styledText);

		Button button = new Button(styledText, SWT.PUSH);
		button.setText("Sponsor");
		int offset = text.indexOf('\uFFFC');
		addControl(styledText, button, offset);
		button.setLocation(styledText.getLocationAtOffset(offset));
		button.addSelectionListener(widgetSelectedAdapter(e -> goDonate()));

		final Cursor cursor = getDisplay().getSystemCursor(SWT.CURSOR_ARROW);
		button.addListener(SWT.MouseEnter, e -> button.setCursor(cursor));

		// reposition widgets on paint event
		styledText.addPaintObjectListener(event -> {
			Control control = (Control) event.style.data;
			Point pt = control.getSize();
			int x = event.x + MARGIN;
			int y = event.y + event.ascent - 2 * pt.y / 3;
			control.setLocation(x, y);
		});
	}

	private void addBackgroundImage(StyledText styledText) {
		Image image = Activator.getDefault().getImageRegistry().get("logo");
		if (image != null) {
			styledText.addListener(SWT.Paint, e -> {
				var rect = image.getBounds();
				var canvas = styledText.getBounds();
				var ratio = Math.min((double) canvas.width / rect.width, (double) canvas.height / rect.height);
				var width = (int) (rect.width * ratio);
				var height = (int) (rect.height * ratio);
				var left = canvas.width - width;
				var top = canvas.height - height;

				GC gc = e.gc;
				gc.setAlpha(50);
				gc.drawImage(image, 0, 0, rect.width, rect.height, left, top, width, height);
			});
		}
	}

	@Override
	public void dispose() {
	}

	
	private void goDonate() {
		getDisplay().asyncExec(() -> {
			try {
				PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
						.openURL(new URL("https://www.eclipse.org/sponsor/ide"));
			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (MalformedURLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		});
	}

	private Display getDisplay() {
		return shell.getDisplay();
	}

	@Override
	public void setFocus() {
		
	}
}