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
    val RING_TYPE_8 = 8
    val RING_TYPE_9 = 9
    val RING_TYPE_10 = 10
    val RING_TYPE_11 = 11
    val RING_TYPE_12 = 12
    val RING_TYPE_13 = 13
    val RING_TYPE_14 = 14
    val RING_TYPE_15 = 15
    val RING_TYPE_16 = 16
    val RING_TYPE_17 = 17
    val RING_TYPE_18 = 18
    val RING_TYPE_19 = 19

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
            RING_TYPE_8 -> MediaPlayer.create(context, R.raw.ring_8)
            RING_TYPE_9 -> MediaPlayer.create(context, R.raw.ring_9)
            RING_TYPE_10 -> MediaPlayer.create(context, R.raw.ring_10)
            RING_TYPE_11 -> MediaPlayer.create(context, R.raw.ring_11)
            RING_TYPE_12 -> MediaPlayer.create(context, R.raw.ring_12)
            RING_TYPE_13 -> MediaPlayer.create(context, R.raw.ring_13)
            RING_TYPE_14 -> MediaPlayer.create(context, R.raw.ring_14)
            RING_TYPE_15 -> MediaPlayer.create(context, R.raw.ring_15)
            RING_TYPE_16 -> MediaPlayer.create(context, R.raw.ring_16)
            RING_TYPE_17 -> MediaPlayer.create(context, R.raw.ring_17)
            RING_TYPE_18 -> MediaPlayer.create(context, R.raw.ring_18)
            RING_TYPE_19 -> MediaPlayer.create(context, R.raw.ring_19)
            else -> MediaPlayer.create(context, R.raw.telephone_ring)
        }
        return mediaPlayer
    }

}