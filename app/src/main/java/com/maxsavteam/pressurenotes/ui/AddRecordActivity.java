package com.maxsavteam.pressurenotes.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Button;

import com.maxsavteam.pressurenotes.R;
import com.maxsavteam.pressurenotes.data.Record;
import com.maxsavteam.pressurenotes.data.RecordsManager;
import com.maxsavteam.pressurenotes.ui.widget.InputEditText;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddRecordActivity extends AppCompatActivity {

	private long selectedTime;

	private Pair<Boolean, String> validateSys(String s){
		if(s.isEmpty())
			return new Pair<>( false, getString( R.string.should_not_be_empty ) );
		int i = Integer.parseInt( s );
		if(i > 200)
			return new Pair<>( false, getString( R.string.too_big_value ) );
		return new Pair<>( true, null );
	}

	private Pair<Boolean, String> validateDia(String s){
		if(s.isEmpty())
			return new Pair<>( false, getString( R.string.should_not_be_empty ) );
		int i = Integer.parseInt( s );
		if(i > 130)
			return new Pair<>( false, getString( R.string.too_big_value ) );
		return new Pair<>( true, null );
	}

	private Pair<Boolean, String> validatePulse(String s){
		if(s.isEmpty())
			return new Pair<>( false, getString( R.string.should_not_be_empty ) );
		return new Pair<>( true, null );
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected( item );
	}

	@Override
	public void onBackPressed() {
		setResult( RESULT_CANCELED );
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_add_record );

		ActionBar bar = getSupportActionBar();
		if(bar != null)
			bar.setDisplayHomeAsUpEnabled( true );

		selectedTime = System.currentTimeMillis();
		setButtonText( selectedTime );

		Button btn = findViewById( R.id.btn_save );
		btn.setOnClickListener( v -> {
			InputEditText sysEditText = findViewById( R.id.sys_edit_text );
			InputEditText diaEditText = findViewById( R.id.dia_edit_text );
			InputEditText pulseEditText = findViewById( R.id.pulse_edit_text );
			boolean isValid = true;

			String s = sysEditText.getText().toString();
			Pair<Boolean, String> p = validateSys( s );
			if( !p.first ){
				sysEditText.setError( p.second );
				isValid = false;
			}

			s = diaEditText.getText().toString();
			p = validateDia( s );
			if(!p.first){
				diaEditText.setError( p.second );
				isValid = false;
			}

			s = pulseEditText.getText().toString();
			p = validatePulse( s );
			if(!p.first){
				pulseEditText.setError( p.second );
				isValid = false;
			}

			if(isValid){
				int sys = Integer.parseInt( sysEditText.getText().toString() );
				int dia = Integer.parseInt( diaEditText.getText().toString() );
				int pulse = Integer.parseInt( pulseEditText.getText().toString() );
				Record record = new Record( sys, dia, pulse, selectedTime );
				RecordsManager.getInstance()
						.add( record )
						.save();
				setResult( RESULT_OK );
				super.onBackPressed();
			}
		} );
	}

	private void showDatePicker(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis( selectedTime );
		DatePickerDialog dialog = new DatePickerDialog( this );
		dialog.getDatePicker()
				.setMaxDate( System.currentTimeMillis() );
		dialog.setOnDateSetListener( (view, year, month, dayOfMonth)->{
			calendar.set( Calendar.YEAR, year );
			calendar.set( Calendar.MONTH, month );
			calendar.set( Calendar.DAY_OF_MONTH, dayOfMonth );
			showTimePicker( calendar );
		} );
		dialog.show();
	}

	private void showTimePicker(Calendar calendar){
		TimePickerDialog dialog = new TimePickerDialog( this, (view, hourOfDay, minute)->{
			calendar.set( Calendar.HOUR_OF_DAY, hourOfDay );
			calendar.set( Calendar.MINUTE, minute );
			selectedTime = calendar.getTimeInMillis();

			setButtonText( selectedTime );
		}, calendar.get( Calendar.HOUR_OF_DAY ), calendar.get( Calendar.MINUTE ), true );

		dialog.show();
	}

	private void setButtonText(long time){
		Button btn = findViewById( R.id.btn_measure_time );
		btn.setText( DateFormat.getDateTimeInstance().format( new Date( time ) ) );
		btn.setOnClickListener( v -> {
			showDatePicker();
		} );
	}
}