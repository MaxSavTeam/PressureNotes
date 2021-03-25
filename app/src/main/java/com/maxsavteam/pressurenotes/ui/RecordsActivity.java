package com.maxsavteam.pressurenotes.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.maxsavteam.pressurenotes.R;
import com.maxsavteam.pressurenotes.adapters.RecordsListAdapter;
import com.maxsavteam.pressurenotes.data.Record;
import com.maxsavteam.pressurenotes.data.RecordsManager;
import com.maxsavteam.pressurenotes.utils.RequestCodesConstants;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {

	private RecordsListAdapter mRecordsListAdapter;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
		if ( requestCode == RequestCodesConstants.ADD_RECORD && resultCode == RESULT_OK ) {
			mRecordsListAdapter.update( RecordsManager.getRecords() );
		}
		super.onActivityResult( requestCode, resultCode, data );
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_records );

		setupRecyclerView();

		findViewById( R.id.fab_add_record ).setOnClickListener( v->{
			startActivityForResult( new Intent( this, AddRecordActivity.class ), RequestCodesConstants.ADD_RECORD );
		} );

		findViewById( R.id.fab_view_chart ).setOnClickListener( v->{
			startActivityForResult( new Intent( this, ChartActivity.class ), RequestCodesConstants.VIEW_CHART );
		} );
	}

	private void setupRecyclerView() {
		ArrayList<Record> records = RecordsManager.getRecords();
		RecyclerView recyclerView = findViewById( R.id.records_recycler_view );

		LinearLayoutManager manager = new LinearLayoutManager( this );
		manager.setOrientation( RecyclerView.VERTICAL );
		recyclerView.setLayoutManager( manager );

		mRecordsListAdapter = new RecordsListAdapter( this, records );
		recyclerView.setAdapter( mRecordsListAdapter );
	}
}