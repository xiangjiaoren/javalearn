package com.qualityeclipse.favorities.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import com.qualityeclipse.favorities.views.SampleView;

public class FavoritesViewTest {

	private static final String VIEW_ID = "com.qualityeclipse.favorities.views.SampleView";
	private SampleView testView;

	public void setUp() throws Exception {

		waitForJobs();
		testView = (SampleView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().showView(VIEW_ID);
		
		waitForJobs();
		delay(3000);
		
	}

	public void testView() {
		TableViewer viewer =  testView.getSampleViewer();
		Object[] expectedContent = new Object[] { "one","two","three"  };
		Object[] expectedLabels = new String[] { "one","two","three"  };
		IStructuredContentProvider contentProvider = (IStructuredContentProvider)viewer.getContentProvider();
		assertArrayEquals(expectedContent,contentProvider.getElements(viewer.getInput()));
		
		ITableLabelProvider labelProvider = (ITableLabelProvider) viewer.getLabelProvider();
		
		for(int i = 0; i<expectedLabels.length; i++)assertEquals(expectedLabels[i],labelProvider.getColumnText(expectedContent[i], 1));
		
		
	}
	
	
	
	public void waitForJobs() {
		while (!Job.getJobManager().isIdle()) {

			 delay(1000);
		}

	}
	
	private void delay(long waitTimeMillis) {

		Display display = Display.getCurrent();
		if (display != null) {
			long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
			while (System.currentTimeMillis() < endTimeMillis) {
				if (!display.readAndDispatch())
					display.sleep();

			}
			display.update();

		} else {

			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException e) {
			}
		}
	}
}
