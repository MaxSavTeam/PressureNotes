package com.maxsavteam.pressurenotes.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.maxsavteam.pressurenotes.R;

public class ThemeActivity extends AppCompatActivity {

	protected int textColor;
	protected int windowBackgroundColor;
	protected int cardViewBackgroundColor;
	public static boolean isDarkMode = false;

	private void applyLightTheme(){
		isDarkMode = false;
		setTheme( R.style.Theme_PressureNotes );
	}

	private void applyDarkTheme(){
		isDarkMode = true;
		setTheme( R.style.Theme_PressureNotes_Dark );
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		SharedPreferences sp = getSharedPreferences( "settings", MODE_PRIVATE );
		int mode = sp.getInt( "dark_mode", 2 );
		if(mode == 0){
			applyLightTheme();
		}else if(mode == 1){
			applyDarkTheme();
		}else{
			switch ( getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK ) {
				case Configuration.UI_MODE_NIGHT_YES:
					applyDarkTheme();
					break;
				case Configuration.UI_MODE_NIGHT_NO:
				default:
					applyLightTheme();
					break;
			}
		}

		TypedValue typedValue = new TypedValue();
		getTheme().resolveAttribute( R.attr.textColor, typedValue, false );
		textColor = getColor( typedValue.data );

		getTheme().resolveAttribute( R.attr.windowBackgroundColor, typedValue, false );
		windowBackgroundColor = getColor( typedValue.data );

		getTheme().resolveAttribute( R.attr.cardViewBackgroundColor, typedValue, false );
		cardViewBackgroundColor = getColor( typedValue.data );
	}
}
