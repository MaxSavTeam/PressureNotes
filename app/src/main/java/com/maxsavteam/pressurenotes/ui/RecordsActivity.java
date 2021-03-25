package com.maxsavteam.pressurenotes.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.maxsavteam.pressurenotes.R;
import com.maxsavteam.pressurenotes.adapters.RecordsListAdapter;
import com.maxsavteam.pressurenotes.data.Record;
import com.maxsavteam.pressurenotes.data.RecordsManager;
import com.maxsavteam.pressurenotes.utils.RequestCodesConstants;
import com.maxsavteam.pressurenotes.utils.SharedConstants;

import java.util.ArrayList;

public class RecordsActivity extends ThemeActivity {

	private RecordsListAdapter mRecordsListAdapter;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
		if ( requestCode == RequestCodesConstants.ADD_RECORD && resultCode == RESULT_OK ) {
			mRecordsListAdapter.update( RecordsManager.getRecords() );
		}
		super.onActivityResult( requestCode, resultCode, data );
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if ( item.getItemId() == R.id.item_select_theme ) {
			openThemePicker();
		}
		return super.onOptionsItemSelected( item );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate( R.menu.records_list_menu, menu );
		return super.onCreateOptionsMenu( menu );
	}

	private void openThemePicker() {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		SharedPreferences sp = getSharedPreferences( "settings", MODE_PRIVATE );
		builder
				.setSingleChoiceItems( R.array.theme_variants, sp.getInt( "dark_mode", 2 ), (dialog, which)->{
					sp.edit().putInt( "dark_mode", which ).apply();
					Intent intent = new Intent(this, RecordsActivity.class);
					intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity( intent );
					finish();
				} )
				.setTitle( R.string.select_theme )
				.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_records );

		SharedConstants.appFilesDir = getExternalFilesDir( null ).getPath();

		RecordsManager.getInstance(); // initialize

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