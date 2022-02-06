package com.maxsavteam.pressurenotes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maxsavteam.pressurenotes.R;
import com.maxsavteam.pressurenotes.adapters.RecordsListAdapter;
import com.maxsavteam.pressurenotes.data.Record;
import com.maxsavteam.pressurenotes.data.RecordsManager;
import com.maxsavteam.pressurenotes.utils.SharedConstants;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {

	private RecordsListAdapter mRecordsListAdapter;

	private final ActivityResultLauncher<Intent> addRecordActivityLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
				if(result.getResultCode() == RESULT_OK){
					findViewById( R.id.noRecordsTextView ).setVisibility( View.GONE );
					mRecordsListAdapter.update( RecordsManager.getRecords() );
				}
			}
	);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_records );

		SharedConstants.appFilesDir = getExternalFilesDir( null ).getPath();

		RecordsManager.getInstance(); // initialize

		setupRecyclerView();

		findViewById( R.id.fab_add_record ).setOnClickListener( v->addRecordActivityLauncher.launch( new Intent( this, AddRecordActivity.class ) ) );

		findViewById( R.id.fab_view_chart ).setOnClickListener( v->{
			if ( RecordsManager.getRecordsCount() == 0 ) {
				Toast.makeText( this, R.string.there_is_no_records, Toast.LENGTH_SHORT ).show();
			} else {
				startActivity( new Intent( this, ChartActivity.class ) );
			}
		} );
	}

	private final RecordsListAdapter.RecordsListAdapterCallback mAdapterCallback = ()->{
		if(RecordsManager.getRecordsCount() == 0)
			findViewById( R.id.noRecordsTextView ).setVisibility( View.VISIBLE );
	};

	private void setupRecyclerView() {
		ArrayList<Record> records = RecordsManager.getRecords();
		if(records.size() > 0){
			findViewById( R.id.noRecordsTextView ).setVisibility( View.GONE );
		}
		RecyclerView recyclerView = findViewById( R.id.records_recycler_view );

		LinearLayoutManager manager = new LinearLayoutManager( this );
		manager.setOrientation( RecyclerView.VERTICAL );
		recyclerView.setLayoutManager( manager );

		mRecordsListAdapter = new RecordsListAdapter( this, records, mAdapterCallback );
		recyclerView.setAdapter( mRecordsListAdapter );
	}
}