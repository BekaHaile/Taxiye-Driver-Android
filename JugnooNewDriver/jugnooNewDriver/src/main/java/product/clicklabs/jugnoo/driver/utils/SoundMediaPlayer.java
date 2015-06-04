package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class SoundMediaPlayer {
	
	public static MediaPlayer mediaPlayer;
	public static int loopCount, runCount;
    public static boolean cancelRingPlaying;
    
	public static void startSound(Context context, int soundFileId, int loopCount, boolean maxSound, boolean cancelRingPlaying){
		try {
			stopSound();
			if(maxSound){
				AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
			}
			SoundMediaPlayer.loopCount = loopCount;
			SoundMediaPlayer.runCount = 0;
			mediaPlayer = MediaPlayer.create(context, soundFileId);
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			    @Override
			    public void onCompletion(MediaPlayer mp) {
			    	SoundMediaPlayer.runCount++;
			    	if(SoundMediaPlayer.runCount >= SoundMediaPlayer.loopCount){
                        SoundMediaPlayer.cancelRingPlaying = false;
			    		stopSound();
			    	}
			    	else{
			    		mediaPlayer.start();
			    	}
			    }
			});
			mediaPlayer.start();
            if(cancelRingPlaying){
                SoundMediaPlayer.cancelRingPlaying = true;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stopSound(){
		try {
            if(!SoundMediaPlayer.cancelRingPlaying) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
                SoundMediaPlayer.loopCount = 0;
                SoundMediaPlayer.runCount = 0;
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			mediaPlayer = null;
		}
	}
}