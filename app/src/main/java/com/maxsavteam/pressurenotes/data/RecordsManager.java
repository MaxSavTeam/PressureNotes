package com.maxsavteam.pressurenotes.data;

import com.maxsavteam.pressurenotes.App;
import com.maxsavteam.pressurenotes.utils.SharedConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import team.maxsav.logger.Logger;

public class RecordsManager {

	private static final String TAG = App.TAG + " RecordsManager";
	private static RecordsManager instance;

	private static final String EMPTY_JSON = "{\"records\":[]}";

	private final ArrayList<Record> mRecords = new ArrayList<>();

	private final ManagerByPeriod mManagerByPeriod;

	public static RecordsManager getInstance() {
		if ( instance == null ) {
			instance = new RecordsManager();
		}
		return instance;
	}

	private RecordsManager() {
		JSONObject jsonObject;
		String dataString;
		try {
			dataString = readDataFromFile();
		} catch (IOException e) {
			e.printStackTrace();
			Logger.i( TAG, "RecordsManager: " + e );
			dataString = EMPTY_JSON;
		}
		try {
			jsonObject = new JSONObject( dataString );

			JSONArray records = jsonObject.getJSONArray( "records" );
			for (int i = 0; i < records.length(); i++) {
				JSONObject recordObject = records.getJSONObject( i );
				int sys = recordObject.getInt( "sys" );
				int dia = recordObject.getInt( "dia" );
				int pulse = recordObject.getInt( "pulse" );
				long time = recordObject.getLong( "measure_time" );
				mRecords.add( new Record( sys, dia, pulse, time ) );
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Logger.i( TAG, "RecordsManager: " + e );
		}
		sort();
		mManagerByPeriod = new ManagerByPeriod( mRecords );
	}

	private String readDataFromFile() throws IOException {
		File file = new File( SharedConstants.appFilesDir + "/records" );
		FileInputStream fis = new FileInputStream( file );
		byte[] buffer = new byte[ 1024 ];
		int len;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( (int) file.length() );
		while ( ( len = fis.read( buffer ) ) != -1 ) {
			outputStream.write( buffer, 0, len );
		}
		return outputStream.toString();
	}

	public RecordsManager add(Record r) {
		mRecords.add( r );
		sort();
		return this;
	}

	public RecordsManager remove(int index){
		mRecords.remove( index );
		return this;
	}

	public void save() {
		String data;
		try {
			JSONArray array = new JSONArray();
			for (Record record : mRecords) {
				array.put( record.getJSON() );
			}
			data = new JSONObject()
					.put( "records", array )
					.toString();
		} catch (JSONException e) {
			Logger.i( TAG, "save: " + e );
			data = EMPTY_JSON;
		}
		try {
			writeDataToFile( data );
		} catch (IOException e) {
			e.printStackTrace();
			Logger.i( TAG, "save: " + e );
		}
	}

	private void sort() {
		mRecords.sort( (o1, o2)->{
			return -Long.compare( o1.getMeasureTime(), o2.getMeasureTime() ); // descending order
		} );
	}

	public static ArrayList<Record> getRecords() {
		return new ArrayList<>( getInstance().mRecords );
	}

	public static int getRecordsCount(){
		return getInstance().mRecords.size();
	}

	private void writeDataToFile(String data) throws IOException {
		File file = new File( SharedConstants.appFilesDir + "/records" );
		if ( !file.exists() && !file.createNewFile() ) {
			throw new IOException( "Failed to create records file" );
		}
		FileOutputStream fos = new FileOutputStream( file );
		fos.write( data.getBytes( StandardCharsets.UTF_8 ) );
		fos.flush();
		fos.close();
	}

	public static ManagerByPeriod getManagerByPeriod(){
		return getInstance().mManagerByPeriod;
	}

	public static class ManagerByPeriod {
		private final ArrayList<Record> mRecords;

		private ManagerByPeriod(ArrayList<Record> records) {
			mRecords = records;
		}

		public boolean isAnyRecordAtThisPeriodExists(long from, long to){
			for(Record record : mRecords){
				long time = record.getMeasureTime();
				if(from <= time && time < to)
					return true;
			}
			return false;
		}

		public ArrayList<Record> getAllRecordsAtPeriod(long from, long to){
			ArrayList<Record> records = new ArrayList<>();
			for(Record record : mRecords){
				long time = record.getMeasureTime();
				if(from <= time && time < to)
					records.add( record );
			}
			return records;
		}

	}

}
