package com.maxsavteam.pressurenotes.data;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class DiffUtilCallback extends DiffUtil.Callback {

	private final ArrayList<Record> oldList;
	private final ArrayList<Record> newList;

	public DiffUtilCallback(ArrayList<Record> oldList, ArrayList<Record> newList) {
		this.oldList = oldList;
		this.newList = newList;
	}

	@Override
	public int getOldListSize() {
		return oldList.size();
	}

	@Override
	public int getNewListSize() {
		return newList.size();
	}

	@Override
	public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
		return oldList.get( oldItemPosition ).getRecordLocalId() == newList.get( newItemPosition ).getRecordLocalId();
	}

	@Override
	public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
		return oldList.get( oldItemPosition ).equals( newList.get( newItemPosition ) );
	}
}
