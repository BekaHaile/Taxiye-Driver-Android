package product.clicklabs.jugnoo.driver.home

import android.content.Context
import android.media.MediaPlayer
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.Prefs

object RingtoneTypes {

    val RING_TYPE_ONE = 1
    val RING_TYPE_TWO = 2
    val RING_TYPE_THREE = 3
    val RING_TYPE_FOUR = 4
    val RING_TYPE_FIVE = 5
    val RING_TYPE_SIX = 6
    val RING_TYPE_SEVEN = 7

    fun getMediaPlayerFromRingtone(context:Context, ringType:Int, checkSaved:Boolean):MediaPlayer{
        val ringT = if(checkSaved) Prefs.with(context).getInt(Constants.KEY_RING_TYPE, ringType) else ringType
        var mediaPlayer: MediaPlayer = when (ringT) {
            RING_TYPE_ONE ->  MediaPlayer.create(context, R.raw.delivery_ring)
            RING_TYPE_TWO -> MediaPlayer.create(context, R.raw.telephone_ring)
            RING_TYPE_THREE -> MediaPlayer.create(context, R.raw.ring_type_three)
            RING_TYPE_FOUR ->MediaPlayer.create(context, R.raw.ring_type_four)
            RING_TYPE_FIVE -> MediaPlayer.create(context, R.raw.ring_type_five)
            RING_TYPE_SIX -> MediaPlayer.create(context, R.raw.ring_type_six)
            RING_TYPE_SEVEN -> MediaPlayer.create(context, R.raw.ring_type_seven)
            else -> MediaPlayer.create(context, R.raw.telephone_ring)
        }
        return mediaPlayer
    }

}