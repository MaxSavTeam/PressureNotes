package com.maxsavteam.pressurenotes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
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

	private RecordsListAdapter recordsListAdapter;

	private final ActivityResultLauncher<Intent> addRecordActivityLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
				if(result.getResultCode() == RESULT_OK){
					findViewById( R.id.noRecordsTextView ).setVisibility( View.GONE );
					recordsListAdapter.update( RecordsManager.getRecords() );
				}
			}
	);

	private final ActivityResultLauncher<Intent> editRecordActivityLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
				Intent data = result.getData();
				if(result.getResultCode() == RESULT_OK && data != null){
					if(data.getBooleanExtra( "time_changed", false )){
						// position of record have changed
						recordsListAdapter.update( RecordsManager.getRecords() );
					}else{
						recordsListAdapter.updateRecordWithId( data.getIntExtra( "record_id", 0 ) );
					}
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

		findViewById( R.id.fab_add_record ).setOnClickListener( v->addRecordActivityLauncher.launch( new Intent( this, RecordEditorActivity.class ) ) );

		findViewById( R.id.fab_view_chart ).setOnClickListener( v->{
			if ( RecordsManager.getRecordsCount() == 0 ) {
				Toast.makeText( this, R.string.there_is_no_records, Toast.LENGTH_SHORT ).show();
			} else {
				startActivity( new Intent( this, ChartActivity.class ) );
			}
		} );
	}

	private final RecordsListAdapter.RecordsListAdapterCallback mAdapterCallback = (id, buttonView)->{
		PopupMenu popupMenu = new PopupMenu( this, buttonView );
		getMenuInflater().inflate( R.menu.record_popup_menu, popupMenu.getMenu() );
		popupMenu.setOnMenuItemClickListener( item->{
			if(item.getItemId() == R.id.item_delete){
				openDeleteRecordDialog( id );
			}else if(item.getItemId() == R.id.item_edit){
				editRecordActivityLauncher.launch( new Intent(this, RecordEditorActivity.class)
						.putExtra( "record_id", id )
				);
			}

			return true;
		} );

		popupMenu.show();
	};

	private void openDeleteRecordDialog(int id){
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder
				.setTitle( R.string.delete )
				.setMessage( R.string.delete_record )
				.setNegativeButton( R.string.delete, ( (dialog, which)->{
					RecordsManager.getInstance()
							.removeById( id )
							.save();
					recordsListAdapter.removeRecordWithId( id );
				} ) )
				.show();
	}

	private void setupRecyclerView() {
		ArrayList<Record> records = RecordsManager.getRecords();
		if(records.size() > 0){
			findViewById( R.id.noRecordsTextView ).setVisibility( View.GONE );
		}
		RecyclerView recyclerView = findViewById( R.id.records_recycler_view );

		LinearLayoutManager manager = new LinearLayoutManager( this );
		manager.setOrientation( RecyclerView.VERTICAL );
		recyclerView.setLayoutManager( manager );

		recordsListAdapter = new RecordsListAdapter( this, records, mAdapterCallback );
		recyclerView.setAdapter( recordsListAdapter );
	}
}