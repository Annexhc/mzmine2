package net.sf.mzmine.util.logging.clock;

import java.text.DecimalFormat;

public class DebugTimer {

	private long start = -1, current =-1;

	private DecimalFormat format = new DecimalFormat("#0.0");
	
	public DebugTimer() {
		super();
		startNewTimer();
	}

	public long getStart() {
		return start;
	}


	public void startNewTimer() {
		start = System.nanoTime();
		current = start;
	}
	public void setCurrentTime() {
		current = System.nanoTime();
	}

	public void printTimeAndSetCurrent(String msg) {
		printTime(msg);
		setCurrentTime();
	}

	public void printTime(String msg) {
		double ctime = ((System.nanoTime()-current)/1000000.0);
		double time = ((System.nanoTime()-start)/1000000.0);
		System.out.println(msg+" (time: "+format.format(ctime)+" ms; total: "+format.format(time)+")");
	}
}
