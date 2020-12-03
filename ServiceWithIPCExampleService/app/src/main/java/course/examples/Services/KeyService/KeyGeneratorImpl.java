package course.examples.Services.KeyService;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.media.MediaPlayer.OnCompletionListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//import android.support.v4.app.NotificationCompat;
import course.examples.Services.KeyCommon.KeyGenerator;

public class KeyGeneratorImpl extends Service {

	private MediaPlayer mPlayer;
	private static String CHANNEL_ID = "Music player style" ;

	private static final int NOTIFICATION_ID = 1;
	private Notification notification ;

	int currentTrack = 0;


	// Set of already assigned IDs
	// Note: These keys are not guaranteed to be unique if the Service is killed 
	// and restarted.
	
	private final static Set<UUID> mIDs = new HashSet<UUID>();

	// Implement the Stub for this Object
	private final KeyGenerator.Stub mBinder = new KeyGenerator.Stub() {
	
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
					mPlayer.release();
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
				mPlayer.release();
				mPlayer = null;
			}
		}

	};

//	@Override
//	public void onCreate() {
//		super.onCreate();
//		mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.audio2);
//		// Stop Service when music has finished playing
//		mPlayer.setOnCompletionListener(new OnCompletionListener() {
//
//			@Override
//			public void onCompletion(MediaPlayer mp) {
//
//				// stop Service if it was started with this ID
//				// Otherwise let other start commands proceed
//				mPlayer.release();
//
//			}
//		});
//	}

	// Return the Stub defined above
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
}
