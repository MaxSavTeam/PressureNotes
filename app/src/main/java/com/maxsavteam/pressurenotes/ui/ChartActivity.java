package com.maxsavteam.pressurenotes.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.maxsavteam.pressurenotes.App;
import com.maxsavteam.pressurenotes.R;
import com.maxsavteam.pressurenotes.adapters.RecordsListAdapter;
import com.maxsavteam.pressurenotes.data.Record;
import com.maxsavteam.pressurenotes.data.RecordsManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChartActivity extends ThemeActivity {

	private static final String TAG = App.TAG + " ChartActivity";
	private Map<Integer, Record> mPositions = new HashMap<>();
	private GraphView mGraphView;

	public static final int CHART_VIEW_TYPE_DAY = 0;
	public static final int CHART_VIEW_TYPE_WEEK = 1;
	public static final int CHART_VIEW_TYPE_MONTH = 2;
	public static final int CHART_VIEW_TYPE_ALL = 3;
	private Viewport mGraphViewViewport;

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected( item );
	}

	@Retention(RetentionPolicy.RUNTIME)
	@IntDef({ CHART_VIEW_TYPE_DAY, CHART_VIEW_TYPE_WEEK, CHART_VIEW_TYPE_MONTH, CHART_VIEW_TYPE_ALL })
	public @interface ChartViewTypes {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_chart );

		ActionBar actionBar = getSupportActionBar();
		if(actionBar != null){
			actionBar.setDisplayHomeAsUpEnabled( true );
		}

		mGraphView = findViewById( R.id.graph_view );

		initializeModeSpinner();

		mGraphViewViewport = mGraphView.getViewport();

		mGraphViewViewport.setScalable( true );

		mGraphViewViewport.setScrollable( true );

		mGraphViewViewport.setXAxisBoundsManual( true );
		mGraphViewViewport.setYAxisBoundsManual( true );
		mGraphViewViewport.setBorderColor( super.textColor );

		GridLabelRenderer renderer = mGraphView.getGridLabelRenderer();
		renderer.setLabelFormatter( mLabelFormatter );
		renderer.setGridColor( textColor );
		renderer.setHorizontalLabelsColor( textColor );
		renderer.setVerticalLabelsColor( textColor );
	}

	private void initializeModeSpinner() {
		Spinner spinner = findViewById( R.id.spinner_mode );
		String[] data = new String[]{
				getString( R.string.day ),
				getString( R.string.week ),
				getString( R.string.month )
		};
		ArrayAdapter<String> adapter = new ArrayAdapter<>( this, R.layout.spinner_item, data );
		adapter.setDropDownViewResource( R.layout.spinner_dropdown_item );

		spinner.setAdapter( adapter );
		spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				initializePeriodSpinner( position );
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// ignore
			}
		} );
	}

	private static class Period {
		public final long from;
		public final long to;
		public final String label;

		public Period(long from, long to, String label) {
			this.from = from;
			this.to = to;
			this.label = label;
		}
	}

	private ArrayList<Period> getPeriods(Calendar startCalendar, int stepField, long minTime) {
		Locale locale = getResources().getConfiguration().getLocales().get( 0 );
		ArrayList<Period> periods = new ArrayList<>();
		while ( minTime <= startCalendar.getTimeInMillis() ) {
			long to = startCalendar.getTimeInMillis();
			startCalendar.roll( stepField, -1 );
			if(to < startCalendar.getTimeInMillis()){
				// this means that there was a jump at the end of CURRENT year
				startCalendar.roll( Calendar.YEAR, -1 );
			}
			long from = startCalendar.getTimeInMillis() + 1000; // add one second to transfer to the next day
			from = Math.max( from, minTime );

			if ( RecordsManager.getManagerByPeriod().isAnyRecordAtThisPeriodExists( from, to ) ) {
				SimpleDateFormat format = new SimpleDateFormat( "dd MMM", locale );
				String formattedFrom = format.format( new Date( from ) );
				String formattedTo = format.format( new Date( to ) );
				String label;
				if ( formattedFrom.equals( formattedTo ) ) {
					label = formattedFrom;
				}else {
					label = formattedFrom + " - " + formattedTo;
				}
				periods.add( new Period( from, to, label ) );
			}
		}

		return periods;
	}

	private void initializePeriodSpinner(@ChartViewTypes int chartMode) {
		long minTime = RecordsManager.getRecords().get( RecordsManager.getRecordsCount() - 1 ).getMeasureTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis( minTime );
		// reset to start of the day
		calendar.set( Calendar.HOUR_OF_DAY, 0 );
		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );
		minTime = calendar.getTimeInMillis();

		Calendar now = Calendar.getInstance();
		now.setTimeInMillis( System.currentTimeMillis() );

		calendar.set( Calendar.YEAR, now.get( Calendar.YEAR ) );
		calendar.set( Calendar.MONTH, now.get( Calendar.MONTH ) );
		calendar.set( Calendar.DAY_OF_YEAR, now.get( Calendar.DAY_OF_YEAR ) );

		calendar.set( Calendar.HOUR_OF_DAY, 23 );
		calendar.set( Calendar.MINUTE, 59 );
		calendar.set( Calendar.SECOND, 59 );
		calendar.set( Calendar.MILLISECOND, 0 );

		ArrayList<Period> periods = new ArrayList<>();
		if ( chartMode == CHART_VIEW_TYPE_DAY ) {
			periods = getPeriods( calendar, Calendar.DAY_OF_YEAR, minTime );
		} else if ( chartMode == CHART_VIEW_TYPE_WEEK ) {
			periods = getPeriods( calendar, Calendar.WEEK_OF_YEAR, minTime );
		} else if ( chartMode == CHART_VIEW_TYPE_MONTH ) {
			periods = getPeriods( calendar, Calendar.MONTH, minTime );
		}

		String[] data = new String[ periods.size() ];
		for (int i = 0; i < periods.size(); i++)
			data[ i ] = periods.get( i ).label;

		Spinner spinner = findViewById( R.id.spinner_period );
		ArrayAdapter<String> adapter = new ArrayAdapter<>( this, R.layout.spinner_item, data );
		adapter.setDropDownViewResource( R.layout.spinner_dropdown_item );
		spinner.setAdapter( adapter );
		ArrayList<Period> finalPeriods = periods;
		spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				initialize( finalPeriods.get( position ).from, finalPeriods.get( position ).to );
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// ignore
			}
		} );
	}

	private void initialize(long fromPeriod, long toPeriod) {
		mPositions = new HashMap<>();
		mGraphView.removeAllSeries();

		ArrayList<Record> records = RecordsManager.getManagerByPeriod().getAllRecordsAtPeriod( fromPeriod, toPeriod );
		int n = records.size();

		int minSys = Integer.MAX_VALUE, maxSys = 0;
		int minDia = Integer.MAX_VALUE, maxDia = 0;
		for (Record record : records) {
			minSys = Math.min( minSys, record.getSystolicPressure() );
			maxSys = Math.max( maxSys, record.getSystolicPressure() );

			minDia = Math.min( minDia, record.getDiastolicPressure() );
			maxDia = Math.max( maxDia, record.getDiastolicPressure() );
		}

		DataPoint[][] dataPoints = new DataPoint[ 2 ][ n ];
		int[] seriesColors = new int[]{ R.color.orange, R.color.light_green };
		String[] seriesTitles = new String[]{ getString( R.string.systolic_pressure ), getString( R.string.diastolic_pressure ) };

		int next = -1;
		if ( n == 1 ) {
			next++; // centered
		}
		for (int i = n - 1; i >= 0; i--) {
			Record record = records.get( i );
			mPositions.put( ++next, record );
			int j = n - i - 1;
			dataPoints[ 0 ][ j ] = new DataPoint( next, record.getSystolicPressure() );
			dataPoints[ 1 ][ j ] = new DataPoint( next, record.getDiastolicPressure() );
		}

		for (int it = 0; it < 2; it++) {
			LineGraphSeries<DataPoint> series = new LineGraphSeries<>( dataPoints[ it ] );

			series.setAnimated( true );
			series.setDrawDataPoints( true );
			series.setDataPointsRadius( 15f );

			series.setColor( getColor( seriesColors[ it ] ) );
			series.setOnDataPointTapListener( mOnDataPointTapListener );
			series.setTitle( seriesTitles[ it ] );
			mGraphView.addSeries( series );
		}

		mGraphViewViewport.setMinX( 0 );
		if ( n == 1 ) {
			mGraphViewViewport.setMaxX( next + 1 );
		} else {
			mGraphViewViewport.setMaxX( next );
		}

		int minY = Math.max( 0, Math.min( minDia, minSys ) - 10 );
		int maxY = Math.max( maxDia, maxSys ) + 10;
		mGraphViewViewport.setMinY( minY );
		mGraphViewViewport.setMaxY( maxY );

		mGraphView.getGridLabelRenderer().setNumHorizontalLabels( records.size() );

		LegendRenderer legendRenderer = mGraphView.getLegendRenderer();
		legendRenderer.setVisible( true );
		legendRenderer.setTextColor( Color.WHITE );
	}

	private final LabelFormatter mLabelFormatter = new LabelFormatter() {
		@Override
		public String formatLabel(double value, boolean isValueX) {
			if ( !isValueX ) {
				return String.valueOf( (int) value );
			}
			Record record = mPositions.get( (int) value );
			if ( record == null ) {
				return "";
			}
			long time = record.getMeasureTime();

			return new SimpleDateFormat( "dd.MM", Locale.ROOT ).format( new Date( time ) );
		}

		@Override
		public void setViewport(Viewport viewport) {

		}
	};

	private final OnDataPointTapListener mOnDataPointTapListener = (series, dataPoint)->{
		Record record = mPositions.get( (int) dataPoint.getX() );
		if ( record == null ) {
			return;
		}

		View view = LayoutInflater.from( this ).inflate( R.layout.record_item, null );
		RecordsListAdapter.ViewHolder holder = new RecordsListAdapter.ViewHolder( view );
		RecordsListAdapter.fillViewHolder( holder, record, this );

		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder
				.setView( view )
				.setPositiveButton( R.string.close, (dialog, which)->dialog.cancel() );
		AlertDialog dialog = builder.create();
		dialog.getWindow().getDecorView().setBackgroundColor( cardViewBackgroundColor );
		dialog.show();
	};
}