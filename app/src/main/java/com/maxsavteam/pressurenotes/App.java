package com.maxsavteam.pressurenotes;

import android.app.Application;
import android.util.Log;

import java.io.IOException;

import team.maxsav.logger.Logger;

public class App extends Application {

	public static final String TAG = "PressureNotes";

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			Logger.initialize(
					new Logger.Initializer( this )
							.setDebug( BuildConfig.DEBUG )
							.setTimerPeriod( 15 )
							.setAutoFlushOnException( true )
							.setPrintErrorOnException( true )
							.setRsaPublicKey( null )
			);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e( TAG, "onCreate: logger initialization", e );
		}
	}
}
