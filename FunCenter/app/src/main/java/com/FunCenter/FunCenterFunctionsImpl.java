package com.FunCenter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import myAPI.FunCenterFunctions;

public class FunCenterFunctionsImpl extends Service {

	private MediaPlayer mPlayer;
	private static String CHANNEL_ID = "Music player style" ;
	private int mStartID;

	private static final int NOTIFICATION_ID = 1;
	private Notification notification ;

	int currentTrack = 0;


	// Set of already assigned IDs
	// Note: These keys are not guaranteed to be unique if the Service is killed 
	// and restarted.
	
	private final static Set<UUID> mIDs = new HashSet<UUID>();

	// Implement the Stub for this Object
	private final FunCenterFunctions.Stub mBinder = new FunCenterFunctions.Stub() {
	
		// Implement the remote method
		public String[] getKey() {
		
			UUID id;
			
			// Acquire lock to ensure exclusive access to mIDs 
			// Then examine and modify mIDs
			
			synchronized (mIDs) {
				
				do {
				
					id = UUID.randomUUID();
				
				} while (mIDs.contains(id));

				mIDs.add(id);
			}
			String[] s;
			s = new String[]{ id.toString()};
			Log.i("Ugo says", "String is: " + s[0]) ;
			return s;
		}

		public String getString() {
			return "Hello";
		}

		public Bitmap getImage(int imageNumber) {
			int imageID;
			if (imageNumber == 1) imageID = R.drawable.image1;
			else if (imageNumber == 2) imageID = R.drawable.image2;
			else if (imageNumber == 3) imageID = R.drawable.image3;
			else imageID = R.drawable.ic_launcher;
			return BitmapFactory.decodeResource(getResources(), imageID);
		}

		public void startMusic(int audioNumber) {
			int audioID;
			if (audioNumber == 1) audioID = R.raw.audio1;
			else if (audioNumber == 2) audioID = R.raw.audio2;
			else if (audioNumber == 3) audioID = R.raw.audio3;
			else audioID = -1;

			if (audioID > 0) {
				// Set up the Media Player
				if (mPlayer == null) {
					mPlayer = MediaPlayer.create(getApplicationContext(), audioID);
				}
				else if (audioID != currentTrack) {
					mPlayer.stop();
//					mPlayer.release();
					mPlayer = MediaPlayer.create(getApplicationContext(), audioID);
				}

				mPlayer.start();
				currentTrack = audioID;
			}

		}

		public void pauseMusic() {

			if (mPlayer != null)
				mPlayer.pause();
		}

		public void stopMusic() {
			if (mPlayer != null) {
				mPlayer.stop();
//				mPlayer.release();
				mPlayer = null;
			}
		}

	};

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i("Ugo says", "Entered onCreate()");

		// UB: Starting in Oreo notifications have a notification channel
		//     The channel defines basic properties of
		this.createNotificationChannel();

		// Create a notification area notification so the user
		// can get back to the MusicServiceClient

		final Intent notificationIntent = new Intent(getApplicationContext(),
				FunCenterFunctionsImpl.class);

		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0) ;

		notification =
				new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
						.setSmallIcon(android.R.drawable.ic_media_play)
						.setOngoing(true).setContentTitle("Music Playing")
						.setContentText("FunCenter")
						.setTicker("Music is playing!")
						.setFullScreenIntent(pendingIntent, false)
						.build();

		// Put this Service in a foreground state, so it won't
		// readily be killed by the system
		startForeground(NOTIFICATION_ID, notification);
		Log.i("Ugo says", "Foreground service started");

	}

	// UB 11-12-2018:  Now Oreo wants communication channels...
	private void createNotificationChannel() {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = "Music player notification";
			String description = "The channel for music player notifications";
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

	// Return the Stub defined above
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {

		Log.i("Ugo says", "Entered onStartCommand()");

		if (null != mPlayer) {

			// ID for this start command
			mStartID = startid;

			if (mPlayer.isPlaying()) {

				// Rewind to beginning of song
				mPlayer.seekTo(0);

			} else {

				// Start playing song
				mPlayer.start();

			}

		}

		// Don't automatically restart this Service if it is killed
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.i("Ugo says", "Destroy called") ;
		if (mPlayer != null) {
			mPlayer.stop();
		}
		super.onDestroy();
	}
}
