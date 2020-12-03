package course.examples.Services.KeyClient;

import android.app.Activity ;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import course.examples.Services.KeyCommon.KeyGenerator;

public class KeyServiceUser extends Activity {

	protected static final String TAG = "KeyServiceUser";
	private KeyGenerator mKeyGeneratorService;
	private boolean mIsBound = false;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */


	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.main);

		Intent i = new Intent(KeyGenerator.class.getName());
		ResolveInfo info = getPackageManager().resolveService(i, 0);
		i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
		startForegroundService(i);

		final TextView output = (TextView) findViewById(R.id.output);
		final ImageView imageView = (ImageView) findViewById(R.id.imageView);

		final Button goButton = (Button) findViewById(R.id.go_button);
		goButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				try {

					// Call KeyGenerator and get a new ID
					if (mIsBound) {
						output.setText(mKeyGeneratorService.getKey()[0]);
					} else {
						Log.i(TAG, "Ugo says that the service was not bound!");
					}

				} catch (RemoteException e) {

					Log.e(TAG, e.toString());

				}
			}
		});

		final Button getImageButton = (Button) findViewById(R.id.button1);
		getImageButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				try {

					// Call KeyGenerator and get a new ID
					if (mIsBound) {
						int imageNumber = 0;
						final EditText editText = findViewById(R.id.editText);
						String text = editText.getText().toString();
						if (text.matches("[0-9]+")) imageNumber = Integer.parseInt(editText.getText().toString());
						imageView.setImageBitmap(mKeyGeneratorService.getImage(imageNumber));
					} else {
						Log.i(TAG, "Ugo says that the service was not bound!");
					}

				} catch (RemoteException e) {

					Log.e(TAG, e.toString());

				}
			}
		});

		final Button playAudioButton = (Button) findViewById(R.id.playButton);
		playAudioButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				try {

					// Call KeyGenerator and get a new ID
					if (mIsBound) {
						int audioNumber = 0;
						final EditText editText = findViewById(R.id.editText2);
						String text = editText.getText().toString();
						if (text.matches("[0-9]+")) audioNumber = Integer.parseInt(editText.getText().toString());
						mKeyGeneratorService.startMusic(audioNumber);
						Log.i(TAG, "Start music called");
					} else {
						Log.i(TAG, "XX: Ugo says that the service was not bound!");
					}

				} catch (RemoteException e) {

					Log.e(TAG, e.toString());

				}
			}
		});

		final Button pauseAudioButton = (Button) findViewById(R.id.pauseButton);
		pauseAudioButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				try {

					// Call KeyGenerator and get a new ID
					if (mIsBound) {
						mKeyGeneratorService.pauseMusic();
					} else {
						Log.i(TAG, "XX: Ugo says that the service was not bound!");
					}

				} catch (RemoteException e) {

					Log.e(TAG, e.toString());

				}
			}
		});

		final Button stopAudioButton = (Button) findViewById(R.id.stopButton);
		stopAudioButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				try {

					// Call KeyGenerator and get a new ID
					if (mIsBound) {
						mKeyGeneratorService.stopMusic();
					} else {
						Log.i(TAG, "XX: Ugo says that the service was not bound!");
					}

				} catch (RemoteException e) {

					Log.e(TAG, e.toString());

				}
			}
		});

	}

	// Bind to KeyGenerator Service
	@Override
	protected void onResume() {
		super.onResume();

		if (!mIsBound) {

			boolean b = false;
			Intent i = new Intent(KeyGenerator.class.getName());

			// UB:  Stoooopid Android API-20 no longer supports implicit intents
			// to bind to a service #@%^!@..&**!@
			// Must make intent explicit or lower target API level to 19.
			ResolveInfo info = getPackageManager().resolveService(i, 0);
			i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

			b = bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);
			if (b) {
				Log.i(TAG, "Ugo says bindService() succeeded!");
			} else {
				Log.i(TAG, "Ugo says bindService() failed!");
			}
		}

		else {
			Log.i(TAG, "Ugo says service is already binded");
		}
	}

	// Unbind from KeyGenerator Service
	@Override
	protected void onPause() {

		super.onPause();

	}

	private final ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder iservice) {

			mKeyGeneratorService = KeyGenerator.Stub.asInterface(iservice);

			mIsBound = true;

		}

		public void onServiceDisconnected(ComponentName className) {

			mKeyGeneratorService = null;

			mIsBound = false;

		}
	};

	@Override
	public void onStart() {
		super.onStart();


	}

	@Override
	public void onStop() {
		super.onStop();

		if (mIsBound) {
			unbindService(this.mConnection);
			mIsBound = false;
			Log.i(TAG, "Ugo says unbindService() succeeded!");
		}

	}

	@Override
	protected void onDestroy() {
//		Intent i = new Intent(KeyGenerator.class.getName());
//		ResolveInfo info = getPackageManager().resolveService(i, 0);
//		i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
		Intent i = new Intent();
		i.setComponent(new ComponentName("course.examples.Services.KeyService", "course.examples.Services.KeyService.KeyGeneratorImpl"));
		stopService(i);
		Log.i(TAG, "Service stopped!");
		super.onDestroy();
	}
}
