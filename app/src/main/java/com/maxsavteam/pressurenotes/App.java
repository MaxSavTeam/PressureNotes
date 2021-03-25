package com.maxsavteam.pressurenotes;

import android.app.Application;
import android.util.Log;

import com.maxsavitsky.exceptionhandler.ExceptionHandler;

import java.io.IOException;

import team.maxsav.logger.Logger;

public class App extends Application {

	public static final String TAG = "PressureNotes";

	@Override
	public void onCreate() {
		super.onCreate();

		Thread.setDefaultUncaughtExceptionHandler( new ExceptionHandler( this, null, true ) );

		try {
			Logger.initialize( this, null, BuildConfig.DEBUG, 15, true, true );
		} catch (IOException e) {
			e.printStackTrace();
			Log.e( TAG, "onCreate: logger initialization", e );
		}
	}
}
