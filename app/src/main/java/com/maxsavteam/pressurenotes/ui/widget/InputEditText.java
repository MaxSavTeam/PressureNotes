package com.maxsavteam.pressurenotes.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

public class InputEditText extends TextInputEditText {
	public InputEditText(@NonNull @NotNull Context context) {
		super( context );
	}

	public InputEditText(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
		super( context, attrs );
	}

	public InputEditText(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
		super( context, attrs, defStyleAttr );
	}

	@NonNull
	@Override
	public Editable getText() {
		Editable e = super.getText();
		if(e == null)
			return Editable.Factory.getInstance().newEditable("");
		return e;
	}
}
