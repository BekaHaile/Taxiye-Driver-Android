package product.clicklabs.jugnoo.driver.datastructure

import android.content.Context
import product.clicklabs.jugnoo.driver.GpsDistanceCalculator
import product.clicklabs.jugnoo.driver.MeteringService

/**
 * Created by shankar on 5/30/16.
 */
object CustomerRideDataGlobal {
    @JvmStatic
    fun getDistance(context: Context?): Double {
        return GpsDistanceCalculator.getTotalDistanceFromSP(context)
    }

    @JvmStatic
    fun getWaitTime(context: Context?): Long {
        return GpsDistanceCalculator.getWaitTimeFromSP(context)
    }

    @JvmStatic
    fun getHaversineDistance(context: Context?): Double {
        return GpsDistanceCalculator.getTotalHaversineDistanceFromSP(context)
    }

    @JvmStatic
    fun updateDistance(context: Context?, distance:Double){
        MeteringService.gpsInstance(context).updateTotalDistance(context, distance)
    }

}