package com.maxsavteam.pressurenotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.maxsavteam.pressurenotes.App;
import com.maxsavteam.pressurenotes.R;
import com.maxsavteam.pressurenotes.data.DiffUtilCallback;
import com.maxsavteam.pressurenotes.data.Record;
import com.maxsavteam.pressurenotes.utils.ColorsLevelResolver;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import team.maxsav.logger.Logger;

public class RecordsListAdapter extends RecyclerView.Adapter<RecordsListAdapter.ViewHolder> {

	private static final String TAG = App.TAG + " RecordsListAdapter";
	private ArrayList<Record> mRecords;
	private final Locale mLocale;
	private final Context mContext;

	public RecordsListAdapter(Context context, ArrayList<Record> records) {
		mRecords = records;
		mLocale = context.getResources().getConfiguration().getLocales().get( 0 );
		mContext = context;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder( LayoutInflater.from( parent.getContext() ).inflate( R.layout.record_item, parent, false ) );
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Record record = mRecords.get( position );
		holder.sysValue.setText( String.format( mLocale, "%d", record.getSystolicPressure() ) );
		holder.diaValue.setText( String.format( mLocale, "%d", record.getDiastolicPressure() ) );
		holder.pulseValue.setText( String.format( mLocale, "%d", record.getPulse() ) );

		holder.time.setText( DateFormat.getDateTimeInstance().format( new Date( record.getMeasureTime() ) ) );

		holder.sysIndicator.setBackgroundColor( mContext.getColor( ColorsLevelResolver.getColorForSys( record.getSystolicPressure() ) ) );
		holder.diaIndicator.setBackgroundColor( mContext.getColor( ColorsLevelResolver.getColorForDia( record.getDiastolicPressure() ) ) );

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

		public ViewHolder(@NonNull View itemView) {
			super( itemView );

			sysValue = itemView.findViewById( R.id.item_sys_value );
			diaValue = itemView.findViewById( R.id.item_dia_value );
			pulseValue = itemView.findViewById( R.id.item_pulse_value );

			sysIndicator = itemView.findViewById( R.id.item_sys_indicator );
			diaIndicator = itemView.findViewById( R.id.item_dia_indicator );

			time = itemView.findViewById( R.id.item_time );
		}

	}

}
