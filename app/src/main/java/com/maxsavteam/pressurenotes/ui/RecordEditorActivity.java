package com.maxsavteam.pressurenotes.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.maxsavteam.pressurenotes.R;
import com.maxsavteam.pressurenotes.data.Record;
import com.maxsavteam.pressurenotes.data.RecordsManager;
import com.maxsavteam.pressurenotes.ui.widget.InputEditText;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class RecordEditorActivity extends AppCompatActivity {

	private long selectedTime;

	private boolean isArrhythmia = false;

	private Pair<Boolean, String> validateSys(String s) {
		if ( s.isEmpty() ) {
			return new Pair<>( false, getString( R.string.should_not_be_empty ) );
		}
		int i = Integer.parseInt( s );
		if ( i > 200 ) {
			return new Pair<>( false, getString( R.string.too_big_value ) );
		}
		return new Pair<>( true, null );
	}

	private Pair<Boolean, String> validateDia(String s) {
		if ( s.isEmpty() ) {
			return new Pair<>( false, getString( R.string.should_not_be_empty ) );
		}
		int i = Integer.parseInt( s );
		if ( i > 130 ) {
			return new Pair<>( false, getString( R.string.too_big_value ) );
		}
		return new Pair<>( true, null );
	}

	private Pair<Boolean, String> validatePulse(String s) {
		if ( s.isEmpty() ) {
			return new Pair<>( false, getString( R.string.should_not_be_empty ) );
		}
		return new Pair<>( true, null );
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if ( item.getItemId() == android.R.id.home ) {
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
		if ( bar != null ) {
			bar.setDisplayHomeAsUpEnabled( true );
		}

		selectedTime = System.currentTimeMillis();
		setMeasureTimeButtonText( selectedTime );

		findViewById( R.id.btn_arrhythmia ).setOnClickListener( v->{
			isArrhythmia = !isArrhythmia;
			updateArrhythmiaButtonState();
		} );

		InputEditText sysEditText = findViewById( R.id.sys_edit_text );
		InputEditText diaEditText = findViewById( R.id.dia_edit_text );
		InputEditText pulseEditText = findViewById( R.id.pulse_edit_text );
		TextInputEditText commentEditText = findViewById( R.id.comment_edit_text );

		int editingRecordId = getIntent().getIntExtra( "record_id", -1 );
		Record editingRecord;
		if(editingRecordId != -1) {
			Optional<Record> op = RecordsManager.getInstance().findRecordWithId(editingRecordId);
			if(op.isPresent()){
				editingRecord = op.get();
			}else{
				setResult( RESULT_CANCELED );
				finish();
				return;
			}

			setTitle( R.string.edit );

			sysEditText.setText( String.valueOf( editingRecord.getSystolicPressure() ) );
			diaEditText.setText( String.valueOf( editingRecord.getDiastolicPressure() ) );
			pulseEditText.setText( String.valueOf( editingRecord.getPulse() ) );
			commentEditText.setText( editingRecord.getComment() );

			isArrhythmia = editingRecord.isArrhythmia();
			updateArrhythmiaButtonState();

			selectedTime = editingRecord.getMeasureTime();
			setMeasureTimeButtonText( selectedTime );
		}else{
			editingRecord = null;
		}

		Button btn = findViewById( R.id.btn_save );
		btn.setOnClickListener( v->{
			boolean isValid = true;

			String s = sysEditText.getText().toString();
			Pair<Boolean, String> p = validateSys( s );
			if ( !p.first ) {
				sysEditText.setError( p.second );
				isValid = false;
			}

			s = diaEditText.getText().toString();
			p = validateDia( s );
			if ( !p.first ) {
				diaEditText.setError( p.second );
				isValid = false;
			}

			s = pulseEditText.getText().toString();
			p = validatePulse( s );
			if ( !p.first ) {
				pulseEditText.setError( p.second );
				isValid = false;
			}

			if ( isValid ) {
				int sys = Integer.parseInt( sysEditText.getText().toString() );
				int dia = Integer.parseInt( diaEditText.getText().toString() );
				int pulse = Integer.parseInt( pulseEditText.getText().toString() );

				String comment = null;
				Editable e = commentEditText.getText();
				if(e != null){
					comment = e.toString().trim();
				}

				boolean isTimeChanged = false;
				Record record;
				if(editingRecord != null) {
					record = editingRecord;
					isTimeChanged = editingRecord.getMeasureTime() != selectedTime;
				} else
					record = new Record();
				record.setSystolicPressure( sys );
				record.setDiastolicPressure( dia );
				record.setPulse( pulse );
				record.setMeasureTime( selectedTime );
				record.setArrhythmia( isArrhythmia );
				record.setComment( comment );

				if(editingRecord == null) {
					RecordsManager.getInstance()
							.add( record )
							.sort()
							.save();
					setResult( RESULT_OK );
				}else{
					RecordsManager.getInstance()
							.sort()
							.save();
					setResult( RESULT_OK,
							new Intent()
									.putExtra( "record_id", editingRecordId )
									.putExtra( "time_changed", isTimeChanged )
					);
				}
				super.onBackPressed();
			}
		} );
	}

	private void updateArrhythmiaButtonState(){
		int color = isArrhythmia ? R.color.red : R.color.light_gray;
		Button btnArrhythmia = findViewById( R.id.btn_arrhythmia );
		btnArrhythmia.setTextColor( getColor( color ) );
		TextViewCompat.setCompoundDrawableTintList(
				btnArrhythmia,
				ColorStateList.valueOf( getColor( color ) )
		);
	}

	private void showDatePicker() {
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

	private void showTimePicker(Calendar calendar) {
		TimePickerDialog dialog = new TimePickerDialog( this, (view, hourOfDay, minute)->{
			calendar.set( Calendar.HOUR_OF_DAY, hourOfDay );
			calendar.set( Calendar.MINUTE, minute );
			selectedTime = calendar.getTimeInMillis();

			setMeasureTimeButtonText( selectedTime );
		}, calendar.get( Calendar.HOUR_OF_DAY ), calendar.get( Calendar.MINUTE ), true );

		dialog.show();
	}

	private void setMeasureTimeButtonText(long time) {
		Button btn = findViewById( R.id.btn_measure_time );
		btn.setText( DateFormat.getDateTimeInstance().format( new Date( time ) ) );
		btn.setOnClickListener( v->showDatePicker() );
	}
}