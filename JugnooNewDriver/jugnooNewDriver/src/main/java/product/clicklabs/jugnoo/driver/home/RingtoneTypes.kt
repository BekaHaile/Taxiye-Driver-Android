package product.clicklabs.jugnoo.driver.home

import android.content.Context
import android.media.MediaPlayer
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.Prefs

object RingtoneTypes {

    val RING_TYPE_1 = 1
    val RING_TYPE_2 = 2
    val RING_TYPE_3 = 3
    val RING_TYPE_4 = 4
    val RING_TYPE_5 = 5

    fun getMediaPlayerFromRingtone(context:Context, ringType:Int, checkSaved:Boolean):MediaPlayer{
        val ringT = if(checkSaved) Prefs.with(context).getInt(Constants.KEY_RING_TYPE, ringType) else ringType
        var mediaPlayer: MediaPlayer = when (ringT) {
            RING_TYPE_1 ->  MediaPlayer.create(context, R.raw.delivery_ring)
            RING_TYPE_2 -> MediaPlayer.create(context, R.raw.telephone_ring)
            RING_TYPE_3 -> MediaPlayer.create(context, R.raw.ring_3)
            RING_TYPE_4 -> MediaPlayer.create(context, R.raw.ring_4)
            RING_TYPE_5 -> MediaPlayer.create(context, R.raw.ring_5)
            else -> MediaPlayer.create(context, R.raw.telephone_ring)
        }
        return mediaPlayer
    }

}