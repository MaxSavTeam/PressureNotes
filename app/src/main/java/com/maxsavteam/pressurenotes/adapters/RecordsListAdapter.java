package com.maxsavteam.pressurenotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.maxsavteam.pressurenotes.App;
import com.maxsavteam.pressurenotes.R;
import com.maxsavteam.pressurenotes.data.Record;
import com.maxsavteam.pressurenotes.utils.ColorsLevelResolver;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecordsListAdapter extends RecyclerView.Adapter<RecordsListAdapter.ViewHolder> {

	private static final String TAG = App.TAG + " RecordsListAdapter";
	private ArrayList<Record> records;
	private final Context mContext;
	private final RecordsListAdapterCallback mAdapterCallback;

	public interface RecordsListAdapterCallback {
		void onMoreButtonClick(int id, View button);
	}

	public RecordsListAdapter(Context context, ArrayList<Record> records, RecordsListAdapterCallback callback) {
		this.records = deepCopy( records );
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

		if ( record.getComment() == null || record.getComment().isEmpty() ) {
			holder.commentLayout.setVisibility( View.GONE );
		} else {
			holder.commentLayout.setVisibility( View.VISIBLE );
			holder.commentTextView.setText(record.getComment());
		}
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Record record = records.get( position );
		fillViewHolder( holder, record, mContext );

		holder.moreButton.setOnClickListener( v->mAdapterCallback.onMoreButtonClick( record.getId(), v ) );
	}

	public void removeRecordWithId(int id) {
		for (int i = 0; i < records.size(); i++) {
			if ( records.get( i ).getId() == id ) {
				notifyItemRemoved( i );
				records.remove( i );
				break;
			}
		}
	}

	public void update(ArrayList<Record> newData) {
		DiffUtil.DiffResult result = DiffUtil.calculateDiff( new DiffUtil.Callback() {
			@Override
			public int getOldListSize() {
				return records.size();
			}

			@Override
			public int getNewListSize() {
				return newData.size();
			}

			@Override
			public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
				return records.get( oldItemPosition ).getId() ==
						newData.get( newItemPosition ).getId();
			}

			@Override
			public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
				return records.get( oldItemPosition )
						.equals( newData.get( newItemPosition ) );
			}
		} );
		records = deepCopy( newData );
		result.dispatchUpdatesTo( this );
	}

	private ArrayList<Record> deepCopy(ArrayList<Record> data) {
		ArrayList<Record> arrayList = new ArrayList<>();
		for (Record r : data)
			arrayList.add( new Record( r ) );
		return arrayList;
	}

	public void updateRecord(Record newRecord) {
		for (int i = 0; i < records.size(); i++)
			if ( records.get( i ).getId() == newRecord.getId() ) {
				records.set( i, new Record( newRecord ) );
				notifyItemChanged( i );
				break;
			}
	}

	@Override
	public int getItemCount() {
		return records.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public final TextView sysValue;
		public final TextView diaValue;
		public final TextView pulseValue;
		public final TextView time;
		public final LinearLayout sysIndicator;
		public final LinearLayout diaIndicator;
		public final ImageView arrhythmiaImageView;
		public final ImageButton moreButton;

		public final TextView commentTextView;
		public final LinearLayout commentLayout;

		public ViewHolder(@NonNull View itemView) {
			super( itemView );

			sysValue = itemView.findViewById( R.id.item_sys_value );
			diaValue = itemView.findViewById( R.id.item_dia_value );
			pulseValue = itemView.findViewById( R.id.item_pulse_value );

			sysIndicator = itemView.findViewById( R.id.item_sys_indicator );
			diaIndicator = itemView.findViewById( R.id.item_dia_indicator );

			commentTextView = itemView.findViewById( R.id.item_comment );
			commentLayout = itemView.findViewById( R.id.item_comment_layout );

			time = itemView.findViewById( R.id.item_time );

			moreButton = itemView.findViewById( R.id.item_more );

			arrhythmiaImageView = itemView.findViewById( R.id.item_arrhythmia_image_view );
		}

	}

}
