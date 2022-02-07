package com.maxsavteam.pressurenotes.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

public class Record {

	private static final AtomicInteger nextId = new AtomicInteger(0);

	private final int id = nextId.incrementAndGet();

	private int systolicPressure;
	private int diastolicPressure;
	private int pulse;
	private long measureTime;
	private boolean isArrhythmia;

	public int getId() {
		return id;
	}

	public int getSystolicPressure() {
		return systolicPressure;
	}

	public void setSystolicPressure(int systolicPressure) {
		this.systolicPressure = systolicPressure;
	}

	public int getDiastolicPressure() {
		return diastolicPressure;
	}

	public void setDiastolicPressure(int diastolicPressure) {
		this.diastolicPressure = diastolicPressure;
	}

	public int getPulse() {
		return pulse;
	}

	public void setPulse(int pulse) {
		this.pulse = pulse;
	}

	public long getMeasureTime() {
		return measureTime;
	}

	public void setMeasureTime(long measureTime) {
		this.measureTime = measureTime;
	}

	public boolean isArrhythmia() {
		return isArrhythmia;
	}

	public void setArrhythmia(boolean arrhythmia) {
		isArrhythmia = arrhythmia;
	}

	public JSONObject getJSON() throws JSONException {
		return new JSONObject()
				.put( "sys", systolicPressure )
				.put( "dia", diastolicPressure )
				.put( "pulse", pulse )
				.put( "measure_time", measureTime )
				.put( "arrhythmia", isArrhythmia );
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		Record record = (Record) o;

		if ( systolicPressure != record.systolicPressure ) {
			return false;
		}
		if ( diastolicPressure != record.diastolicPressure ) {
			return false;
		}
		if ( pulse != record.pulse ) {
			return false;
		}
		if ( measureTime != record.measureTime ) {
			return false;
		}
		return isArrhythmia == record.isArrhythmia;
	}

	@Override
	public int hashCode() {
		int result = systolicPressure;
		result = 31 * result + diastolicPressure;
		result = 31 * result + pulse;
		result = 31 * result + (int) ( measureTime ^ ( measureTime >>> 32 ) );
		result = 31 * result + ( isArrhythmia ? 1 : 0 );
		return result;
	}
}
