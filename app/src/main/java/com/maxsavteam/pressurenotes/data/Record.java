package com.maxsavteam.pressurenotes.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Record {

	private static int nextId = 0;

	private final int mSystolicPressure;
	private final int mDiastolicPressure;
	private final int mPulse;
	private final long mMeasureTime;
	private final int recordLocalId;

	public Record(int systolicPressure, int diastolicPressure, int pulse, long measureTime) {
		mSystolicPressure = systolicPressure;
		mDiastolicPressure = diastolicPressure;
		mPulse = pulse;
		mMeasureTime = measureTime;
		recordLocalId = nextId++;
	}

	public int getSystolicPressure() {
		return mSystolicPressure;
	}

	public int getDiastolicPressure() {
		return mDiastolicPressure;
	}

	public int getPulse() {
		return mPulse;
	}

	public long getMeasureTime() {
		return mMeasureTime;
	}

	public int getRecordLocalId() {
		return recordLocalId;
	}

	public JSONObject getJSON() throws JSONException {
		return new JSONObject()
				.put( "sys", mSystolicPressure )
				.put( "dia", mDiastolicPressure )
				.put( "pulse", mPulse )
				.put( "measure_time", mMeasureTime );
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

		if ( mSystolicPressure != record.mSystolicPressure ) {
			return false;
		}
		if ( mDiastolicPressure != record.mDiastolicPressure ) {
			return false;
		}
		if ( mPulse != record.mPulse ) {
			return false;
		}
		return mMeasureTime == record.mMeasureTime;
	}

	@Override
	public int hashCode() {
		int result = mSystolicPressure;
		result = 31 * result + mDiastolicPressure;
		result = 31 * result + mPulse;
		result = 31 * result + (int) ( mMeasureTime ^ ( mMeasureTime >>> 32 ) );
		return result;
	}
}
