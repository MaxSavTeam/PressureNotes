package com.maxsavteam.pressurenotes.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.maxsavteam.pressurenotes.R;
import com.maxsavteam.pressurenotes.data.RecordsManager;
import com.maxsavteam.pressurenotes.utils.RequestCodesConstants;
import com.maxsavteam.pressurenotes.utils.SharedConstants;

public class LaunchActivity extends AppCompatActivity {

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if(requestCode == RequestCodesConstants.RECORDS_ACTIVITY){
			finish();
		}
		super.onActivityResult( requestCode, resultCode, data );
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );

		SharedConstants.appFilesDir = getExternalFilesDir( null ).getPath();

		RecordsManager.getInstance(); // initialize

		startActivityForResult( new Intent( this, RecordsActivity.class ), RequestCodesConstants.RECORDS_ACTIVITY );
	}
}