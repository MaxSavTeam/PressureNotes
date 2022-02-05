package com.maxsavteam.pressurenotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.maxsavteam.pressurenotes.App;
import com.maxsavteam.pressurenotes.R;
import com.maxsavteam.pressurenotes.data.Record;
import com.maxsavteam.pressurenotes.data.RecordsManager;
import com.maxsavteam.pressurenotes.utils.ColorsLevelResolver;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecordsListAdapter extends RecyclerView.Adapter<RecordsListAdapter.ViewHolder> {

	private static final String TAG = App.TAG + " RecordsListAdapter";
	private ArrayList<Record> mRecords;
	private final Context mContext;
	private final RecordsListAdapterCallback mAdapterCallback;

	public interface RecordsListAdapterCallback{
		void onItemRemoved();
	}

	public RecordsListAdapter(Context context, ArrayList<Record> records, RecordsListAdapterCallback callback) {
		mRecords = records;
		mContext = context;
		mAdapterCallback = callback;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder( LayoutInflater.from( parent.getContext() ).inflate( R.layout.record_item, parent, false ) );
	}

	public static void fillViewHolder(ViewHolder holder, Record record, Context context) {
		holder.sysValue.setText( String.format( Locale.ROOT, "%d", record.getSystolicPressure() ) );
		holder.diaValue.setText( String.format( Locale.ROOT, "%d", record.getDiastolicPressure() ) );
		holder.pulseValue.setText( String.format( Locale.ROOT, "%d", record.getPulse() ) );

		holder.time.setText( DateFormat.getDateTimeInstance().format( new Date( record.getMeasureTime() ) ) );

		holder.sysIndicator.setBackgroundColor( context.getColor( ColorsLevelResolver.getColorForSys( record.getSystolicPressure() ) ) );
		holder.diaIndicator.setBackgroundColor( context.getColor( ColorsLevelResolver.getColorForDia( record.getDiastolicPressure() ) ) );

		holder.arrhythmiaImageView.setVisibility( record.isArrhythmia() ? View.VISIBLE : View.GONE );

		holder.itemView.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				holder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener( this );
				float maxX = Math.max( holder.sysIndicator.getX(), holder.diaIndicator.getX() );
				holder.sysIndicator.setX( maxX );
				holder.diaIndicator.setX( maxX );
			}
		} );
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Record record = mRecords.get( position );
		fillViewHolder( holder, record, mContext );

		holder.itemView.setOnLongClickListener( v->{
			AlertDialog.Builder builder = new AlertDialog.Builder( mContext );
			builder
					.setTitle( R.string.delete )
					.setMessage( R.string.delete_record )
					.setNegativeButton( R.string.delete, ( (dialog, which)->{
						RecordsManager.getInstance()
								.remove( position )
								.save();
						mRecords.remove( position );
						notifyItemRemoved( position );
						mAdapterCallback.onItemRemoved();
					} ) )
					.show();
			return true;
		} );
	}

	public void update(ArrayList<Record> newData) {
		int updatedPos = newData.size() - 1;
		for (int i = 0; i < Math.min( newData.size(), mRecords.size() ); i++) {
			if ( !mRecords.get( i ).equals( newData.get( i ) ) ) {
				updatedPos = i;
				break;
			}
		}
		mRecords = newData;
		notifyItemInserted( updatedPos );
	}

	@Override
	public int getItemCount() {
		return mRecords.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public final TextView sysValue;
		public final TextView diaValue;
		public final TextView pulseValue;
		public final TextView time;
		public final LinearLayout sysIndicator;
		public final LinearLayout diaIndicator;
		public final ImageView arrhythmiaImageView;

		public ViewHolder(@NonNull View itemView) {
			super( itemView );

			sysValue = itemView.findViewById( R.id.item_sys_value );
			diaValue = itemView.findViewById( R.id.item_dia_value );
			pulseValue = itemView.findViewById( R.id.item_pulse_value );

			sysIndicator = itemView.findViewById( R.id.item_sys_indicator );
			diaIndicator = itemView.findViewById( R.id.item_dia_indicator );

			time = itemView.findViewById( R.id.item_time );

			arrhythmiaImageView = itemView.findViewById( R.id.item_arrhythmia_image_view );
		}

	}

}
